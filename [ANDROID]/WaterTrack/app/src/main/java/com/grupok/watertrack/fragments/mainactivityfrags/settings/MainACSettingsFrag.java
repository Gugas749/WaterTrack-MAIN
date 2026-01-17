package com.grupok.watertrack.fragments.mainactivityfrags.settings;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.grupok.watertrack.R;
import com.grupok.watertrack.activitys.MainActivity;
import com.grupok.watertrack.database.LocalDataBase;
import com.grupok.watertrack.database.daos.UserInfosDao;
import com.grupok.watertrack.database.entities.UserInfosEntity;
import com.grupok.watertrack.databinding.FragmentMainACSettingsBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainACSettingsFrag extends Fragment {

    private FragmentMainACSettingsBinding binding;
    private MainActivity parent;
    private LocalDataBase localDataBase;
    private UserInfosDao userInfosDao;
    private UserInfosEntity currentUser;

    public MainACSettingsFrag() {}

    public MainACSettingsFrag(MainActivity parent) {
        this.parent = parent;
    }

    public MainACSettingsFrag(MainActivity mainActivity, UserInfosEntity currentUserInfo) {
        this.parent = mainActivity;
        this.currentUser = currentUserInfo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainACSettingsBinding.inflate(inflater);

        if (parent.currentUserInfo != null) {
            currentUser = parent.currentUserInfo;
            init();
        }

        return binding.getRoot();
    }

    private void init() {
        setupLocalDataBase();
        setupDatePicker();
        fillUserData();
        setupLanguageDropdown();
        setupThemeDropdown();
        //setupSaveButton();
    }

    private void setupLocalDataBase() {
        localDataBase = Room.databaseBuilder(getContext(), LocalDataBase.class, "WaterTrackLocalDB").build();
        userInfosDao = localDataBase.userInfosDao();
    }

    private void fillUserData() {
        binding.editTextUsernameSettings.setText(currentUser.username);
        binding.editTextEmailSettings.setText(currentUser.email);
        binding.editTextBirthDateSettings.setText(currentUser.birthDate);
        binding.editTextAddressSettings.setText(currentUser.address);
    }

    private void setupDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecionar data de nascimento")
                .setTheme(R.style.CustomDatePickerTheme)
                .build();

        View.OnClickListener listener = v -> {
            if (!datePicker.isAdded()) {
                datePicker.show(getParentFragmentManager(), "BIRTH_DATE_PICKER");
            }
        };

        binding.editTextBirthDateSettings.setOnClickListener(listener);
        binding.inputLayoutBirthDateSettings.setEndIconOnClickListener(listener);

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            binding.editTextBirthDateSettings.setText(sdf.format(new Date(selection)));
        });
    }

    private void setupLanguageDropdown() {
        String[] languages = {"Português", "Inglês"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, languages);
        binding.comboBoxLanguageSettings.setAdapter(adapter);
        binding.comboBoxLanguageSettings.setText(currentUser.Language, false);
    }

    private void setupThemeDropdown() {
        String[] themes = {"Claro", "Escuro", "Sistema"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, themes);
        binding.comboBoxThemeSettings.setAdapter(adapter);
        binding.comboBoxThemeSettings.setText(currentUser.Theme, false);
    }
}
