package com.grupok.watertrack.fragments.mainactivityfrags.readingscontadorview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.grupok.watertrack.R;
import com.grupok.watertrack.activitys.MainActivity;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.MeterReadingEntity;
import com.grupok.watertrack.database.entities.MeterTypeEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;
import com.grupok.watertrack.databinding.FragmentMainACReadingsContadorBinding;
import com.grupok.watertrack.scripts.SnackBarShow;
import com.grupok.watertrack.scripts.apiCRUD.APIMethods;

import java.util.ArrayList;
import java.util.List;

public class MainACReadingsContadorFrag extends Fragment implements APIMethods.GetUserByIdResponse {

    private MainActivity parent;
    private Context context;
    private FragmentMainACReadingsContadorBinding binding;
    private List<MeterReadingEntity> meterReadingEntities = new ArrayList<>();
    private RVAdapterReadingsACReadingsContadores readingsAdapter;
    private RVAdapterFieldsReadingsContadores fieldsAdapter;
    private MeterEntity meterSelected;
    private int contadorId;

    //----------------------------//
    private boolean userResponse = false;
    private boolean typeResponse = false;
    private UserInfosEntity selectedUser;
    //----------------------------//
    public SnackBarShow snackBarShow = new SnackBarShow();



    public MainACReadingsContadorFrag() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainACReadingsContadorBinding.inflate(inflater);
        context = getContext();

        if (getArguments() != null) {
            contadorId = getArguments().getInt("contadorId", -1);
            String meterJson = getArguments().getString("lastMeterData", null);
            meterSelected = new Gson().fromJson(meterJson, MeterEntity.class);
        }

        init();
        return binding.getRoot();
    }

    private void init() {
        binding.loadingViewReadingsContadorFragMainAc.setVisibility(View.GONE);

        if (meterReadingEntities.isEmpty()) {
            binding.textViewEmptyFields.setVisibility(View.VISIBLE);
            binding.textViewEmptyFields.setText("Este contador não tem leituras.");
        } else {
            binding.textViewEmptyFields.setVisibility(View.VISIBLE);
            setupRVView();
        }
    }

    private void setupRVView() {
        readingsAdapter = new RVAdapterReadingsACReadingsContadores(context, meterReadingEntities);
        readingsAdapter.setOnSelectionChangedListener(selectedReading -> {
            if (selectedReading != null) {
                fieldsAdapter.atualizarCampos(selectedReading);
                binding.textViewEmptyFields.setVisibility(View.GONE);
            } else {
                binding.textViewEmptyFields.setVisibility(View.VISIBLE);
            }
        });

        binding.rvReadingsReadingsContadorMainAc.setLayoutManager(new LinearLayoutManager(context));
        binding.rvReadingsReadingsContadorMainAc.setAdapter(readingsAdapter);

        fieldsAdapter = new RVAdapterFieldsReadingsContadores(context, null, meterSelected, selectedUser);
        binding.rvFieldsReadingsContadorMainAc.setLayoutManager(new LinearLayoutManager(context));
        binding.rvFieldsReadingsContadorMainAc.setAdapter(fieldsAdapter);
    }
    public void setMeterReadings(List<MeterReadingEntity> list) {
        this.meterReadingEntities = list;

        if (binding != null) {
            setupRVView();
        }
    }
    public void onGetUserByIdResponse(boolean response, String message, UserInfosEntity user) {
        if(response){
            userResponse = true;
            selectedUser = user;
            if(typeResponse && userResponse){
                setupRVView();
            }
        }else{
            snackBarShow.display(binding.getRoot(), getString(R.string.apiMethods_VolleyError), -1, 1, binding.snackbarViewReadingsContadorFrag, context);
        }
    }
}
