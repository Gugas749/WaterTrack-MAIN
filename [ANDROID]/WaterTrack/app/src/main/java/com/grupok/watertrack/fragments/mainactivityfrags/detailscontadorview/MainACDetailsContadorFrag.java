package com.grupok.watertrack.fragments.mainactivityfrags.detailscontadorview;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.grupok.watertrack.activitys.MainActivity;
import com.grupok.watertrack.database.entities.EnterpriseEntity;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.MeterTypeEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;
import com.grupok.watertrack.databinding.FragmentMainACDetailsContadorBinding;
import com.grupok.watertrack.scripts.apiCRUD.APIMethods;

import java.util.List;


public class MainACDetailsContadorFrag extends Fragment implements APIMethods.GetEnterpriseByIdResponse, APIMethods.GetUserByIdResponse, APIMethods.GetMeterTypeByIdResponse {

    private MainActivity parent;
    private Context context;
    private FragmentMainACDetailsContadorBinding binding;
    private MeterEntity meterSelected;
    private boolean enterpriseResponse = false;
    private boolean userResponse = false;
    private boolean typeResponse = false;
    private EnterpriseEntity selectedEnterprise;
    private MeterTypeEntity selectedType;
    private UserInfosEntity selectedUser;

    public MainACDetailsContadorFrag() {
        // Required empty public constructor
    }


    public MainACDetailsContadorFrag(MainActivity parent) {
        this.parent = parent;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainACDetailsContadorBinding.inflate(inflater);

        if(parent.currentUserInfo != null && getArguments() != null){
            meterSelected = new Gson().fromJson(
                    getArguments().getString("meter", ""),
                    MeterEntity.class
            );

            init();
        }

        return binding.getRoot();
    }

    private void init(){
        context = getContext();
        disableBackPressed();

        actionLoadRV();

        setupReadingsButton();
        setupReportsButton();
    }
    //-----------------------SETUPS-------------------------------
    private void setupReadingsButton(){
        Bundle data = new Bundle();
        binding.butReadingsDetailsContadorFragMainAc.setOnClickListener(v -> {
            data.putInt("contadorId", meterSelected.id);
            parent.cycleFragments("ReadingsContadorFrag", data);
        });
    }
    private void setupReportsButton(){
        Bundle data = new Bundle();
        binding.butReportProblemDetailsContadorFragMainAc.setOnClickListener(v ->{
            data.putInt("contadorId", meterSelected.id);
            parent.cycleFragments("ReportFrag", data);
        });
    }
    //------------------------------------------------------
    private void disableBackPressed(){
        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    parent.cycleFragments("MainViewFrag", null);
                    return true;
                }
                return false;
            }
        });
    }
    //------------------------------LOAD RV RELATED---------------------------------
    private void actionLoadRV(){
        binding.loadingViewDetailsContadorFragMainAc.setVisibility(View.VISIBLE);
        APIMethods apiMethods = new APIMethods();
        apiMethods.getEnterpriseById(context, meterSelected.enterpriseID);
        apiMethods.setGetEnterpriseByIdResponse(this);

        apiMethods.getUserById(context, meterSelected.userID);
        apiMethods.setGetUserByIdResponse(this);

        apiMethods.getMeterTypeById(context, meterSelected.meterTypeID);
        apiMethods.setGetMeterTypeByIdResponse(this);
    }
    private void finalLoadRV(){
        binding.loadingViewDetailsContadorFragMainAc.setVisibility(View.GONE);
        RVAdapterFieldsDetailsContadores fieldsAdapter = new RVAdapterFieldsDetailsContadores(context, meterSelected, selectedType, selectedUser, selectedEnterprise);
        binding.rvFieldsInfoContadorDetailsContadorFragMainAc.setLayoutManager(new LinearLayoutManager(context));
        binding.rvFieldsInfoContadorDetailsContadorFragMainAc.setAdapter(fieldsAdapter);
    }
    @Override
    public void onGetEnterpriseByIdResponse(boolean response, String message, EnterpriseEntity enterprise) {
        if(response){
            enterpriseResponse = true;
            selectedEnterprise = enterprise;
            if(enterpriseResponse && typeResponse && userResponse){
                finalLoadRV();
            }
        }else{
            //TODO: display snackbar com o erro
        }
    }
    @Override
    public void onGetUserByIdResponse(boolean response, String message, UserInfosEntity user) {
        if(response){
            userResponse = true;
            selectedUser = user;
            if(enterpriseResponse && typeResponse && userResponse){
                finalLoadRV();
            }
        }else{
            //TODO: display snackbar com o erro
        }
    }
    @Override
    public void onGetMeterTypeByIdResponse(boolean response, String message, MeterTypeEntity type) {
        if(response){
            typeResponse = true;
            selectedType = type;
            if(enterpriseResponse && typeResponse && userResponse){
                finalLoadRV();
            }
        }else{
            //TODO: display snackbar com o erro
        }
    }
    //------------------------------------------------------
}