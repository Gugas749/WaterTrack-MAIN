package com.grupok.watertrack.fragments.mainactivityfrags.mainview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;

import android.util.Log;


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

import java.io.Serializable;
import java.util.List;

public class RVAdapterMainAcMainView extends RecyclerView.Adapter<RVAdapterMainAcMainView.MyViewHolder> implements Serializable{
    private Context context;
    private List<MeterEntity> contadoresEntityList;
    private MetersItemClick listenner;
    private int classeUser;
    private MainActivity parent;

    private int selectedItem = RecyclerView.NO_POSITION;

    public interface MetersItemClick{
        void onMetersItemClick(MeterEntity contador);
    }

    public RVAdapterMainAcMainView(Context context, List<MeterEntity> contadoresEntityList, int classeUser, MainActivity parent) {
        this.context = context;
        this.contadoresEntityList = contadoresEntityList;
        this.classeUser = classeUser;
        this.parent = parent;
    }
    public void updateData(List<MeterEntity> contadoresEntityList){
        this.contadoresEntityList = contadoresEntityList;
        notifyDataSetChanged();
    }
    public void setItemClickListenner(MetersItemClick listenner){
        this.listenner = listenner;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_contadores_mainac, parent, false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final MeterEntity contadorSelected = contadoresEntityList.get(position);
        if(contadorSelected != null){
            if(classeUser == 1){
                holder.textViewNameOrAddress.setText(contadorSelected.nome);
                holder.textViewAddressOrId.setText(contadorSelected.address);
            }else{
                holder.textViewNameOrAddress.setText(contadorSelected.address);
                holder.textViewAddressOrId.setText(String.valueOf(contadorSelected.id));
            }

            int color = 0;
            TypedValue typedValue = new TypedValue();
            holder.stateIcon.setImageResource(R.drawable.radios_button_icon_24);
            switch (contadorSelected.state){
                case 0: // Desativo
                    context.getTheme().resolveAttribute(R.attr.colorError, typedValue, true);
                    color = typedValue.data;
                    break;
                case 1: //Ativo
                    context.getTheme().resolveAttribute(R.attr.colorSuccess, typedValue, true);
                    color = typedValue.data;
                    break;
                case 2: //Com problema
                    context.getTheme().resolveAttribute(R.attr.colorWarning, typedValue, true);
                    color = typedValue.data;
                    holder.stateIcon.setImageResource(R.drawable.warning_24);
                    break;
                default:
                    context.getTheme().resolveAttribute(R.attr.colorWarning, typedValue, true);
                    color = typedValue.data;
                    holder.stateIcon.setImageResource(R.drawable.warning_24);
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
                listenner.onMetersItemClick(contadorSelected);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contadoresEntityList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewAddressOrId, textViewNameOrAddress;
        CardView cardView;
        ImageView stateIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNameOrAddress = itemView.findViewById(R.id.textView_NameOrAddress_rvRowContadores_MainAc);
            cardView = itemView.findViewById(R.id.cardView_Holder_rvRowContadores_MainAc);
            textViewAddressOrId = itemView.findViewById(R.id.textView_AddressOrId_rvRowContadores_MainAc);
            stateIcon = itemView.findViewById(R.id.imageView_iconHolder2_rvRowContadores_MainAc);
        }
    }
}
