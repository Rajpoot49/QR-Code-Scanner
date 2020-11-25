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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventerit.qrcodescanner.Model.User;
import com.inventerit.qrcodescanner.R;
import com.inventerit.qrcodescanner.activities.LoginActivity;
import com.inventerit.qrcodescanner.adapter.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

public class AllUsersFragment extends Fragment {

    private FirebaseAuth auth;
    private UsersAdapter adapter;
    private List<User> userList = new ArrayList<>();
//    @BindView(R.id.urecyclerView)
    RecyclerView recyclerView;
    DatabaseReference reference;
    private ProgressDialog pDialog;

//    @BindView(R.id.progressBar)
//    ProgressBar progressBar;

    public static AllUsersFragment newInstance() {
        return new AllUsersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_users, container, false);

//        ButterKnife.bind(getActivity(),view);
        initpDialog();
        recyclerView = view.findViewById(R.id.urecyclerView);
//        progressBar = view.findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        showpDialog();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.orderByChild("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    userList.add(new User(dataSnapshot1.child("uid").getValue().toString(),dataSnapshot1.child("username").getValue().toString(),dataSnapshot1.child("email").getValue().toString()
                            ,dataSnapshot1.child("id").getValue().toString(),dataSnapshot1.child("profile_pic").getValue().toString()));
                }
                adapter = new UsersAdapter(userList,getActivity());
                recyclerView.setAdapter(adapter);
                hidepDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hidepDialog();
                Toast.makeText(getActivity(),"Data cannot be fetched.",Toast.LENGTH_LONG).show();
            }
        });

        return view;
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
}