package com.grupok.watertrack.fragments.mainactivityfrags.settings;

import android.content.Context;
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
import com.grupok.watertrack.fragments.alertDialogFragments.AlertDialogQuestionFragment;
import com.grupok.watertrack.scripts.CustomAlertDialogFragment;
import com.grupok.watertrack.scripts.SnackBarShow;
import com.grupok.watertrack.scripts.apiCRUD.APIMethods;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainACSettingsFrag extends Fragment implements
        CustomAlertDialogFragment.ConfirmButtonClickAlertDialogQuestionFrag,
        CustomAlertDialogFragment.CancelButtonClickAlertDialogQuestionFrag,
        APIMethods.PatchUserProfileResponse,
        APIMethods.PatchUserResponse {

    private FragmentMainACSettingsBinding binding;
    private MainActivity parent;
    private MainACSettingsFrag THIS;
    private LocalDataBase localDataBase;
    private UserInfosDao userInfosDao;
    private UserInfosEntity currentUser;
    public SnackBarShow snackBarShow = new SnackBarShow();

    public MainACSettingsFrag() {}

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
        THIS = this;
        setupLocalDataBase();
        setupDatePicker();
        fillUserData();
        setupSaveButton();
    }

    private void setupLocalDataBase() {
        localDataBase = Room.databaseBuilder(getContext(), LocalDataBase.class, "WaterTrackLocalDB").build();
        userInfosDao = localDataBase.userInfosDao();
    }

    private void setupSaveButton(){
        binding.butSaveSettingsFragMainAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.editTextUsernameSettingsFragMainAc.getText() != null && !binding.editTextUsernameSettingsFragMainAc.getText().toString().isEmpty()){
                    binding.inputLayoutTextInputUsernameSettingsFragMainAc.setError(null);
                }else{
                    binding.inputLayoutTextInputUsernameSettingsFragMainAc.setError(getString(R.string.general_requiredField));
                    return;
                }

                if(binding.editTextEmailSettingsFragMainAc.getText() != null && !binding.editTextEmailSettingsFragMainAc.getText().toString().isEmpty()){
                    binding.inputLayoutTextInputEmailSettingsFragMainAc.setError(null);
                }else{
                    binding.inputLayoutTextInputEmailSettingsFragMainAc.setError(getString(R.string.general_requiredField));
                    return;
                }

                if(binding.editTextBDateSettingsFragMainAc.getText() != null && !binding.editTextBDateSettingsFragMainAc.getText().toString().isEmpty()){
                    binding.inputLayoutBDateSettingsFragMainAc.setError(null);
                }else{
                    binding.inputLayoutBDateSettingsFragMainAc.setError(getString(R.string.general_requiredField));
                    return;
                }

                if(binding.editTextAddressSettingsFragMainAc.getText() != null && !binding.editTextAddressSettingsFragMainAc.getText().toString().isEmpty()){
                    binding.inputLayoutTextInputAddressSettingsFragMainAc.setError(null);
                }else{
                    binding.inputLayoutTextInputAddressSettingsFragMainAc.setError(getString(R.string.general_requiredField));
                    return;
                }

                CustomAlertDialogFragment customAlertDialogFragment = new CustomAlertDialogFragment();
                AlertDialogQuestionFragment fragment= new AlertDialogQuestionFragment(getString(R.string.mainActivity_SettingsFrag_AlertDialog_SaveButton_Title), getString(R.string.mainActivity_SettingsFrag_AlertDialog_SaveButton_Desc), customAlertDialogFragment, customAlertDialogFragment, "2");

                customAlertDialogFragment.setConfirmListenner(MainACSettingsFrag.this);
                customAlertDialogFragment.setCancelListenner(MainACSettingsFrag.this);
                customAlertDialogFragment.setCustomFragment(fragment);
                customAlertDialogFragment.setTag("MainACSettingsView_SavePressed");
                customAlertDialogFragment.show(getParentFragmentManager(), "CustomAlertDialogFragment");
            }
        });
    }

    private void fillUserData() {
        binding.editTextUsernameSettingsFragMainAc.setText(currentUser.username);
        binding.editTextEmailSettingsFragMainAc.setText(currentUser.email);
        binding.editTextBDateSettingsFragMainAc.setText(currentUser.birthDate);
        binding.editTextAddressSettingsFragMainAc.setText(currentUser.address);
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

        binding.editTextBDateSettingsFragMainAc.setOnClickListener(listener);
        binding.inputLayoutBDateSettingsFragMainAc.setEndIconOnClickListener(listener);

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            binding.editTextBDateSettingsFragMainAc.setText(sdf.format(new Date(selection)));
        });
    }

    @Override
    public void onConfirmButtonClicked(String Tag) {
        switch (Tag){
            case "MainACSettingsView_SavePressed":
                binding.loadingViewSettingsFragMainAc.setVisibility(View.VISIBLE);
                APIMethods apiMethods = new APIMethods();
                apiMethods.setPatchUserResponse(MainACSettingsFrag.this);
                apiMethods.patchUser(getContext(), currentUser,
                        binding.editTextUsernameSettingsFragMainAc.getText().toString().trim(),
                        binding.editTextEmailSettingsFragMainAc.getText().toString().trim());
                break;
        }
    }

    @Override
    public void onCancelButtonClicked(String Tag) {
        switch (Tag){
            case "MainACSettingsView_SavePressed":
                break;
        }
    }

    @Override
    public void onPatchUserProfileResponse(boolean response, String message) {
        binding.loadingViewSettingsFragMainAc.setVisibility(View.GONE);
        if(response){
            parent.logout();
        }else{
            snackBarShow.display(binding.getRoot(), message, -1, 1, binding.snackbarViewSettingsFragMainAC, parent);
        }
    }

    @Override
    public void onPatchUserResponse(boolean response, String message, Context context) {
        if(response){
            APIMethods apiMethods = new APIMethods();
            apiMethods.setPatchUserProfileResponse(THIS);
            apiMethods.patchUserProfile(context, currentUser,
                    binding.editTextAddressSettingsFragMainAc.getText().toString().trim(),
                    binding.editTextBDateSettingsFragMainAc.getText().toString().trim());
        }else{
            binding.loadingViewSettingsFragMainAc.setVisibility(View.GONE);
            snackBarShow.display(binding.getRoot(), getString(R.string.mainActivity_SettingsFrag_UserEmailTaken), -1, 1, binding.snackbarViewSettingsFragMainAC, parent);
        }
    }
}
