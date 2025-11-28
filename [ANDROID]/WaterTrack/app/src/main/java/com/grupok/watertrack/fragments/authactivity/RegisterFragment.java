package com.grupok.watertrack.fragments.authactivity;

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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.grupok.watertrack.R;
import com.grupok.watertrack.activitys.AuthActivity;
import com.grupok.watertrack.database.LocalDataBase;
import com.grupok.watertrack.database.daos.ContadoresDao;
import com.grupok.watertrack.database.daos.LogsContadoresDao;
import com.grupok.watertrack.database.daos.UserInfosDao;
import com.grupok.watertrack.database.entities.UserInfosEntity;
import com.grupok.watertrack.databinding.FragmentLoginBinding;
import com.grupok.watertrack.databinding.FragmentRegisterBinding;
import com.grupok.watertrack.scripts.SnackBarShow;
import com.grupok.watertrack.scripts.apiCRUD.APIMethods;

public class RegisterFragment extends Fragment implements APIMethods.SignUpResponse {

    private AuthActivity parent;
    private FragmentRegisterBinding binding;
    private String loginEmailInputed;
    private RegisterFragment THIS;
    private Context context;
    public SnackBarShow snackBarShow = new SnackBarShow();

    public RegisterFragment() {
        // Required empty public constructor
    }

    public RegisterFragment(AuthActivity parent) {
        this.parent = parent;
    }

    public RegisterFragment(AuthActivity parent, String loginEmailInputed) {
        this.parent = parent;
        this.loginEmailInputed = loginEmailInputed;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater);

        init();

        return binding.getRoot();
    }

    private void init(){
        THIS = this;
        context = this.getContext();
        setupOldUserBut();
        setupRegisterButton();

        loadOldInfo();
    }

    //------------------------------- SETUPS -----------------------------------
    private void setupRegisterButton(){
        binding.butRegisterRegisterFragAuthAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signupEmptyFieldsTest() && signupFieldsTest()){
                    signupAction();
                }
            }
        });
        binding.editTextConfirmPasswordRegisterFragAuthAc.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (signupEmptyFieldsTest()) {
                    if(signupFieldsTest()){
                        closeKeyboard();
                        signupAction();
                    }
                }
                return true;
            }
            return false;
        });
    }
    private void setupOldUserBut(){
        binding.butOldUserRegisterFragAuthAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.cycleFragments("LoginFrag", binding.editTextEmailRegisterFragAuthAc.getText().toString(), null);
            }
        });
    }

    //------------------------------- OUTROS -------------------------------------
    private void loadOldInfo(){
        if(!loginEmailInputed.isEmpty()){
            binding.editTextEmailRegisterFragAuthAc.setText(loginEmailInputed);
        }
    }
    private boolean signupEmptyFieldsTest(){
        boolean aux = true;
        if(binding.editTextEmailRegisterFragAuthAc.getText() == null || binding.editTextEmailRegisterFragAuthAc.getText().toString().trim().isEmpty()){
            binding.outlinedTextFieldEmail.setError(getString(R.string.authActivity_RegisterFrag_Field_RequiredField_Error));
            aux = false;
        }else{
            binding.outlinedTextFieldEmail.setError(null);
            binding.outlinedTextFieldEmail.setErrorEnabled(false);
        }
        if(binding.editTextPasswordRegisterFragAuthAc.getText() == null || binding.editTextPasswordRegisterFragAuthAc.getText().toString().trim().isEmpty()){
            binding.outlinedTextFieldPassword.setError(getString(R.string.authActivity_RegisterFrag_Field_RequiredField_Error));
            aux = false;
        }else{
            binding.outlinedTextFieldPassword.setError(null);
            binding.outlinedTextFieldPassword.setErrorEnabled(false);
        }
        if(binding.editTextConfirmPasswordRegisterFragAuthAc.getText() == null || binding.editTextConfirmPasswordRegisterFragAuthAc.getText().toString().trim().isEmpty()){
            binding.outlinedTextFieldConfirmPassword.setError(getString(R.string.authActivity_RegisterFrag_Field_RequiredField_Error));
            aux = false;
        }else{
            binding.outlinedTextFieldConfirmPassword.setError(null);
            binding.outlinedTextFieldConfirmPassword.setErrorEnabled(false);
        }
        return aux;
    }
    private boolean signupFieldsTest(){
        boolean aux = true;
        if(!binding.editTextConfirmPasswordRegisterFragAuthAc.getText().toString().trim().equals(binding.editTextPasswordRegisterFragAuthAc.getText().toString().trim())){
            binding.outlinedTextFieldConfirmPassword.setError(getString(R.string.authActivity_RegisterFrag_Field_PasswordsDiferent_Error));
            aux = false;
        }else{
            binding.outlinedTextFieldConfirmPassword.setError(null);
            binding.outlinedTextFieldConfirmPassword.setErrorEnabled(false);
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.editTextEmailRegisterFragAuthAc.getText().toString().trim()).matches()){
            binding.outlinedTextFieldEmail.setError(getString(R.string.authActivity_RegisterFrag_Field_EmailNotValid_Error));
            aux = false;
        }
        else{
            binding.outlinedTextFieldConfirmPassword.setError(null);
            binding.outlinedTextFieldConfirmPassword.setErrorEnabled(false);
        }
        return aux;
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
    private void signupAction(){
        binding.loadingViewRegisterFrag.setVisibility(View.VISIBLE);
        APIMethods apiMethods = new APIMethods();
        apiMethods.signup(getContext(),
                binding.editTextEmailRegisterFragAuthAc.getText().toString().trim(),
                binding.editTextConfirmPasswordRegisterFragAuthAc.getText().toString().trim());
        apiMethods.setSignUpResponse(THIS);
    }
    @Override
    public void onSignUpResponse(boolean response, String message) {
        binding.loadingViewRegisterFrag.setVisibility(View.GONE);
        if(response){
            binding.editTextEmailRegisterFragAuthAc.setText("");
            binding.editTextPasswordRegisterFragAuthAc.setText("");
            binding.editTextConfirmPasswordRegisterFragAuthAc.setText("");
            snackBarShow.display(binding.getRoot(), getString(R.string.authActivity_RegisterFrag_VerifyEmail), -1, 2, binding.snackbarViewRegisterFrag, context);
        }else{
            snackBarShow.display(binding.getRoot(), message, -1, 2, binding.snackbarViewRegisterFrag, context);
        }
    }
}