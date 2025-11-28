package com.grupok.watertrack.fragments.mainactivityfrags.detailscontadorview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grupok.watertrack.R;
import com.grupok.watertrack.database.entities.ContadorEntity;
import com.grupok.watertrack.database.entities.LogsContadoresEntity;
import com.grupok.watertrack.fragments.mainactivityfrags.readingscontadorview.RVAdapterFieldsReadingsContadores;

import java.util.ArrayList;
import java.util.List;

public class RVAdapterFieldsDetailsContadores extends RecyclerView.Adapter<RVAdapterFieldsDetailsContadores.ViewHolder>{

    private final Context context;
    private List<RVAdapterFieldsDetailsContadores.ShownFields> fieldsList;

    private ContadorItemClick callback;

    public interface ContadorItemClick{
        void onBackupsItemClick(ContadorEntity contador);
    }

    public RVAdapterFieldsDetailsContadores(Context context, ContadorEntity leitura) {
        this.context = context;
        fieldsList = new ArrayList<>();
        atualizarCampos(leitura);
    }

    public void atualizarCampos(ContadorEntity leitura) {
        fieldsList.clear();
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields("Nome do Contador", leitura.nome));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields("Morada do Contador", leitura.morada));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields("IDMORADOR",String.valueOf(leitura.idMorador)));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields("TIPO",String.valueOf (leitura.idTipo)));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields("EMPRESA",String.valueOf (leitura.idEmpresa)));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields("CLASSE", leitura.classe));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields("Data de Instalação", leitura.dataInstalacao));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields("Capacidade Maxima", leitura.capacidadeMax));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields("Unidade de Medida", leitura.unidadeMedida));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields("Temperatura Suportada", leitura.temperaturaSuportada));
        fieldsList.add(new RVAdapterFieldsDetailsContadores.ShownFields("Estado do Contador", String.valueOf (leitura.estado)));
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
