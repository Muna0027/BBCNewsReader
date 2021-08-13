package com.example.bbcnewsreader.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.bbcnewsreader.R;
import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String ThemeColor = "themeColor";

    SharedPreferences prefs;
    String themeColor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // set the theme color
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String themeColor = prefs.getString(ThemeColor, null);

        androidx.appcompat.widget.Toolbar tBar = findViewById(R.id.toolbar);

        if (themeColor.equals("Yellow")){
            tBar.setBackgroundColor(Color.parseColor("#FFC107"));
            RadioButton yellow = findViewById(R.id.radioBtn_Yellow);
            yellow.setChecked(true);
        }
        else {
            tBar.setBackgroundColor(Color.parseColor("#FAD7F6"));
            RadioButton purple = findViewById(R.id.radioBtn_Purple);
            purple.setChecked(true);
        }
        setSupportActionBar(tBar);
        setTitle("Settings");


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


            RadioGroup radioBtnGroup = (RadioGroup) findViewById(R.id.radioBtnGrp);
            Button btnSave = (Button) findViewById(R.id.btnSave);

            btnSave.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (radioBtnGroup.getCheckedRadioButtonId() != -1)
                    {
                        RadioButton selectedRadioButton = findViewById(radioBtnGroup.getCheckedRadioButtonId());
                        String color = selectedRadioButton.getText().toString();

                        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(ThemeColor, color);
                        editor.commit();

                        androidx.appcompat.widget.Toolbar tBar = findViewById(R.id.toolbar);
                        if (color.equals("Yellow")){
                            tBar.setBackgroundColor(Color.parseColor("#FFC107"));
                        }
                        else {
                            tBar.setBackgroundColor(Color.parseColor("#FAD7F6"));
                        }

                        Toast.makeText(SettingsActivity.this, "Settings saved", Toast.LENGTH_LONG);
                    }
                }

            });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.main_page:
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                break;
            case R.id.rss_reader_page:
                startActivity(new Intent(SettingsActivity.this, RssReaderActivity.class));
                break;
            case R.id.favorites_page:
                startActivity(new Intent(SettingsActivity.this, FavoritesActivity.class));
                break;
            case R.id.settings_page:
                startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }
}