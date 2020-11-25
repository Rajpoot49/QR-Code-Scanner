package com.inventerit.qrcodescanner.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventerit.qrcodescanner.Model.Codes;
import com.inventerit.qrcodescanner.Model.HistoryModel;
import com.inventerit.qrcodescanner.Model.UserSumTableModel;
import com.inventerit.qrcodescanner.R;
import com.inventerit.qrcodescanner.activities.ScanCodeActivity;
import com.inventerit.qrcodescanner.adapter.HistoryAdapter;
import com.inventerit.qrcodescanner.adapter.TypeAdapter;
import com.inventerit.qrcodescanner.adapter.UserSumTableAdapter;
import com.inventerit.qrcodescanner.adapter.ValAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ScanFragment extends Fragment {

    public static ScanFragment newInstance(){
        return new ScanFragment();
    }
    private static final int CAMERA_PERMISSION_CODE = 100;
    private Button qrbtn;
    public static TextView result;
    public static TextView resulttv;
    public static ImageView imageView;
    private FirebaseAuth auth;
    private TextView userSumtv, secSumtv;
    public static TextView typetv, valtv, timetv;
    int userSum =0;
    int secSum = 0;
//    private ProgressBar progressBar;
    private LinearLayout userSumLayout, secSumLayout;

    private List<UserSumTableModel> userSumTableModels = new ArrayList<>();

    private List<HistoryModel> historyModel = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserSumTableAdapter adapter;
    private TextView userSumTitle, secSumTitle;

    public void clearText(){
        typetv.setText("");
        valtv.setText("");
        timetv.setText("");
        result.setText("");
        imageView.setVisibility(View.GONE);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        typetv = view.findViewById(R.id.tvtype);
        valtv = view.findViewById(R.id.valtv);
        timetv = view.findViewById(R.id.timetv);
        qrbtn = view.findViewById(R.id.qrbtn);
        result = view.findViewById(R.id.codetv);
        resulttv = view.findViewById(R.id.resulttv);
        imageView = view.findViewById(R.id.img);
        auth = FirebaseAuth.getInstance();
        userSumtv = view.findViewById(R.id.typetv);
        userSumLayout = view.findViewById(R.id.usersumlayout);
        recyclerView = view.findViewById(R.id.tableRecyclerView);
        secSumLayout = view.findViewById(R.id.secsumlayout);
        secSumtv = view.findViewById(R.id.secsumtv);
        userSumTitle = view.findViewById(R.id.userSumtv);
        secSumTitle = view.findViewById(R.id.secSumtv);

        initpDialog();

        qrbtn.setText(getString(R.string.scanqr_barcode));
        resulttv.setText(getString(R.string.result));
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        secSumTitle.setText(getString(R.string.secsum));
        userSumTitle.setText(getString(R.string.usersum));
        qrbtn.setOnClickListener(v -> checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE));
        LoadUserProfile(auth.getCurrentUser().getUid());

        return view;
    }
    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(getActivity(), permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { permission },
                    requestCode);

        }else{
            startActivity(new Intent(getActivity(), ScanCodeActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(getActivity(), ScanCodeActivity.class));
            }
            else {
                Toast.makeText(getActivity(),"Please Allow Permission to Scan", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getCodesList();
    }

    private void LoadUserProfile(String userid) {
        showpDialog();
        //access the current user database
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //fetch the scanned document history
                reference.child("scanneddoc").orderByChild("type").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(historyModel.size()>0){
                            historyModel.clear();
                            userSum = 0;
                            secSum = 0;
                        }
                        if(userSumTableModels.size()>0){
                            userSumTableModels.clear();
                        }
                        for(int i=0;i<codesList.size();i++){
                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                if(dataSnapshot1.child("result").getValue().toString().equals(codesList.get(i).getCode())){
                                    historyModel.add(new HistoryModel(dataSnapshot1.child("time").getValue().toString(),codesList.get(i).getType(),
                                            dataSnapshot1.child("result").getValue().toString(),dataSnapshot1.getKey().toString(),
                                            codesList.get(i).getVal(), dataSnapshot1.child("date").getValue().toString(),
                                            dataSnapshot1.child("secsum").getValue().toString(),dataSnapshot1.child("nr").getValue().toString()));
                                }
                            }
                        }

                        Log.i("history",historyModel.toString()+historyModel.size());

                        Date date = new Date(System.currentTimeMillis());

                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());

                        for(int i=0;i<historyModel.size();i++){
                            userSum += Integer.parseInt(historyModel.get(i).getVal());
                            secSum += Integer.parseInt(historyModel.get(i).getSecSum());
                            adapter = new UserSumTableAdapter(getActivity(),historyModel);
                            recyclerView.setAdapter(adapter);
                        }
                        userSumtv.setText(userSum+"");
                        secSumtv.setText(secSum+"");
                        hidepDialog();
                        userSumLayout.setVisibility(View.VISIBLE);
                        secSumLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(),"Error Occurred",Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Error Occurred",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        clearText();
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