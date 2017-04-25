package trainedge.sample_proj;

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
        final Animation a1= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation);


        Thread mythread = new Thread() {
            @Override
            public void run() {

                try {
                    logo.startAnimation(a1);
                    Thread.sleep(3000);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    Intent abtIntent=new Intent(splash.this,loginactivity.class);
                    startActivity(abtIntent);
                    finish();
                }


            }
        };

        mythread.start();
    }
}
