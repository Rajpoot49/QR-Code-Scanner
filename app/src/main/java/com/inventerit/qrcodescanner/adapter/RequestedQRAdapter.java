package com.inventerit.qrcodescanner.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inventerit.qrcodescanner.Model.Codes;
import com.inventerit.qrcodescanner.Model.HistoryModel;
import com.inventerit.qrcodescanner.Model.PendingDocModel;
import com.inventerit.qrcodescanner.R;

import java.util.List;

public class RequestedQRAdapter extends RecyclerView.Adapter<RequestedQRAdapter.ViewHolder> {

    private Context context;
    private List<PendingDocModel> requestedQRList;
    private List<Codes> codesList;

    public RequestedQRAdapter(Context context, List<PendingDocModel> requetedQRList, List<Codes> codesList){
        this.context = context;
        this.requestedQRList = requetedQRList;
        this.codesList = codesList;
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
        PendingDocModel requestedList = requestedQRList.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.qrCode.setText(Html.fromHtml("<strong>Code:</strong> "+requestedList.getResult(),Html.FROM_HTML_MODE_COMPACT));
            holder.reqTime.setText("Date: "+requestedList.getDate());
            for(int i=0;i<codesList.size();i++) {
                if(codesList.get(i).getCode().equals(requestedList.getResult()))
                    holder.reqType.setText(context.getString(R.string.type)+":"+codesList.get(i).getType());
            }
        } else {
            holder.qrCode.setText(Html.fromHtml("<strong>Code:</strong> "+requestedList.getResult()));
            holder.reqTime.setText("Date: "+requestedList.getDate());
            for(int i=0;i<codesList.size();i++) {
                if(codesList.get(i).getCode().equals(requestedList.getResult()))
                    holder.reqType.setText(context.getString(R.string.type)+":"+codesList.get(i).getType());
            }
        }
    }


    @Override
    public int getItemCount() {
        return requestedQRList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView qrCode, reqTime, reqType;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reqTime = itemView.findViewById(R.id.rw_time_tv);
            qrCode = itemView.findViewById(R.id.scanhistory);
            reqType = itemView.findViewById(R.id.rw_typetv);
        }
    }
}
