package trainedge.sample_proj;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;

import static trainedge.sample_proj.PlaceSelectionActivity.KEY_ADDRESS;
import static trainedge.sample_proj.PlaceSelectionActivity.KEY_LAT;
import static trainedge.sample_proj.PlaceSelectionActivity.KEY_LNG;

public class AllGeofencesActivity extends ActionBarActivity {
    private double lat;
    private double lng;
    private String selectedAddress;

    // region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_geofences);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AllGeofencesFragment())
                    .commit();
        }

        GeofenceController.getInstance().init(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this, "erroe loading data", Toast.LENGTH_SHORT).show();
        }
        selectedAddress = extras.getString(KEY_ADDRESS);
        lat = extras.getDouble(KEY_LAT);
        lng = extras.getDouble(KEY_LNG);
    }






    @Override
    protected void onResume() {
        super.onResume();

        int googlePlayServicesCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.i(AllGeofencesActivity.class.getSimpleName(), "googlePlayServicesCode = " + googlePlayServicesCode);

        if (googlePlayServicesCode == 1 || googlePlayServicesCode == 2 || googlePlayServicesCode == 3) {
            GooglePlayServicesUtil.getErrorDialog(googlePlayServicesCode, this, 0).show();
        }
    }

    // endregion
}
