package com.example.signfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    String uName, pWord = "";

    public void signUp() {
        mainActivity();
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
        mainActivity();
        //check username/password match
        //if(both match)
            //allow logIn and start mainActivity
        //else
            //display message about username/password are incorrect
    }

    public void mainActivity() {
        Intent startMainAct = new Intent(this, MainActivity.class);
        startActivity(startMainAct);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText)findViewById(R.id.userNameBox);
        password = (EditText)findViewById(R.id.passwordBox);

        Button signUp = (Button)findViewById(R.id.signUpButton);
        Button logIn = (Button)findViewById(R.id.logInButton);

        uName = username.getText().toString();
        pWord = password.getText().toString();

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
