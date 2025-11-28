package com.grupok.watertrack.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.google.gson.Gson;
import com.grupok.watertrack.database.LocalDataBase;
import com.grupok.watertrack.database.daos.ContadoresDao;
import com.grupok.watertrack.database.daos.LogsContadoresDao;
import com.grupok.watertrack.database.daos.UserInfosDao;
import com.grupok.watertrack.database.entities.UserInfosEntity;
import com.grupok.watertrack.databinding.ActivityAuthBinding;
import com.grupok.watertrack.R;
import com.grupok.watertrack.fragments.authactivity.LoginFragment;
import com.grupok.watertrack.fragments.authactivity.RegisterFragment;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    private boolean isLogged;
    private AuthActivity THIS;
    //----------------LOCAL DB-----------------
    private LocalDataBase localDataBase;
    private UserInfosDao userInfosDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        //EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        //-------------------- SHAREDPREFERENCE INFO ------------------------
        SharedPreferences prefs = getSharedPreferences("Perf_User", MODE_PRIVATE);
        isLogged = prefs.getBoolean("logged", false);
        String userEmail = prefs.getString("userEmail", "");

        if(isLogged){
            setupLocalDataBase();
            DatabaseCallback callback = new DatabaseCallback() {
                @Override
                public void onTaskCompleted(UserInfosEntity result) {
                    if(result != null){
                        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                        intent.putExtra("currentUser", new Gson().toJson(result));
                        startActivity(intent);
                    }else{
                        cycleFragments("LoginFrag", "", null);
                    }
                }
            };
            new LocalDatabaseGetUserInfo(callback, userEmail).execute();
        }else{
            cycleFragments("LoginFrag", "", null);
        }
    }

    public void cycleFragments(String goTo, String extra, UserInfosEntity user){
        switch (goTo){
            case "LoginFrag":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_auth_activity, new LoginFragment(this, extra)).commitAllowingStateLoss();
                break;
            case "RegisterFrag":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_auth_activity, new RegisterFragment(this, extra)).commitAllowingStateLoss();
                break;
            case "MainAC":
                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                intent.putExtra("currentUser", new Gson().toJson(user));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.uiMode != getApplicationContext().getResources().getConfiguration().uiMode) {
            recreate();
        }
    }
    private void setupLocalDataBase(){
        localDataBase = Room.databaseBuilder(this, LocalDataBase.class, "WaterTrackLocalDB").build();
        userInfosDao = localDataBase.userInfosDao();
    }
    public interface DatabaseCallback {
        void onTaskCompleted(UserInfosEntity result);
    }
    private class LocalDatabaseGetUserInfo extends AsyncTask<Void, Void, UserInfosEntity> {
        private DatabaseCallback callback;
        private String userEmail;

        public LocalDatabaseGetUserInfo(DatabaseCallback callback, String userEmail) {
            this.callback = callback;
            this.userEmail = userEmail;
        }

        @Override
        protected UserInfosEntity doInBackground(Void... voids) {
            Log.i("WATERTRACKINFO", "Inserting data to the local DB...");

            return userInfosDao.getUser(userEmail);
        }

        @Override
        protected void onPostExecute(UserInfosEntity result) {
            if (callback != null) {
                callback.onTaskCompleted(result);
            }
        }
    }
}