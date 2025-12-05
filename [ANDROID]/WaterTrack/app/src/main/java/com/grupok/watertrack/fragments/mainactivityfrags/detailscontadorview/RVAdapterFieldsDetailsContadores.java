package com.grupok.watertrack.fragments.mainactivityfrags.detailscontadorview;

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
import com.grupok.watertrack.scripts.apiCRUD.APIMethods;

import java.util.ArrayList;
import java.util.List;

public class RVAdapterFieldsDetailsContadores extends RecyclerView.Adapter<RVAdapterFieldsDetailsContadores.ViewHolder>{

    private final Context context;
    private List<RVAdapterFieldsDetailsContadores.ShownFields> fieldsList;
    private EnterpriseEntity selectedEnterprise;
    private MeterTypeEntity selectedType;
    private UserInfosEntity selectedUser;

    private ContadorItemClick callback;

    public interface ContadorItemClick{
        void onBackupsItemClick(MeterEntity contador);
    }

    public RVAdapterFieldsDetailsContadores(Context context, MeterEntity leitura, MeterTypeEntity selectedType, UserInfosEntity selectedUser, EnterpriseEntity selectedEnterprise) {
        this.context = context;
        this.selectedUser = selectedUser;
        this.selectedType = selectedType;
        this.selectedEnterprise = selectedEnterprise;
        fieldsList = new ArrayList<>();
        atualizarCampos(leitura);
    }

    public void atualizarCampos(MeterEntity leitura) {
        fieldsList.clear();
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_Name), leitura.nome));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_Address), leitura.address));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_Username), selectedUser.username));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_Description), selectedType.description));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_EnterpriseName), selectedEnterprise.name));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_Classe), leitura.classe));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_InstalationDate), leitura.instalationDate));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_ShutdownDate), leitura.shutdownDate));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_MaxCapacity), leitura.maxCapacity));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_MeasureUnity), leitura.measureUnity));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields(context.getString(R.string.mainActivity_DetailsContadorFrag_RV_fieldsList_SupportedTemperature), leitura.supportedTemperature));

        String status = "N/A";
        switch (leitura.state){
            case 0:
                status = "Desativado";
                break;
            case 1:
                status = "Ativo";
                break;
            case 2:
                status = "Com Problema";
                break;
        }
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields("Estado do Contador", status));
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
