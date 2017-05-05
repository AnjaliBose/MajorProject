package trainedge.sample_proj;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

public class Profileview extends AppCompatActivity {

    public static final String TAG = "Profile";
    private ImageView imageView;
    private FirebaseAuth auth;
    private TextView email;
    private TextView name;
    private ProgressBar progressBar;
    private String username;
    private String email1;
    private boolean emailVerified;
    private String uid;
    private String providerId;
    private UserInfo profile;
    private Uri photoUrl;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileview);
      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Profile Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = (ImageView) findViewById(R.id.imageView);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            finish();


        }
        email.setText(user.getEmail());
        name.setText(user.getDisplayName());

        //Loading image from below url into imageView
        Picasso.with(this)
                .load(user.getPhotoUrl())
                .into(imageView);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    //user is signed in
                    // dialog.dismiss();
                    Intent detail = new Intent(Profileview.this,loginactivity.class);
                    startActivity(detail);
                    finish();
                    detail.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(detail);
                }

            }
        };

       
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
