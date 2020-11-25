package com.inventerit.qrcodescanner.activities;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Share;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.GoogleSignInOptionsExtensionParcelable;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.internal.GoogleApiAvailabilityCache;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventerit.qrcodescanner.Model.User;
import com.inventerit.qrcodescanner.QRUtils;
import com.inventerit.qrcodescanner.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private static final String USERADDED = "useradded";
    @BindView(R.id.signin_email)
    MaterialEditText regEmail;
    @BindView(R.id.signin_pass)
    MaterialEditText regPass;
    @BindView(R.id.btn_login)
    Button signInBtn;
    @BindView(R.id.forgot_password)
    TextView forgotPass;
    @BindView(R.id.reg_tv)
    TextView reg_tv;
//    @BindView(R.id.progressBar)
//    ProgressBar progressBar;

    private String fbid = "1";
    private int RC_SIGN_IN =1;
    private boolean isTextExmpty = false;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth.AuthStateListener authStateListener;
    private LoginButton loginButton;
    private AccessTokenTracker accessTokenTracker;

    private ArrayList<String> isIDExist = new ArrayList<>();
    private boolean isSignIn = false;
    private boolean isNewUser = false;
    private boolean isAndroidID = false;

    FirebaseAuth auth;
    private CallbackManager mCallbackManager;
    private String androidId;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_login);
        //will bind the view
        ButterKnife.bind(this);

        initpDialog();
        getUsersData();

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
            startActivity(intent);
            finish();
        }else{

            androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.i("id",androidId);

            signInButton = findViewById(R.id.googleSignInButton);
            loginButton = findViewById(R.id.login_button);
            loginButton.setReadPermissions("email","public_profile");
            mCallbackManager = CallbackManager.Factory.create();
            FacebookSdk.sdkInitialize(getApplicationContext());

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    signInOptions();
                }
            },2000);
        }
        SharedPreferences preferences = getSharedPreferences("Settings",MODE_PRIVATE);
        boolean isLangSet = preferences.getBoolean("langSet",false);
        if(!isLangSet) {
            chooseLanguage();
        }
    }

    private void chooseLanguage() {
        final String[] listItems = {"Serbian","English"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.selectlanguage))
                .setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            setLocals("sr");
                            recreate();
                        }else{
                            setLocals("en");
                            recreate();
                        }
                    }
                }).show();
    }
    public void setLocals(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences preferences = getSharedPreferences("Settings",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("my_lang",lang);
        editor.putBoolean("langSet",true);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences preferences = getSharedPreferences("Settings",MODE_PRIVATE);
        String language = preferences.getString("my_lang","");
        setLocals(language);
    }

    private void signInOptions() {

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fbid = loginResult.getAccessToken().getUserId();
                handleFacebookLoginToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        //initialize the click listeners
        ClickLinsteners();
    }

    private List<User> userList;
    private ArrayList<String> isUserExist = new ArrayList<>();

    private void getUsersData() {
        userList = new ArrayList<>();
        DatabaseReference userDataref = FirebaseDatabase.getInstance().getReference("Users");

        userDataref.orderByChild("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    userList.add(new User(ds.child("uid").getValue().toString(),ds.child("username").getValue().toString(),ds.child("email").getValue().toString()
                    ,ds.child("id").getValue().toString(),ds.child("profile_pic").getValue().toString()));
                    isIDExist.add(ds.child("uid").getValue().toString());
                    isUserExist.add(ds.child("email").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    private void updateUID(String uid, String id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        reference.child("uid").setValue(uid);
    }

    public void addUsers(String email, String username, String id, String uid, String fbid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("email",email);
        hashMap1.put("username",username);
        hashMap1.put("id",id);
        hashMap1.put("uid",uid);
        hashMap1.put("fbid",fbid);

        reference.child(id).setValue(hashMap1);

        hidepDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try{
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            for(int i=0;i<userList.size();i++){
                if(userList.get(i).getUserId().equals(androidId) && acc.getEmail().equals(userList.get(i).getEmail())){
                    FirebaseGoogleAuth(acc);
                    isNewUser = true;
                    break;
                }else if(acc.getEmail().equals(userList.get(i).getEmail()) && (!isIDExist.contains(androidId))){

                    isNewUser = true;
                    isAndroidID = true;
                    FirebaseGoogleAuth(acc);
                    break;

                }else if(isIDExist.contains(androidId)){
                    isNewUser = true;
                    isAndroidID = true;
//                    break;
                }
            }
            if(!isNewUser){
                isAndroidID = false;
                FirebaseGoogleAuth(acc);
            }else {
                Toast.makeText(this, "This device is already registered", Toast.LENGTH_SHORT).show();
            }
        }catch (ApiException e){
            e.printStackTrace();

            hidepDialog();
            Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
//            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        showpDialog();
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    if(!isUserExist.contains(user.getEmail())) {
                        addUsers(user.getEmail(), user.getDisplayName(), user.getUid(),androidId, fbid);
                    }
                    if (isAndroidID){
                        updateUID(androidId,user.getUid());
                    }
                    startActivity(new Intent(LoginActivity.this,DashBoardActivity.class));

//                    Toast.makeText(LoginActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                }else {

                    hidepDialog();
                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleFacebookLoginToken(AccessToken accessToken) {
        showpDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    if(!isUserExist.contains(user.getEmail())) {
                        addUsers(user.getEmail(), user.getDisplayName(), user.getUid(),androidId,fbid);
                    }if(!isAndroidID){
                        updateUID(androidId,user.getUid());
                    }
                    startActivity(new Intent(LoginActivity.this,DashBoardActivity.class));

                }else{

                    hidepDialog();
                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ClickLinsteners() {

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPassActivity.class));
            }
        });
        reg_tv.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = regEmail.getText().toString();
                String txt_password = regPass.getText().toString();

                if(!QRUtils.emailValidation(txt_email)){
                    Toast.makeText(LoginActivity.this, "Invalid Email.", Toast.LENGTH_SHORT).show();
                }else{

                    if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                        Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                        isTextExmpty = true;
                    }
                    if(!isTextExmpty){
                        for(int i=0;i<userList.size();i++) {
                            if(userList.get(i).getEmail().equals(txt_email) && userList.get(i).getUserId().equals(androidId)) {
                                isNewUser = true;
                                firebaseSignIn(txt_email, txt_password, userList.get(i).getId());
                                break;
                            }else if(userList.get(i).getEmail().equals(txt_email) && (!isIDExist.contains(androidId))){
                                isAndroidID = true;
                                isNewUser= true;
                                firebaseSignIn(txt_email, txt_password, userList.get(i).getId());
                                break;
                            }
                        }
                    }
                    if(!isNewUser){
                        Toast.makeText(LoginActivity.this, "You already have an account with this device", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void firebaseSignIn(String email, String password, String id) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        updateUID(androidId,id);
                        Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        hidepDialog();
                        Toast.makeText(LoginActivity.this, "Wrong email or password.", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(LoginActivity.this);
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
