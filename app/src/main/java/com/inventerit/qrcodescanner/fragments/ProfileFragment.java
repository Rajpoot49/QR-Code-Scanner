package com.inventerit.qrcodescanner.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventerit.qrcodescanner.Model.HistoryModel;
import com.inventerit.qrcodescanner.R;
import com.inventerit.qrcodescanner.adapter.HistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

//    @BindView(R.id.pEmail)
    TextView email;
//    @BindView(R.id.pFullname)
    TextView fullname;
//    @BindView(R.id.progressBar)
//    ProgressBar progressBar;
//    @BindView(R.id.pHistorytv)
    TextView pHistorytv;
//    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private HistoryAdapter adapter;
    private List<HistoryModel> models = new ArrayList<>();
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
    public static ProfileFragment newInstance(){
        return new ProfileFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

//        ButterKnife.bind(getActivity(),root);

        email = root.findViewById(R.id.pEmail);
        fullname = root.findViewById(R.id.pFullname);
        pHistorytv = root.findViewById(R.id.pHistorytv);
        recyclerView = root.findViewById(R.id.recyclerView);

        initpDialog();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //bind recycler view
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        showpDialog();
        //Load current user profile
        LoadUserProfile(firebaseUser.getUid());

        return root;
    }
    private void LoadUserProfile(String userid) {
        //access the current user database
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get fullname and email of the current user
                fullname.setText(dataSnapshot.child("username").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());

                //clear all the data
                models.clear();
                //fetch the scanned document history
                reference.child("scanneddoc").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                            models.add(new HistoryModel(dataSnapshot1.child("time").getValue().toString(),dataSnapshot1.child("type").getValue().toString(),
                                    dataSnapshot1.child("result").getValue().toString(),dataSnapshot1.getKey().toString(),
                                    dataSnapshot1.child("val").getValue().toString(), dataSnapshot1.child("date").getValue().toString(),
                                    dataSnapshot1.child("secsum").getValue().toString(),dataSnapshot1.child("nr").getValue().toString()));

                        }
                        //set adapter

                        adapter = new HistoryAdapter(getActivity(),models);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        hidepDialog();
                        Toast.makeText(getActivity(),"Error Occurred",Toast.LENGTH_LONG).show();
                    }
                });

                hidepDialog();
                fullname.setVisibility(View.VISIBLE);
                email.setVisibility(View.VISIBLE);
                pHistorytv.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hidepDialog();
                Toast.makeText(getActivity(),"Error Occurred",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        getSupportActionBar().setTitle("Profile");
    }

}