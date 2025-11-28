package com.grupok.watertrack.activitys;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.room.Room;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.grupok.watertrack.R;
import com.grupok.watertrack.database.LocalDataBase;
import com.grupok.watertrack.database.daos.AvariasContadoresDao;
import com.grupok.watertrack.database.daos.ContadoresDao;
import com.grupok.watertrack.database.daos.EmpresasDao;
import com.grupok.watertrack.database.daos.LogsContadoresDao;
import com.grupok.watertrack.database.daos.TecnicoInfoDao;
import com.grupok.watertrack.database.daos.TiposContadoresDao;
import com.grupok.watertrack.database.daos.UserInfosDao;
import com.grupok.watertrack.database.entities.AvariasContadoresEntity;
import com.grupok.watertrack.database.entities.ContadorEntity;
import com.grupok.watertrack.database.entities.EmpresasEntity;
import com.grupok.watertrack.database.entities.LogsContadoresEntity;
import com.grupok.watertrack.database.entities.TecnicoInfoEntity;
import com.grupok.watertrack.database.entities.TiposContadoresEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;
import com.grupok.watertrack.databinding.ActivityMainBinding;
import com.grupok.watertrack.fragments.alertDialogFragments.AlertDialogQuestionFragment;
import com.grupok.watertrack.fragments.mainactivityfrags.addcontadorview.MainAcAddContadorFrag;

