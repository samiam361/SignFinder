package com.example.signfinder;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    TextView message;
    String uName, pWord, logInMessage = "";
    String resultS;
    int signIn = 0;

    public void signUp() {
        uName = username.getText().toString();
        pWord = password.getText().toString();
        signIn = 0;

        new LoginAsync().execute(uName, pWord);
    }

    public void logIn() {
        uName = username.getText().toString();
        pWord = password.getText().toString();
        signIn = 1;

        new LoginAsync().execute(uName, pWord);
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

        message = (TextView)findViewById(R.id.logInMessage);

        Button signUp = (Button)findViewById(R.id.signUpButton);
        Button logIn = (Button)findViewById(R.id.logInButton);

        message = (TextView)findViewById(R.id.logInMessage);
        message.setVisibility(View.INVISIBLE);

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

    public class LoginAsync extends AsyncTask<String, String, String> {

        public String dbUName, dbPWord = "";

        public LoginAsync() {

        }

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {
            if(signIn == 0) {
                try {
                    dbUName = (String)arg0[0];
                    dbPWord = (String)arg0[1];
                    String link = "http://ec2-18-219-194-235.us-east-2.compute.amazonaws.com/db_signInCheck.php?username="+dbUName+"&password="+dbPWord;

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
                        break;
                    }

                    resultS = sb.toString();







                    boolean help = resultS.equals(" 0 ");
                    if(help == true) {
                        try {
                            link = "http://ec2-18-219-194-235.us-east-2.compute.amazonaws.com/db_signIn.php?username="+dbUName+"&password="+dbPWord;

                            url = new URL(link);
                            client = new DefaultHttpClient();
                            request = new HttpGet();
                            request.setURI(new URI(link));
                            response = client.execute(request);
                            in = new BufferedReader(new
                                    InputStreamReader(response.getEntity().getContent()));

                            logInMessage = "Account creation successful! Please log in.";
                            message.setText(logInMessage);
                            message.setVisibility(View.VISIBLE);


                        }
                        catch(Exception e) {
                            logInMessage = e.getMessage();
                            message.setText(logInMessage);
                            message.setVisibility(View.VISIBLE);
                        }

                    }
                    else {
                        logInMessage = "Username already in use. Please choose another.";
                        message.setText(logInMessage);
                        message.setVisibility(View.VISIBLE);
                    }





                    return resultS;
                } catch(Exception e) {
                    logInMessage = "Error: " + e.getMessage();
                    message.setText(logInMessage);
                    message.setVisibility(View.VISIBLE);
                }
            }
            else {
                try {
                    dbUName = (String)arg0[0];
                    dbPWord = (String)arg0[1];
                    String link = "http://ec2-18-219-194-235.us-east-2.compute.amazonaws.com/db_logIn.php?username="+dbUName+"&password="+dbPWord;

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
                        break;
                    }

                    resultS = sb.toString();

                    if(resultS.equals(" 1 ") == true) {
                        try {


                            mainActivity();

                        }
                        catch(Exception e) {
                            logInMessage = e.getMessage();
                            message.setText(logInMessage);
                            message.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        logInMessage = "Username or Password Incorrect";
                        message.setText(logInMessage);
                        message.setVisibility(View.VISIBLE);
                    }


                    return resultS;
                } catch(Exception e) {
                    logInMessage = "Error: Can't check database";
                    message.setText(logInMessage);
                    message.setVisibility(View.VISIBLE);
                }
            }

            return resultS;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
}
