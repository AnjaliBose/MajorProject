package trainedge.sample_proj;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.String.format;

public class GeofenceService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, TextToSpeech.OnInitListener {

    String service;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private int speak;


    public GeofenceService() {
        service = "location_geofence_service";
    }

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        buildGoogleApiClient();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
        return START_STICKY_COMPATIBILITY;
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

    }

    public String getCurrentTime() {
        SimpleDateFormat dateformat =
                new SimpleDateFormat("HH:mm:ss MM/dd/yyyy", Locale.US);
        return (dateformat.format(new Date()));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        if (!Geocoder.isPresent()) {
            Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
            return;
        }
        //currentLocation reqeuest
        createLocationRequest();
        try {
            startLocationUpdates();
        } catch (Exception e) {

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "connection suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "connection failed " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            handle_geofire(location);
        }
    }

    private void handle_geofire(Location location) {
        mCurrentLocation = location;//call for address here
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tasks").child(uid).child("geofire");

        GeoFire geoFire = new GeoFire(ref);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 0.5); //five hundred meter

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                final String msgToDisplay = format("you have to perform task %s  at his location", key, 0);
                DatabaseReference tasks = FirebaseDatabase.getInstance().getReference("tasks").child(uid).child(key);
                tasks.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if (!dataSnapshot.getValue(Boolean.class)) {
                                TaskGeofenceNotification.notify(GeofenceService.this, msgToDisplay, 1);
                                launchTTS(msgToDisplay);

                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                //System.out.println("All initial data has been loaded and events have been fired!");
                //Toast.makeText(GeofenceService.this, "service ready", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });
    }

    TextToSpeech engine;

    private void launchTTS(String msgToDisplay) {
        engine = new TextToSpeech(getApplicationContext(), this);
        speak = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            speak = engine.speak(msgToDisplay, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            engine.speak(msgToDisplay, TextToSpeech.QUEUE_FLUSH, null);
        }

        if (speak == TextToSpeech.SUCCESS) {
            return;
        } else {
            Intent intent = new Intent(getApplicationContext(), CreateTask.class);
            intent.putExtra(CreateTask.TEXT, msgToDisplay);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000 * 5);
        mLocationRequest.setFastestInterval(60000 * 4);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Toast.makeText(this, "tts enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "sorry your TTS was not enabled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {

        if (engine != null) {
            engine.stop();
            engine.shutdown();
        }
        super.onDestroy();
    }

}

