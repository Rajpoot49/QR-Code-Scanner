package com.inventerit.qrcodescanner.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.internal.DialogRedirect;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventerit.qrcodescanner.Model.PendingDocModel;
import com.inventerit.qrcodescanner.R;
import com.inventerit.qrcodescanner.fragments.PendingDocFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PendingDocAdapter extends RecyclerView.Adapter<PendingDocAdapter.ViewHolder> {

    private List<PendingDocModel> pendingDocModels;
    private Context context;
    private boolean isDontallow=false;
    private boolean isDelHis = false;


    public PendingDocAdapter(Context context, List<PendingDocModel> pendingDocModels){
        this.context = context;
        this.pendingDocModels = pendingDocModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_recyclerview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingDocModel model = pendingDocModels.get(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(model.getAuthority().equals(user.getUid())){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Pending").child(model.getId());
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Authority").child(model.getAuthorityId());

            holder.name.setText(model.getName());
            holder.email.setText(model.getEmail());
            holder.qrcode.setText(model.getResult());

            holder.dontallow.setOnClickListener(v -> {
                isDontallow = true;
                isDelHis = true;
                holder.allow.setClickable(false);
                holder.dontallow.setClickable(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure, you don't want to allow.")
                        .setPositiveButton("Ok", (dialogInterface, i1) -> {
                            reference.removeValue();
                            removePending(model.getResult(),position);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.allow.setClickable(true);
                                            holder.dontallow.setClickable(true);
                                        }
                                    },2000);
                dialogInterface.dismiss();
                        })
                .setNegativeButton("Cancel", (dialogInterface, i1) -> {
                    dialogInterface.cancel();
                }).show();
            });

            holder.allow.setOnClickListener(v -> {
                isDelHis = true;
                isDontallow = true;
                holder.allow.setClickable(false);
                holder.dontallow.setClickable(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Are you sure, you want to allow")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                HashMap<String, Object> map = new HashMap<>();
                                map.put("authority", model.getRequestId());
                                map.put("name",model.getName());
                                map.put("pending", "Don't Allow");
                                map.put("request", "");
                                map.put("result", model.getResult());
                                map.put("time", "");
                                map.put("type", "QR Code");
                                setHistory(model.getResult(),"1","1",model.getRequestId());

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int i = 0; i < pendingDocModels.size(); i++) {
                                            if (model.getResult().equals(pendingDocModels.get(i).getResult())) {
                                                DatabaseReference penReference = FirebaseDatabase.getInstance().getReference().child("Pending").child(pendingDocModels.get(i).getId());
                                                penReference.child("authority").setValue(model.getRequestId());
                                                reference.removeValue();
                                                holder.allow.setClickable(true);
                                                holder.dontallow.setClickable(true);
                                            }
                                        }
                                    }
                                },3000);


                                reference1.setValue(map);
                                removePending(model.getResult(),position);
                                removeHistory(model.getResult());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            });

        }
    }

    @Override
    public int getItemCount() {
        return pendingDocModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, email;
        public TextView dontallow,allow, qrcode;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            qrcode = itemView.findViewById(R.id.qrcode);
            name = itemView.findViewById(R.id.p_nametv);
            email = itemView.findViewById(R.id.p_email_tv);
            dontallow = itemView.findViewById(R.id.dallowtv);
            allow = itemView.findViewById(R.id.allowtv);
        }
    }


    public void removeHistory(String result1){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("scanneddoc");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if(snapshot.child("result").getValue().toString().equals(result1)){
                        if(isDelHis) {
                            reference.child(snapshot.getKey().toString()).removeValue();
                            isDelHis = false;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void removePending(String result, int i) {
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Users").child(pendingDocModels.get(i).getRequestId())
                .child("pendings");

        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if(snapshot.child("code").getValue().toString().equals(result)){
                        if(isDontallow) {
                            ref2.child(snapshot.getKey().toString()).removeValue();
                            isDontallow = false;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // set request user's history on allow button
    private void setHistory(String result, String type, String val, String id){

        if(isDelHis) {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss aa",
                    Locale.ENGLISH);

            SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

            DatabaseReference setHistoryRef = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("scanneddoc");

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("result", result);
            hashMap.put("time", dateFormat.format(date));
            hashMap.put("date", dateFormat1.format(date));
            hashMap.put("type", type);
            hashMap.put("val", val);
            hashMap.put("nr", "1");
            hashMap.put("authority", id);
            hashMap.put("request", "");
            hashMap.put("pending", "Don't Allow");
            hashMap.put("secsum", "0");

            setHistoryRef.push().setValue(hashMap);
        }
    }

}
