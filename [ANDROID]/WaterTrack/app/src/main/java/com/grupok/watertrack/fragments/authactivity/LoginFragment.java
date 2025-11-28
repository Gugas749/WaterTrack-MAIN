package com.grupok.watertrack.fragments.authactivity;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.grupok.watertrack.R;
import com.grupok.watertrack.activitys.AuthActivity;
import com.grupok.watertrack.database.LocalDataBase;
import com.grupok.watertrack.database.daos.UserInfosDao;
import com.grupok.watertrack.database.entities.UserInfosEntity;
import com.grupok.watertrack.databinding.FragmentLoginBinding;
import com.grupok.watertrack.scripts.SnackBarShow;
import com.grupok.watertrack.scripts.apiCRUD.APIMethods;

import java.util.List;

public class LoginFragment extends Fragment implements APIMethods.LoginResponse {
    private AuthActivity parent;
    private FragmentLoginBinding binding;
    private String loginEmailInputed;
    private LoginFragment THIS;
    private Context context;
    public SnackBarShow snackBarShow = new SnackBarShow();
    //----------------LOCAL DB-----------------
    private LocalDataBase localDataBase;
    private UserInfosDao userInfosDao;

    public LoginFragment() {
        // Required empty public constructor
    }

    public LoginFragment(AuthActivity parent) {
        this.parent = parent;
    }

    public LoginFragment(AuthActivity parent, String loginEmailInputed) {
        this.parent = parent;
        this.loginEmailInputed = loginEmailInputed;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater);

        init();

        return binding.getRoot();
    }

    private void init(){
        THIS = this;
        context = this.getContext();
        setupNewUserBut();
        setupLoginBut();

        loadOldInfo();
        setupLocalDataBase();
    }

    //------------------------------- SETUPS -----------------------------------
    private void setupLocalDataBase(){
        localDataBase = Room.databaseBuilder(parent, LocalDataBase.class, "WaterTrackLocalDB").build();
        userInfosDao = localDataBase.userInfosDao();
    }
    private void setupLoginBut(){
        binding.butLoginLoginFragAuthAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginEmptyFieldsTest()){
                    loginAction();
                }
            }
        });
        binding.editTextPasswordLoginFragAuthAc.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String password = v.getText().toString().trim();

                if (password.isEmpty()) {
                    binding.outlinedTextFieldPassword.setError(getString(R.string.authActivity_LoginFrag_Field_RequiredField_Error));
                } else {
                    binding.outlinedTextFieldPassword.setError(null);
                    closeKeyboard();
                    loginAction();
                }
                return true;
            }
            return false;
        });
    }
    private void setupNewUserBut(){
        binding.butRegisterLoginFragAuthAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfosEntity user = null;
                parent.cycleFragments("RegisterFrag", binding.editTextEmailLoginFragAuthAc.getText().toString(), user);
            }
        });
    }
    //------------------------------- OUTROS -------------------------------------
    private boolean loginEmptyFieldsTest(){
        boolean aux = true;
        if(binding.editTextEmailLoginFragAuthAc.getText() == null || binding.editTextEmailLoginFragAuthAc.getText().toString().trim().isEmpty()){
            binding.outlinedTextFieldEmail.setError(getString(R.string.authActivity_LoginFrag_Field_RequiredField_Error));
            aux = false;
        }else{
            binding.outlinedTextFieldEmail.setError(null);
            binding.outlinedTextFieldEmail.setErrorEnabled(false);
        }
        if(binding.editTextPasswordLoginFragAuthAc.getText() == null || binding.editTextPasswordLoginFragAuthAc.getText().toString().trim().isEmpty()){
            binding.outlinedTextFieldPassword.setError(getString(R.string.authActivity_LoginFrag_Field_RequiredField_Error));
            aux = false;
        }else{
            binding.outlinedTextFieldPassword.setError(null);
            binding.outlinedTextFieldPassword.setErrorEnabled(false);
        }
        return aux;
    }
    private void loadOldInfo(){
        if(loginEmailInputed != null){
            if(!loginEmailInputed.isEmpty()){
                binding.editTextEmailLoginFragAuthAc.setText(loginEmailInputed);
            }
        }
    }
    private void closeKeyboard(){
        View view = parent.getCurrentFocus();
        if (view == null) {
            view = new View(getContext());
        }

        InputMethodManager imm = (InputMethodManager) parent.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void loginAction(){
        binding.loadingViewLoginFrag.setVisibility(View.VISIBLE);
        APIMethods apiMethods = new APIMethods();
        apiMethods.login(getContext(), binding.parentViewLoginFrag,
                binding.editTextEmailLoginFragAuthAc.getText().toString().trim(),
                binding.editTextPasswordLoginFragAuthAc.getText().toString().trim());
        apiMethods.setLoginResponse(THIS);
    }
    @Override
    public void onLoginResponse(boolean response, UserInfosEntity user, String message) {
        binding.loadingViewLoginFrag.setVisibility(View.GONE);
        if(response){
            DatabaseCallback callback = new DatabaseCallback() {
                @Override
                public void onTaskCompleted(UserInfosEntity result) {
                    if(result != null){
                        SharedPreferences prefs = parent.getSharedPreferences("Perf_User", MODE_PRIVATE);
                        prefs.edit().putBoolean("logged", true).apply();
                        prefs.edit().putString("userEmail", result.email).apply();
                        parent.cycleFragments("MainAC", "", result);
                    }else{
                        snackBarShow.display(binding.getRoot(), getString(R.string.authActivity_LoginFrag_LocalDB_Insert_Error), -1, 1, binding.snackbarViewLoginFrag, context);

                    }
                }
            };
            new LocalDatabaseInsertUserInfo(callback, user).execute();
        }else{
            snackBarShow.display(binding.getRoot(), message, -1, 1, binding.snackbarViewLoginFrag, context);
        }
    }
    //----------------------DATABASE OPERATIONS---------------------------
    public interface DatabaseCallback {
        void onTaskCompleted(UserInfosEntity result);
    }
    private class LocalDatabaseInsertUserInfo extends AsyncTask<Void, Void, UserInfosEntity> {
        private DatabaseCallback callback;
        private UserInfosEntity userInfo;

        public LocalDatabaseInsertUserInfo(DatabaseCallback callback, UserInfosEntity userInfo) {
            this.callback = callback;
            this.userInfo = userInfo;
        }

        @Override
        protected UserInfosEntity doInBackground(Void... voids) {
            Log.i("WATERTRACKINFO", "Inserting data to the local DB...");

            long id = userInfosDao.insert(userInfo);

            if(id == -1){//se nao inserir fica null para dar login outra vez
                userInfo = null;
            }
            return userInfo;
        }

        @Override
        protected void onPostExecute(UserInfosEntity result) {
            if (callback != null) {
                callback.onTaskCompleted(result);
            }
        }
    }
}