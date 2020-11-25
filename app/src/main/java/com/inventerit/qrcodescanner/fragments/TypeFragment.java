package com.inventerit.qrcodescanner.fragments;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventerit.qrcodescanner.Model.Codes;
import com.inventerit.qrcodescanner.R;
import com.inventerit.qrcodescanner.adapter.TypeAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TypeFragment extends Fragment {

    //    @BindView(R.id.progressBar)
//    ProgressBar progressBar;
    //    @BindView(R.id.saveCodeBtn)
    Button saveCodeBtn;
    //    @BindView(R.id.codelistet)
    public static TextView codeListEt;
    public static MaterialEditText etType;
    //    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    //    @BindView(R.id.cardview)
    public static CardView addCodeCV;
    public static String id = "";
    String type="", code="";

    private TypeAdapter adapter;
    private List<Codes> codesList = new ArrayList<>();

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private ArrayList<String> typeArrayList;

    public static TypeFragment newInstance() {
        return new TypeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_type, container, false);

        etType = view.findViewById(R.id.etType);
        addCodeCV = view.findViewById(R.id.cardview);
        saveCodeBtn = view.findViewById(R.id.saveCodeBtn);
        codeListEt = view.findViewById(R.id.codelistet);
        recyclerView = view.findViewById(R.id.qr_recyclerView);
        initpDialog();

        auth = FirebaseAuth.getInstance();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        getCodesList();

        ClickListeners();

        return view;
    }
    private void ClickListeners() {

        saveCodeBtn.setOnClickListener(view->{
            type = etType.getText().toString().trim();
            if(!type.isEmpty()){
                showpDialog();

                isTypeExist(type);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateType(id, code, type);
                    }
                },4000);

            }else Toast.makeText(getActivity(),"Please enter code",Toast.LENGTH_LONG).show();
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

    private void updateType(String id, String code, String type) {
        reference = FirebaseDatabase.getInstance().getReference().child("CodesList").child(id);
        reference.child("type").setValue(type);
        Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
        hidepDialog();
    }

    private void getCodesList(){
        showpDialog();


        reference = FirebaseDatabase.getInstance().getReference().child("CodesList");

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
                adapter = new TypeAdapter(getActivity(),codesList);
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
}