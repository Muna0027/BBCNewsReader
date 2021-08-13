package com.example.bbcnewsreader.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.bbcnewsreader.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String ThemeColor = "themeColor";
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the theme color
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String themeColor = prefs.getString(ThemeColor, null);
        if(themeColor==null){
            themeColor="Yellow";
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(ThemeColor, "Yellow");
            editor.commit();
        }

        androidx.appcompat.widget.Toolbar tBar = findViewById(R.id.toolbar);

        if (themeColor.equals("Yellow")){
            tBar.setBackgroundColor(Color.parseColor("#FFC107"));
        }
        else {
            tBar.setBackgroundColor(Color.parseColor("#FAD7F6"));
        }
        setSupportActionBar(tBar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button btn_rss_reader = findViewById(R.id.BtnRssReader);
        btn_rss_reader.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, RssReaderActivity.class));
            }
        });

        Button btn_favorites = findViewById(R.id.BtnFavorites);
        btn_favorites.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        switch(item.getItemId())
        {
            case R.id.help_item:
                message = "BBC News Reader App";
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Navigation
        switch(item.getItemId())
        {
            case R.id.main_page:
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;
            case R.id.rss_reader_page:
                startActivity(new Intent(MainActivity.this, RssReaderActivity.class));
                break;
            case R.id.favorites_page:
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                break;
            case R.id.settings_page:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }
}