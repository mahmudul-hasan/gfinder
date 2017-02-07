package com.mhstudio.gasfinder.gaslistpage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mhstudio.gasfinder.R;

public class GaslistFragment extends Fragment {

    private String mParam1;
    private String mParam2;


    public GaslistFragment() {
        // Required empty public constructor
    }

    public static GaslistFragment newInstance(String param1, String param2) {
        GaslistFragment fragment = new GaslistFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gaslist, container, false);
        return rootView;
    }

}
