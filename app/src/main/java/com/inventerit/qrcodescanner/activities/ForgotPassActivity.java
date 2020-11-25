package com.inventerit.qrcodescanner.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.inventerit.qrcodescanner.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.inventerit.qrcodescanner.QRUtils.emailValidation;

public class ForgotPassActivity extends AppCompatActivity {

    @BindView(R.id.send_email)
    EditText send_email;
    @BindView(R.id.btn_reset)
    Button resetBtn;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        ButterKnife.bind(this);
        initpDialog();

        firebaseAuth = FirebaseAuth.getInstance();
        ClickListeners();
    }


    private void ClickListeners() {
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpDialog();
                String email = send_email.getText().toString();

                if (email.equals("")){
                    hidepDialog();
                    Toast.makeText(ForgotPassActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                } else {
                    if(!emailValidation(email)){
                        hidepDialog();
                        Toast.makeText(ForgotPassActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    }else{
                        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                hidepDialog();
                                new AlertDialog.Builder(ForgotPassActivity.this)
                                        .setTitle("Reset Password")
                                        .setMessage("An email has been sent to your registered account.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(ForgotPassActivity.this, LoginActivity.class));
                                                dialog.dismiss();
                                            }
                                        }).show();
                            } else {
                                hidepDialog();
                                String error = task.getException().getMessage();
                                Toast.makeText(ForgotPassActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    }

            }
        });
    }
    protected void initpDialog() {
        pDialog = new ProgressDialog(ForgotPassActivity.this);
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
