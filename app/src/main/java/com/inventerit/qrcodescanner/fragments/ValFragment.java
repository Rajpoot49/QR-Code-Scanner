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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventerit.qrcodescanner.Model.Codes;
import com.inventerit.qrcodescanner.R;
import com.inventerit.qrcodescanner.adapter.TypeAdapter;
import com.inventerit.qrcodescanner.adapter.ValAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ValFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ValFragment extends Fragment {

    //    @BindView(R.id.progressBar)
//    ProgressBar progressBar;
    //    @BindView(R.id.saveCodeBtn)
    Button saveCodeBtn;
    //    @BindView(R.id.codelistet)
    public static TextView codeListEt;
    public static MaterialEditText etVal;
    //    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    //    @BindView(R.id.cardview)
    public static CardView addCodeCV;
    public static String id = "";
    String val="", code="";

    private ValAdapter adapter;
    private List<Codes> codesList = new ArrayList<>();

    private FirebaseAuth auth;
    private DatabaseReference reference;

    public static ValFragment newInstance() {
        return new ValFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_val, container, false);

        etVal = view.findViewById(R.id.etType);
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
            val = etVal.getText().toString().trim();

            if(!val.isEmpty()){
                isValExist(val);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(valArrayList.contains("true")){
                            updateVal(id,code,val);
                        }else{
                            Toast.makeText(getActivity(), "Value already exist.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },4000);
            }else Toast.makeText(getActivity(),"Please enter code",Toast.LENGTH_LONG).show();
        });

    }

    private void updateVal(String id, String code, String val) {

        showpDialog();

        reference = FirebaseDatabase.getInstance().getReference().child("CodesList").child(id);
        reference.child("val").setValue(val);
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
                adapter = new ValAdapter(getActivity(),codesList);
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

    private List<String> valArrayList;

    private void isValExist(String valstr) {
        valArrayList = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String type3= dataSnapshot1.child("code").getValue().toString();
                    if(dataSnapshot1.child("val").getValue().toString().equals(valstr)){
                        valArrayList.add("true");
                    }else{
                        valArrayList.add("false");
                    }
                }

                Log.i("CodesArrayList",valArrayList.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }
}