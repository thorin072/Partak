package com.delaquess.doodlz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.common.ByteArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

// custom View for drawing
public class DoodleView extends View {

    // used to determine whether user moved a finger enough to draw again
    private static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap; // drawing area for displaying or saving
    private Canvas bitmapCanvas; // used to to draw on the bitmap
    private final Paint paintScreen; // used to draw bitmap onto screen
    private final Paint paintLine; // used to draw lines onto bitmap
    public Bitmap BitmapUser;

    private PointDobot pointDobot;
    private LineDobot lineDobot = new LineDobot();
    private PictureDobot pictureDobot;
    private PacketDobot packetDobot;
    private int id_contour=0;
    public String JsonResponse;

    // Maps of current Paths being drawn and Points in those Paths
    private final Map<Integer, Path> pathMap = new HashMap<>();
    private final Map<Integer, Point> previousPointMap =  new HashMap<>();

    // DoodleView constructor initializes the DoodleView
    public DoodleView(Context context, AttributeSet attrs) {

        super(context, attrs); // pass context to View's constructor

        paintScreen = new Paint(); // used to display bitmap onto screen
        pictureDobot=new PictureDobot();

        // set the initial display settings for the painted line
        paintLine = new Paint();
        paintLine.setAntiAlias(true); // smooth edges of drawn line
        paintLine.setColor(Color.BLACK); // default color is black
        paintLine.setStyle(Paint.Style.STROKE); // solid line
        paintLine.setStrokeWidth(5); // set the default line width
        paintLine.setStrokeCap(Paint.Cap.ROUND); // rounded line ends

    }

