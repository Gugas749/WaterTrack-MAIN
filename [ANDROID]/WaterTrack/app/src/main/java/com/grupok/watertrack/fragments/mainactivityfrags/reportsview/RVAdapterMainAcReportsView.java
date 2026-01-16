package com.grupok.watertrack.fragments.mainactivityfrags.reportsview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.grupok.watertrack.R;
import com.grupok.watertrack.activitys.MainActivity;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.ReportsEntity;

import java.io.Serializable;
import java.util.List;

public class RVAdapterMainAcReportsView extends RecyclerView.Adapter<RVAdapterMainAcReportsView.MyViewHolder> implements Serializable{
    private Context context;
    private List<ReportsEntity> reportsEntityList;
    private List<MeterEntity> meterEntityList;
    private ReportsItemClick listenner;
    private MainActivity parent;

    private int selectedItem = RecyclerView.NO_POSITION;

    public interface ReportsItemClick{
        void onReportsItemClick(ReportsEntity report);
    }

    public RVAdapterMainAcReportsView(Context context, List<ReportsEntity> reportsEntityList, MainActivity parent, List<MeterEntity> meterEntityList) {
        this.context = context;
        this.reportsEntityList = reportsEntityList;
        this.meterEntityList = meterEntityList;
        this.parent = parent;
    }
    public void updateData(List<ReportsEntity> contadoresEntityList){
        this.reportsEntityList = contadoresEntityList;
        notifyDataSetChanged();
    }
    public void setItemClickListenner(ReportsItemClick listenner){
        this.listenner = listenner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_reports_reportsview_mainac, parent, false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ReportsEntity reportSelected = reportsEntityList.get(position);
        if(reportSelected != null){
            MeterEntity selectedMeter = null;
            for (MeterEntity meter : meterEntityList) {
                if(meter.id == reportSelected.meterID){
                    selectedMeter = meter;
                }
            }

            holder.textViewLabel1.setText(reportSelected.description.toString());
            if(selectedMeter != null){
                holder.textViewLabel2.setText(selectedMeter.address);
            }else{
                holder.textViewLabel2.setText("");
            }

            int color = 0;
            TypedValue typedValue = new TypedValue();
            holder.stateIcon.setImageResource(R.drawable.radios_button_icon_24);
            switch (reportSelected.problemState){
                case 0: // RESOLVIDO
                    context.getTheme().resolveAttribute(R.attr.colorSuccess, typedValue, true);
                    color = typedValue.data;
                    break;
                case 1: //EM PROGRESSO
                    context.getTheme().resolveAttribute(R.attr.colorWarning, typedValue, true);
                    color = typedValue.data;
                    break;
                default:
                case 2: // POR RESOLVER
                    context.getTheme().resolveAttribute(R.attr.colorError, typedValue, true);
                    color = typedValue.data;
                    break;
            }
            holder.stateIcon.setImageTintList(ColorStateList.valueOf(color));

            int lastPosition = getItemCount() - 1;
            if (position == lastPosition){
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
                float margin = context.getResources().getDimension(com.intuit.sdp.R.dimen._70sdp);
                params.bottomMargin = (int)margin;
                holder.cardView.setLayoutParams(params);
            }else{
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
                float margin = context.getResources().getDimension(com.intuit.sdp.R.dimen._4sdp);
                params.bottomMargin = (int)margin;
                holder.cardView.setLayoutParams(params);
            }
        }

        holder.cardView.setOnClickListener(v -> {
            notifyItemChanged(selectedItem);
            selectedItem = holder.getBindingAdapterPosition();
            notifyItemChanged(selectedItem);

            if (listenner != null) {
                listenner.onReportsItemClick(reportSelected);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportsEntityList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewLabel1, textViewLabel2;
        CardView cardView;
        ImageView stateIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLabel1 = itemView.findViewById(R.id.textView_Label1_rvRowReports_ReportsView_MainAc);
            cardView = itemView.findViewById(R.id.cardView_Holder_rvRowReports_ReportsView_MainAc);
            textViewLabel2 = itemView.findViewById(R.id.textView_Label2_rvRowReports_ReportsView_MainAc);
            stateIcon = itemView.findViewById(R.id.imageView_statusIcon_rvRowReports_ReportsView_MainAc);
        }
    }
}
