package trainedge.sample_proj;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class CreateTask extends Activity implements
        TextToSpeech.OnInitListener {

    public static final String TEXT = "trainedge.sample_proj.text";
    /**
     * Called when the activity is first created.
     */
    private TextToSpeech tts;
    private Button btnSpeak;
    private TextView txtText;
    private String text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        text = getIntent().getStringExtra(TEXT);
        tts = new TextToSpeech(this, this);
        txtText = (TextView) findViewById(R.id.txtText);
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
}

