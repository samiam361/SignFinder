package com.example.signfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText username = (EditText)findViewById(R.id.userNameBox);
    EditText password = (EditText)findViewById(R.id.passwordBox);

    public void signUp() {
        //check database for username is unique
        //if (username is unique && password is not blank) {
            //allow sign up (put info in database)
            //start mainActivity
        //else if(password is blank)
            //display message about password not being blank
        //else {
            //display message about username not being unique
    }

    public void logIn() {
        //check username/password match
        //if(both match)
            //allow logIn and start mainActivity
        //else
            //display message about username/password are incorrect
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button signUp = (Button)findViewById(R.id.signUpButton);
        Button logIn = (Button)findViewById(R.id.logInButton);

        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signUp();
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View z) {
                logIn();
            }
        });
    }
}
