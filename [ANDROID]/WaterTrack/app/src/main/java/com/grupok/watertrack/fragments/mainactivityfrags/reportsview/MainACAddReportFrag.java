package com.grupok.watertrack.fragments.mainactivityfrags.reportsview;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grupok.watertrack.R;
import com.grupok.watertrack.activitys.MainActivity;
import com.grupok.watertrack.database.entities.ReportsEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;

public class MainACAddReportFrag extends Fragment {

    public MainACAddReportFrag(MainActivity parent, UserInfosEntity currentUserInfo) {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_a_c_add_report, container, false);
    }
}