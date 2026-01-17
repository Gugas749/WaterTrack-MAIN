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
import com.grupok.watertrack.R;
import com.grupok.watertrack.activitys.MainActivity;
import com.grupok.watertrack.database.entities.EnterpriseEntity;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.MeterTypeEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;
import com.grupok.watertrack.databinding.FragmentMainACAddReadingsBinding;
import com.grupok.watertrack.scripts.apiCRUD.APIMethods;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainACAddReadingsFrag extends Fragment {

    private FragmentMainACAddReadingsBinding binding;
    private MainActivity parent;
    private UserInfosEntity currentUser;
    private List<MeterEntity> meterEntityList;
    private MainACAddReadingsFrag THIS;


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

        //getInfos();
        //setupSaveButton();
        setupDatePicker();
    }
    private void getInfos(){
        binding.loadingViewAddReadingsFragMainAc.setVisibility(View.VISIBLE);
        APIMethods apiMethods = new APIMethods();
        apiMethods.setGetUsersResponse(THIS);
        apiMethods.setGetEnterpriseResponse(THIS);

        apiMethods.getUsers(getContext());
        apiMethods.getMeterTypes(getContext());
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
    private void filterTechinicians(){

    }

    // <editor-fold desc="DROPDOWNS">
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
    // </editor-fold>
}