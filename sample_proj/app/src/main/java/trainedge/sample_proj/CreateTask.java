package trainedge.sample_proj;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class CreateTask extends Activity implements
        TextToSpeech.OnInitListener, View.OnClickListener {

    public static final String TEXT = "trainedge.sample_proj.text";
    public static final String KEY = "trainedge.sample_proj.key";
    /**
     * Called when the activity is first created.
     */
    private TextToSpeech tts;
    private Button btnSpeak;
    private TextView txtText;
    private String text;
    private Button taskbtn;
    private Button snoozebtn;
    private String key;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        text = getIntent().getStringExtra(TEXT);
        key = getIntent().getStringExtra(KEY);
        tts = new TextToSpeech(this, this);
        txtText = (TextView) findViewById(R.id.txtText);
        taskbtn = (Button) findViewById(R.id.taskbtn);
        taskbtn.setOnClickListener(this);
        txtText.setText(text);
        snoozebtn = (Button) findViewById(R.id.snoozebtn);
        snoozebtn.setOnClickListener(this);

    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {

            } else {
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
        } else {

            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskbtn:
                completTask();
                break;
            case R.id.snoozebtn:
                snoozeTask();
                break;

        }


    }

    private void snoozeTask() {
        finish();
    }

    private void completTask() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tasks").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //final DatabaseReference georef2 = FirebaseDatabase.getInstance().getReference("tasks").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("geofire");

        //ref.child(key).removeValue(new DatabaseReference.CompletionListener() {
        ref.child(key).child("status").setValue(true, new DatabaseReference.CompletionListener() {
           // @Override
            //public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
              //  if (databaseError == null) {
                //    Toast.makeText(CreateTask.this, "Removed", Toast.LENGTH_SHORT).show();
                  //  georef2.child(key).removeValue();

           @Override
           public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
               if (databaseError != null)
                   Toast.makeText(CreateTask.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
               }
           });

        }

    }



