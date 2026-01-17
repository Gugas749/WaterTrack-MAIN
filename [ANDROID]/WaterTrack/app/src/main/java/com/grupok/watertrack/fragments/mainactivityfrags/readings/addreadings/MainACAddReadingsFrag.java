package com.grupok.watertrack.fragments.mainactivityfrags.readings.addreadings;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.grupok.watertrack.R;
import com.grupok.watertrack.activitys.MainActivity;
import com.grupok.watertrack.database.entities.EnterpriseEntity;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.MeterReadingEntity;
import com.grupok.watertrack.database.entities.MeterTypeEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;
import com.grupok.watertrack.databinding.FragmentMainACAddReadingsBinding;
import com.grupok.watertrack.scripts.SnackBarShow;
import com.grupok.watertrack.scripts.apiCRUD.APIMethods;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainACAddReadingsFrag extends Fragment implements
        APIMethods.GetUsersResponse,
        APIMethods.GetTechniciansResponse, APIMethods.PostReadingResponse {

    private FragmentMainACAddReadingsBinding binding;
    private MainActivity parent;
    private UserInfosEntity currentUser;
    private List<MeterEntity> meterEntityList = new ArrayList<>();
    private List<UserInfosEntity> technicianList = new ArrayList<>();
    private List<String> technicianStringList = new ArrayList<>();
    private List<String> metersStringList = new ArrayList<>();
    private MainACAddReadingsFrag THIS;
    private SnackBarShow snackBarShow = new SnackBarShow();


    public MainACAddReadingsFrag() {
        // Required empty public constructor
    }

    public MainACAddReadingsFrag(MainActivity parent, UserInfosEntity currentUser, List<MeterEntity> meterEntityList) {
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
        binding = FragmentMainACAddReadingsBinding.inflate(inflater);

        if(parent != null && currentUser != null){
            init();
        }

        return binding.getRoot();
    }

    private void init(){
        THIS = this;
        disableBackPressed();

        getInfos();
        setupSaveButton();
        setupDatePicker();
    }
    private void getInfos(){
        binding.loadingViewAddReadingsFragMainAc.setVisibility(View.VISIBLE);
        APIMethods apiMethods = new APIMethods(parent);

        SharedPreferences prefs = parent.getSharedPreferences("Perf_User", MODE_PRIVATE);
        String role = prefs.getString("role", "");

        if(role.equals("admin")){
            apiMethods.setGetUsersResponse(THIS);
            apiMethods.getUsers(getContext());
        }else{
            loadInfos();
        }
    }
    private void loadInfos(){
        binding.loadingViewAddReadingsFragMainAc.setVisibility(View.GONE);

        fillTechnicianDropdown();
        fillMetersDropdown();
    }
    private void disableBackPressed(){
        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                }
                return false;
            }
        });
    }
    private void setupDatePicker() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        binding.datePickerInstallationDateAddReadingsFragMainAc.setText(date);

        // CRIAR A INSTANCIA DO DATEPICKER
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select installation date")
                .setTheme(R.style.CustomDatePickerTheme)
                .build();

        // MOSTRAR O FRAG/POPUP DO DATEPICKER
        View.OnClickListener openPickerListener = v -> {
            if (!datePicker.isAdded()) {
                datePicker.show(getParentFragmentManager(), "DATE_PICKER");
            }
        };

        binding.datePickerInstallationDateAddReadingsFragMainAc.setOnClickListener(openPickerListener);
        binding.inputLayoutDatePickerDateAddReadingsFragMainAc.setEndIconOnClickListener(openPickerListener);

        // CLICK OK
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(selection));

            binding.datePickerInstallationDateAddReadingsFragMainAc.setText(formattedDate);
        });
    }
    private boolean validateFields(){
        boolean aux = true;
        //------------- METER -------------------
        int selectedUserID = getSelectedItemId(binding.comboBoxMeterAddReadingsFragMainAc, metersStringList);
        if (selectedUserID > 0) {
            binding.textInputLayoutComboBoxMeterAddReadingsFragMainAc.setError(null);
        }else{
            binding.textInputLayoutComboBoxMeterAddReadingsFragMainAc.setError(getString(R.string.general_selectValidOption));
            aux = false;
        }

        //------------- TECHNICIAN -------------------
        int technicianID = currentUser.userId;
        SharedPreferences prefs = parent.getSharedPreferences("Perf_User", MODE_PRIVATE);
        String role = prefs.getString("role", "");

        if(role.equals("admin")){
            technicianID = getSelectedItemId(binding.comboBoxTechnicianAddReadingsFragMainAc, technicianStringList);
            if (technicianID > 0) {
                binding.textInputLayoutComboBoxTechnicianAddReadingsFragMainAc.setError(null);
            }else{
                binding.textInputLayoutComboBoxTechnicianAddReadingsFragMainAc.setError(getString(R.string.general_selectValidOption));
                return false;
            }
        }
        //------------- READING -------------------
        if(binding.editTextReadingAddReadingsFragMainAc.getText() != null && !binding.editTextReadingAddReadingsFragMainAc.getText().toString().isEmpty()){
            binding.inputLayoutTextInputReadingAddReadingsFragMainAc.setError(null);
        }else{
            binding.inputLayoutTextInputReadingAddReadingsFragMainAc.setError(getString(R.string.general_requiredField));
            aux = false;
        }
        //------------- ACCUMULATED CONSUMPTION -------------------
        if(binding.editTextAcConsumptionAddReadingsFragMainAc.getText() != null && !binding.editTextAcConsumptionAddReadingsFragMainAc.getText().toString().isEmpty()){
            binding.inputLayoutTextInputAcConsumptionAddReadingsFragMainAc.setError(null);
        }else{
            binding.inputLayoutTextInputAcConsumptionAddReadingsFragMainAc.setError(getString(R.string.general_requiredField));
            aux = false;
        }
        //------------- WATER PRESSURE -------------------
        if(binding.editTextWaterPressureAddReadingsFragMainAc.getText() != null && !binding.editTextWaterPressureAddReadingsFragMainAc.getText().toString().isEmpty()){
            binding.inputLayoutTextInputWaterPressureAddReadingsFragMainAc.setError(null);
        }else{
            binding.inputLayoutTextInputWaterPressureAddReadingsFragMainAc.setError(getString(R.string.general_requiredField));
            aux = false;
        }
        //------------- DATE -------------------
        if(binding.datePickerInstallationDateAddReadingsFragMainAc.getText() != null && !binding.datePickerInstallationDateAddReadingsFragMainAc.getText().toString().isEmpty()){
            binding.inputLayoutDatePickerDateAddReadingsFragMainAc.setError(null);
        }else{
            binding.inputLayoutDatePickerDateAddReadingsFragMainAc.setError(getString(R.string.general_requiredField));
            aux = false;
        }

        return aux;
    }
    private void setupSaveButton(){
        binding.butSaveAddReadingsFragAuthAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    int selectedMeterID = getSelectedItemId(binding.comboBoxMeterAddReadingsFragMainAc, metersStringList);
                    int technicianID = currentUser.userId;
                    SharedPreferences prefs = parent.getSharedPreferences("Perf_User", MODE_PRIVATE);
                    String role = prefs.getString("role", "");

                    if(role.equals("admin")){
                        technicianID = getSelectedItemId(binding.comboBoxTechnicianAddReadingsFragMainAc, technicianStringList);
                    }

                    MeterReadingEntity reading = new MeterReadingEntity(
                            technicianID, selectedMeterID,
                            binding.editTextReadingAddReadingsFragMainAc.getText().toString().trim(),
                            binding.editTextAcConsumptionAddReadingsFragMainAc.getText().toString().trim(),
                            binding.datePickerInstallationDateAddReadingsFragMainAc.getText().toString().trim(),
                            binding.editTextWaterPressureAddReadingsFragMainAc.getText().toString().trim()
                    );

                    binding.loadingViewAddReadingsFragMainAc.setVisibility(View.VISIBLE);
                    APIMethods apiMethods = new APIMethods(parent);
                    apiMethods.setPostReadingResponse(THIS);
                    apiMethods.postReading(getContext(), currentUser, reading);
                }
            }
        });
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

    // <editor-fold desc="DROPDOWNS">
    private void fillMetersDropdown() {
        for (MeterEntity object : meterEntityList) {
            String aux = object.id+" - "+object.address;
            metersStringList.add(aux);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                metersStringList
        );

        binding.comboBoxMeterAddReadingsFragMainAc.setAdapter(adapter);

        // CODIGO PARA QUANDO CLICAR O ICON NO FIM DO INPUT ABRIR A DROPDOWN DIRETO
        binding.textInputLayoutComboBoxMeterAddReadingsFragMainAc.setEndIconOnClickListener(v -> binding.comboBoxMeterAddReadingsFragMainAc.showDropDown());
        binding.comboBoxMeterAddReadingsFragMainAc.setOnClickListener(v -> binding.comboBoxMeterAddReadingsFragMainAc.showDropDown());
    }
    private void fillTechnicianDropdown(){
        SharedPreferences prefs = parent.getSharedPreferences("Perf_User", MODE_PRIVATE);
        String role = prefs.getString("role", "");

        for (UserInfosEntity object : technicianList) {
            String aux = object.userId+" - "+object.username;
            technicianStringList.add(aux);
        }

        if(role.equals("admin")){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    technicianStringList
            );

            binding.comboBoxTechnicianAddReadingsFragMainAc.setAdapter(adapter);

            // CODIGO PARA QUANDO CLICAR O ICON NO FIM DO INPUT ABRIR A DROPDOWN DIRETO
            binding.textInputLayoutComboBoxTechnicianAddReadingsFragMainAc.setEndIconOnClickListener(v -> binding.comboBoxTechnicianAddReadingsFragMainAc.showDropDown());
            binding.comboBoxTechnicianAddReadingsFragMainAc.setOnClickListener(v -> binding.comboBoxTechnicianAddReadingsFragMainAc.showDropDown());

        }else{
            binding.comboBoxTechnicianAddReadingsFragMainAc.setEnabled(false);
            binding.comboBoxTechnicianAddReadingsFragMainAc.setText(currentUser.username);
        }
    }
    // </editor-fold>

    @Override
    public void onGetUsersResponse(boolean response, String responseText, List<UserInfosEntity> users) {
        if(response){
            APIMethods apiMethods = new APIMethods(parent);
            apiMethods.setGetTechniciansResponse(THIS);
            apiMethods.getTechnicians(THIS.getContext(), users);
        }else{
            snackBarShow.display(binding.getRoot(), responseText, -1, 1, binding.snackbarViewAddReadingsFragMainAC, parent);
        }
    }

    @Override
    public void onGetTechniciansResponse(boolean response, String message, List<UserInfosEntity> list) {
        if(response){
            technicianList.addAll(list);
            loadInfos();
        }else{
            snackBarShow.display(binding.getRoot(), message, -1, 1, binding.snackbarViewAddReadingsFragMainAC, parent);
        }
    }

    @Override
    public void onPostReadingResponse(boolean response, String message) {
        binding.loadingViewAddReadingsFragMainAc.setVisibility(View.GONE);
        if(response){
            parent.cycleFragments("MainViewFrag", null);
        }else{
            snackBarShow.display(binding.getRoot(), message, -1, 1, binding.snackbarViewAddReadingsFragMainAC, parent);
        }
    }
}