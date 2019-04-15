package com.example.signfinder;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
import static com.example.signfinder.LoginActivity.editor;
//import static com.example.signfinder.SettingsActivity.editor;

/*
    The main activity hold the main functionality of the code.
 */

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage, keymessage;
    public static TextView nearbyMessage, results;
    public static ListView signsView, nearbyView, visitView;
    public static Spinner searchSpin, optionsSpin;
    public static ListAdapter adapter;
    public BottomNavigationView navigation;
    private SharedPreferences sp;
    private int nav, radius;
    public static boolean notifOn, stayLoggedOn = false;
    public static int listPos, listID;
    public double latitude, longitude, newLat, newLong;
    public static Context context;
    private LocationManager locMan;
    public SearchView searchbar;
    public static String userName, radValue = "";
    public static ArrayList<HashMap<String, String>> nearbyList = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> signList = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> visitList = new ArrayList<HashMap<String, String>>();

    /*
        This function deals with the bottom navigation tabs.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                /*
                    Nearby tab. Calls locationHelp() to find signs and displays the list or says
                    there are no signs near..
                    Notifies the user if that setting is on.
                 */
                case R.id.navigation_home:
                    listID = 1;
                    mTextMessage.setText(R.string.title_nearby);
                    setContentView(R.layout.activity_main);
                    nav = R.id.navigation;
                    nearbyView = (ListView) findViewById(R.id.listviewNear);
                    navSetUp();
                    locationHelper();
                    if(nearbyList.isEmpty() == false) {
                        listPos = 0;
                        if(notifOn == true) {
                            notifyUser();
                        }

                    }
                    else {
                        nearbyMessage = (TextView) findViewById(R.id.nearbyMessage);
                        nearbyMessage.setText("There are no signs near you.");
                        nearbyMessage.setVisibility(View.VISIBLE);
                    }
                    nearbyHelp(nearbyList, nearbyView);
                    clickListen(nearbyView);
                    return true;
                    /*
                        Visited tab. Starts the visit activity and displays the list.
                     */
                case R.id.navigation_dashboard:
                    listID = 2;
                    mTextMessage.setText(R.string.title_visited);
                    setContentView(R.layout.visited);
                    nav = R.id.navigation2;
                    visitView = (ListView) findViewById(R.id.visitList);
                    navSetUp();
                    new VisitActivity(getApplicationContext()).execute("db_visit", userName);
                    clickListen(visitView);
                    return true;
                /*
                    Search tab. Calls searchHelp() on user input.
                 */
                case R.id.navigation_notifications:
                    listID = 3;
                    mTextMessage.setText(R.string.title_search);
                    setContentView(R.layout.search);
                    nav = R.id.navigation3;
                    keymessage = (TextView) findViewById(R.id.keywordMessage);
                    results = (TextView) findViewById(R.id.noResults);
                    signsView = (ListView) findViewById(R.id.searchList);
                    searchbar = (SearchView) findViewById(R.id.searchBar);
                    searchSpin = (Spinner) findViewById(R.id.spinner);
                    optionsSpin = (Spinner) findViewById(R.id.spinner2);
                    searchHelp();
                    navSetUp();
                    clickListen(signsView);
                    //SearchActivity();
                    return true;
            }
            return false;
        }
    };


    /*
        Handles all of the search capability.
     */
    public void searchHelp() {
        popSearchSpinner();
        searchSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /*
                Provides different functionality depending on which item is chosen from the first spinner.
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                //Populates the second spinner with county/city options
                if(item.equals("City/County")) {
                    searchbar.setVisibility(INVISIBLE);
                    keymessage.setText("Select a County/City.");
                    popCitySpin(R.array.city_county_options, "db_citySearch");
                }
                //populates the second spinner with district options
                else if(item.equals("District")) {
                    searchbar.setVisibility(INVISIBLE);
                    keymessage.setText("Select a District.");
                    popCitySpin(R.array.district_options, "db_districtSearch");
                }
                //Enables the search bar and calls callSearch on submit
                else if(item.equals("Keyword")) {
                    searchbar.setVisibility(View.VISIBLE);
                    keymessage.setText("Enter a single keyword.");
                    optionsSpin.setAdapter(null);
                    searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            callSearch(query);
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            //callSearch(newText);
                            return true;
                        }

                        //Starts the search activity with the entered text as a parameter
                        public void callSearch(String query) {
                            new SearchActivity(getApplicationContext()).execute(query, "db_titleSearch");
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    /*
        Updates the list adapter with contents of the nearby list without getting locations
     */
    public void nearbyHelp(ArrayList<HashMap<String, String>> list, ListView view) {
        if(list.isEmpty()) {
            nearbyMessage.setVisibility(View.VISIBLE);
        }
        else {
            view.setAdapter(null);
            adapter = new SimpleAdapter(context, list,
                    R.layout.list_item, new String[]{"title", "location"},
                    new int[]{R.id.signName, R.id.signLocation});
            view.setAdapter(adapter);
        }
    }

    //Populates the first spinner
    public void popSearchSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_options, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        searchSpin.setAdapter(adapter);

    }

    //Populates the second spinner
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
                new SearchActivity(getApplicationContext()).execute(item, fileName);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    //Pushes notifications when the nearby list has signs
    public static void notifyUser() {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, SignClickActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "signChannel")
                .setSmallIcon(R.drawable.ic_info_black_24dp)
                .setContentTitle("New Sign Nearby!")
                .setContentText("You're close to a sign! Tap to read it!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());


    }

    //Item select listener for the options menu item on the main activity
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
                editor.putString("username_setting", " ");
                editor.commit();
                loginActivity();
                return true;
        }
        return false;
    }

    //Gets the user's location and starts the nearby activity with lat and long as parameters
    public void locationHelper() {
        // Acquire a reference to the system Location Manager
        locMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                newLat = location.getLatitude();
                newLong = location.getLongitude();
                if(newLat != latitude && newLong != longitude) {
                    latitude = newLat;
                    longitude = newLong;
                    radValue = sp.getString("search_radius", "1");
                    notifOn = sp.getBoolean("notifications_switch", true);
                    new NearbyActivity(getApplicationContext()).execute("db_nearby", Double.toString(latitude), Double.toString(longitude), userName, radValue);
                }

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

    //Uses the results of permission requests for location
    @Override
    public void onRequestPermissionsResult(int request, String[] permissions,int[] grantresults){
        switch(request) {
            case 1:
                if(grantresults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationHelper();
                }
                else{

                }
        }
    }

    //Starts the about activity
    public void aboutActivity() {
        Intent startAboutAct = new Intent(this, AboutActivity.class);
        startActivity(startAboutAct);
    }

    //starts the settings activity
    public void settingsActivity() {
        Intent startSettingsAct = new Intent(this, SettingsActivity.class);
        startActivity(startSettingsAct);
    }

    //Starts the logInActivity
    public void loginActivity() {
        Intent startLogAct = new Intent(this, LoginActivity.class);
        startActivity(startLogAct);
    }

    //Populates the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appmenu, menu);
        return true;
    }

    //sets up the bottom navigation tabs
    public void navSetUp() {
        navigation = (BottomNavigationView) findViewById(nav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    //listener for when sings are clicked from in any list view
    public void clickListen(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPos = position;
                Intent startSignClick = new Intent(view.getContext(), SignClickActivity.class);
                startActivityForResult(startSignClick, 0);
            }
        });
    }

    //creates the notifications channel
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("signChannel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void settingsHelp() {
        sp = getSharedPreferences("", Context.MODE_PRIVATE);
    }


    //Main activity onCreate. Always starts on the nearby tab so this calls locationHelp()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingsHelp();

        createNotificationChannel();
        context = this;

        listID = 1;
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

        clickListen(nearbyView);
    }

}

