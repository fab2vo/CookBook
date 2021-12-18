package com.fdx.cookbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import java.util.UUID;

public class RatePickerFragment extends DialogFragment {
    public static final String EXTRA_RATE = "com.fdx.cookbook.rate";
    private static final String ARG_ID= "recipeId";
    private static final String TAG = "DebugRatePickerFragment";
    private int mRate;
    private UUID mUUID;
    public static RatePickerFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ID, uuid);
        RatePickerFragment fragment = new RatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle SavedInstanceState) {
        mUUID = (UUID) getArguments().getSerializable(ARG_ID);
        View v= LayoutInflater.from(getActivity())
                .inflate(R.layout.rank_dialog, null);
        RatingBar ratingBar = (RatingBar)v.findViewById(R.id.dialog_ratingbar);
        ratingBar.setRating(4);
        AlertDialog.Builder dialog= new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.DS_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mRate=(int) ratingBar.getRating();
                                addNotetoRecipe();
                                sendResult(Activity.RESULT_OK, mRate);
                            }
                        });
        dialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return ;
                    }
                });
        AlertDialog dia=dialog.show();
        Button b=dia.getButton(DialogInterface.BUTTON_POSITIVE);
        if (b != null) {
            b.setTextColor(getResources().getColor(R.color.fg_icon));
        }
        return dia;
    }

    private void sendResult(int resultCode, int rate) {
        if (getTargetFragment() == null) {
            return;
        }
        // renvoie le new rating (cela ne serta à rien, juste pour essayer)
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RATE, rate);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
     private void addNotetoRecipe(){
         SessionInfo loSession= SessionInfo.get(getActivity());
         CookBook locookbook=CookBook.get(getActivity());
         Recipe r=locookbook.getRecipe(mUUID);
         User lu=loSession.getUser();
         r.addNote(new Note(mRate, lu));
         r.updateTS(AsynCallFlag.NEWRATING, true);
         locookbook.updateRecipe(r);
     }
}
