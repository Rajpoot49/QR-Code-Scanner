package com.inventerit.qrcodescanner.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.inventerit.qrcodescanner.Model.Codes;
import com.inventerit.qrcodescanner.R;
import com.inventerit.qrcodescanner.adapter.CodesAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QrCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QrCodeFragment extends Fragment {

//    @BindView(R.id.progressBar)
//    ProgressBar progressBar;
//    @BindView(R.id.addCodeBtn)
    Button addCodeBtn;
//    @BindView(R.id.saveCodeBtn)
    Button saveCodeBtn;
//    @BindView(R.id.codelistet)
    MaterialEditText codeListEt, etType;
//    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
//    @BindView(R.id.cardview)
    CardView addCodeCV;
    private ProgressDialog pDialog;

    ArrayList<String> codesArrayList;
    ArrayList<String> typeArrayList;


    private CodesAdapter adapter;
    private List<Codes> codesList = new ArrayList<>();

    private String type, code;

    private FirebaseAuth auth;
    private DatabaseReference reference;

    public static QrCodeFragment newInstance() {
        return new QrCodeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qr_code, container, false);

//        ButterKnife.bind(getActivity(),view);

        initpDialog();

        etType = view.findViewById(R.id.etType);
        addCodeBtn = view.findViewById(R.id.addCodeBtn);
        addCodeCV = view.findViewById(R.id.cardview);
        saveCodeBtn = view.findViewById(R.id.saveCodeBtn);
        codeListEt = view.findViewById(R.id.codelistet);
        recyclerView = view.findViewById(R.id.qr_recyclerView);


        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("CodesList");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        getCodesList();

        ClickListeners();

        return view;
    }

    private void isQRExist(String str){
        codesArrayList = new ArrayList<>();
            DatabaseReference codeReference = reference;

            codeReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if(dataSnapshot.exists()){
//                        codesArrayList.add("true");
//                    }else{
//                        codesArrayList.add("false");
//                    }

                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        String type3= dataSnapshot1.child("code").getValue().toString();
                        if(dataSnapshot1.child("code").getValue().toString().equals(str)){
                            codesArrayList.add("true");
                        }else{
                            codesArrayList.add("false");
                        }
                    }
                    Log.i("CodesArrayList",codesArrayList.toString());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

    }

    private void ClickListeners() {
        addCodeBtn.setOnClickListener(view-> addCodeCV.setVisibility(View.VISIBLE) );

        saveCodeBtn.setOnClickListener(view->{
            code = codeListEt.getText().toString().trim();
            type = etType.getText().toString().trim();

            showpDialog();
            isQRExist(code);
            isTypeExist(type);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (code.isEmpty() || type.isEmpty()) {
                        Toast.makeText(getActivity(), "All fields are required...", Toast.LENGTH_LONG).show();
                        hidepDialog();
                    } else {
                        if (codesArrayList.contains("true")) {
                            Toast.makeText(getActivity(), "QR Code already exist...", Toast.LENGTH_LONG).show();
                            hidepDialog();
                        } else {
                            saveCodeToFireBase(code, type);
                        }

                        if (codesArrayList.size() > 0 && typeArrayList.size() > 0) {
                            codesArrayList.clear();
                            typeArrayList.clear();
                        }
                    }
                }
            },4000);

        });
    }

    private void isTypeExist(String type) {
        typeArrayList = new ArrayList<>();

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        String type3= dataSnapshot1.child("code").getValue().toString();
                        if(dataSnapshot1.child("type").getValue().toString().equals(type)){
                            typeArrayList.add("true");
                        }else{
                            typeArrayList.add("false");
                        }
                    }

                    Log.i("CodesArrayList",typeArrayList.toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

    }

    private void getCodesList(){
        showpDialog();

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
                adapter = new CodesAdapter(codesList,getActivity());
                recyclerView.setAdapter(adapter);

                hidepDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                hidepDialog();
                Toast.makeText(getActivity(),"Data cannot be fetched.",Toast.LENGTH_LONG).show();

            }
        });
    }

    private void saveCodeToFireBase(String code, String type) {

        String uuid = UUID.randomUUID().toString();
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("id",uuid);
        hashMap.put("code",code);
        hashMap.put("type",type);
        hashMap.put("val","0");


        reference.push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(),"Added",Toast.LENGTH_LONG).show();
                codeListEt.setText("");
                etType.setText("");
                hidepDialog();
            }
        });

    }
    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }
}