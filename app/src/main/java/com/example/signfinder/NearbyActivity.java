package com.example.signfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

import static com.example.signfinder.MainActivity.adapter;
import static com.example.signfinder.MainActivity.nearbyList;
import static com.example.signfinder.MainActivity.nearbyMessage;
import static com.example.signfinder.MainActivity.nearbyView;
import static com.example.signfinder.MainActivity.signList;
import static com.example.signfinder.MainActivity.signsView;
import static com.example.signfinder.MainActivity.visitList;

public class NearbyActivity extends AsyncTask<String, String, Void> {

    public String signInfo, latitude, longitude, file, username, radius;
    private Context context;
    private double latlongMod;


    //default constructor
    public NearbyActivity() {

    }

    //constructor
    public NearbyActivity(Context context) {
        this.context = context;
    }


    //Queries the database with the user's location and returns a JSON file with all signs in the search radius
    @Override
    protected Void doInBackground(String... arg0) {
        try {
            nearbyList.clear();
            file = (String)arg0[0];
            latitude = (String)arg0[1];
            longitude = (String)arg0[2];
            username = (String)arg0[3];
            radius = (String)arg0[4];


            /*
                Gets the user specified search radius
             */
            if(radius.equals("1")) {
                latlongMod = 0.00724;
            }
            else if(radius.equals("2")) {
                latlongMod = 0.0145;
            }
            else if(radius.equals("3")) {
                latlongMod = 0.0289;
            }
            else {
                latlongMod = 0.0724;
            }

            /*
                This block of code communicates with the server and the database
             */
            String link = "http://ec2-18-219-194-235.us-east-2.compute.amazonaws.com/"+file+".php?lat="+latitude+"&long="+longitude+"&username="+username+"&mod="+latlongMod;

            URL url = new URL(link);
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(link));
            HttpResponse response = client.execute(request);
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = null;

            // Read Server Response
            while((line = in.readLine()) != null) {
                sb.append(line);

            }

            signInfo = sb.toString();

                //Convert JSON to a hashmap of strings and adds these to a list
                JSONObject reader = new JSONObject(signInfo);
                JSONArray signs = reader.getJSONArray("signs");

                for(int j = 0; j < signs.length(); j++) {
                    JSONObject c = signs.getJSONObject(j);
                    String title = c.getString("title");
                    String location = c.getString("location");
                    String text = c.getString("text");

                    HashMap<String, String> sign = new HashMap<>();

                    sign.put("title", title);
                    sign.put("location", location);
                    sign.put("text", text);

                    nearbyList.add(sign);
                }



        }
        catch(Exception e) {
            String helpMe = e.toString();


        }

        return null;

    }


    //Depending on the contents of the list from the last method, this displays the list or a message
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        if(nearbyList.isEmpty()) {
            nearbyMessage.setVisibility(View.VISIBLE);
            nearbyView.setAdapter(null);
        }
        else {
            nearbyMessage.setVisibility(View.INVISIBLE);
            nearbyView.setAdapter(null);
            adapter = new SimpleAdapter(context, nearbyList,
                    R.layout.list_item, new String[]{"title", "location"},
                    new int[]{R.id.signName, R.id.signLocation});
            nearbyView.setAdapter(adapter);
            if(MainActivity.notifOn == true) {
                MainActivity.notifyUser();
            }

        }

    }

}
