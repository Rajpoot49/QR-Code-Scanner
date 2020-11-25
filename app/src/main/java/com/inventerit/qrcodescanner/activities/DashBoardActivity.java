package com.inventerit.qrcodescanner.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventerit.qrcodescanner.R;
import com.inventerit.qrcodescanner.fragments.AuthorityFragment;
import com.inventerit.qrcodescanner.fragments.RequestedQRFragment;
import com.inventerit.qrcodescanner.fragments.SettingsFragment;
import com.inventerit.qrcodescanner.fragments.TypeFragment;
import com.inventerit.qrcodescanner.fragments.AllUsersFragment;
import com.inventerit.qrcodescanner.fragments.PendingDocFragment;
import com.inventerit.qrcodescanner.fragments.QrCodeFragment;
import com.inventerit.qrcodescanner.fragments.ScanFragment;
import com.inventerit.qrcodescanner.fragments.ValFragment;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.io.File;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashBoardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth auth;
    private FirebaseUser user;
    public boolean adminbool = false;
    private TextView name, email;
    private CircleImageView profileImage;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(getResources().getString(R.string.app_name));

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(auth.getCurrentUser().getEmail().equals("miodrag22@gmail.com") || auth.getCurrentUser().getEmail().equals("test@gmail.com")){
            adminbool = true;

        }

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        replaceFragment(ScanFragment.newInstance(),false);

        View headerView = navigationView.getHeaderView(0);
        name = headerView.findViewById(R.id.nav_name);
        email = headerView.findViewById(R.id.nav_email);
        profileImage = headerView.findViewById(R.id.nav_profileImage);

        if(adminbool) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_type).setVisible(true);
            menu.findItem(R.id.nav_qrlist).setVisible(true);
            menu.findItem(R.id.nav_allusers).setVisible(true);
            menu.findItem(R.id.nav_val).setVisible(true);
        }

        DatabaseReference reference;
        //access the current user database
        reference = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get fullname and email of the current user
                name.setText(dataSnapshot.child("username").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());

                if(user!=null){
                    if(user.getPhotoUrl()!=null){
                        String photoUrl = user.getPhotoUrl().toString() + "?type=large";
                        Picasso.get().load(photoUrl).into(profileImage);
                    }
                }
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if(account!=null) {
                    if (account.getId() != null) {
                        if (user.getUid().equals(account.getId())) {
                            String photoUrl = account.getPhotoUrl().toString() + "?type=large";
                            Picasso.get().load(photoUrl).into(profileImage);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DashBoardActivity.this,"Error Occurred",Toast.LENGTH_LONG).show();
            }
        });


//        NavigationView navigationView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_scan, R.id.nav_profile, R.id.nav_logout)
//                .setDrawerLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack){
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment,fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(adminbool){

        }

        switch (id){
            case R.id.nav_scan:
                replaceFragment(ScanFragment.newInstance(),false);
                drawer.closeDrawers();
                break;
            case R.id.nav_type:
                replaceFragment(TypeFragment.newInstance(),false);
                drawer.closeDrawers();
                break;
            case R.id.nav_val:
                replaceFragment(ValFragment.newInstance(),false);
                drawer.closeDrawers();
                break;
            case R.id.nav_qrlist:
                replaceFragment(QrCodeFragment.newInstance(),false);
                drawer.closeDrawers();
                break;
            case R.id.nav_allusers:
                replaceFragment(AllUsersFragment.newInstance(),false);
                drawer.closeDrawers();
                break;
            case R.id.nav_profile:
                startActivity(new Intent(DashBoardActivity.this,ProfileActivity.class));
                drawer.closeDrawers();
                break;
            case R.id.nav_authority:
                replaceFragment(AuthorityFragment.newInstance(),false);
                drawer.closeDrawers();
                break;
            case R.id.nav_pendingqr:
                replaceFragment(new PendingDocFragment(),false);
                drawer.closeDrawers();
                break;
            case R.id.nav_changeLang:
                startActivity(new Intent(DashBoardActivity.this,SettingsActivity.class));
                drawer.closeDrawers();
                break;
            case R.id.nav_requestedqr:
                replaceFragment(new RequestedQRFragment(),false);
                drawer.closeDrawers();
                break;
            case R.id.nav_logout:
                if(auth !=null){
                    deleteCache();
                    if(LoginManager.getInstance()!=null)
                    {
                        LoginManager.getInstance().logOut();
                    }
                    auth.signOut();
                    startActivity(new Intent(DashBoardActivity.this, LoginActivity.class));
                    finishAffinity();
                    finish();
                }
                break;
        }
        return false;
    }

    public void deleteCache() {
            try {
                File dir = getCacheDir();
                deleteDir(dir);
            } catch (Exception e) {
                e.getMessage();
            }
        }

        public boolean deleteDir(File dir) {
            if (dir != null && dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
                return dir.delete();
            } else if(dir!= null && dir.isFile()) {
                return dir.delete();
            } else {
                return false;
            }
    }
}