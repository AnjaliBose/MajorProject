package trainedge.sample_proj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "HomeActivity";
    // private CalendarView calendarView;
    public FirebaseAuth mAuth;

    private FirebaseDatabase dbInstance;
    private DatabaseReference dbRef;
    List<TaskModel> commentList;
    private String getuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AllGeofencesActivity.class);
                startActivity(intent);


            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //get the data and covert into java obj
        dbInstance = FirebaseDatabase.getInstance();
        getuid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbRef = dbInstance.getReference("tasks").child(getuid);
        //dbRef = dbInstance.getReference("comments").child(uid);

        //creating blank list in memory
        commentList = new ArrayList<>();

        //recyclerview obj

        final RecyclerView rvComments = (RecyclerView) findViewById(R.id.rvComments);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        //passing layout manager in recyclerview
        rvComments.setLayoutManager(manager);

        final TaskAdapter adapter = new TaskAdapter(commentList);
        rvComments.setAdapter(adapter);

        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));

        rvComments.setItemAnimator(animator);
        rvComments.getItemAnimator().setAddDuration(1000);
        //setup listener
        //using anonymous class
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("please wait");
        dialog.setCancelable(false);
        dialog.show();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //data is in dataSnapshot obj
                int position = 0;
                commentList.clear();
                if (dataSnapshot.hasChildren()) { //tab
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // datasnapshot.getChildren().iter
                        if (!snapshot.child("status").getValue(Boolean.class)) {
                            commentList.add(new TaskModel(snapshot));
                            adapter.notifyItemInserted(position);
                            position++;
                        }
                    }
                    Toast.makeText(HomeActivity.this, "data loaded", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(HomeActivity.this, "No comments", Toast.LENGTH_SHORT).show();
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));

            return true;
        }
        else if (id == R.id.action_profile)
        {
            startActivity(new Intent(this, Profileview.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_task) {
            // move too createTask page

        } else if (id == R.id.nav_view) {
            startActivity(new Intent(this, CreateTask.class));


            // Intent createintend = new Intent(HomeActivity.this,CreateTask.class);
            // startActivity(createintend);

        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));


        } else if (id == R.id.nav_history) {
            startActivity(new Intent(this, History.class));


        } else if (id == R.id.nav_feed) {
            startActivity(new Intent(this, FeedbackActivity.class));

        } else if (id == R.id.nav_share) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, "GeoFi");

            share.putExtra(Intent.EXTRA_TEXT, "Your friend has invited you to join the app./n To join click the link");
            startActivity(Intent.createChooser(share, "Share via..."));


        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            try {
                LoginManager.getInstance().logOut();
                AccessToken.setCurrentAccessToken(null);
            } catch (Exception ignore) {

            }
            Intent lgtIntent = new Intent(HomeActivity.this, loginactivity.class);
            startActivity(lgtIntent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
