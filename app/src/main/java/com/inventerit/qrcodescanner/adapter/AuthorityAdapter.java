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
import com.inventerit.qrcodescanner.Model.AuthorityModel;
import com.inventerit.qrcodescanner.R;

import java.util.List;

public class AuthorityAdapter extends RecyclerView.Adapter<AuthorityAdapter.ViewHolder> {

    private Context context;
    private String name;
    private List<AuthorityModel> authorityModel;

    public AuthorityAdapter(Context context, List<AuthorityModel> authorityModels){
        this.context = context;
        this.authorityModel = authorityModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.authority_item_recyclerview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AuthorityModel model = authorityModel.get(position);

        holder.name.setText(model.getName());
        holder.code.setText(model.getResult());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                new AlertDialog.Builder(context)
                        .setTitle("Authority")
                        .setMessage("You are the last one that previously scanned that QR Code!!!")
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return authorityModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, code;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.a_nametv);
            code = itemView.findViewById(R.id.a_code);
        }
    }

}
