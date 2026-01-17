package com.grupok.watertrack.fragments.mainactivityfrags.meters.detailsmeterview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grupok.watertrack.R;
import com.grupok.watertrack.database.entities.EnterpriseEntity;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.MeterTypeEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RVAdapterFieldsDetailsContadores extends RecyclerView.Adapter<RVAdapterFieldsDetailsContadores.ViewHolder>{

    private final Context context;
    private List<RVAdapterFieldsDetailsContadores.ShownFields> fieldsList;
    private EnterpriseEntity selectedEnterprise;
    private MeterTypeEntity selectedType;
    private UserInfosEntity selectedUser;

    public RVAdapterFieldsDetailsContadores(Context context, MeterEntity meterEntity, MeterTypeEntity selectedType, UserInfosEntity selectedUser, EnterpriseEntity selectedEnterprise) {
        this.context = context;
        this.selectedUser = selectedUser;
        this.selectedType = selectedType;
        this.selectedEnterprise = selectedEnterprise;
        fieldsList = new ArrayList<>();
        atualizarCampos(meterEntity);
    }

    public void atualizarCampos(MeterEntity meterEntity) {
        fieldsList.clear();
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_ID), String.valueOf(meterEntity.id)));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_Address), meterEntity.address));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_Username), selectedUser.username));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_Description), selectedType.description));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_EnterpriseName), selectedEnterprise.name));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_Classe), meterEntity.classe));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_InstalationDate), meterEntity.instalationDate));

        if(meterEntity.shutdownDate != null && !meterEntity.shutdownDate.equals("null")){
            fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_ShutdownDate), meterEntity.shutdownDate));
        }

        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_MaxCapacity), meterEntity.maxCapacity));

        String unity = "N/A";
        List<String> list1 = Arrays.asList(context.getResources().getStringArray(R.array.meterUnitys));
        int auxUnity = Integer.parseInt(meterEntity.measureUnity);
        auxUnity--;
        unity = list1.get(auxUnity);
        String unityFinal = unity.split("-", 2)[1].trim();
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_MeasureUnity), unityFinal));

        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_SupportedTemperature), meterEntity.supportedTemperature));

        String status = "N/A";
        List<String> list2 = Arrays.asList(context.getResources().getStringArray(R.array.meterStatus));
        status = list2.get(meterEntity.state);
        String statusFinal = status.split("-", 2)[1].trim();
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_Status), statusFinal));

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RVAdapterFieldsDetailsContadores.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_row_fields_contadores, parent, false);
        return new RVAdapterFieldsDetailsContadores.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapterFieldsDetailsContadores.ViewHolder holder, int position) {
        RVAdapterFieldsDetailsContadores.ShownFields shownFields = fieldsList.get(position);
        holder.textView_Label_rvRowFields_DetailsContadores.setText(shownFields.label);
        holder.textView_Data_rvRowFields_DetailsContadores.setText(shownFields.data);
    }

    @Override
    public int getItemCount() {
        return fieldsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_Label_rvRowFields_DetailsContadores, textView_Data_rvRowFields_DetailsContadores;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_Label_rvRowFields_DetailsContadores = itemView.findViewById(R.id.textView_Label_rvRowFields_Contadores);
            textView_Data_rvRowFields_DetailsContadores = itemView.findViewById(R.id.textView_Data_rvRowFields_Contadores);
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
