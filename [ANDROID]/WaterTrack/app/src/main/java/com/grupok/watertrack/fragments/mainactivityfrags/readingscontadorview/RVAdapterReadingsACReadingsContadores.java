package com.grupok.watertrack.fragments.mainactivityfrags.readingscontadorview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grupok.watertrack.R;
import com.grupok.watertrack.database.entities.MeterReadingEntity;

import java.util.List;

public class RVAdapterReadingsACReadingsContadores
        extends RecyclerView.Adapter<RVAdapterReadingsACReadingsContadores.ViewHolder> {

    private final Context context;
    private List<MeterReadingEntity> readingsEntities;
    private int selectedPosition = -1; // Nenhuma selecionada por defeito
    private OnSelectionChangedListener listener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(MeterReadingEntity selectedReading);
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener){
        this.listener = listener;
    }

    public RVAdapterReadingsACReadingsContadores(Context context, List<MeterReadingEntity> readingsEntities) {
        this.context = context;
        this.readingsEntities = readingsEntities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.rv_row_readings_readingscontadores, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MeterReadingEntity leitura = readingsEntities.get(position);
        holder.data.setText(leitura.date);

        holder.radioButton.setChecked(position == selectedPosition);

        holder.radioButton.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
            if (listener != null) {
                listener.onSelectionChanged(readingsEntities.get(selectedPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return readingsEntities == null ? 0 : readingsEntities.size();
    }

    public MeterReadingEntity getSelectedReading() {
        if (selectedPosition != -1) return readingsEntities.get(selectedPosition);
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;
        TextView data;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radioData_rvRowReadings_ReadingsContadores);
            data = itemView.findViewById(R.id.textView_Data_rvRowReadings_ReadingsContadores);
        }
    }
}
