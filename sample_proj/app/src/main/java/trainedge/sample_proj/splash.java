package trainedge.sample_proj;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class splash extends AppCompatActivity {

    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        logo = (ImageView) findViewById(R.id.logo);
        final Animation a1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation);

        logo = (ImageView) findViewById(R.id.logo);
        logo.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {


                Intent i = new Intent(splash.this, loginactivity.class);
                startActivity(i);
                finish();
            }
        }).rotationY(1080)
                .scaleX(75.2f)
                .scaleY(75.2f)
                .setDuration(3000)
                .setStartDelay(100)
                .start();


    }
}
