package com.grupok.watertrack.fragments.mainactivityfrags.mainview;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.grupok.watertrack.R;
import com.grupok.watertrack.database.entities.ContadorEntity;
import com.grupok.watertrack.activitys.MainActivity;
import com.grupok.watertrack.databinding.FragmentMainACMainViewBinding;
import com.grupok.watertrack.scripts.SnackBarShow;

import java.util.ArrayList;
import java.util.List;

public class MainACMainViewFrag extends Fragment implements RVAdapterMainAcMainView.ContadorItemClick {

    private MainActivity parent;
    private Context context;
    private MainACMainViewFrag THIS;
    private SnackBarShow snackBarShow;
    public int popUpMenuOption = 0; //1- Address 2-Name
    private FragmentMainACMainViewBinding binding;
    private List<ContadorEntity> contadoresEntityList;
    private RVAdapterMainAcMainView adapter;

    public MainACMainViewFrag() {
        // Required empty public constructor
    }

    public MainACMainViewFrag(MainActivity parent, List<ContadorEntity> contadoresEntityList) {
        this.parent = parent;
        this.contadoresEntityList = contadoresEntityList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainACMainViewBinding.inflate(inflater);

        if(parent.currentUserInfo != null){
            init();
        }

        return binding.getRoot();
    }

    private void init(){
        THIS = this;
        context = getContext();
        adapter = new RVAdapterMainAcMainView(this.getContext(), contadoresEntityList, parent.currentUserInfo.cargo, parent);
        adapter.setItemClickListenner(this);
        snackBarShow = new SnackBarShow();

        ContadorEntity example = new ContadorEntity("Gui", "R. Dr. Duarte Álvares Abreu 21, Cadaval, Lisboa, Portugal", 1,1,1,"A", "dataInstalacao", "capMax", "uniMedida", "tempSup", 0);
        contadoresEntityList.add(example);
        example = new ContadorEntity("Diogo", "R. Das Flores 21, Lourinha, Lisboa, Portugal", 1,1,1,"A", "dataInstalacao", "capMax", "uniMedida", "tempSup", 1);
        contadoresEntityList.add(example);
        example = new ContadorEntity("Gutti", "Rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr", 1,1,1,"A", "dataInstalacao", "capMax", "uniMedida", "tempSup", 2);
        contadoresEntityList.add(example);


        if(parent.currentUserInfo.cargo == 1){
            binding.butAddContadorMainViewMainAc.setVisibility(View.GONE);
        }

        loadRv();
        setupSearchButton();
        setupAddContadorButton();
        setupSeachTextChange();
        disableBackPressed();
    }
    private void setupSearchButton(){
        binding.butSearchContadorMainViewMainAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.textViewNoItemsToDisplayFragBackups.getVisibility() == View.VISIBLE){
                    snackBarShow.display(binding.getRoot(), getString(R.string.mainActivity_MainViewFrag_SnackBar_NothingToSearch), -1, 1, binding.butSearchContadorMainViewMainAc, context);
                }else{

                    if(binding.outlinedTextFieldSearchMainViewFragMainAc.getVisibility() == View.VISIBLE) {
                        binding.outlinedTextFieldSearchMainViewFragMainAc.setVisibility(View.GONE);
                    }
                    else{
                        PopupMenu popupMenu = new PopupMenu(context, binding.butSearchContadorMainViewMainAc);
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_mainview_mainac, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                int id = item.getItemId();
                                if (id == R.id.option_SearchByAddress_PopupMenu_MainView_MainAC) {
                                    popUpMenuOption = 1;
                                    menuItemClickHandler();
                                    return true;
                                } else if (id == R.id.option_SearchByName_PopupMenu_MainView_MainAC) {
                                    popUpMenuOption = 2;
                                    menuItemClickHandler();
                                    return true;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                }
            }
        });
    }
    private void setupAddContadorButton(){
        binding.butAddContadorMainViewMainAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.cycleFragments("AddContadorFrag", null);
            }
        });
    }
    private void setupSeachTextChange(){
        binding.editTextSeachMainViewFragMainAc.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                //acontece depois de ter mudança no texto
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //acontece antes de ter mudança no texto
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //acontece sempre q o texto muda
                List<ContadorEntity> filtered = filterBySearch(""+s);
                adapter.updateData(filtered);
                adapter.notifyDataSetChanged();
            }
        });
    }
    //-------------------------FUNCTIONS-------------------------------
    private void loadRv(){
        adapter.updateData(new ArrayList<>());
        adapter.notifyDataSetChanged();
        if(!contadoresEntityList.isEmpty()){
            adapter.updateData(contadoresEntityList);
            binding.rvContadoresMainViewMainAc.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvContadoresMainViewMainAc.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else{
            binding.textViewNoItemsToDisplayFragBackups.setVisibility(View.VISIBLE);
            binding.rvContadoresMainViewMainAc.setVisibility(View.GONE);
        }

    }
    private void menuItemClickHandler(){
        if (binding.outlinedTextFieldSearchMainViewFragMainAc.getVisibility() == View.GONE) {
            binding.outlinedTextFieldSearchMainViewFragMainAc.setVisibility(View.VISIBLE);

            binding.editTextSeachMainViewFragMainAc.postDelayed(() -> {
                binding.editTextSeachMainViewFragMainAc.requestFocus();

                InputMethodManager imm = (InputMethodManager) requireContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(binding.editTextSeachMainViewFragMainAc, InputMethodManager.SHOW_FORCED);
                }
            }, 150);// delay em ms para aparecer o teclado a tempo
        }
        else
            binding.outlinedTextFieldSearchMainViewFragMainAc.setVisibility(View.GONE);

        binding.outlinedTextFieldSearchMainViewFragMainAc.setVisibility(View.VISIBLE);

        binding.editTextSeachMainViewFragMainAc.postDelayed(() -> {
            binding.editTextSeachMainViewFragMainAc.requestFocus();

            InputMethodManager imm = (InputMethodManager) requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(binding.editTextSeachMainViewFragMainAc, InputMethodManager.SHOW_FORCED);
            }
        }, 150); // delay em ms para aparecer o teclado a tempo

    }
    private List<ContadorEntity> filterBySearch(String text){
        List<ContadorEntity> filtered = new ArrayList<>();
        switch (popUpMenuOption){
            case 1:
                for (ContadorEntity contador : contadoresEntityList) {
                    if(contador.morada.toLowerCase().contains(text.toLowerCase())){
                        filtered.add(contador);
                    }
                }
                break;
            case 2:
                for (ContadorEntity contador : contadoresEntityList) {
                    if(contador.nome.toLowerCase().contains(text.toLowerCase())){
                        filtered.add(contador);
                    }
                }
                break;
            default:
                snackBarShow.display(binding.getRoot(), getString(R.string.mainActivity_MainViewFrag_SnackBar_OptionNotSelected), -1, 1, binding.butSearchContadorMainViewMainAc, context);
                break;
        }

        return filtered;
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
    @Override
    public void onBackupsItemClick(ContadorEntity contador) {
        Bundle data = new Bundle();
        data.putInt("contadorId", contador.id);
        parent.cycleFragments("DetailsContadorFrag", data);
    }
}