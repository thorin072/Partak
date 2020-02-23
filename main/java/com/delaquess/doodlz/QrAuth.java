package com.delaquess.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.ByteMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;


public class QrAuth extends DialogFragment {

    private ImageView QrView;
    public  String NIK;
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        // create the dialog
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        View QrCodeDialogFragment =
                getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_qr_auth, null);
        builder.setView(QrCodeDialogFragment); // add GUI to dialog

        // get the ImageView
        QrView = (ImageView) QrCodeDialogFragment.findViewById(
                R.id.qr_image);


        Bundle bundles = this.getArguments();
        String nik = bundles.getString("Nik");
        QrView.setImageBitmap(qrGeneration(nik));

        NIK=nik;
        // add Set Line Width Button
        builder.setPositiveButton(R.string.button_set_inst,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id) {
                        //ДЕЙСТВИЯ НА НАЖАТИЕ
                    }
                }
        );


        return builder.create(); // return dialog
    }

   public Bitmap qrGeneration(String url){

        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // H = 30% damage

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        int size = 300;

        ByteMatrix bitMatrix = null;
        try {
            bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE,size, size, hintMap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = bitMatrix.width();
        Bitmap bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                bmp.setPixel(y, x, bitMatrix.get(x, y)==0 ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;


    }

    // return a reference to the MainActivityFragment
    private MainActivityFragment getDoodleFragment() {
        return (MainActivityFragment) getFragmentManager().findFragmentById(
                R.id.doodleFragment);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivityFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(true);
    }

    // tell MainActivityFragment that dialog is no longer displayed
    @Override
    public void onDetach() {
        super.onDetach();
        MainActivityFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(false);
    }





}
