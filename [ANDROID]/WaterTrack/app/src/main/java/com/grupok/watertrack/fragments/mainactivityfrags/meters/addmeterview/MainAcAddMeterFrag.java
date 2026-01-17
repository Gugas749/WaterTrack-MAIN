package com.grupok.watertrack.fragments.mainactivityfrags.meters.addmeterview;

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
import com.grupok.watertrack.database.entities.MeterTypeEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;
import com.grupok.watertrack.databinding.FragmentMainAcAddContadorBinding;
import com.grupok.watertrack.scripts.SnackBarShow;
import com.grupok.watertrack.scripts.apiCRUD.APIMethods;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainAcAddMeterFrag extends Fragment implements APIMethods.GetUsersResponse, APIMethods.GetEnterpriseResponse, APIMethods.GetMeterTypesResponse, APIMethods.PostMeterResponse {

    private FragmentMainAcAddContadorBinding binding;
    private MainActivity parent;
    private MainAcAddMeterFrag THIS;
    private UserInfosEntity currentUser;
    private List<UserInfosEntity> userList = new ArrayList<>();
    private List<MeterTypeEntity> meterTypeList = new ArrayList<>();
    private List<EnterpriseEntity> enterpriseList = new ArrayList<>();
    private boolean userListResponse = false, meterTypeListResponse = false, enterpriseListResponse = false;
    private SnackBarShow snackBarShow = new SnackBarShow();
    private List<String> enterpriseStringList = new ArrayList<>(), meterTypeStringList = new ArrayList<>(), userStringList = new ArrayList<>();


    public MainAcAddMeterFrag() {
        // Required empty public constructor
    }
    public MainAcAddMeterFrag(MainActivity parent, UserInfosEntity currentUser) {
        this.parent = parent;
        this.currentUser = currentUser;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainAcAddContadorBinding.inflate(inflater);

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
        binding.loadingViewAddMeterFragMainAc.setVisibility(View.VISIBLE);
        APIMethods apiMethods = new APIMethods(parent);
        apiMethods.setGetUsersResponse(THIS);
        apiMethods.setGetMeterTypesResponse(THIS);
        apiMethods.setGetEnterpriseResponse(THIS);

        apiMethods.getUsers(getContext());
        apiMethods.getMeterTypes(getContext());
        apiMethods.getEnterprises(getContext());
    }
    private void loadInfos(){
        if(userListResponse && enterpriseListResponse && meterTypeListResponse){
            binding.loadingViewAddMeterFragMainAc.setVisibility(View.GONE);
            for (UserInfosEntity object : userList) {
                String aux = object.userId+" - "+object.username;
                userStringList.add(aux);
            }
            for (MeterTypeEntity object : meterTypeList) {
                String aux = object.id+" - "+object.description;
                meterTypeStringList.add(aux);
            }
            for (EnterpriseEntity object : enterpriseList) {
                String aux = object.id+" - "+object.name;
                enterpriseStringList.add(aux);
            }

            fillEnterpriseDropdown();
            fillUserDropdown();
            fillMeterTypeDropdown();
            fillClassDropdown();
            fillUnitysDropdown();
            fillStatusDropdown();
        }
    }
    //---------------------------SETUPS---------------------------
    private void setupDatePicker() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        binding.datePickerInstallationDateAddContadorFragMainAc.setText(date);

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

        binding.datePickerInstallationDateAddContadorFragMainAc.setOnClickListener(openPickerListener);
        binding.inputLayoutDatePickerInstallationDateAddContadorFragMainAc.setEndIconOnClickListener(openPickerListener);

        // CLICK OK
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(selection));

            binding.datePickerInstallationDateAddContadorFragMainAc.setText(formattedDate);
        });
    }
    private void setupSaveButton(){
        binding.butSaveAddMeterFragAuthAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    int selectedUserID = getSelectedItemId(binding.comboBoxResidentsAddContadorFragMainAc, userStringList);
                    int selectedMeterTypeID = getSelectedItemId(binding.comboBoxIdTypeAddContadorFragMainAc, meterTypeStringList);
                    int selectedEnterpriseID = currentUser.enterpriseID;
                    SharedPreferences prefs = parent.getSharedPreferences("Perf_User", MODE_PRIVATE);
                    String role = prefs.getString("role", "");

                    if(role.equals("admin")){
                        selectedEnterpriseID = getSelectedItemId(binding.comboBoxIdEnterpriseAddContadorFragMainAc, enterpriseStringList);
                    }

                    List<String> list = Arrays.asList(getResources().getStringArray(R.array.meterStatus));
                    int state = getSelectedItemId(binding.comboBoxStatusAddContadorFragMainAc, list);
                    List<String> list2 = Arrays.asList(getResources().getStringArray(R.array.meterUnitys));
                    String unity = String.valueOf(getSelectedItemId(binding.comboBoxMesureUnityAddContadorFragMainAc, list2));

                    MeterEntity meter = new MeterEntity(
                            binding.editTextAddressAddContadorFragMainAc.getText().toString().trim(),
                            selectedUserID,
                            selectedMeterTypeID,
                            selectedEnterpriseID,
                            binding.comboBoxClassAddContadorFragMainAc.getText().toString().trim(),
                            binding.datePickerInstallationDateAddContadorFragMainAc.getText().toString().trim(),
                            null,
                            binding.editTextMaxCapAddContadorFragMainAc.getText().toString().trim(),
                            unity,
                            binding.editTextSupportedTempAddContadorFragMainAc.getText().toString().trim(),
                            state
                    );

                    binding.loadingViewAddMeterFragMainAc.setVisibility(View.VISIBLE);
                    APIMethods apiMethods = new APIMethods(parent);
                    apiMethods.setPostMeterResponse(THIS);
                    apiMethods.postMeter(getContext(), currentUser, meter);
                }
            }
        });
    }
    //---------------------------DROPDOWN RELATED---------------------------
    private void fillEnterpriseDropdown(){
        SharedPreferences prefs = parent.getSharedPreferences("Perf_User", MODE_PRIVATE);
        String role = prefs.getString("role", "");

        if(role.equals("admin")){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    enterpriseStringList
            );

            binding.comboBoxIdEnterpriseAddContadorFragMainAc.setAdapter(adapter);

            // CODIGO PARA QUANDO CLICAR O ICON NO FIM DO INPUT ABRIR A DROPDOWN DIRETO
            binding.textInputLayoutComboBoxIdEnterpriseAddContadorFragMainAc.setEndIconOnClickListener(v -> binding.comboBoxIdEnterpriseAddContadorFragMainAc.showDropDown());
            binding.comboBoxIdEnterpriseAddContadorFragMainAc.setOnClickListener(v -> binding.comboBoxIdEnterpriseAddContadorFragMainAc.showDropDown());

        }else{
            binding.comboBoxIdEnterpriseAddContadorFragMainAc.setEnabled(false);
            for (EnterpriseEntity enterprise : enterpriseList) {
                if(enterprise.id == currentUser.enterpriseID){
                    binding.comboBoxIdEnterpriseAddContadorFragMainAc.setText(enterprise.name);
                }
            }
        }
    }
    private void fillUserDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                userStringList
        );

        binding.comboBoxResidentsAddContadorFragMainAc.setAdapter(adapter);

        // CODIGO PARA QUANDO CLICAR O ICON NO FIM DO INPUT ABRIR A DROPDOWN DIRETO
        binding.textInputLayoutComboBoxResidentsAddContadorFragMainAc.setEndIconOnClickListener(v -> binding.comboBoxResidentsAddContadorFragMainAc.showDropDown());
        binding.comboBoxResidentsAddContadorFragMainAc.setOnClickListener(v -> binding.comboBoxResidentsAddContadorFragMainAc.showDropDown());

    }
    private void fillMeterTypeDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                meterTypeStringList
        );

        binding.comboBoxIdTypeAddContadorFragMainAc.setAdapter(adapter);

        // CODIGO PARA QUANDO CLICAR O ICON NO FIM DO INPUT ABRIR A DROPDOWN DIRETO
        binding.textInputLayoutComboBoxIdTypeAddContadorFragMainAc.setEndIconOnClickListener(v -> binding.comboBoxIdTypeAddContadorFragMainAc.showDropDown());
        binding.comboBoxIdTypeAddContadorFragMainAc.setOnClickListener(v -> binding.comboBoxIdTypeAddContadorFragMainAc.showDropDown());

    }
    private void fillClassDropdown() {
        String[] classes = getResources().getStringArray(R.array.meterClass);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                classes
        );

        binding.comboBoxClassAddContadorFragMainAc.setAdapter(adapter);

        // CODIGO PARA QUANDO CLICAR O ICON NO FIM DO INPUT ABRIR A DROPDOWN DIRETO
        binding.textInputLayoutComboBoxClassAddContadorFragMainAc.setEndIconOnClickListener(v -> binding.comboBoxClassAddContadorFragMainAc.showDropDown());
        binding.comboBoxClassAddContadorFragMainAc.setOnClickListener(v -> binding.comboBoxClassAddContadorFragMainAc.showDropDown());

    }
    private void fillUnitysDropdown() {
        String[] unitys = getResources().getStringArray(R.array.meterUnitys);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                unitys
        );

        binding.comboBoxMesureUnityAddContadorFragMainAc.setAdapter(adapter);

        // CODIGO PARA QUANDO CLICAR O ICON NO FIM DO INPUT ABRIR A DROPDOWN DIRETO
        binding.inputLayoutComboBoxMeasureUnityAddContadorFragMainAc.setEndIconOnClickListener(v -> binding.comboBoxMesureUnityAddContadorFragMainAc.showDropDown());
        binding.comboBoxMesureUnityAddContadorFragMainAc.setOnClickListener(v -> binding.comboBoxMesureUnityAddContadorFragMainAc.showDropDown());

    }
    private void fillStatusDropdown() {
        String[] list = getResources().getStringArray(R.array.meterStatus);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                list
        );

        binding.comboBoxStatusAddContadorFragMainAc.setAdapter(adapter);

        // CODIGO PARA QUANDO CLICAR O ICON NO FIM DO INPUT ABRIR A DROPDOWN DIRETO
        binding.inputLayoutComboBoxStatusAddContadorFragMainAc.setEndIconOnClickListener(v -> binding.comboBoxStatusAddContadorFragMainAc.showDropDown());
        binding.comboBoxStatusAddContadorFragMainAc.setOnClickListener(v -> binding.comboBoxStatusAddContadorFragMainAc.showDropDown());

    }
    //---------------------------FUNCTIONS---------------------------
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
    private boolean validateFields(){
        boolean aux = true;
        //------------- ADDRESS -------------------
        if(binding.editTextAddressAddContadorFragMainAc.getText() != null && !binding.editTextAddressAddContadorFragMainAc.getText().toString().isEmpty()){
            binding.outlinedTextFieldAddressAddContadorFragMainAc.setError(null);
        }else{
            binding.outlinedTextFieldAddressAddContadorFragMainAc.setError(getString(R.string.general_requiredField));
            aux = false;
        }
        //------------- RESIDENT -------------------
        int selectedUserID = getSelectedItemId(binding.comboBoxResidentsAddContadorFragMainAc, userStringList);
        if (selectedUserID > 0) {
            binding.textInputLayoutComboBoxResidentsAddContadorFragMainAc.setError(null);
        }else{
            binding.textInputLayoutComboBoxResidentsAddContadorFragMainAc.setError(getString(R.string.general_selectValidOption));
            aux = false;
        }
        //------------- TYPE -------------------
        int selectedMeterTypeID = getSelectedItemId(binding.comboBoxIdTypeAddContadorFragMainAc, meterTypeStringList);
        if (selectedMeterTypeID > 0) {
            binding.textInputLayoutComboBoxIdTypeAddContadorFragMainAc.setError(null);
        }else{
            binding.textInputLayoutComboBoxIdTypeAddContadorFragMainAc.setError(getString(R.string.general_selectValidOption));
            aux = false;
        }
        //------------- ENTERPRISE -------------------
        int selectedEnterpriseID = currentUser.enterpriseID;
        SharedPreferences prefs = parent.getSharedPreferences("Perf_User", MODE_PRIVATE);
        String role = prefs.getString("role", "");

        if(role.equals("admin")){
            selectedEnterpriseID = getSelectedItemId(binding.comboBoxIdEnterpriseAddContadorFragMainAc, enterpriseStringList);
            if (selectedEnterpriseID > 0) {
                binding.textInputLayoutComboBoxIdEnterpriseAddContadorFragMainAc.setError(null);
            }else{
                binding.textInputLayoutComboBoxIdEnterpriseAddContadorFragMainAc.setError(getString(R.string.general_selectValidOption));
                return false;
            }
        }
        //------------- CLASS -------------------
        if(binding.comboBoxClassAddContadorFragMainAc.getText() != null && !binding.comboBoxClassAddContadorFragMainAc.getText().toString().isEmpty()){
            binding.textInputLayoutComboBoxClassAddContadorFragMainAc.setError(null);
        }else{
            binding.textInputLayoutComboBoxClassAddContadorFragMainAc.setError(getString(R.string.general_selectValidOption));
            aux = false;
        }
        //------------- INSTALATION DATE -------------------
        if(binding.editTextAddressAddContadorFragMainAc.getText() != null && !binding.editTextAddressAddContadorFragMainAc.getText().toString().isEmpty()){
            binding.outlinedTextFieldAddressAddContadorFragMainAc.setError(null);
        }else{
            binding.outlinedTextFieldAddressAddContadorFragMainAc.setError(getString(R.string.general_requiredField));
            aux = false;
        }
        //------------- MAX CAP -------------------
        if(binding.editTextMaxCapAddContadorFragMainAc.getText() != null && !binding.editTextMaxCapAddContadorFragMainAc.getText().toString().isEmpty()){
            binding.inputLayoutTextInputMaxCapAddContadorFragMainAc.setError(null);
        }else{
            binding.inputLayoutTextInputMaxCapAddContadorFragMainAc.setError(getString(R.string.general_requiredField));
            aux = false;
        }
        //------------- MEASURE UNITY -------------------
        if(binding.comboBoxMesureUnityAddContadorFragMainAc.getText() != null && !binding.comboBoxMesureUnityAddContadorFragMainAc.getText().toString().isEmpty()){
            binding.inputLayoutComboBoxMeasureUnityAddContadorFragMainAc.setError(null);
        }else{
            binding.inputLayoutComboBoxMeasureUnityAddContadorFragMainAc.setError(getString(R.string.general_selectValidOption));
            aux = false;
        }
        //------------- SUPPORTED TEMP -------------------
        if(binding.editTextSupportedTempAddContadorFragMainAc.getText() != null && !binding.editTextSupportedTempAddContadorFragMainAc.getText().toString().isEmpty()){
            binding.inputLayoutTextInputSupportedTempAddContadorFragMainAc.setError(null);
        }else{
            binding.inputLayoutTextInputSupportedTempAddContadorFragMainAc.setError(getString(R.string.general_requiredField));
            aux = false;
        }
        //------------- STATUS -------------------
        if(binding.comboBoxStatusAddContadorFragMainAc.getText() != null && !binding.comboBoxStatusAddContadorFragMainAc.getText().toString().isEmpty()){
            binding.inputLayoutComboBoxStatusAddContadorFragMainAc.setError(null);
        }else{
            binding.inputLayoutComboBoxStatusAddContadorFragMainAc.setError(getString(R.string.general_selectValidOption));
            aux = false;
        }

        return aux;
    }

    // <editor-fold desc="API RESPONSES">
    @Override
    public void onGetUsersResponse(boolean response, String message, List<UserInfosEntity> users) {
        if(response){
            userListResponse = true;
            userList.addAll(users);
            loadInfos();
        }else{
            snackBarShow.display(binding.getRoot(), message, -1, 1, binding.snackbarViewAddMeterFragMainAC, parent);
        }
    }
    @Override
    public void onGetEnterpriseResponse(boolean response, String message, List<EnterpriseEntity> enterprise) {
        if(response){
            enterpriseListResponse = true;
            enterpriseList.addAll(enterprise);
            loadInfos();
        }else{
            snackBarShow.display(binding.getRoot(), message, -1, 1, binding.snackbarViewAddMeterFragMainAC, parent);
        }
    }
    @Override
    public void onGetMeterTypesResponse(boolean response, String message, List<MeterTypeEntity> list) {
        if(response){
            meterTypeListResponse = true;
            meterTypeList.addAll(list);
            loadInfos();
        }else{
            snackBarShow.display(binding.getRoot(), message, -1, 1, binding.snackbarViewAddMeterFragMainAC, parent);
        }
    }
    @Override
    public void onPostMeterResponse(boolean response, String message) {
        binding.loadingViewAddMeterFragMainAc.setVisibility(View.GONE);
        if(response){
            parent.cycleFragments("MainViewFrag", null);
        }else{
            snackBarShow.display(binding.getRoot(), message, -1, 1, binding.snackbarViewAddMeterFragMainAC, parent);
        }
    }
    // </editor-fold>
}