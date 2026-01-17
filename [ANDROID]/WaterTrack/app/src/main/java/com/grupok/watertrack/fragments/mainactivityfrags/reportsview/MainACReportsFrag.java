package com.grupok.watertrack.fragments.mainactivityfrags.reportsview;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.google.gson.Gson;
import com.grupok.watertrack.R;
import com.grupok.watertrack.activitys.MainActivity;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.ReportsEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;
import com.grupok.watertrack.databinding.FragmentMainACReportBinding;
import com.grupok.watertrack.scripts.SnackBarShow;
import com.grupok.watertrack.scripts.apiCRUD.APIMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainACReportsFrag extends Fragment implements APIMethods.GetReportsResponse,
        RVAdapterMainAcReportsView.ReportsItemClick,
        APIMethods.GetReportsByUserIDResponse,
        APIMethods.GetReportsByMeterIDResponse {

    private MainActivity parent;
    private UserInfosEntity currentUser;
    private List<ReportsEntity> reportsEntityList = new ArrayList<>();
    public SnackBarShow snackBarShow = new SnackBarShow();
    private FragmentMainACReportBinding binding;
    private List<MeterEntity> meterEntityList = new ArrayList<>();
    private List<String> listString = new ArrayList<>();

    public MainACReportsFrag() {
        // Required empty public constructor
    }

    public MainACReportsFrag(MainActivity parent, List<MeterEntity> contadoresEntityList, UserInfosEntity currentUser) {
        this.parent = parent;
        this.meterEntityList = contadoresEntityList;
        this.currentUser = currentUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainACReportBinding.inflate(inflater, container, false);

        if (parent != null && parent.currentUserInfo != null) {
            init();
        }

        return binding.getRoot();
    }

    private void init() {
        setupAddReportsButton();

        binding.loadingViewReportFragMainAc.setVisibility(View.VISIBLE);
        getInfos();
    }

    // <editor-fold desc="SETUPS">
    private void setupAddReportsButton(){
        binding.butCreateReportReportsFragMainAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.cycleFragments("AddReportFrag", null);
            }
        });
    }
    // </editor-fold>

    // <editor-fold desc="FUNCTIONS">
    private void getInfos(){
        boolean aux = false;
        APIMethods apiMethods = new APIMethods();

        if(getArguments() != null){
            aux = getArguments().getBoolean("fromMeterView", false);
        }

        if(aux){
            int meterID = getArguments().getInt("meterID", -1);
            apiMethods.setGetReportsByMeterIDResponse(MainACReportsFrag.this);
            apiMethods.getReportsByMeterID(getContext(), currentUser, meterID);
        }else{
            SharedPreferences prefs = parent.getSharedPreferences("Perf_User", MODE_PRIVATE);
            String role = prefs.getString("role", "");

            apiMethods.setGetReportsResponse(MainACReportsFrag.this);
            apiMethods.setGetReportsByUserIDResponse(MainACReportsFrag.this);

            switch (role){
                case "resident":
                    apiMethods.getReportsByUserID(getContext(), currentUser);
                    break;
                case "technician":
                case "admin":
                    apiMethods.getReports(getContext());
                    break;
            }
        }
    }
    private void filterReportsByEnterprise(){
        List<ReportsEntity> filteredList = new ArrayList<>();
        for (MeterEntity meter : meterEntityList) {
            for (ReportsEntity report : reportsEntityList) {
                if(report.meterID == meter.id){
                    filteredList.add(report);
                }
            }
        }

        reportsEntityList.clear();
        reportsEntityList.addAll(filteredList);

        loadReports();
    }
    private void loadReports(){
        RVAdapterMainAcReportsView adapter = new RVAdapterMainAcReportsView(getContext(), reportsEntityList, parent, meterEntityList);
        adapter.updateData(new ArrayList<>());
        adapter.setItemClickListenner(this);
        adapter.notifyDataSetChanged();
        if(!reportsEntityList.isEmpty()){
            adapter.updateData(reportsEntityList);
            binding.rvReportsReportsFragMainAC.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvReportsReportsFragMainAC.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else{
            binding.textViewNoItemsToDisplayReportsFragMainAC.setVisibility(View.VISIBLE);
            binding.rvReportsReportsFragMainAC.setVisibility(View.GONE);
        }

    }
    // </editor-fold>

    // <editor-fold desc="API RESPONSES">
    @Override
    public void onGetReportsResponse(boolean response, String responseText, List<ReportsEntity> reportsEntities) {
        binding.loadingViewReportFragMainAc.setVisibility(View.GONE);
        if(response){
            if(!reportsEntities.isEmpty()){
                reportsEntityList.addAll(reportsEntities);
            }
            SharedPreferences prefs = parent.getSharedPreferences("Perf_User", MODE_PRIVATE);
            String role = prefs.getString("role", "");
            if(role.equals("technician")){
                filterReportsByEnterprise();
            }else{
                loadReports();
            }
        }else{
            snackBarShow.display(binding.getRoot(), responseText, -1, 1, binding.snackbarViewReportsFragMainAC, parent);
        }
    }
    @Override
    public void onGetReportsByUserIDResponse(boolean response, String responseText, List<ReportsEntity> reportsEntities) {
        binding.loadingViewReportFragMainAc.setVisibility(View.GONE);
        if(response){
            if(!reportsEntities.isEmpty()){
                reportsEntityList.addAll(reportsEntities);
            }
            loadReports();
        }else{
            snackBarShow.display(binding.getRoot(), responseText, -1, 1, binding.snackbarViewReportsFragMainAC, parent);
        }
    }
    @Override
    public void onGetReportsByMeterIDResponse(boolean response, String responseText, List<ReportsEntity> reportsEntities) {
        binding.loadingViewReportFragMainAc.setVisibility(View.GONE);
        if(response){
            if(!reportsEntities.isEmpty()){
                reportsEntityList.addAll(reportsEntities);
            }
            loadReports();
        }else{
            snackBarShow.display(binding.getRoot(), responseText, -1, 1, binding.snackbarViewReportsFragMainAC, parent);
        }
    }
    // </editor-fold>

    @Override
    public void onReportsItemClick(ReportsEntity report) {
        Bundle data = new Bundle();
        data.putString("report", new Gson().toJson(report));
        parent.cycleFragments("DetailsReportFrag", data);
    }
}
