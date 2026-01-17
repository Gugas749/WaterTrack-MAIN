package com.grupok.watertrack.fragments.mainactivityfrags.creditsview;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grupok.watertrack.R;
import com.grupok.watertrack.activitys.MainActivity;
import com.grupok.watertrack.databinding.FragmentMainACCreditsBinding;

import java.util.ArrayList;
import java.util.List;


public class MainACCreditsFrag extends Fragment {

    private MainActivity parent;
    private FragmentMainACCreditsBinding binding;


    public MainACCreditsFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainACCreditsBinding.inflate(inflater);

        init();

        return binding.getRoot();
    }

    private void init(){
        List<String> credits = new ArrayList<>();
        credits.add(getString(R.string.credits_developers_1));
        credits.add(getString(R.string.credits_developers_2));
        credits.add(getString(R.string.credits_developers_3));
        credits.add(getString(R.string.credits_icons_search));
        credits.add(getString(R.string.credits_icons_water_drop));
        credits.add(getString(R.string.credits_icons_radios_button_icon));
        credits.add(getString(R.string.credits_icons_warning));
        credits.add(getString(R.string.credits_icons_info));
        credits.add(getString(R.string.credits_icons_exit));
        credits.add(getString(R.string.credits_icons_settings));
        credits.add(getString(R.string.credits_icons_arrow));
        credits.add(getString(R.string.credits_icons_close));

        RVAdapterCredits adapter = new RVAdapterCredits(credits);
        binding.rvCreditsCreditsFragMainAc.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCreditsCreditsFragMainAc.setAdapter(adapter);
    }
}