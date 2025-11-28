package com.grupok.watertrack.fragments.mainactivityfrags.readingscontadorview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grupok.watertrack.R;
import com.grupok.watertrack.database.entities.LogsContadoresEntity;

import java.util.ArrayList;
import java.util.List;

public class RVAdapterFieldsReadingsContadores extends RecyclerView.Adapter<RVAdapterFieldsReadingsContadores.ViewHolder> {

    private final Context context;
    private List<ShownFields> fieldsList;

    public RVAdapterFieldsReadingsContadores(Context context, LogsContadoresEntity leitura) {
        this.context = context;
        fieldsList = new ArrayList<>();
        atualizarCampos(leitura);
    }

    public void atualizarCampos(LogsContadoresEntity leitura) {
        fieldsList.clear();
        fieldsList.add(new ShownFields("Referencia do Contador", String.valueOf(leitura.idContador)));
        fieldsList.add(new ShownFields("Leitura", leitura.leitura));
        fieldsList.add(new ShownFields("Consumo Acumulado", leitura.consumoAcumulado));
        fieldsList.add(new ShownFields("Data", leitura.data));
        fieldsList.add(new ShownFields("Pressão da Água", leitura.pressaoAgua));
        fieldsList.add(new ShownFields("Observações", leitura.descricao));
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
