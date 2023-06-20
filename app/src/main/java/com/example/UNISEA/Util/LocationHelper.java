package com.example.UNISEA.Util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.UNISEA.Model.LocationPos;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationHelper {
    private final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

    private Context ctx;
    private Activity activity;

    // Google's API for location services
    private FusedLocationProviderClient fusedLocationClient;
    // configuration of all settings of FusedLocationProviderClient
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    private final int Request_Code_Location = 22;
    private LocationPos location;

    public LocationHelper(Context ctx, Activity activity) {
        this.ctx = ctx;
        this.activity = activity;
        setLocationAPI();
    }

    private void setLocationAPI() {
        Log.d("test", "api start");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.ctx);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);      // location updates every 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if(locationResult != null) {
                    Log.d("LocationTest", "Location updates");
                    renewUserLocation(locationResult.getLastLocation());
                }else{
                    Log.d("LocationTest", "Location updates fail: null");
                }
            }
        };
    }

    public void updateLocation() {
        //if user grants permission
        if (ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallBack, null);

            // get the last location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this.activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location == null) {
                                Log.d("LocationTest", "null");
                            }else {
                                Log.d("LocationTest", "Success");
                                renewUserLocation(location);
                            }
                        }
                    });

        }else{
            //if user hasn't granted permission, ask for it explicitly
            ActivityCompat.requestPermissions(this.activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_Code_Location);
        }
    }

    private void renewUserLocation(Location loc) {
        String longitude = String.valueOf(loc.getLongitude());
        String latitude = String.valueOf(loc.getLatitude());
//        Log.d("location_lat", latitude);
//        Log.d("location_long", longitude);
        location = new LocationPos(longitude, latitude);
    }

    public void setUserLocation() {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Location");
        database.child(uid).setValue(location);
    }

    /**
     * Calculate the distance between two locations in kilometer
     * Refer to <a href="https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula">stackoverflow.com</a>
     * @param userPos - location of user 1
     * @param venuePos - location of user 2
     * @return the distance between two users to 2 decimal places in string format
     */
    public static String calculateDistanceInKilometer(LocationPos userPos, LocationPos venuePos) {

        double userLat = Double.parseDouble(userPos.getLatitude());
        double userLng = Double.parseDouble(userPos.getLongitude());
        double venueLat = Double.parseDouble(venuePos.getLatitude());
        double venueLng = Double.parseDouble(venuePos.getLongitude());

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return String.format("%.1f", AVERAGE_RADIUS_OF_EARTH_KM * c);
    }

}
