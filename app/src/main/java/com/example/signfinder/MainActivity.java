package com.example.signfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.Menu;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Spinner searchSpin;
    private BottomNavigationView navigation;
    private int nav;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_nearby);
                    setContentView(R.layout.activity_main);
                    nav = R.id.navigation;
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
                    searchSpin = (Spinner) findViewById(R.id.spinner);
                    popSpinner();
                    navSetUp();
                    return true;
            }
            return false;
        }
    };

    public void popSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_options, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        searchSpin.setAdapter(adapter);

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

        mTextMessage = (TextView) findViewById(R.id.message);
        nav = R.id.navigation;
        navSetUp();
        ListView listView = (ListView) findViewById(R.id.listviewNear);
    }

}
