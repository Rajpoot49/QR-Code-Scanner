package com.inventerit.qrcodescanner.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventerit.qrcodescanner.Model.Codes;
import com.inventerit.qrcodescanner.Model.HistoryModel;
import com.inventerit.qrcodescanner.R;
import com.inventerit.qrcodescanner.adapter.HistoryAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.pEmail)
    TextView email;
    @BindView(R.id.pFullname)
    TextView fullname;
//    @BindView(R.id.progressBar)
//    ProgressBar progressBar;
    @BindView(R.id.pHistorytv)
    TextView pHistorytv;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.profileImage)
    CircleImageView profileImage;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private HistoryAdapter adapter;
    private List<HistoryModel> models = new ArrayList<>();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //bind recycler view
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        initpDialog();
        showpDialog();

        if(getIntent().hasExtra("userid")){
            LoadUserProfile(getIntent().getStringExtra("userid"));
        }else{
            //Load current user profile
            LoadUserProfile(firebaseUser.getUid());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

                if(firebaseUser!=null){
                    if(firebaseUser.getPhotoUrl()!=null){
                        String photoUrl = firebaseUser.getPhotoUrl().toString() + "?type=large";
                        Picasso.get().load(photoUrl).into(profileImage);
                    }
                }


                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if(account!=null) {
                    if (account.getId() != null) {
                        if (firebaseUser.getUid().equals(account.getId())) {
                            String photoUrl = account.getPhotoUrl().toString() + "?type=large";
                            Picasso.get().load(photoUrl).into(profileImage);
                        }
                    }
                }



                //clear all the data
                models.clear();
                //fetch the scanned document history
                reference.child("scanneddoc").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                        for(int i=0;i<codesList.size();i++){
                            for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
//                                if(dataSnapshot1.child("result").getValue().toString().equals(codesList.get(i).getCode())){
                                    models.add(new HistoryModel(dataSnapshot1.child("time").getValue().toString(),dataSnapshot1.child("type").getValue().toString(),
                                            dataSnapshot1.child("result").getValue().toString(),dataSnapshot1.getKey().toString(),
                                            dataSnapshot1.child("val").getValue().toString(), dataSnapshot1.child("date").getValue().toString(),
                                            dataSnapshot1.child("secsum").getValue().toString(),dataSnapshot1.child("nr").getValue().toString()));
//                                }
//                            }
                        }
                        //set adapter
                        adapter = new HistoryAdapter(ProfileActivity.this,models);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        hidepDialog();
                        Toast.makeText(getApplicationContext(),"Error Occurred",Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(),"Error Occurred",Toast.LENGTH_LONG).show();
            }
        });
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

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(getString(R.string.menu_profile));
    }
    protected void initpDialog() {

        pDialog = new ProgressDialog(ProfileActivity.this);
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
