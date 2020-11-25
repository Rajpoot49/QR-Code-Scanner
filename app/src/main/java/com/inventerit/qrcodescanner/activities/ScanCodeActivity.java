package com.inventerit.qrcodescanner.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.inventerit.qrcodescanner.Model.AuthorityModel;
import com.inventerit.qrcodescanner.Model.Codes;
import com.inventerit.qrcodescanner.Model.HistoryModel;
import com.inventerit.qrcodescanner.Model.PendingDocModel;
import com.inventerit.qrcodescanner.Model.PendingstwoModel;
import com.inventerit.qrcodescanner.adapter.HistoryAdapter;
import com.inventerit.qrcodescanner.adapter.UserSumTableAdapter;
import com.inventerit.qrcodescanner.fragments.ScanFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private String name;
    private String type, result, val;

    private boolean isExist;
    HashMap<String, Object> hashMap;
    List<Codes> codes = new ArrayList<>();
    List<AuthorityModel> authorityModels;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private boolean isScanned = false;
    private List<HistoryModel> model;

    private ArrayList<String> existHistoryArrayList;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseReference usernameref = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        usernameref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("username").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getCodesList();
        checkAuthority();
        getPendingDoc();
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("scanneddoc");

        LoadUserProfile(firebaseUser.getUid());

    }

    private List<PendingDocModel> pendingDocModels;
    private List<PendingstwoModel> pendingstwoModels;
    private ArrayList<String> pExist;

    private void getPendingDoc(){
        pendingDocModels = new ArrayList<>();
        pendingstwoModels = new ArrayList<>();
        pExist = new ArrayList<>();
        DatabaseReference reference5 = FirebaseDatabase.getInstance().getReference().child("Pending");

        reference5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    pendingDocModels.add(new PendingDocModel(snapshot.child("name").getValue().toString(),snapshot.child("email").getValue().toString(),
                            snapshot.child("pending").getValue().toString(),snapshot.getKey().toString(),snapshot.child("authority").getValue().toString(),
                            snapshot.child("requestId").getValue().toString(),snapshot.child("result").getValue().toString(), snapshot.child("authorityId").getValue().toString(),
                            snapshot.child("date").getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference rf2 = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("pendings");
        rf2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    pendingstwoModels.add(new PendingstwoModel(ds.getKey().toString(),ds.child("code").getValue().toString()));
                    pExist.add(ds.child("code").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    String username;

    private void addPending(int i, String rawResult){

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss aa",
                Locale.ENGLISH);

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference().child("Pending");

        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("authority", authorityModels.get(i).getAuthority());
        hashMap1.put("pending", "Don't Allow");
        hashMap1.put("email", firebaseUser.getEmail());
        hashMap1.put("name", username);
        hashMap1.put("requestId", firebaseUser.getUid());
        hashMap1.put("result", rawResult);
        hashMap1.put("authorityId", authorityModels.get(i).getId());
        hashMap1.put("date",dateFormat1.format(date));

        reference3.push().setValue(hashMap1);

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanCodeActivity.this);
        builder.setTitle("Pending Request")
                .setMessage("You can't scan, someone already scanned this document. Request for scanning this document is send to that user.")
                .setPositiveButton("Ok", (dialogInterface, i1) -> {
                    DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid())
                            .child("pendings");

                    HashMap<String, Object> hm = new HashMap<>();
                    hm.put("code", rawResult.toString());
                    hm.put("id", "1");

                    rf.push().setValue(hm);
                    onBackPressed();
                    scannerView.resumeCameraPreview(ScanCodeActivity.this);
                }).show();

    }

    @Override
    public void handleResult(Result rawResult) {
        existInAuthority(rawResult.toString());
        codeExistInHistory(rawResult.toString());

        boolean isPending = false;
        Date currentTime = Calendar.getInstance().getTime();
        if(authorityModels.size()!=0) {
            for (int i = 0; i < authorityModels.size(); i++) {
                if(pendingDocModels.size()!=0){
                    for (int j = 0; j < pendingDocModels.size(); j++) {
                        if(pendingstwoModels.size()!=0) {
                            for (int a = 0; a < pendingstwoModels.size(); a++) {
                                if (pExist.contains(rawResult.toString()) && (!isPending)) {

                                    isPending = true;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ScanCodeActivity.this);
                                    builder.setTitle("Pending Request")
                                            .setMessage("Request is in pending state")
                                            .setPositiveButton("Ok", (dialogInterface, i1) -> {

                                                onBackPressed();
                                                scannerView.resumeCameraPreview(ScanCodeActivity.this);

                                            }).show();

                                    break;
                                } else if ((!authorityModels.get(i).getAuthority().equals(firebaseUser.getUid()))
                                        && authorityModels.get(i).getResult().equals(rawResult.toString()) && (!isPending)
                                && (!pExist.contains(rawResult.toString()))) {

                                    isPending = true;
                                    addPending(i,rawResult.toString());

                                    break;
                                }else if((!isPending) && authorityModels.get(i).getAuthority().equals(firebaseUser.getUid())
                                        && authorityModels.get(i).getResult().equals(rawResult.toString())){
                                    isPending = true;
                                    scanQR(rawResult, currentTime);
                                    break;
                                }
                            }
                        }else if ((!authorityModels.get(i).getAuthority().equals(firebaseUser.getUid()))
                                && authorityModels.get(i).getResult().equals(rawResult.toString()) && (!isPending)) {

                                isPending = true;

                                addPending(i, rawResult.toString());
                                break;

                        }
                        else if((!isPending) && authorityModels.get(i).getAuthority().equals(firebaseUser.getUid())
                                && authorityModels.get(i).getResult().equals(rawResult.toString())){
                            isPending = true;
                            scanQR(rawResult, currentTime);
                        }
                    }
                }else if (!authorityModels.get(i).getAuthority().equals(firebaseUser.getUid()) && authorityModels.get(i).getResult().equals(rawResult.toString()) && (!isPending)) {
                    isPending = true;
                    addPending(i, rawResult.toString());
                    break;
                }
                else if((!isPending) && authorityModels.get(i).getAuthority().equals(firebaseUser.getUid())
                        && authorityModels.get(i).getResult().equals(rawResult.toString())){
                    isPending = true;
                    scanQR(rawResult, currentTime);
                }
            }
        }
        else{
            isPending = true;
            scanQR(rawResult, currentTime);
        }

                //Do something after 100ms
//            }
//        }, 1000);
//         if(!isPending){
//             isPending = true;
//             scanQR(rawResult,currentTime);
//        }
    }

    private void LoadUserProfile(String userid) {
        model = new ArrayList<>();
        //access the current user database
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("username").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("scanneddoc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    model.add(new HistoryModel(dataSnapshot1.child("time").getValue().toString(), dataSnapshot1.child("type").getValue().toString(),
                            dataSnapshot1.child("result").getValue().toString(), dataSnapshot1.getKey().toString(),
                            dataSnapshot1.child("val").getValue().toString(), dataSnapshot1.child("date").getValue().toString(),
                            dataSnapshot1.child("secsum").getValue().toString(),dataSnapshot1.child("nr").getValue().toString()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void codeExistInHistory(String rawResult){
        existHistoryArrayList = new ArrayList<>();

        for(int j=0;j<model.size();j++) {

            DatabaseReference existHistoryReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("scanneddoc");

            existHistoryReference.child(model.get(j).getId()).child("result");

            existHistoryReference.orderByChild("result").equalTo(rawResult).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        existHistoryArrayList.add("true");
                    }else{
                        existHistoryArrayList.add("false");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("history", existHistoryArrayList.contains("true") + "");
            }
        },3000);
    }

    private void scanQR(Result rawResult, Date currentTime) {

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss aa",
                Locale.ENGLISH);

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);

        for(int i=0;i<codes.size();i++) {

            if (rawResult.toString().equals(codes.get(i).getCode())) {


                result = rawResult.toString();

                for(int a=0;a<codes.size();a++){
                    if(codes.get(a).getCode().equals(result)){
                        type = codes.get(a).getType();
                        val = codes.get(a).getVal();
                    }
                }

                if(!isScanned) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            DatabaseReference historyReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).
                                    child("scanneddoc");
                            if (!existHistoryArrayList.contains("true")) {
                                isScanned = true;

                            hashMap = new HashMap<>();
                            hashMap.put("result", result);
                            hashMap.put("time", dateFormat.format(date));
                            hashMap.put("date",dateFormat1.format(date));
                            hashMap.put("type", type);
                            hashMap.put("val", val);
                            hashMap.put("nr","1");
                            hashMap.put("authority", firebaseUser.getUid());
                            hashMap.put("request", "");
                            hashMap.put("pending", "Don't Allow");
                            hashMap.put("secsum","0");


                            if (!historyReference.push().setValue(hashMap).isSuccessful()) {

                                onBackPressed();
                                scannerView.resumeCameraPreview(ScanCodeActivity.this);
                                Toast.makeText(getApplicationContext(), "Saved in history", Toast.LENGTH_LONG).show();
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (!isExist) {
                                            hashMap.put("name",name);
                                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Authority");
                                            reference2.push().setValue(hashMap);
                                        }
                                    }
                                }, 4000);
                            }
                        }else{
                                for(int i=0;i<model.size();i++){
                                    if(model.get(i).getResult().equals(rawResult.toString())){
                                        String nr = model.get(i).getNr();
                                        nr = String.valueOf(Integer.parseInt(String.valueOf(nr)) + 1);
                                        historyReference.child(model.get(i).getId()).child("nr").setValue(nr);
                                    }
                                }
                                Toast.makeText(getApplicationContext(),"You have this Qr code in your list already",Toast.LENGTH_LONG).show();
                            }
                            ScanFragment.resulttv.setText("Result:");
                            ScanFragment.result.setTextIsSelectable(true);
                            ScanFragment.result.setText("QR Code: " + result);
                            ScanFragment.typetv.setText("Type: " + type);
                            ScanFragment.timetv.setText("Time: " + currentTime.toString());
                            ScanFragment.valtv.setText("Val: " + val);
                            ScanFragment.imageView.setVisibility(View.VISIBLE);
                            onBackPressed();
                            scannerView.resumeCameraPreview(ScanCodeActivity.this);

                        }
                    }, 4000);
                }

            }
        }

    }

    private void existInAuthority(String rawResult){
        for (int j = 0; j < authorityModels.size(); j++) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Authority");
            reference.child(authorityModels.get(j).getId()).child("result").child(authorityModels.get(j).getResult());
            reference.orderByChild("result").equalTo(rawResult).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        isExist = true;
                    } else {
                        isExist = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();

    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    private void getCodesList(){

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("CodesList");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    codes.add(new Codes(dataSnapshot1.child("code").getValue().toString(), dataSnapshot1.getKey().toString(),
                            dataSnapshot1.child("type").getValue().toString(),dataSnapshot1.child("val").getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(),"Data cannot be fetched.",Toast.LENGTH_LONG).show();

            }
        });
    }

    public void checkAuthority(){
        authorityModels = new ArrayList<>();
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Authority");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    authorityModels.add(new AuthorityModel(dataSnapshot1.child("authority").getValue().toString(),dataSnapshot1.child("type").getValue().toString(),
                            dataSnapshot1.getKey().toString(),dataSnapshot1.child("pending").getValue().toString(),dataSnapshot1.child("request").getValue().toString(),
                            dataSnapshot1.child("result").getValue().toString(),dataSnapshot1.child("time").getValue().toString(),
                            dataSnapshot1.child("name").getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Data cannot be fetched.",Toast.LENGTH_LONG).show();
            }
        });
    }
}
