package com.delaquess.doodlz;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class MainActivityFragment extends Fragment {

    private DoodleView doodleView; // handles touch events and draws
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private boolean dialogOnScreen = false;
    public String NIK_INST;
    public int StatusMemory;

    // value used to determine whether user shook the device to erase
    private static final int ACCELERATION_THRESHOLD = 100000;

    // used to identify the request for using external storage, which
    // the save image feature needs
    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1;

    // called when Fragment's view needs to be created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view =
                inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true); // this fragment has menu items to display

        // get reference to the DoodleView
        doodleView = (DoodleView) view.findViewById(R.id.doodleView);

        // initialize acceleration values
        acceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;

        GetPermision();
        return view;

    }

    // start listening for sensor events
    @Override
    public void onResume() {

        super.onResume();
        enableAccelerometerListening(); // listen for shake event

    }

    // enable listening for accelerometer events
    private void enableAccelerometerListening() {

        // get the SensorManager
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(
                        Context.SENSOR_SERVICE);

        // register to listen for accelerometer events
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    // stop listening for accelerometer events
    @Override
    public void onPause() {

        super.onPause();
        disableAccelerometerListening(); // stop listening for shake

    }

    // disable listening for accelerometer events
    private void disableAccelerometerListening() {

        // get the SensorManager
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(
                        Context.SENSOR_SERVICE);

        // stop listening for accelerometer events
        sensorManager.unregisterListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));

    }

    // event handler for accelerometer events
    private final SensorEventListener sensorEventListener =
            new SensorEventListener() {

                // use accelerometer to determine whether user shook device
                @Override
                public void onSensorChanged(SensorEvent event) {

                    // ensure that other dialogs are not displayed
                    if (!dialogOnScreen) {

                        // get x, y, and z values for the SensorEvent
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];

                        // save previous acceleration value
                        lastAcceleration = currentAcceleration;

                        // calculate the current acceleration
                        currentAcceleration = x * x + y * y + z * z;

                        // calculate the change in acceleration
                        acceleration = currentAcceleration *
                                (currentAcceleration - lastAcceleration);

                        // if the acceleration is above a certain threshold
                        if (acceleration > ACCELERATION_THRESHOLD) confirmErase();
                    }

                }

                // required method of interface SensorEventListener
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };

    // confirm whether image should be erase
    private void confirmErase() {

        EraseImageDialogFragment fragment = new EraseImageDialogFragment();
        fragment.show(getFragmentManager(), "erase dialog");

    }

    // displays the fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.doodle_fragment_menu, menu);

    }

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // switch based on the MenuItem id
        switch (item.getItemId()) {

            case R.id.about:
                AboutDialog about = new AboutDialog();
                about.show(getFragmentManager(), "about dialog");
                return true;

            case R.id.color:

                ColorDialogFragment colorDialog = new ColorDialogFragment();
                colorDialog.show(getFragmentManager(), "color dialog");

                return true; // consume the menu event

            case R.id.line_width:

                LineWidthDialogFragment widthDialog =
                        new LineWidthDialogFragment();

                widthDialog.show(getFragmentManager(), "line width dialog");

                return true; // consume the menu event

            case R.id.delete_drawing:

                confirmErase(); // confirm before erasing image

                return true; // consume the menu event

            case R.id.drop_qr:

                IstagramDialogFragment instDialog =
                        new IstagramDialogFragment();
                instDialog.show(getFragmentManager(), "inst dialog");
                return true; // consume the menu event

            case R.id.drop_aws:

                if (NIK_INST == null) {

                    Toast.makeText(this.getContext(), "Для отправки рисунка на сервер " +
                                    "требуется ввести свой никнейм в Меню -> Инстаграмм!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //Получить json
                    saveImage(NIK_INST);
                    String json = getJSON();
                    getBitmapFile(getBitmapUrl(), "coordinate.jpg");
                    Log.d("JSON", json);

                    //Отправка на сервер
                    Intent intent = new Intent(getActivity(), AWS.class);
                    intent.putExtra("url", NIK_INST + ".json");
                    intent.putExtra("json", json);

                    startActivity(intent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getBitmapFile(Bitmap bitmap, String name) {

        File filesDir = getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File imageFile = new File(filesDir, name);

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
    }

    private String getJSON() {
        return doodleView.JsonResponse;
    }

    private Bitmap getBitmapUrl() {
        return doodleView.BitmapUser;
    }
    // requests for the permission needed for saving the image if
    // necessary or saves the image if the app already has permission

    private int GetPermision() {
        // to save the image
        if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            // shows an explanation for why permission is needed
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());

                // set Alert Dialog's message
                builder.setMessage(R.string.permission_explanation);

                // add an OK button to the dialog
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // request permission
                                requestPermissions(new String[]{
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        SAVE_IMAGE_PERMISSION_REQUEST_CODE);
                            }
                        });

                // display the dialog
                builder.create().show();

            } else {// request permission

                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        SAVE_IMAGE_PERMISSION_REQUEST_CODE);
            }
        } else { // if app already has permission to write to external storage
            return  getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return 1;
    }


    private void saveImage(String nik_inst) {// checks if the app does not have permission needed

        if (StatusMemory==0){
            doodleView.saveImage(nik_inst); // save the image
        }
        else{
            GetPermision();

        }


    }

    // called by the system when the user either grants or denies the
    // permission for saving an image
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        // switch chooses appropriate action based on which feature
        // requested permission
        switch (requestCode) {

            case SAVE_IMAGE_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    doodleView.saveImage(NIK_INST); // save the image
                return;
        }
    }

    // returns the DoodleView
    public DoodleView getDoodleView() {return doodleView;}

    // indicates whether a dialog is displayed
    public void setDialogOnScreen(boolean visible) {dialogOnScreen = visible;}

}
