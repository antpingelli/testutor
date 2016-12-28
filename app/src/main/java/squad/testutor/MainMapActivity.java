package squad.testutor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;


public class MainMapActivity
        extends FragmentActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<String, Float> classColorMap;
    private List<String> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        Button bAddLocation = (Button) findViewById(R.id.addLocation);
        Button bAddGroup = (Button) findViewById(R.id.addGroup);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLocationMarker();
            }
        });

        bAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainMapActivity.this, AddGroupActivity.class);
                MainMapActivity.this.startActivity(i);
            }
        });

        //Initialize class list
        classList.add("MATH 140");
        classList.add("MATH 141");
        classList.add("CHEM 135");
        classList.add("CMSC 131");
        classList.add("CMSC 132");
        classList.add("PHIL 100");

        //Initialize color and class map
        float i = 0;
        for (String str : classList) {
            classColorMap.put(str, i);
            i += 30.0;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_map_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_reset_passpoints:
                return true;
            case R.id.replace_lock_image:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("debug", "setting location...");
            mMap.setMyLocationEnabled(true);
        } else {
            Log.d("debug", "failed to set location...");
        }

        LatLng college_park = new LatLng(38.986161, -76.942720);
        //mMap.addMarker(new MarkerOptions().position(college_park).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(college_park, 15.0f));
    }

    private void addLocationMarker() {
        Location lastLocation;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            LatLng lastLocationLL = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(lastLocationLL).title("Current Location"));
        } else {
            Toast.makeText(MainMapActivity.this, "Marker did not work", Toast.LENGTH_SHORT).show();
        }
    }
}
