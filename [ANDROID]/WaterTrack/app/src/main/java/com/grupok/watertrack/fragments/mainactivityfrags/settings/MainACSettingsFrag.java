package com.grupok.watertrack.fragments.mainactivityfrags.settings;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

public class MainACSettingsFrag extends Fragment {

    private FragmentMainACSettingsBinding binding;
    private MainActivity parent;
    private MainACSettingsFrag THIS;
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

        SharedPreferences prefs = parent.getSharedPreferences("Perf_User", MODE_PRIVATE);
        String urlAPI = prefs.getString("urlAPI", "172.22.21.222");
        binding.editTextAPIURLSettingsFragMainAc.setText(urlAPI);

        setupSaveButton();
    }
    private void setupSaveButton(){
        binding.butSaveSettingsFragMainAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = parent.getSharedPreferences("Perf_User", MODE_PRIVATE);
                prefs.edit().putString("urlAPI", binding.editTextAPIURLSettingsFragMainAc.getText().toString().trim()).apply();
                parent.cycleFragments("MainViewFrag", null);
            }
        });
    }
}
