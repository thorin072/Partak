package com.delaquess.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AboutDialog extends DialogFragment {

    // tell MainActivityFragment that dialog is now displayed
    @Override
    public Dialog onCreateDialog(final Bundle bundle) {

        // create the dialog
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        final View AboutDialog =
                getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_about_dialog, null);

        builder.setView(AboutDialog); // add GUI to dialog
        super.onCreate(bundle);
        setRetainInstance(true);

        // add Set Line Width Button
        builder.setPositiveButton(R.string.button_set_about,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create(); // return dialog
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
        if (fragment != null)fragment.setDialogOnScreen(true);
    }

    // tell MainActivityFragment that dialog is no longer displayed
    @Override
    public void onDetach() {

        super.onDetach();
        MainActivityFragment fragment = getDoodleFragment();
        if (fragment != null)fragment.setDialogOnScreen(false);
    }

}
