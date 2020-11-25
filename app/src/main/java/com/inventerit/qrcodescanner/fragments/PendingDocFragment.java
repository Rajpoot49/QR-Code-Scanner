package com.inventerit.qrcodescanner.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventerit.qrcodescanner.Model.PendingDocModel;
import com.inventerit.qrcodescanner.R;
import com.inventerit.qrcodescanner.activities.LoginActivity;
import com.inventerit.qrcodescanner.adapter.PendingDocAdapter;

import java.util.ArrayList;
import java.util.List;

public class PendingDocFragment extends Fragment {

//    public static ProgressBar progressBar;
    private PendingDocAdapter adapter;
    private List<PendingDocModel> models = new ArrayList<>();
    private RecyclerView recyclerView;
    public static List<PendingDocModel> modelList = new ArrayList<>();
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private View root;
    public static ProgressDialog pDialog;

    protected void initpDialog() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }
//    public static PendingDocFragment newInstance(){
//        return new PendingDocFragment();
//    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_pendingdoc, container, false);

        initpDialog();

        recyclerView = root.findViewById(R.id.precyclerView);


        //bind recycler view
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        getPendingDoc();

        return root;
    }

    private void getPendingDoc(){
        showpDialog();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference5 = FirebaseDatabase.getInstance().getReference().child("Pending");

        reference5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(models.size()>0){
                    models.clear();
                }
                if(modelList.size()>0){
                    modelList.clear();
                }

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    models.add(new PendingDocModel(snapshot.child("name").getValue().toString(),snapshot.child("email").getValue().toString(),
                            snapshot.child("pending").getValue().toString(),snapshot.getKey().toString(),snapshot.child("authority").getValue().toString(),
                            snapshot.child("requestId").getValue().toString(),snapshot.child("result").getValue().toString(),
                            snapshot.child("authorityId").getValue().toString(),snapshot.child("date").getValue().toString()));
                }
                boolean isExist = false;
                for(int i=0;i<models.size();i++){
                    if(models.get(i).getAuthority().equals(user.getUid())){
                        isExist = true;
                        modelList.add(new PendingDocModel(models.get(i).getName(),models.get(i).getEmail(),models.get(i).getPendingState(),models.get(i).getId(),
                                models.get(i).getAuthority(),models.get(i).getRequestId(),models.get(i).getResult(),models.get(i).getAuthorityId(),models.get(i).getDate()));
                    }
                }
                if(isExist){
                    recyclerView.setVisibility(View.VISIBLE);
                }else{
                    TextView pendingtv = root.findViewById(R.id.pendingtv);
                    pendingtv.setVisibility(View.VISIBLE);
                }

                hidepDialog();
                adapter = new PendingDocAdapter(getActivity(),modelList);
//                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}