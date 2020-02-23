package com.delaquess.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class IstagramDialogFragment extends DialogFragment{

    private EditText nik_inst;
    public  String NIK;

    // tell MainActivityFragment that dialog is now displayed
    @Override
    public Dialog onCreateDialog(final Bundle bundle) {

        // create the dialog
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        final View istagramDialogFragment =
                getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_istagram_dialog, null);

        builder.setView(istagramDialogFragment); // add GUI to dialog
        super.onCreate(bundle);
        setRetainInstance(true);

        // add Set Line Width Button
        builder.setPositiveButton(R.string.button_set_inst,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id) {

                        nik_inst = (EditText) istagramDialogFragment.findViewById(R.id.inst_edit_nik);
                        String s=nik_inst.getText().toString();

                        if (s.length()==0){

                            Toast.makeText(istagramDialogFragment.getContext(),"Повторите ввод никнейма!",Toast.LENGTH_SHORT).show();

                        }

                        else {

                            Date currentDate = new Date();
                            DateFormat timeFormat = new SimpleDateFormat("HHmmss", Locale.getDefault());
                            String timeText = timeFormat.format(currentDate);
                            String url = nik_inst.getText().toString()+timeText;
                            NIK=url;
                            Toast.makeText(istagramDialogFragment.getContext(),"Успех! Ваш никнейм сохранен!",Toast.LENGTH_SHORT).show();

                        }
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

        fragment.NIK_INST=NIK;

        if (fragment != null)fragment.setDialogOnScreen(false);
    }
}
