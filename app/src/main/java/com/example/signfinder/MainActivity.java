package com.example.signfinder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.Menu;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.INVISIBLE;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    public static TextView nearbyMessage;
    public static ListView signsView, nearbyView;
    public static Spinner searchSpin, optionsSpin;
    public static ListAdapter adapter;
    public BottomNavigationView navigation;
    private int nav;
    public double latitude, longitude;
    public Context context;
    private LocationManager locMan;
    public SearchView searchbar;
    public String signInfo;
    public static ArrayList<HashMap<String, String>> nearbyList = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> signList = new ArrayList<HashMap<String, String>>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_nearby);
                    setContentView(R.layout.activity_main);
                    nav = R.id.navigation;
                    nearbyView = (ListView) findViewById(R.id.listviewNear);
                    navSetUp();
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_visited);
                    setContentView(R.layout.visited);
                    nav = R.id.navigation2;
                    navSetUp();
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_search);
                    setContentView(R.layout.search);
                    nav = R.id.navigation3;
                    signsView = (ListView) findViewById(R.id.searchList);
                    searchbar = (SearchView) findViewById(R.id.searchBar);
                    searchSpin = (Spinner) findViewById(R.id.spinner);
                    optionsSpin = (Spinner) findViewById(R.id.spinner2);
                    searchHelp();
                    navSetUp();

                    //SearchActivity();
                    return true;
            }
            return false;
        }
    };

    public void searchHelp() {
        popSearchSpinner();
        searchSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                if(item.equals("City/County")) {
                    searchbar.setActivated(false);
                    popCitySpin(R.array.city_county_options, "db_citySearch");
                }
                else if(item.equals("District")) {
                    searchbar.setActivated(false);
                    popCitySpin(R.array.district_options, "db_districtSearch");
                }
                else if(item.equals("Title")) {
                    searchbar.setActivated(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void popSearchSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_options, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        searchSpin.setAdapter(adapter);

    }

    public void popCitySpin(int spinID, String file) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                spinID, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        optionsSpin.setAdapter(adapter);

        final String fileName = file;
        optionsSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                //getSigns(item, 0);
                new SearchActivity(getApplicationContext()).execute(item, fileName);
                //adapter = new SimpleAdapter(getApplicationContext(), signList, android.R.layout.simple_list_item_1, new String[] {"title", "text"}, new int[] {android.R.id.text1, android.R.id.text2});
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_settings:
                settingsActivity();
                return true;
            case R.id.about_settings:
                aboutActivity();
                return true;
            case R.id.logout:
                loginActivity();
                return true;
        }
        return false;
    }

    public void locationHelper() {
        // Acquire a reference to the system Location Manager
        locMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                new NearbyActivity(getApplicationContext()).execute("db_nearby", Double.toString(latitude), Double.toString(longitude));
                return;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        if (
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            Location loc = new Location("gps");
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }


    }

    @Override
    public void onRequestPermissionsResult(int request, String[] permissions,int[] grantresults){
        switch(request) {
            case 1:
                if(grantresults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                else{

                }
        }
    }


    public void aboutActivity() {
        Intent startAboutAct = new Intent(this, AboutActivity.class);
        startActivity(startAboutAct);
    }

    public void settingsActivity() {
        Intent startSettingsAct = new Intent(this, SettingsActivity.class);
        startActivity(startSettingsAct);
    }

    public void loginActivity() {
        Intent startLogAct = new Intent(this, LoginActivity.class);
        startActivity(startLogAct);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appmenu, menu);
        return true;
    }

    public void navSetUp() {
        navigation = (BottomNavigationView) findViewById(nav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        nearbyView = (ListView) findViewById(R.id.listviewNear);
        adapter = new SimpleAdapter(getApplicationContext(), nearbyList, android.R.layout.simple_list_item_1, new String[] {"signs"}, new int[] {android.R.id.text1});
        nearbyView.setAdapter(adapter);
        mTextMessage = (TextView) findViewById(R.id.message);
        nearbyMessage = (TextView) findViewById(R.id.nearbyMessage);
        nearbyMessage.setText("There are no signs near you.");
        nearbyMessage.setVisibility(INVISIBLE);
        nav = R.id.navigation;
        navSetUp();
        locationHelper();

    }

}
