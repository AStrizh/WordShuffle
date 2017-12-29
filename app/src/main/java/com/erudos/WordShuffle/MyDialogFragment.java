package com.erudos.WordShuffle;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by abstr on 12/28/2017.
 */

public class MyDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sample_dialog, container, false);
        getDialog().setTitle("Simple Dialog");
        ImageView view =  getActivity().findViewById(R.id.pokemonimage);
        view.setImageDrawable(  getActivity().getDrawable(R.drawable.pikachu));
        return rootView;
    }
}
