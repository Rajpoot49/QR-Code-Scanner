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
import com.inventerit.qrcodescanner.fragments.ValFragment;

import java.util.List;

public class ValAdapter extends RecyclerView.Adapter<ValAdapter.ViewHolder> {

    private Context context;
    private List<Codes> codesList;

    public ValAdapter(Context context, List<Codes> codesList){
        this.context = context;
        this.codesList = codesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.val_recyclerview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Codes codes = codesList.get(position);

        holder.codetv.setText("Type: "+codes.getType());
        holder.codeVal.setText("Val: "+codes.getVal());
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValFragment.addCodeCV.setVisibility(View.VISIBLE);
                ValFragment.id = codes.getId();
                ValFragment.codeListEt.setText(codes.getType());
                ValFragment.etVal.setText(codes.getVal());
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView codetv, update, codeVal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            codetv = itemView.findViewById(R.id.codetype);
            codeVal = itemView.findViewById(R.id.codeval);
            update = itemView.findViewById(R.id.update);
        }
    }
}
