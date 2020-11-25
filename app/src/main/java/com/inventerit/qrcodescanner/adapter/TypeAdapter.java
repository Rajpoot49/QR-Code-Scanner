package com.inventerit.qrcodescanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inventerit.qrcodescanner.Model.Codes;
import com.inventerit.qrcodescanner.R;
import com.inventerit.qrcodescanner.fragments.TypeFragment;

import java.util.List;


public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.MyViewHolder> {

    private Context context;
    private List<Codes> codesList;

    public TypeAdapter(Context context, List<Codes> codesList){
        this.context = context;
        this.codesList = codesList;
    }

    @NonNull
    @Override
    public TypeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.type_recyclerview, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeAdapter.MyViewHolder holder, int position) {
        Codes codes = codesList.get(position);

        holder.codetv.setText("QR Code: "+codes.getCode());
        holder.codeType.setText(context.getString(R.string.type)+":"+codes.getType());
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeFragment.addCodeCV.setVisibility(View.VISIBLE);
                TypeFragment.id = codes.getId();
                TypeFragment.codeListEt.setText(codes.getCode());
                TypeFragment.etType.setText(codes.getType());
            }
        });

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return codesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView codetv, update, codeType;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            codetv = itemView.findViewById(R.id.codename);
            codeType = itemView.findViewById(R.id.codetype);
            update = itemView.findViewById(R.id.update);
        }
    }
}
