package com.grupok.watertrack.fragments.mainactivityfrags.addcontadorview;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.grupok.watertrack.R;
import com.grupok.watertrack.activitys.MainActivity;
import com.grupok.watertrack.database.LocalDataBase;
import com.grupok.watertrack.database.daos.UserInfosDao;
import com.grupok.watertrack.database.entities.UserInfosEntity;
import com.grupok.watertrack.databinding.FragmentMainAcAddContadorBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainAcAddContadorFrag extends Fragment {

    private FragmentMainAcAddContadorBinding binding;
    private MainActivity parent;
    private UserInfosDao userInfosDao;
    private LocalDataBase localDataBase;
    private List<String> listString = new ArrayList<>();

    public MainAcAddContadorFrag() {
        // Required empty public constructor
    }
    public MainAcAddContadorFrag(MainActivity parent) {
        this.parent = parent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainAcAddContadorBinding.inflate(inflater);

        if(parent.currentUserInfo != null){
            init();
        }

        return binding.getRoot();
    }
    private void init(){
        disableBackPressed();

        setupLocalDataBase();
        setupDatePicker();

        new LocalDatabaseUpdateTask().execute();
        fillDropdownMenu(listString);
        validateInsertionDropdownMenu();
    }
    //---------------------------SETUPS---------------------------
    private void setupLocalDataBase(){
        localDataBase = Room.databaseBuilder(getContext(), LocalDataBase.class, "WaterTrackLocalDB").build();
        userInfosDao = localDataBase.userInfosDao();
    }
    private void setupDatePicker() {
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
    //---------------------------DROPDOWN RELATED---------------------------
    private void fillDropdownMenu(List<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                list
        );

        binding.comboBoxResidentsAddContadorFragMainAc.setAdapter(adapter);

        // CODIGO PARA QUANDO CLICAR O ICON NO FIM DO INPUT ABRIR A DROPDOWN DIRETO
        binding.textInputLayoutComboBoxResidentsAddContadorFragMainAc.setEndIconOnClickListener(v -> binding.comboBoxResidentsAddContadorFragMainAc.showDropDown());
        binding.comboBoxResidentsAddContadorFragMainAc.setOnClickListener(v -> binding.comboBoxResidentsAddContadorFragMainAc.showDropDown());
    }
    private void validateInsertionDropdownMenu() {
        binding.comboBoxResidentsAddContadorFragMainAc.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            Toast.makeText(getContext(), ""+selected, Toast.LENGTH_SHORT).show();
        });
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
    private class LocalDatabaseUpdateTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... voids) {
            List<UserInfosEntity> userList = userInfosDao.getUserInfos();
            for (UserInfosEntity user : userList){
                listString.add(""+user.id+" - "+user.username);
            }

            return listString;
        }

        @Override
        protected void onPostExecute(List<String> objects) {

        }
    }
}