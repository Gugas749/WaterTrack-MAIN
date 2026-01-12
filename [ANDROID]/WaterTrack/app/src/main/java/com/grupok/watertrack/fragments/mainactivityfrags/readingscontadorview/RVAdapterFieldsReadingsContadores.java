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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        fieldsList.add(new ShownFields(context.getString(R.string.mainActivity_ReadingsContadorFrag_RV_fieldsList_ID), String.valueOf(leitura.id)));
        fieldsList.add(new ShownFields(context.getString(R.string.mainActivity_ReadingsContadorFrag_RV_fieldsList_Creator), String.valueOf(leitura.tecnicoID))); // TODO: METER O USERNAME DO TECNICO QUE FEZ A LEITURA
        fieldsList.add(new ShownFields(context.getString(R.string.mainActivity_ReadingsContadorFrag_RV_fieldsList_AdressContador),  String.valueOf(meterEntity.address)));
        fieldsList.add(new ShownFields(context.getString(R.string.mainActivity_ReadingsContadorFrag_RV_fieldsList_Reading), leitura.reading));
        fieldsList.add(new ShownFields(context.getString(R.string.mainActivity_ReadingsContadorFrag_RV_fieldsList_AccumulatedConsumption), leitura.accumulatedConsumption));

        //PARA FORMATAR A DATA (yyyy-mm-dd PARA dd-mm-yyyy) //TODO: FAZER UM SCRIPT GLOBAL PRA NAO TAR SEMPRE A REPETIR CODIGO
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(leitura.date, inputFormatter);
        fieldsList.add(new ShownFields(context.getString(R.string.mainActivity_ReadingsContadorFrag_RV_fieldsList_Date), date.format(outputFormatter)));

        fieldsList.add(new ShownFields(context.getString(R.string.mainActivity_ReadingsContadorFrag_RV_fieldsList_WaterPressure), leitura.waterPressure));
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
