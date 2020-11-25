package com.inventerit.qrcodescanner.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inventerit.qrcodescanner.Model.Codes;
import com.inventerit.qrcodescanner.R;

import java.util.List;

public class CodesAdapter extends RecyclerView.Adapter<CodesAdapter.MyViewHolder> {

    private List<Codes> codesList;
    private Context context;


    public CodesAdapter(List<Codes> codesList,
                        Context context){
        this.context = context;
        this.codesList = codesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.code_item_recyclerview, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Codes codes = codesList.get(position);

        holder.codetv.setText(codes.getCode());
        holder.codeType.setText("Type: "+codes.getType());
        holder.codeVal.setText("Val: "+codes.getVal());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure, you want to delete it")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCodes(codes.getId());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
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
        public TextView codetv, delete, codeType, codeVal;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            codetv = itemView.findViewById(R.id.codename);
            codeType = itemView.findViewById(R.id.codetype);
            codeVal = itemView.findViewById(R.id.codeval);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    private void deleteCodes(String id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("CodesList").child(id);
        reference.removeValue();

    }

}
