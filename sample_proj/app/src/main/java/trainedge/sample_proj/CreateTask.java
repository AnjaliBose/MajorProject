package trainedge.sample_proj;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CreateTask extends AppCompatActivity {

    private TextView txtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtv = (TextView) findViewById(R.id.txtv);
        txtv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CreateTask.this, PlaceSelectionActivity.class);
                startActivity(intent);
            }
        });




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
