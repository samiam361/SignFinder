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
import static com.example.signfinder.MainActivity.results;
import static com.example.signfinder.MainActivity.signList;
import static com.example.signfinder.MainActivity.signsView;

public class SearchActivity extends AsyncTask<String, String, Void> {

    public String signInfo, item, file;
    private Context context;

    public SearchActivity() {

    }

    public SearchActivity(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... arg0) {
        try {
            if(!signList.isEmpty()){
                signList.clear();
            }


            file = (String)arg0[1];
            item = (String)arg0[0];
            item = item.replace(" ", "_");
            String link = "http://ec2-18-219-194-235.us-east-2.compute.amazonaws.com/"+file+".php?city="+item;

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
            //char a = signInfo.charAt(4735);


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

                signList.add(sign);
            }

        }
        catch(Exception e) {
            String helpMe = e.toString();
            //return helpMe;
        }

        return null;

    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        signsView.setAdapter(null);
        if(signList.isEmpty() == true) {
            results.setVisibility(View.VISIBLE);
        }
        else {
            results.setVisibility(View.INVISIBLE);
        }
        adapter = new SimpleAdapter(context, signList,
                R.layout.list_item, new String[]{"title", "location"},
                new int[]{R.id.signName, R.id.signLocation});
        signsView.setAdapter(adapter);
    }

}