    // creates Bitmap and Canvas based on View's size
    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {

        bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);

        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE); // erase the Bitmap with white

    }

    // clear the painting
    public void clear() {

        pathMap.clear(); // remove all paths
        previousPointMap.clear(); // remove all previous points
        bitmap.eraseColor(Color.WHITE); // clear the bitmap
        invalidate(); // refresh the screen
        this.pictureDobot = new PictureDobot();


    }

    // set the painted line's color
    public void setDrawingColor(int color) {
        paintLine.setColor(color);
    }

    // return the painted line's color
    public int getDrawingColor() {
        return paintLine.getColor();
    }

    // set the painted line's width
    public void setLineWidth(int width) {
        paintLine.setStrokeWidth(width);
    }

    // return the painted line's width
    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();
    }

    // perform custom drawing when the DoodleView is refreshed on screen
    @Override
    protected void onDraw(Canvas canvas) {// draw the background screen
        canvas.drawBitmap(bitmap, 0, 0, paintScreen);

        // for each path currently being drawn
        for (Integer key : pathMap.keySet())
            canvas.drawPath(pathMap.get(key), paintLine); // draw line
    }

    // handle touch event
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked(); // event type
        int actionIndex = event.getActionIndex(); // pointer (i.e., finger)

        // determine whether touch started, ended or is moving
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {

            touchStarted(event.getX(actionIndex), event.getY(actionIndex),
                    event.getPointerId(actionIndex));

        }

        else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {

            touchEnded(event.getPointerId(actionIndex));

        }

        else {touchMoved(event);}

        invalidate(); // redraw
        return true;
    }

    private void workspaceCheck(int x,int y){

        //Если точка попадает в область
        if (x>0&x<1080&y>0&y<1582){

            //добовляем ее к линии
            pointDobot = new PointDobot(x,y);
            lineDobot.addPoint(pointDobot);

            Log.d("addPoint", pointDobot.getPoint());
        }else {

            //иначе, если предыдущая точка попадала, создаем новую линию,
            // если вышли за границы давно, то ничего не делаем
            if(pointDobot.getX()>0&pointDobot.getX()<1080&
                    pointDobot.getY()>0&pointDobot.getY()<1582){

                pictureDobot.addLine(lineDobot);

                Log.d("lineId", String.valueOf(pictureDobot.getLineId()));

                lineDobot.newLine();
                pointDobot.newPoint(x,y);

                Log.d("addPoint2", pointDobot.getPoint());

            }
        }
    }

    // called when the user touches the screen
    private void touchStarted(float x, float y, int lineID){

        Path path; // used to store the path for the given touch id
        Point point; // used to store the last point in path

        // if there is already a path for lineID
        if (pathMap.containsKey(lineID)) {

            path = pathMap.get(lineID); // get the Path
            path.reset(); // resets the Path because a new touch has started
            point = previousPointMap.get(lineID); // get Path's last point

        }else {

            path = new Path();
            pathMap.put(lineID, path); // add the Path to Map
            point = new Point(); // create a new Point
            previousPointMap.put(lineID, point); // add the Point to the Map

        }

        // move to the coordinates of the touch
        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;

        //первая координата в линии
        workspaceCheck(Math.round(x),Math.round(y));
    }

    // called when the user drags along the screen
    private void touchMoved(MotionEvent event) {

        // for each of the pointers in the given MotionEvent
        for (int i = 0; i < event.getPointerCount(); i++) {

            // получаем ID линии и ее индекс
            int pointerID = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerID);

            // Log.d("NEW pointerID", Integer.valueOf(pointerID).toString());
            // Log.d("NEW pointerIndex", Integer.valueOf(pointerIndex).toString());
            // if there is a path associated with the pointer

            if (pathMap.containsKey(pointerID)) {

                // get the new coordinates for the pointer
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                // get the path and previous point associated with
                // this pointer
                Path path = pathMap.get(pointerID);
                Point point = previousPointMap.get(pointerID);

                // calculate how far the user moved from the last update
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                // if the distance is significant enough to matter

                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {

                    //добавляем координату в линию
                    workspaceCheck(point.x,point.y);

                    // move the path to the new location
                    path.quadTo(point.x, point.y, (newX + point.x) / 2,
                            (newY + point.y) / 2);

                    // store the new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }

    // called when the user finishes a touch
    private void touchEnded(int lineID) {

        Path path = pathMap.get(lineID); // get the corresponding Path
        pictureDobot.addLine(lineDobot);
        lineDobot.newLine();

        Log.d("MES:", "Добавлен новый путь с id "+Integer.valueOf(id_contour).toString());
        Log.d("lineId", String.valueOf(pictureDobot.getLineId()));

        bitmapCanvas.drawPath(path, paintLine); // draw to bitmapCanvas
        path.reset(); // reset the Path
        id_contour++;
    }

    //private byte[] ByteBitmap (Bitmap bitmap){
   //     int width = bitmap.getWidth()/2;
//        int height = bitmap.getHeight()/2;
//
//        int size = bitmap.getRowBytes() * bitmap.getHeight();
//        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
//        bitmap.copyPixelsToBuffer(byteBuffer);
//        return byteBuffer.array();
//    }




    // save the current image to the Gallery
    public void saveImage(String nik) {

        // use "Doodlz" followed by current time as the image name
        String name = "My_masterpiece_Partak" + System.currentTimeMillis() + ".jpg";

        // insert the image on the device
        String location = MediaStore.Images.Media.insertImage(
                getContext().getContentResolver(), bitmap, name,name);

        BitmapUser= bitmap;
        if (location != null) {// display a message indicating that the image was saved

            //прикрепляем к рисунку имя
            packetDobot = new PacketDobot(nik,pictureDobot);
            Log.d("json",(packetDobot.getPacket()).toString());
            pictureDobot = new PictureDobot();


            //JsonResponse - джейсон который будет передан дальше
            JsonResponse = packetDobot.getPacket();

        }
        else {

            // display a message indicating that there was an error saving
            Toast message = Toast.makeText(getContext(),
                    R.string.message_error_saving, Toast.LENGTH_SHORT);

            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);

            message.show();
        }
    }
}
