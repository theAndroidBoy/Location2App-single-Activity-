package easyapps.com.location2app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    protected GoogleApiClient googleApiClient;
    protected Location location;
    protected LocationRequest mLocationRequest;
    protected TextView latitudeTextView;
    protected TextView longitudeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("flow", "onCreate: ");
        setContentView(R.layout.activity_main);
        latitudeTextView = findViewById(R.id.latitude);
        longitudeTextView = findViewById(R.id.longitude);

        buildGoogleApiClient();


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("flow", "onStart: ");
        if (googleApiClient.isConnected() != true)
            googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("flow", "onStop: ");
        if (googleApiClient.isConnected())
            googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("flow", "onConnected: ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            showAskDialog();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("flow", "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("flow", "onConnectionFailed: ");
    }

    private void buildGoogleApiClient() {
        Log.i("flow", "buildGoogleApiClient: ");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    private void requestPermission() {
        Log.i("flow", "requestPermission: ");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                , 101);

    }

    private void permissionDeniedsnackBar() {
        Log.i("flow", "permissionDeniedsnackBar: ");
        Snackbar snackbar = Snackbar.make(findViewById(R.id.latitude), "Location permission " + //you can place any view in first parameter
                "is required to get the latitude and longitude" +                               //it does't matter
                "", Snackbar.LENGTH_LONG);

        snackbar.setAction("ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();

            }
        });
        snackbar.setActionTextColor(Color.GREEN);
        View sb = snackbar.getView();
        TextView textView = (TextView) sb.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.CYAN);
        snackbar.show();
    }


    private void updateUI(Location location) {
        Log.i("flow", "updateUI:");
        if (location != null) {
            latitudeTextView.setText(String.valueOf(location.getLatitude()));
            longitudeTextView.setText(String.valueOf(location.getLongitude()));
        }
    }


    private void continueLocationUpdateRequest() {
        Log.i("flow", "continueLocationUpdateRequest: ");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);

    }

    //.............................................
    private void singleLocationUpdateRequest()
    {
        Log.i("flow", "singleLocationUpdateRequest: ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return; //their is no need for this if statement but android made us do this.
        }
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        updateUI(location);
    }
    private void showAskDialog()
    {
        Log.i("flow", "showAskDialog: single or continuous?");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("what do you want?")
                .setCancelable(false)
                .setPositiveButton("Single update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("flow", "onClick: single clicked ");
                        singleLocationUpdateRequest();

                    }
                })
                .setNegativeButton("Continuous update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("flow", "onClick: continuous clicked ");
                        continueLocationUpdateRequest();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.i("flow", "onRequestPermissionsResult: ");
        switch (requestCode) {
            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    showAskDialog();

                } else {
                    //permission denied
                    permissionDeniedsnackBar();
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            //add more cases if you are making more than one permission request
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("flow", "onLocationChanged: ");
        updateUI(location);
    }

}
