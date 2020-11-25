package com.inventerit.qrcodescanner.fragments;

import android.app.ProgressDialog;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventerit.qrcodescanner.Model.AuthorityModel;
import com.inventerit.qrcodescanner.R;
import com.inventerit.qrcodescanner.activities.LoginActivity;
import com.inventerit.qrcodescanner.adapter.AuthorityAdapter;
import com.inventerit.qrcodescanner.adapter.CodesAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AuthorityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AuthorityFragment extends Fragment {

    private List<AuthorityModel> modelList = new ArrayList<>();
    private AuthorityAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressDialog pDialog;

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

    public static AuthorityFragment newInstance() {
        AuthorityFragment fragment = new AuthorityFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authority, container, false);

        initpDialog();

        showpDialog();
        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        getAuthorityList();

        return view;
    }

    private void getAuthorityList(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Authority");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(modelList.size()>0){
                    modelList.clear();
                }

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    if(dataSnapshot1.child("authority").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                        modelList.add(new AuthorityModel(dataSnapshot1.child("authority").getValue().toString(),dataSnapshot1.child("type").getValue().toString(),
                                dataSnapshot1.getKey().toString(),dataSnapshot1.child("pending").getValue().toString(),dataSnapshot1.child("request").getValue().toString(),
                                dataSnapshot1.child("result").getValue().toString(),dataSnapshot1.child("time").getValue().toString(),
                                dataSnapshot1.child("name").getValue().toString()));
                    }
                }
                adapter = new AuthorityAdapter(getActivity(),modelList);
                recyclerView.setAdapter(adapter);
                hidepDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hidepDialog();
            }
        });
    }

}