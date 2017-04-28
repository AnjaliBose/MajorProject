package trainedge.sample_proj;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

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

public class History extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "History";
    private FirebaseDatabase dbInstance;
    private DatabaseReference dbRef;
    List<TaskModel> commentList;
    private String getuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


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
                        if (snapshot.child("status").getValue(Boolean.class)) {
                            commentList.add(new TaskModel(snapshot));
                            adapter.notifyItemInserted(position);
                            position++;
                        }
                    }
                    Toast.makeText(History.this, "data loaded", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(History.this, "No comments", Toast.LENGTH_SHORT).show();
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(History.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
}
