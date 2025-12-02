package com.grupok.watertrack.fragments.mainactivityfrags.readingscontadorview;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.grupok.watertrack.activitys.MainActivity;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.LogsContadoresEntity;
import com.grupok.watertrack.databinding.FragmentMainACReadingsContadorBinding;

import java.util.ArrayList;
import java.util.List;



public class MainACReadingsContadorFrag extends Fragment {
    private MainActivity parent;
    private Context context;
    private MainACReadingsContadorFrag THIS;
    private FragmentMainACReadingsContadorBinding binding;
    private List<MeterEntity> contadoresEntityList;
    private List<LogsContadoresEntity> logsContadoresEntitiesList;
    private RVAdapterFieldsReadingsContadores fieldsAdapter;
    private int contadorId;

    public MainACReadingsContadorFrag() {
        // Required empty public constructor
    }

    public MainACReadingsContadorFrag(MainActivity parent, List<LogsContadoresEntity> logsContadoresEntitiesList, List<MeterEntity> contadoresEntityList) {
        this.parent = parent;
        this.contadoresEntityList = contadoresEntityList;
        this.logsContadoresEntitiesList = logsContadoresEntitiesList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainACReadingsContadorBinding.inflate(inflater);

        if(parent.currentUserInfo != null){
            init();
        }

        if (getArguments() != null) {
            contadorId = getArguments().getInt("contadorId", -1);
            Log.d("LOGTESTE", "Received contadorId: " + contadorId);
        }

        return binding.getRoot();
    }

    private void init(){
        THIS = this;
        context = getContext();

        logsContadoresEntitiesList = new ArrayList<>();
        logsContadoresEntitiesList.add(new LogsContadoresEntity(1, "1402m3", "14m3", "23/10/2025", "100", "Leiura Normal", false));
        logsContadoresEntitiesList.add(new LogsContadoresEntity(2, "4306m3", "14022m3", "24/11/2025", "200", "Leitura Habibi", false));
        logsContadoresEntitiesList.add(new LogsContadoresEntity(3, "28562m3", "242m3", "25/12/2025", "50", "sdjadjkajdkajdkajdkjadkjadjaskjdkadjakdjkasjdkasjdkasjsdksadkjsakdjsakdjkasdjkajdkasjdkjasdjsjkasjdkasdjkjka", false));

        fieldsAdapter = new RVAdapterFieldsReadingsContadores(context, logsContadoresEntitiesList.get(0));
        RVAdapterReadingsACReadingsContadores rvAdapterReadingsACReadingsContadores = new RVAdapterReadingsACReadingsContadores(context, logsContadoresEntitiesList, leituraSelecionada -> {
            fieldsAdapter.atualizarCampos(leituraSelecionada);
        });

        // ====
        binding.rvFieldsReadingsContadorMainAc.setLayoutManager(new LinearLayoutManager(context));
        binding.rvFieldsReadingsContadorMainAc.setAdapter(fieldsAdapter);

        binding.rvReadingsReadingsContadorMainAc.setLayoutManager(new LinearLayoutManager(context));
        binding.rvReadingsReadingsContadorMainAc.setAdapter(rvAdapterReadingsACReadingsContadores);
    }

}