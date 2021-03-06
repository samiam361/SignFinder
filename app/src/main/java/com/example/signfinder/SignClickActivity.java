package com.example.signfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import static com.example.signfinder.MainActivity.listID;

public class SignClickActivity extends AppCompatActivity {
    private TextView signTitle, signLoc, signText;

    //Has three different things for each list I am using in the app
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_click);


        //initialize each text view
        signTitle = (TextView) findViewById(R.id.signClicktTitle);
        signLoc = (TextView) findViewById(R.id.signClickLoc);
        signText = (TextView) findViewById(R.id.signClickText);


        //case that list being used is nearbyList
        if(listID == 1) {
            signTitle.setText(MainActivity.nearbyList.get(MainActivity.listPos).get("title"));
            signLoc.setText(MainActivity.nearbyList.get(MainActivity.listPos).get("location"));
            signText.setText(MainActivity.nearbyList.get(MainActivity.listPos).get("text"));

        }
        //case that list being used is visit list
        else if(listID == 2) {
            signTitle.setText(MainActivity.visitList.get(MainActivity.listPos).get("title"));
            signLoc.setText(MainActivity.visitList.get(MainActivity.listPos).get("location"));
            signText.setText(MainActivity.visitList.get(MainActivity.listPos).get("text"));
        }
        //case that list being used is search list
        else {
            signTitle.setText(MainActivity.signList.get(MainActivity.listPos).get("title"));
            signLoc.setText(MainActivity.signList.get(MainActivity.listPos).get("location"));
            signText.setText(MainActivity.signList.get(MainActivity.listPos).get("text"));
        }

    }
}
