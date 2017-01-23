package squad.testutor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainMapActivity
        extends FragmentActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;

    static FirebaseUser user;

    static ArrayList<String> classList;
    private ArrayList<Float> classColorList;

    private DatabaseReference classesRef;
    static ArrayList<String> userClasses;

    private RelativeLayout mainLayout;
    private Button addClasses;
    private Button addGroup;
    private TextView noClassesText;
    private View map;

    static String DATABASE = "database";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        user = FirebaseAuth.getInstance().getCurrentUser();

        classesRef = FirebaseDatabase.getInstance().getReference("classes");

        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);

        addClasses = (Button) findViewById(R.id.add_classes);
        addGroup = (Button) findViewById(R.id.add_group);
        noClassesText = (TextView) findViewById(R.id.no_classes_text);
        map = findViewById(R.id.map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initialize class list
        classList = new ArrayList<>();
        classList.add("MATH 140");
        classList.add("MATH 141");
        classList.add("CHEM 135");
        classList.add("CMSC 131");
        classList.add("CMSC 132");
        classList.add("PHIL 100");

        classColorList = new ArrayList<>();
        classColorList.add(BitmapDescriptorFactory.HUE_RED);
        classColorList.add(BitmapDescriptorFactory.HUE_YELLOW);
        classColorList.add(BitmapDescriptorFactory.HUE_CYAN);
        classColorList.add(BitmapDescriptorFactory.HUE_MAGENTA);
        classColorList.add(BitmapDescriptorFactory.HUE_BLUE);
        classColorList.add(BitmapDescriptorFactory.HUE_GREEN);


        if (classesRef.child(user.getUid()) != null) {
            setClasses();
        }

        //listeners
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(MainMapActivity.this, AddGroupActivity.class);
                //MainMapActivity.this.startActivity(i);
            }
        });

        addClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainMapActivity.this, AddClassesActivity.class);
                MainMapActivity.this.startActivity(i);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LoginActivity.TAG, "On create options.....");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_map_menu, menu);
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

    private void setClasses() {
        ValueEventListener classesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(DATABASE, "I am setting userClasses");
                userClasses = new ArrayList<>();
                ArrayList<String> classArr = (ArrayList<String>) dataSnapshot.child(user.getUid()).getValue();
                for (String child : classArr) {
                    userClasses.add(child);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(DATABASE, "load user classes:onCancelled", databaseError.toException());
            }
        };
        classesRef.addListenerForSingleValueEvent(classesListener);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                return null;
            }

            protected void onPostExecute(Void aVoid) {
                if (userClasses != null) {
                    setUI();
                }
            }
        };

        task.execute();
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

    private void setUI() {
        mainLayout.removeView(addClasses);
        mainLayout.removeView(noClassesText);

        for (int i = 1; i <= userClasses.size(); i++) {
            addClass(userClasses.get(i - 1), i);
        }

        String str = "class_switch" + String.valueOf(userClasses.size());
        int id = getResources().getIdentifier(str, "id", getPackageName());

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) map.getLayoutParams();
        params.removeRule(RelativeLayout.ABOVE);
        params.addRule(RelativeLayout.ABOVE, id);
        map.setLayoutParams(params);

        setContentView(mainLayout);
    }


    private void addClass(String text, int index) {
        Log.d(LoginActivity.TAG, "Adding " + text + " at " + index);
        String str = "class_switch" + String.valueOf(index);
        int id = getResources().getIdentifier(str, "id", getPackageName());

        str = "classColor" + String.valueOf(index);
        int color = getResources().getIdentifier(str, "color", getPackageName());

        SwitchCompat aClass = new SwitchCompat(this);
        aClass.setText(text);
        aClass.setId(id);
        aClass.setTextSize(24);
        aClass.setTextColor(color);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_START);
        params.setMargins(10, 0, 0, 10);
        if (index == 1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            String prevStr = "class_switch" + String.valueOf(index - 1);
            int prevId = getResources().getIdentifier(prevStr, "id", getPackageName());

            params.addRule(RelativeLayout.ABOVE, prevId);
        }
        aClass.setLayoutParams(params);

        mainLayout.addView(aClass);
    }
}
