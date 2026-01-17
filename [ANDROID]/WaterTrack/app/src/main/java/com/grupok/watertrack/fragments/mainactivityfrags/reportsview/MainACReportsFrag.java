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

//    private void fillProblem(List<String> list) {
//        String[] problemas = getResources().getStringArray(R.array.problem_report);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                requireContext(),
//                android.R.layout.simple_dropdown_item_1line,
//                problemas
//        );
//
//        binding.comboBoxProblemReportFragMainAc.setAdapter(adapter);
//
//        binding.inputLayoutComboBoxProblemReportFragMainAc.setEndIconOnClickListener(
//                v -> binding.comboBoxProblemReportFragMainAc.showDropDown());
//        binding.comboBoxProblemReportFragMainAc.setOnClickListener(
//                v -> binding.comboBoxProblemReportFragMainAc.showDropDown());
//    }
//    private void setupComboProblem() {
//        binding.comboBoxProblemReportFragMainAc.setOnItemClickListener((adapterView, view, position, id) -> {
//            String selected = listString.get(position);
//            if (selected.equalsIgnoreCase("Other")) {
//                binding.inputLayoutTextInputOtherProblemReportFragMainAc.setVisibility(View.VISIBLE);
//            } else {
//                binding.inputLayoutTextInputOtherProblemReportFragMainAc.setVisibility(View.GONE);
//            }
//        });
//    }

//    private void setupComboMeter() {
//        if (contadoresEntityList == null || contadoresEntityList.isEmpty()) return;
//
//        // Cria adaptador personalizado
//        MeterAdapter adapter = new MeterAdapter(requireContext(), contadoresEntityList);
//        binding.comboBoxMeterReportFragMainAc.setAdapter(adapter);
//
//        // Mostrar lista ao clicar
//        binding.comboBoxMeterReportFragMainAc.setOnClickListener(v -> binding.comboBoxMeterReportFragMainAc.showDropDown());
//        binding.comboBoxMeterReportFragMainAc.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) binding.comboBoxMeterReportFragMainAc.showDropDown();
//        });
//
//        // Mostrar lista novamente quando texto for apagado
//        binding.comboBoxMeterReportFragMainAc.addTextChangedListener(new TextWatcher() {
//            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
//            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().isEmpty()) {
//                    binding.comboBoxMeterReportFragMainAc.showDropDown();
//                }
//            }
//            @Override public void afterTextChanged(Editable s) { }
//        });
//    }
//    private void setupButtonSave() {
//        binding.butSaveReportFragMainAC.setOnClickListener(v -> {
//            if (binding.inputLayoutTextInputOtherProblemReportFragMainAc.getVisibility() == View.VISIBLE) {
//                String novoProblema = binding.editTextOtherProblemReportFragMainAc.getText().toString().trim();
//                if (!novoProblema.isEmpty() && !listString.contains(novoProblema)) {
//                    listString.add(listString.size() - 1, novoProblema);
//                    ((ArrayAdapter<String>) binding.comboBoxProblemReportFragMainAc.getAdapter()).notifyDataSetChanged();
//                    binding.editTextOtherProblemReportFragMainAc.setText("");
//                    binding.inputLayoutTextInputOtherProblemReportFragMainAc.setVisibility(View.GONE);
//                }
//            }
//        });
//    }
//    private void setupUserType() {
//        SharedPreferences prefs = parent.getSharedPreferences("Perf_User", MODE_PRIVATE);
//        String role = prefs.getString("role", "");
//
//        if (role.equals("technician") || role.equals("admin")) { // técnico
//            if (contadorId != -1) {
//                // Se veio um contador no bundle → mostra-o
//                String texto = "" + contadorId;
//                if (!contadorMorada.isEmpty()) texto += " — " + contadorMorada;
//                binding.comboBoxMeterReportFragMainAc.setText(texto);
//            } else {
//                // Caso NÃO venha bundle → deixa o campo vazio, mas lista aparece igual
//                binding.comboBoxMeterReportFragMainAc.setText("");
//                binding.comboBoxMeterReportFragMainAc.setHint("Selecione o contador");
//                // Abre lista ao focar
//                binding.comboBoxMeterReportFragMainAc.setOnFocusChangeListener((v, hasFocus) -> {
//                    if (hasFocus) binding.comboBoxMeterReportFragMainAc.showDropDown();
//                });
//            }
//            Toast.makeText(requireContext(), "Técnico", Toast.LENGTH_SHORT).show();
//
//        } else if (role.equals("resident")) { // morador
//            if (contadorId != -1) {
//                binding.comboBoxMeterReportFragMainAc.setText(contadorId);
//            }
//            Toast.makeText(requireContext(), "Morador", Toast.LENGTH_SHORT).show();
//        }
//    }

    // <editor-fold desc="API RESPONSES">
    @Override
    public void onGetReportsResponse(boolean response, String responseText, List<ReportsEntity> reportsEntities) {
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

    private static class MeterAdapter extends ArrayAdapter<String> implements Filterable {
        private final Context context;
        private final List<MeterEntity> originalList;
        private List<String> filteredList;

        public MeterAdapter(Context context, List<MeterEntity> contadores) {
            super(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
            this.context = context;
            this.originalList = contadores;
            this.filteredList = buildStringList(contadores);
            addAll(filteredList);
        }

        private static List<String> buildStringList(List<MeterEntity> list) {
            List<String> result = new ArrayList<>();
            for (MeterEntity c : list) {
                result.add(c.address);
            }
            return result;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    List<String> filtered = new ArrayList<>();
                    if (constraint == null || constraint.length() == 0) {
                        filtered = buildStringList(originalList);
                    } else {
                        String query = constraint.toString().toLowerCase(Locale.ROOT);
                        for (MeterEntity c : originalList) {
                            if (String.valueOf(c.id).contains(query) ||
                                    c.address.toLowerCase(Locale.ROOT).contains(query)) {
                                filtered.add(c.id + " — " + " (" + c.address + ")");
                            }
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.values = filtered;
                    results.count = filtered.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    clear();
                    if (results != null && results.values != null) {
                        addAll((List<String>) results.values);
                    }
                    notifyDataSetChanged();
                }
            };
        }
    }
}
