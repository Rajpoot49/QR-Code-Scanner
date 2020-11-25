package com.inventerit.qrcodescanner.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventerit.qrcodescanner.Model.User;
import com.inventerit.qrcodescanner.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.inventerit.qrcodescanner.QRUtils.emailValidation;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.reg_fullname)
    MaterialEditText fullname;
    @BindView(R.id.reg_email)
    MaterialEditText regEmail;
    @BindView(R.id.reg_pass)
    MaterialEditText regPass;
    @BindView(R.id.reg_conpass)
    MaterialEditText regConPass;
    @BindView(R.id.btn_register)
    Button regBtn;
    @BindView(R.id.signin_tv)
    TextView signIntv;
//    @BindView(R.id.progressBar)
//    ProgressBar progressBar;

    private String androidId;
    private FirebaseAuth auth;
    private ProgressDialog pDialog;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        initpDialog();

        getUsersData();

        auth = FirebaseAuth.getInstance();

        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        ClickListeners();

    }

    private void ClickListeners() {
        signIntv.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

        regBtn.setOnClickListener(view -> {

            showpDialog();
            String txt_username = fullname.getText().toString();
            String txt_email = regEmail.getText().toString();
            String txt_password = regPass.getText().toString();
            String txt_conPass = regConPass.getText().toString();

            if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_conPass)){
                Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                hidepDialog();
            } else if (txt_password.length() < 6 ){
                hidepDialog();
                Toast.makeText(RegisterActivity.this, "password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                if(!emailValidation(txt_email)){
                    hidepDialog();
                    Toast.makeText(RegisterActivity.this, "Invalid Email.", Toast.LENGTH_SHORT).show();
                }else{
                    if(txt_conPass.equals(txt_password)){
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(!isIDExist.contains(androidId)) {
                                    register(txt_username, txt_email, txt_password);
                                }else{
                                    hidepDialog();
                                    Toast.makeText(RegisterActivity.this, "You already have an account with this device", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },2000);
                    }else{
                        hidepDialog();
                        Toast.makeText(RegisterActivity.this, "Password not matched.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private ArrayList<String> isIDExist = new ArrayList<>();

    private void getUsersData() {
        DatabaseReference userDataref = FirebaseDatabase.getInstance().getReference("Users");

        userDataref.orderByChild("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    isIDExist.add(ds.child("uid").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void register(final String username, String email, String password){


        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;
                        String userid = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", username);
                        hashMap.put("email",email);
                        hashMap.put("uid",androidId);
                        hashMap.put("profile_pic","");

                        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, DashBoardActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        hidepDialog();
                        Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    protected void initpDialog() {

        pDialog = new ProgressDialog(RegisterActivity.this);
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
