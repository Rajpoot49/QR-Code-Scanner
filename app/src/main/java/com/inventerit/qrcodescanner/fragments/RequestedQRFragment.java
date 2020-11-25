package com.inventerit.qrcodescanner.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventerit.qrcodescanner.Model.Codes;
import com.inventerit.qrcodescanner.Model.HistoryModel;
import com.inventerit.qrcodescanner.Model.PendingDocModel;
import com.inventerit.qrcodescanner.R;
import com.inventerit.qrcodescanner.adapter.AuthorityAdapter;
import com.inventerit.qrcodescanner.adapter.PendingDocAdapter;
import com.inventerit.qrcodescanner.adapter.RequestedQRAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestedQRFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestedQRFragment extends Fragment {

    private RequestedQRAdapter adapter;
    private RecyclerView recyclerView;
    public static List<PendingDocModel> modelList = new ArrayList<>();
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private View root;
    public static ProgressDialog pDialog;
    private TextView noDatatv;

    public RequestedQRFragment(){

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getCodesList();
    }

    // TODO: Rename and change types and number of parameters
    public static RequestedQRFragment newInstance() {
        return new RequestedQRFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_requested_qr, container, false);

        initpDialog();
        noDatatv = root.findViewById(R.id.noData);
        showpDialog();

        recyclerView = root.findViewById(R.id.req_recyclerView);


        //bind recycler view
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        getRequestedQRCodeList();

        return root;
    }

    private void getRequestedQRCodeList() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Pending");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(modelList.size()>0){
                    modelList.clear();
                }

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if(user.getUid().equals(snapshot.child("requestId").getValue().toString())){
                        modelList.add(new PendingDocModel(snapshot.child("name").getValue().toString(),snapshot.child("email").getValue().toString(),
                                snapshot.child("pending").getValue().toString(),snapshot.getKey().toString(),snapshot.child("authority").getValue().toString(),
                                snapshot.child("requestId").getValue().toString(),snapshot.child("result").getValue().toString(), snapshot.child("authorityId").getValue().toString(),
                                snapshot.child("date").getValue().toString()));
                    }
                }

                adapter = new RequestedQRAdapter(getActivity(),modelList,codesList);
                recyclerView.setAdapter(adapter);
                hidepDialog();

                if(modelList.size()==0){
                    noDatatv.setVisibility(View.VISIBLE);
                }else{
                    noDatatv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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
    private List<Codes> codesList;
    private void getCodesList(){

        codesList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("CodesList");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(codesList.size()>0){
                    codesList.clear();
                }

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    codesList.add(new Codes(dataSnapshot1.child("code").getValue().toString(), dataSnapshot1.getKey().toString(),
                            dataSnapshot1.child("type").getValue().toString(),dataSnapshot1.child("val").getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}