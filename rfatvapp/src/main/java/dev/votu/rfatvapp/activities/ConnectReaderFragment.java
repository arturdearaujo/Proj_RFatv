package dev.votu.rfatvapp.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.votu.rfatvapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectReaderFragment extends Fragment {


    public ConnectReaderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect_reader, container, false);
    }

}