package com.inventerit.qrcodescanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inventerit.qrcodescanner.Model.HistoryModel;
import com.inventerit.qrcodescanner.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    List<HistoryModel> historyModels;
    Context context;

    public HistoryAdapter(Context context, List<HistoryModel> historyModel){
        this.context = context;
        this.historyModels = historyModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item_recyclerview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryModel model = historyModels.get(position);
        holder.typetv.setText(model.getType());
        holder.timetv.setText(model.getTime());
        holder.scanHistory.setText(model.getResult());

    }

    @Override
    public int getItemCount() {
        return historyModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView typetv, timetv, scanHistory;
        public CircleImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            scanHistory = itemView.findViewById(R.id.scanhistory);
            timetv = itemView.findViewById(R.id.rw_time_tv);
            typetv = itemView.findViewById(R.id.rw_typetv);
            imageView = itemView.findViewById(R.id.profileImage);
        }
    }
}