import com.grupok.watertrack.fragments.mainactivityfrags.addreportview.MainACReportFrag;
import com.grupok.watertrack.fragments.mainactivityfrags.creditsview.MainACCreditsFrag;
import com.grupok.watertrack.fragments.mainactivityfrags.detailscontadorview.MainACDetailsContadorFrag;
import com.grupok.watertrack.fragments.mainactivityfrags.readingscontadorview.MainACReadingsContadorFrag;
import com.grupok.watertrack.fragments.mainactivityfrags.mainview.MainACMainViewFrag;
import com.grupok.watertrack.scripts.CustomAlertDialogFragment;
import com.grupok.watertrack.scripts.apiCRUD.APIMethods;
import com.grupok.watertrack.scripts.localDBCRUD.LocalDBgetAll;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
    CustomAlertDialogFragment.ConfirmButtonClickAlertDialogQuestionFrag,
    CustomAlertDialogFragment.CancelButtonClickAlertDialogQuestionFrag{

    private ActivityMainBinding binding;
    private MainActivity parent;
    private MainActivity THIS;
    private int currentView;
    public UserInfosEntity currentUserInfo;
    private Boolean allDisable;
    private ActionBarDrawerToggle drawerToggleSideMenu;
    //-------------------LISTS---------------
    private List<LogsContadoresEntity> logsContEntitiesList;
    private List<ContadorEntity> contadoresEntityList;
    //-------------------LOCAL DATABASE---------------
    private LocalDataBase localDataBase;
    private LogsContadoresDao logsContadoresDao;
    private ContadoresDao contadoresDao;
    private AvariasContadoresDao avariasContadoresDao;
    private EmpresasDao empresasDao;
    private TecnicoInfoDao tecnicoInfoDao;
    private TiposContadoresDao tiposContadoresDao;
    private UserInfosDao userInfosDao;

    public interface DatabaseCallback {
        void onTaskCompleted(LocalDBgetAll result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        this.currentUserInfo = new Gson().fromJson(
                getIntent().getStringExtra("currentUser"),
                UserInfosEntity.class
        );

        init();
    }

    private void init(){
        THIS = this;
        allDisable = false;
        disableBackPressed();
        logsContEntitiesList = new ArrayList<>();
        contadoresEntityList = new ArrayList<>();

        setupSideMenu();
        setupBackButton();
        setupKeyboardListener();

        cycleFragments("MainViewFrag", null);

        setupLocalDataBase();
    }
    //----------------------SETUPS---------------------------
    private void setupLocalDataBase(){
        localDataBase = Room.databaseBuilder(getApplicationContext(), LocalDataBase.class, "WaterTrackLocalDB").build();
        logsContadoresDao = localDataBase.logsContadoresDao();
        contadoresDao = localDataBase.contadoresDao();
        avariasContadoresDao = localDataBase.avariasContadoresDao();
        empresasDao = localDataBase.empresasDao();
        tecnicoInfoDao = localDataBase.tecnicoInfoDao();
        tiposContadoresDao = localDataBase.tiposContadoresDao();
        userInfosDao = localDataBase.userInfosDao();
    }
    private void setupBackButton(){
        binding.imageViewButtonBackMainAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAlertDialogFragment customAlertDialogFragment = new CustomAlertDialogFragment();
                AlertDialogQuestionFragment fragment= new AlertDialogQuestionFragment(getString(R.string.mainActivity_AlertDialog_BackPressed_AddContador_Title), getString(R.string.mainActivity_AlertDialog_BackPressed_AddContador_Desc), customAlertDialogFragment, customAlertDialogFragment, "2");

                switch (currentView){
                    case 1: // ADD CONTADOR
                        customAlertDialogFragment.setConfirmListenner(THIS);
                        customAlertDialogFragment.setCancelListenner(THIS);
                        customAlertDialogFragment.setCustomFragment(fragment);
                        customAlertDialogFragment.setTag("MainACAddContadorView_BackPressed");
                        customAlertDialogFragment.show(getSupportFragmentManager(), "CustomAlertDialogFragment");
                        break;
                    case 2: // DETAILS CONTADOR
                        cycleFragments("MainViewFrag", null);
                        break;
                    case 3: // READINGS CONTADOR
                        cycleFragments("DetailsContadorFrag", null);
                        break;
                    case 4: // Creditos
                    case 5: // Reports (Side menu)
                        cycleFragments("MainViewFrag", null);
                        break;
                    case 6: // Reports (Details Contador)
                        cycleFragments("DetailsContadorFrag", null);
                        break;
                }
            }
        });
    }
    //----------------------SIDE MENU---------------------------
    private void setupSideMenu(){
        //-------------menu---------------------
        drawerToggleSideMenu = new ActionBarDrawerToggle(this,binding.drawerLayoutMainAcSideMenu,R.string.general_continue,R.string.general_cancel);
        binding.drawerLayoutMainAcSideMenu.addDrawerListener(drawerToggleSideMenu);
        drawerToggleSideMenu.syncState();
        //-----------------------------------------
        View header = binding.navigationViewMainAcSideMenu.getHeaderView(0);
        TextView usernameSideMenu = header.findViewById(R.id.headerSideMenu_Username_MainAc);
        TextView addressSideMenu = header.findViewById(R.id.headerSideMenu_Address_MainAc);
        usernameSideMenu.setText(currentUserInfo.username);
        addressSideMenu.setText(currentUserInfo.address);
        //-----------------------------------------

        binding.imageViewButtonSideMenuMainAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(! binding.drawerLayoutMainAcSideMenu.isDrawerOpen(GravityCompat.END)){
                    binding.drawerLayoutMainAcSideMenu.openDrawer(GravityCompat.END);
                    closeKeyboard();
                }
            }
        });

        binding.navigationViewMainAcSideMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(!allDisable){
                    item.setEnabled(false);
                    if (item.getItemId() == R.id.mainAc_SideMenu_Credits) {
                        Bundle data = null;
                        cycleFragments("CreditsFrag", data);
                    }
                    else if(item.getItemId() == R.id.mainAc_SideMenu_Reports) {
                        Bundle data = new Bundle();
                        data.putBoolean("fromSideMenu", true);
                        cycleFragments("ReportFrag", data);
                    }
                    item.setEnabled(true);
                    binding.drawerLayoutMainAcSideMenu.closeDrawer(GravityCompat.END);
                }
                return true;
            }
        });
        binding.navigationViewMainAcSideMenu.bringToFront();
    }
    public void enableSwipeToOpenSideMenu() {
        if (binding.drawerLayoutMainAcSideMenu != null) {
            binding.drawerLayoutMainAcSideMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
    public void disableSwipeToOpenSideMenu() {
        if (binding.drawerLayoutMainAcSideMenu != null) {
            binding.drawerLayoutMainAcSideMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }
    //----------------------FUNCTIONS---------------------------
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
    public void cycleFragments(String goTo, Bundle data){
        switch (goTo){
            case "MainViewFrag":
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_fragmentContainer_MainAC, new MainACMainViewFrag(this, contadoresEntityList)).commitAllowingStateLoss();
                binding.imageViewButtonBackMainAC.setVisibility(View.GONE);
                currentView = 0;
                break;
            case "AddContadorFrag":
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_fragmentContainer_MainAC, new MainAcAddContadorFrag(this)).commitAllowingStateLoss();
                binding.imageViewButtonBackMainAC.setVisibility(View.VISIBLE);
                currentView = 1;
                break;
            case "DetailsContadorFrag":
                MainACDetailsContadorFrag detailsFrag = new MainACDetailsContadorFrag(this, contadoresEntityList);
                binding.imageViewButtonBackMainAC.setVisibility(View.VISIBLE);
                if (data != null) {
                    detailsFrag.setArguments(data);
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout_fragmentContainer_MainAC, detailsFrag)
                        .commitAllowingStateLoss();
                currentView = 2;
                break;

            case "ReadingsContadorFrag":
                MainACReadingsContadorFrag readingsContadorFrag = new MainACReadingsContadorFrag(this, logsContEntitiesList, contadoresEntityList);
                binding.imageViewButtonBackMainAC.setVisibility(View.VISIBLE);
                if (data != null) {
                    readingsContadorFrag.setArguments(data);
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout_fragmentContainer_MainAC, readingsContadorFrag)
                        .commitAllowingStateLoss();
                currentView = 3;
                break;

            case "CreditsFrag":
                MainACCreditsFrag creditsFrag = new MainACCreditsFrag();
                binding.imageViewButtonBackMainAC.setVisibility(View.VISIBLE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout_fragmentContainer_MainAC, creditsFrag)
                        .commitAllowingStateLoss();
                currentView = 4;
                break;

            case "ReportFrag":
                MainACReportFrag reportFrag = new MainACReportFrag(this, contadoresEntityList);
                binding.imageViewButtonBackMainAC.setVisibility(View.VISIBLE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout_fragmentContainer_MainAC, reportFrag)
                        .commitAllowingStateLoss();
                if(data.getBoolean("fromSideMenu")){
                    currentView = 5;
                }else{
                    currentView = 6;
                }
                break;
        }
    }
    //----------------------THEME DEBUGGER---------------------------
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.uiMode != getApplicationContext().getResources().getConfiguration().uiMode) {
            recreate();
        }
    }
    //----------------------KEYBOARD LISTENNER---------------------------
    private void setupKeyboardListener() {
        View rootView = findViewById(android.R.id.content);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean wasOpen = false;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                boolean isOpen = keypadHeight > screenHeight * 0.15; // threshold for keyboard
                if (isOpen != wasOpen) {
                    wasOpen = isOpen;
                    onKeyboardVisibilityChanged(isOpen);
                }
            }
        });
    }
    private void onKeyboardVisibilityChanged(boolean isOpen) {
        if(isOpen){
            disableSwipeToOpenSideMenu();
        }else{
            enableSwipeToOpenSideMenu();
        }
    }
    private void closeKeyboard(){
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(getApplicationContext());
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    //----------------------QUESTION ALERTDIALOG---------------------------
    @Override
    public void onCancelButtonClicked(String Tag) {
        switch (Tag){
            case "MainACAddContadorView_BackPressed":
                break;
        }
    }
    @Override
    public void onConfirmButtonClicked(String Tag) {
        switch (Tag){
            case "MainACAddContadorView_BackPressed":
                cycleFragments("MainViewFrag", null);
                break;
        }
    }
    //----------------------DATABASE OPERATIONS---------------------------
    private class LocalDatabaseGetAllDataTask extends AsyncTask<Void, Void, LocalDBgetAll> {
        private DatabaseCallback callback;
        private String currentUserEmail;

        public LocalDatabaseGetAllDataTask(DatabaseCallback callback, String currentUserEmail) {
            this.callback = callback;
            this.currentUserEmail = currentUserEmail;
        }

        @Override
        protected LocalDBgetAll doInBackground(Void... voids) {
            Log.i("WATERTRACKINFO", "Fetching data from local DB...");
            List<LogsContadoresEntity> list1 = logsContadoresDao.getLogsContadores();
            List<ContadorEntity> list2 = contadoresDao.getContadores();
            List<AvariasContadoresEntity> list3 = avariasContadoresDao.getAvariasContadores();
            List<EmpresasEntity> list4 = empresasDao.getEmpresas();
            List<TecnicoInfoEntity> list5 = tecnicoInfoDao.getTecnicosInfo();
            List<TiposContadoresEntity> list6 = tiposContadoresDao.getTiposContadores();
            List<UserInfosEntity> userList = userInfosDao.getUserInfos();
            UserInfosEntity userInfo = null;
            for(UserInfosEntity user : userList){
                if(user.email.equals(currentUserEmail)){
                    userInfo = user;
                    break;
                } else if (currentUserEmail.isEmpty()) {
                    userInfo = userInfosDao.getUserInfos().get(0);
                }
            }

            return new LocalDBgetAll(list1, list2, list3, list4, list5, list6, userInfo);
        }

        @Override
        protected void onPostExecute(LocalDBgetAll result) {
            if (callback != null) {
                callback.onTaskCompleted(result);
            }
        }
    }
}