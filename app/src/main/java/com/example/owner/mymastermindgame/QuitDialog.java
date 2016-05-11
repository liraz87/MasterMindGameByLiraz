package com.example.owner.mymastermindgame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by Owner on 08/02/2016.
 */

    // At this class I'm creating the DialogFragment that will show up when the click the "Back" button in his cellphone
    // after the game has started. The use can choose to quit or to save the game for later.
    // Saving the game for later will move the activity to back so whenever the user open the game next time -
    // The game will start from the last move that he made (unless he made a "Destroyed").

public class QuitDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());

        builder.setTitle("Quit");
        builder.setMessage("Would you like to save it for later?").setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().moveTaskToBack(true);
            }
        });

        AlertDialog dialog = builder.create();

        return dialog;
    }

}
