package com.example.signfinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private SharedPreferences sp;
    public static SharedPreferences.Editor editor;



    /*
        Retrieves the string from the username and password Edit Text views.
        The integer called signIn is a marker for which button was pressed. In this case, the Sing Up button.
        The last line starts the async task.
     */
    public void signUp() {
        uName = username.getText().toString();
        pWord = password.getText().toString();
        signIn = 0;

        new LoginAsync().execute(uName, pWord);
    }



    /*
        Retrieves the string from the username and password Edit Text views.
        The integer called signIn is a marker for which button was pressed. In this case, the Log In button.
        The last line starts the async task.
     */
    public void logIn() {
        uName = username.getText().toString();
        pWord = password.getText().toString();
        signIn = 1;

        new LoginAsync().execute(uName, pWord);
    }



    /*
        Helper method to start the Main Activity on successful log in.
     */
    public void mainActivity() {
        Intent startMainAct = new Intent(this, MainActivity.class);
        startActivity(startMainAct);
    }



    /*
        Creates the Log in Activity. Does several things:
            1. Gets shared preferences to see if the user has marked the box to stay logged in.
            2. Initializes the username and password Edit Text views, message Text view, sign up and log in buttons.
            3. Creates and onClickListener for each button.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("", Context.MODE_PRIVATE);
        editor = sp.edit();
        boolean logged = sp.getBoolean("stay_logged", false);
        String nameSet = sp.getString("username_setting", "");
        if(logged == true && nameSet.equals(" ") == false) {
           mainActivity();
        }

        username = (EditText)findViewById(R.id.userNameBox);
        password = (EditText)findViewById(R.id.passwordBox);

        message = (TextView)findViewById(R.id.logInMessage);

        Button signUp = (Button)findViewById(R.id.signUpButton);
        Button logIn = (Button)findViewById(R.id.logInButton);

        message.setText("Choose Login or Sign Up");

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



    /*
        Login Async task which handles all connections the webserver and database.
     */
    public class LoginAsync extends AsyncTask<String, String, String> {

        public String dbUName, dbPWord = "";


        /*
            Default constructor.
         */
        public LoginAsync() {

        }


        /*
            The main bulk of the work done by the Async task. In general, does the following:
                1. If the Sign Up button was pressed, checks the database for the user entered username. If found,
                   prompts the user for a different username. If not found, checks other username and password requirements
                   and creates the account in the database and prompts the user to press the Log In button.
                2. If the Log In button was pressed, checks the database for the entry corresponding to both the username and
                   password entered by the user. If found, starts the Main Activity. If not found, alerts the user that
                   either username or password is incorrect.
                (See method code for more comments)
         */
        @Override
        protected String doInBackground(String... arg0) {

            //Int tag of the Sign Up button.
            if(signIn == 0) {
                try {

                    /*
                        Username and Password must be checked for spaces which are incompatible with PHP which
                        is used to communicate with the server. The replace method does this.
                     */
                    dbUName = (String)arg0[0];
                    dbUName = dbUName.replace(" ", "_");
                    dbPWord = (String)arg0[1];
                    dbPWord = dbPWord.replace(" ", "_");

                    /*
                        Username and Password cannot be null. This if statement checks for this and alerts the user.
                     */
                    if(dbUName.equals("") || dbPWord.equals("")) {
                        message.setText("Username and Password cannot be blank.");
                        return resultS;
                    }

                    /*
                        The following block of code contacts the server with the username and password as variables
                        in the PHP file called db_signInCheck. See db_signInCheck.php in the PHP Files folder for more details.
                     */
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






                    /*
                        If the string returned from the server is " 0 ", then there are no existing accounts with
                        the specified username. The following block of code contacts the database to create the account
                        useing the file db_signIn.php with the username and password as variables. See db_signIn.php in
                        the PHP Files folder for more details.
                     */
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

                            editor.putString("username_setting", uName);
                            editor.commit();

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
            /*
                In this case the int tag is the Log in button and this block of code executes
                similarly to the above block, this time sending the username and password as variables
                to the PHP file called db_logIn.
             */
            else {
                try {
                    dbUName = (String)arg0[0];
                    dbUName = dbUName.replace(" ", "_");
                    dbPWord = (String)arg0[1];
                    dbPWord = dbPWord.replace(" ", "_");
                    if(dbUName.equals("")|| dbPWord.equals("")) {
                        message.setText("Username and Password cannot be blank.");
                        return resultS;
                    }
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

                    if(resultS.equals(" "+uName+" ") == true) {
                        try {
                            editor.putString("username_setting", uName);
                            editor.commit();
                            MainActivity.userName = uName;
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
