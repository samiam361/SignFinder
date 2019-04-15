package com.example.signfinder;

import android.content.Context;
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
import static com.example.signfinder.MainActivity.userName;
import static com.example.signfinder.MainActivity.visitList;
import static com.example.signfinder.MainActivity.visitView;

public class VisitActivity extends AsyncTask<String, String, Void> {

    public String signInfo, latitude, longitude, file;
    private Context context;

    //default constructor
    public VisitActivity() {

    }

    //constructor
    public VisitActivity(Context context) {
        this.context = context;
    }


    //This class works the sam way as the nearby class with different parameters
    @Override
    protected Void doInBackground(String... arg0) {
        try {
            visitList.clear();
            file = (String)arg0[0];
            String link = "http://ec2-18-219-194-235.us-east-2.compute.amazonaws.com/"+file+".php?username="+userName;

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

                visitList.add(sign);
                //if(visitList.contains(sign) == false) {
                //visitList.add(sign);
                //}
            }



        }
        catch(Exception e) {
            String helpMe = e.toString();


        }

        return null;

    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if(visitList.isEmpty()) {
            nearbyMessage.setVisibility(View.VISIBLE);
        }
        else {
            //nearbyMessage.setVisibility(View.INVISIBLE);
            visitView.setAdapter(null);
            adapter = new SimpleAdapter(context, visitList,
                    R.layout.list_item, new String[]{"title", "location"},
                    new int[]{R.id.signName, R.id.signLocation});
            visitView.setAdapter(adapter);
            //MainActivity.notifyUser();
        }

    }

}
