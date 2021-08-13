package com.example.bbcnewsreader.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bbcnewsreader.R;
import com.example.bbcnewsreader.beans.Item;
import com.example.bbcnewsreader.beans.Parser;
import com.example.bbcnewsreader.dao.ApplicationDao;
import com.google.android.material.navigation.NavigationView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavoritesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public List<Item> items = null;
    public boolean isTablet = false;

    public static final String ITEM_ID = "ID";
    public static final String ITEM_TITLE = "TITLE";
    public static final String ITEM_Date = "Date";
    public static final String ITEM_Description = "Description";
    public static final String ITEM_LINK = "LINK";


    public FavoriteReader favoriteReader = new FavoriteReader();
    public FavoriteAdapter favoriteAdapter = null;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String ThemeColor = "themeColor";
    SharedPreferences prefs;

    EditText filterText = null;

    @Override
    protected void onRestart() {
        super.onRestart();
        //Retrieve favorites from DB
        ApplicationDao dao = new ApplicationDao();
        List<Item> items = dao.loadFavorites(FavoritesActivity.this);
        favoriteAdapter.updateAdapter(items);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Read theme color setting from shared preferences
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String themeColor = prefs.getString(ThemeColor, null);

        // Check if user has set color preference and apply preference
        androidx.appcompat.widget.Toolbar tBar = findViewById(R.id.toolbar);
        if (themeColor.equals("Yellow")){
            tBar.setBackgroundColor(Color.parseColor("#FFC107"));
        }
        else {
            tBar.setBackgroundColor(Color.parseColor("#FAD7F6"));
        }
        setSupportActionBar(tBar);
        setTitle("My Favorites");


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Check if device is phone or tablet
        isTablet = findViewById(R.id.fragmentLocation) != null;

        favoriteReader.execute();

        ListView favoriteListView = findViewById(R.id.listView_Favorites);

        favoriteListView.setOnItemClickListener((parent, view, position, id) -> {
            // Create and pass bundle
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_TITLE, items.get(position).getTitle());
            dataToPass.putString(ITEM_Description, items.get(position).getDescription());
            dataToPass.putString(ITEM_LINK, items.get(position).getLink());
            dataToPass.putString(ITEM_Date, items.get(position).getDate());
            dataToPass.putLong(ITEM_ID, id);

            // If device is tablet, load appropriate fragment
            if(isTablet)
            {
                DetailsFragment dFragment = new DetailsFragment();
                dFragment.setArguments( dataToPass );
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment)
                        .commit();
            }
            else
            {
                Intent nextActivity = new Intent(FavoritesActivity.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass);
                startActivity(nextActivity);
            }
        });

        filterText = findViewById(R.id.editText_filter);
        filterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = filterText.getText().toString().toLowerCase(Locale.getDefault());
                favoriteAdapter.filter(text);
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                message = "View chosen articles";
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.main_page:
                startActivity(new Intent(FavoritesActivity.this, MainActivity.class));
                break;
            case R.id.rss_reader_page:
                startActivity(new Intent(FavoritesActivity.this, RssReaderActivity.class));
                break;
            case R.id.favorites_page:
                startActivity(new Intent(FavoritesActivity.this, FavoritesActivity.class));
                break;
            case R.id.settings_page:
                startActivity(new Intent(FavoritesActivity.this, SettingsActivity.class));
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    private class FavoriteReader extends AsyncTask< String, Integer, List<Item>> {

        public List<Item> doInBackground(String ... args)
        {
            try
            {
                ApplicationDao dao = new ApplicationDao();
                items = dao.loadFavorites(FavoritesActivity.this);
            }
            catch (Exception e)
            {
                Log.e("Error", e.getMessage());
            }

            return items;
        }

        public void onProgressUpdate(Integer ... args) {

        }

        public void onPostExecute(List<Item> itemsList) {
            favoriteAdapter = new FavoriteAdapter(itemsList);
            ListView favoriteListView = findViewById(R.id.listView_Favorites);
            favoriteListView.setAdapter(favoriteAdapter);
        }
    }


    private class FavoriteAdapter extends BaseAdapter {

        List<Item> itemsList = new ArrayList<>();

        FavoriteAdapter(List<Item> itemsList){
            this.itemsList.addAll(itemsList);
        }

        @Override
        public int getCount() { return itemsList.size(); }

        @Override
        public Item getItem(int position){
            return itemsList.get(position);
        }

        @Override
        public long getItemId(int position){return getItem(position).getId();}

        @Override
        public View getView(int position, View old, ViewGroup parent) {
            View newView = old;
            LayoutInflater inflater = getLayoutInflater();
            if(newView==null){
                newView = inflater.inflate(R.layout.row_layout, parent, false);
            }

            TextView listItemTextView = newView.findViewById(R.id.textListViewItem);

            listItemTextView.setText(itemsList.get(position).getTitle());

            return newView;
        }

        public void filter(String searchText) {
            searchText = searchText.toLowerCase(Locale.getDefault());
            itemsList.clear();
            if (searchText.length() == 0) {
                itemsList.addAll(items);
            }
            else
            {
                for (Item item : items)
                {
                    if (item.getTitle().toLowerCase(Locale.getDefault()).contains(searchText))
                    {
                        itemsList.add(item);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public void updateAdapter(List<Item> itemsList){
            this.itemsList.clear();
            this.itemsList.addAll(itemsList);
            notifyDataSetChanged();
        }
    }
}