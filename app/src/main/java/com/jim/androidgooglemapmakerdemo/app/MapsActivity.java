package com.jim.androidgooglemapmakerdemo.app;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    protected Location mCurrentLocation;
    private LocationRequest mLocationRequest;

    private NetworkTool networkTool;
    private ArrayList<results> data = new ArrayList<>();


    private TextView tv_LatitudeText, tv_LongitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        networkTool = new NetworkTool(this);

        tv_LatitudeText = (TextView) findViewById(R.id.tv_LatitudeText);
        tv_LongitudeText = (TextView) findViewById(R.id.tv_LongitudeText);

        buildGoogleApiClient();

        setUpMapIfNeeded();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    protected void startLocationUpdates() {
        Log.d("startLocationUpdates", "startLocationUpdates");
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();

    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMyLocationEnabled(true);
            if (mMap != null) {
//                setMyLocation();
            }
        }
    }

    private void setMyLocation() {
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                // TODO Auto-generated method stub
                mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
            }
        });
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("onConnected", "onConnected");
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mCurrentLocation != null) {
            Log.d("FusedLocationApi", "FusedLocationApi");
            tv_LatitudeText.setText(String.valueOf(mCurrentLocation.getLatitude()));
            tv_LongitudeText.setText(String.valueOf(mCurrentLocation.getLongitude()));
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            getNearPlace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("connectionResult", "" + connectionResult.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int i) {
        //TODO something
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateUI();
    }

    private void updateUI() {
        Log.d("updateUI", "updateUI");
        tv_LatitudeText.setText(String.valueOf(mCurrentLocation.getLatitude()));
        tv_LongitudeText.setText(String.valueOf(mCurrentLocation.getLongitude()));
        getNearPlace();
    }

    private void getNearPlace() {
        String latlng = tv_LatitudeText.getText().toString() + "," + tv_LongitudeText.getText().toString();
        networkTool.GetNear(latlng, new NetworkTool.ResponseHandler() {
            @Override
            public void Success(int StatusCode, JSONObject response) {
                JSONArray jsonArray = response.optJSONArray("results");
                if (jsonArray != null) {
                    data.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        data.add(new results(jsonArray.optJSONObject(i)));
                    }
                    addMark();
                }
            }

            @Override
            public void Fail(int status, String reason) {

            }
        });
    }


    private void addMark() {
        mMap.clear();
        for (int i = 0; i < data.size(); i++) {
            LatLng latLng = new LatLng(data.get(i).getLocation().getLatitude(), data.get(i).getLocation().getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(data.get(i).getName()));
        }
    }
}
