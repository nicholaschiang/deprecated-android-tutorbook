package io.github.nicholaschiang.tutorbook;


import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import io.github.nicholaschiang.tutorbook.adapter.FirestoreAdapter;
import io.github.nicholaschiang.tutorbook.adapter.UserAdapter;
import io.github.nicholaschiang.tutorbook.model.User;
import io.github.nicholaschiang.tutorbook.util.UserUtil;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    // Initialize fragments
    AccountFragment account = new AccountFragment();
    DashboardFragment dashboard = new DashboardFragment();
    HomeFragment home = new HomeFragment();
    SearchFragment search = new SearchFragment();
    Fragment currentFragment;

    // Views
    BottomNavigationView mBottomNavigationView;
    Toolbar mToolbar;

    // Constants and other necessary variables
    private static final String TAG = "MainActivity";
    private static final int LIMIT = 50;

    // Firebase and Google Sign-in instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore mFirestore;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Build initial view
        setContentView(R.layout.activity_main);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Initialize Firebase Auth
        initFirebaseAuth();

        // Initialize Google Sign-in Client and Firestore
        initGoogleSignIn();
        initFirestore();

        // Set custom toolbar
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        // Rebuild navigation view to account for possible username change
        setupNavigationView();

        // Add all fragments and show the default frag on open
        initFragments();
    }

    private void initFirebaseAuth() {
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, AccountActivity.class));
            finish();
        }
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    private void initGoogleSignIn() {
        // Initialize Google Sign-in Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    public void initFragments(){
        // Add all the fragments to the frag manager
        getSupportFragmentManager().beginTransaction()
                .add(R.id.rootLayout, account)
                // .add(R.id.rootLayout, dashboard)
                .add(R.id.rootLayout, home)
                .add(R.id.rootLayout, search)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(this, AccountActivity.class));
                finish();
                return true;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
                return true;

            case R.id.action_add_items:
                onAddItemsClicked();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onAddItemsClicked() {
        // Get a reference to the users collection
        CollectionReference users = mFirestore.collection("users");

        for (int i = 0; i < 10; i++) {
            // Get a random Restaurant POJO
            User user = UserUtil.getRandom(this);

            // Add a new document to the users collection with email as doc ID
            users.document(user.getEmail()).set(user, SetOptions.merge());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void setupNavigationView() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        if (mBottomNavigationView != null) {

            // Select second menu item by default and show Fragment accordingly.
            Menu menu = mBottomNavigationView.getMenu();
            selectFragment(menu.getItem(1));

            // Set action to perform when any menu-item is selected.
            mBottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            selectFragment(item);
                            return false;
                        }
                    });
        }
    }

    /**
     * Perform action when any item is selected.
     *
     * @param item Item that is selected.
     */
    protected void selectFragment(MenuItem item) {

        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.action_home:
                // Action to perform when Home Menu item is selected.
                showFrag(home);
                break;

//            case R.id.action_dashboard:
//                // Action to perform when Dashboard Menu item is selected.
//                showFrag(dashboard);
//                break;

            case R.id.action_account:
                // Action to perform when Account Menu item is selected.
                showFrag(account);
                break;

            case R.id.action_search:
                // Action to perform when Search Menu item is selected.
                showFrag(search);
                break;

        }
    }

    public void hideAllFrag(){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().hide(account).hide(search).hide(home).commit();
    }
    public void showFrag(Fragment frag){
        //show and hide correct fragments when commanded
        hideAllFrag();
        getSupportFragmentManager().beginTransaction().show(frag).commit();
        currentFragment = frag;
    }
    public void returnFrag(){
        showFrag(currentFragment);
    }

}
