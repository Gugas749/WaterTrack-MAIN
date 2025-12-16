package com.grupok.watertrack.fragments.mainactivityfrags.readingscontadorview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grupok.watertrack.R;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.MeterReadingEntity;
import com.grupok.watertrack.database.entities.MeterTypeEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;

import java.util.ArrayList;
import java.util.List;

public class RVAdapterFieldsReadingsContadores extends RecyclerView.Adapter<RVAdapterFieldsReadingsContadores.ViewHolder> {

    private final Context context;
    private List<ShownFields> fieldsList;
    private MeterEntity meterEntity;
    private UserInfosEntity selectedUser;

    public RVAdapterFieldsReadingsContadores(Context context, MeterReadingEntity leitura, MeterEntity meterEntity, UserInfosEntity selectedUser) {
        this.context = context;
        fieldsList = new ArrayList<>();
        this.selectedUser = selectedUser;
        this.meterEntity = meterEntity;
        atualizarCampos(leitura);
    }

    public void atualizarCampos(MeterReadingEntity leitura) {
        fieldsList.clear();

        if (leitura == null) {
            notifyDataSetChanged();
            return;
        }

        fieldsList.add(new ShownFields("id", String.valueOf(leitura.id)));
        fieldsList.add(new ShownFields("userid", selectedUser != null ? selectedUser.username : "N/A")); //TODO: FIX DA NULL
        fieldsList.add(new ShownFields("meterid",  String.valueOf(meterEntity.id)));

        String problem = "N/A";
        switch (leitura.problemID){ //TODO: NAO SEI QUAIS OS TIPOS DE PROBLEMAS SAO
            case 0:
                problem = "Leitura Ausente";
                break;
            case 1:
                problem = "Leitura Normal";
                break;
            case 2:
                problem = "Leitura Baixa";
                break;
        }

        fieldsList.add(new ShownFields("problemid",  problem));

        fieldsList.add(new ShownFields("reading", leitura.reading));
        fieldsList.add(new ShownFields("accumulatedConsumption", leitura.accumulatedConsumption));
        fieldsList.add(new ShownFields("Data", leitura.date));
        fieldsList.add(new ShownFields("waterPressure", leitura.waterPressure));
        fieldsList.add(new ShownFields("desc", leitura.desc));
        fieldsList.add(new ShownFields("readingType",  String.valueOf(leitura.readingType))); //TODO FAZER SWITCH NISTO?
        fieldsList.add(new ShownFields("problemState",  String.valueOf(leitura.problemState))); //TODO FAZER SWITCH NISTO?

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_row_fields_contadores, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShownFields shownFields = fieldsList.get(position);
        holder.textView_Label_rvRowFields_ReadingsContadores.setText(shownFields.label);
        holder.textView_Data_rvRowFields_ReadingsContadores.setText(shownFields.data);
    }

    @Override
    public int getItemCount() {
        return fieldsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_Label_rvRowFields_ReadingsContadores, textView_Data_rvRowFields_ReadingsContadores;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_Label_rvRowFields_ReadingsContadores = itemView.findViewById(R.id.textView_Label_rvRowFields_Contadores);
            textView_Data_rvRowFields_ReadingsContadores = itemView.findViewById(R.id.textView_Data_rvRowFields_Contadores);
        }
    }

    public static class ShownFields {
        public String label;
        public String data;

        public ShownFields(String label, String data) {
            this.label = label;
            this.data = data;
        }
    }
}
