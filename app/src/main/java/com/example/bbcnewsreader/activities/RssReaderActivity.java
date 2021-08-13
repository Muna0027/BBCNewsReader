package com.example.bbcnewsreader.activities;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.bbcnewsreader.R;
import com.example.bbcnewsreader.beans.Item;
import com.example.bbcnewsreader.beans.Parser;
import com.google.android.material.navigation.NavigationView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RssReaderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public List<Item> items = null;
    public boolean isTablet = false;


    public static final String ITEM_ID = "ID";
    public static final String ITEM_TITLE = "TITLE";
    public static final String ITEM_Date = "Date";
    public static final String ITEM_Description = "Description";
    public static final String ITEM_LINK = "LINK";


    public RssReader rssReader = new RssReader();
    public RssAdapter rssAdapter = null;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String ThemeColor = "themeColor";
    SharedPreferences prefs;

    EditText filterText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_reader);

        // Set theme Color
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String themeColor = prefs.getString(ThemeColor, null);


        androidx.appcompat.widget.Toolbar tBar = findViewById(R.id.toolbar);

        if (themeColor.equals("Yellow")){
            tBar.setBackgroundColor(Color.parseColor("#FFC107"));
        }
        else {
            tBar.setBackgroundColor(Color.parseColor("#FAD7F6"));
        }
        setSupportActionBar(tBar);
        setTitle("News Feeds");


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        isTablet = findViewById(R.id.fragmentLocation) != null;


        rssReader.execute("https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");

        ListView rssListView = findViewById(R.id.listView_Rss_Reader);


        rssListView.setOnItemClickListener((parent, view, position, id) -> {

            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_TITLE, items.get(position).getTitle());
            dataToPass.putString(ITEM_Description, items.get(position).getDescription());
            dataToPass.putString(ITEM_LINK, items.get(position).getLink());
            dataToPass.putString(ITEM_Date, items.get(position).getDate());
            dataToPass.putLong(ITEM_ID, id);

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
                Intent nextActivity = new Intent(RssReaderActivity.this, EmptyActivity.class);
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
                rssAdapter.filter(text);
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
                message = "RSS Feeds: View all articles";
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
                startActivity(new Intent(RssReaderActivity.this, MainActivity.class));
                break;
            case R.id.rss_reader_page:
                startActivity(new Intent(RssReaderActivity.this, RssReaderActivity.class));
                break;
            case R.id.favorites_page:
                startActivity(new Intent(RssReaderActivity.this, FavoritesActivity.class));
                break;
            case R.id.settings_page:
                startActivity(new Intent(RssReaderActivity.this, SettingsActivity.class));
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    private class RssReader extends AsyncTask< String, Integer, List<Item>> {

        public List<Item> doInBackground(String ... args)
        {
            try
            {
                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                Parser parser = new Parser();
                items = parser.parse(inputStream);
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
            rssAdapter = new RssAdapter(itemsList);
            ListView rssListView = findViewById(R.id.listView_Rss_Reader);
            rssListView.setAdapter(rssAdapter);
        }
    }


    private class RssAdapter extends BaseAdapter {

        List<Item> itemsList = new ArrayList<>();

        RssAdapter(List<Item> itemsList){
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

        /**
         * This method filters the ListView using whatever valued is passed into it.
         *
         * @param  searchText  the string value of the filter that will be applied
         */
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
    }
}