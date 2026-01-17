package com.grupok.watertrack.fragments.mainactivityfrags.reportsview;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.grupok.watertrack.R;
import com.grupok.watertrack.activitys.MainActivity;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.ReportsEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;
import com.grupok.watertrack.databinding.FragmentMainACAddReportBinding;
import com.grupok.watertrack.databinding.FragmentMainACReportBinding;
import com.grupok.watertrack.scripts.SnackBarShow;
import com.grupok.watertrack.scripts.apiCRUD.APIMethods;

import java.util.ArrayList;
import java.util.List;

public class MainACAddReportFrag extends Fragment implements APIMethods.PostReportResponse {

    private FragmentMainACAddReportBinding binding;
    private MainActivity parent;
    private UserInfosEntity currentUser;
    private List<MeterEntity> meterEntityList;
    private List<String> meterStringList = new ArrayList<>();
    public SnackBarShow snackBarShow = new SnackBarShow();

    public MainACAddReportFrag() {
        // Required empty public constructor
    }

    public MainACAddReportFrag(MainActivity parent, UserInfosEntity currentUser, List<MeterEntity> meterEntityList) {
        this.parent = parent;
        this.currentUser = currentUser;
        this.meterEntityList = meterEntityList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainACAddReportBinding.inflate(inflater, container, false);

        if(parent != null){
            init();
        }

        return binding.getRoot();
    }

    private void init(){
        for (MeterEntity meter : meterEntityList) {
            String aux = meter.id+" - "+meter.address;
            meterStringList.add(aux);
        }

        fillMeters();

        setupSaveButton();
    }

    private void setupSaveButton(){
        binding.butSaveAddReportFragMainAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedMeterID = getSelectedItemId(binding.comboBoxMeterAddReportFragMainAc, meterStringList);
                if (selectedMeterID > 0) {
                    binding.inputLayoutTextInputMeterAddReportFragMainAc.setError(null);
                }else{
                    binding.inputLayoutTextInputMeterAddReportFragMainAc.setError(getString(R.string.general_selectValidOption));
                    return;
                }

                if(binding.editTextDescriptionReportFragFragMainAc.getText() != null && binding.editTextDescriptionReportFragFragMainAc.getText().length() > 0){
                    binding.inputLayoutTextInputDescriptionAddReportFragMainAc.setError(null);
                }else{
                    binding.inputLayoutTextInputDescriptionAddReportFragMainAc.setError(getString(R.string.general_requiredField));
                    return;
                }

                binding.loadingViewAddReportFragMainAc.setVisibility(View.VISIBLE);
                APIMethods apiMethods = new APIMethods();
                apiMethods.setPostReportResponse(MainACAddReportFrag.this);
                apiMethods.postReport(getContext(), currentUser, selectedMeterID, binding.editTextDescriptionReportFragFragMainAc.getText().toString().trim());
            }
        });
    }

    private void fillMeters(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                meterStringList
        );

        binding.comboBoxMeterAddReportFragMainAc.setAdapter(adapter);

        binding.inputLayoutTextInputMeterAddReportFragMainAc.setEndIconOnClickListener(
                v -> binding.comboBoxMeterAddReportFragMainAc.showDropDown());
        binding.comboBoxMeterAddReportFragMainAc.setOnClickListener(
                v -> binding.comboBoxMeterAddReportFragMainAc.showDropDown());
    }
    public int getSelectedItemId(MaterialAutoCompleteTextView view, List<String> items) {
        String input = view.getText().toString().trim();

        // VER SE SELECIONOU UM ITEM VALIDO
        if (!items.contains(input)) {
            return 0; // INVALIDO
        }

        // EXTRACT O ID
        try {
            String idPart = input.split("-")[0].trim();
            return Integer.parseInt(idPart);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void onPostReportResponse(boolean response, String message) {
        binding.loadingViewAddReportFragMainAc.setVisibility(View.GONE);
        if(response){
            parent.cycleFragments("ReportFrag", null);
        }else{
            snackBarShow.display(binding.getRoot(), message, -1, 1, binding.snackbarViewReportFragMainAC, getContext());
        }
    }
}