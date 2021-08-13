package com.example.bbcnewsreader.activities;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.bbcnewsreader.R;
import com.example.bbcnewsreader.beans.Item;
import com.example.bbcnewsreader.dao.ApplicationDao;

public class DetailsFragment extends Fragment {

    private Bundle dataFromActivity;
    private AppCompatActivity parentActivity;
    private Menu menu = null;
    private Item listViewItem = null;

    private long id = -1;
    private String title = null;
    private String date = null;
    private String description = null;
    private String link = null;


    // Database coloumn names
    private static final String ITEM_ID = "ID";
    private static final String ITEM_TITLE = "TITLE";
    private static final String ITEM_Date = "Date";
    private static final String ITEM_Description = "Description";
    private static final String ITEM_LINK = "LINK";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_toolbar_menu, menu);
        this.menu = menu;

        // Check to see if ApplicationDao has been created, if not create ApplicationDao.
        if(id == -1) {
            ApplicationDao dao = new ApplicationDao();
            id = dao.checkItem(title, date, getContext());
        }

        if(id == -1){
            menu.getItem(0).setIcon(R.drawable.ic_not_favorite);
        }
        else{
            menu.getItem(0).setIcon(R.drawable.ic_favorite);
        }

        listViewItem = new Item(id, title, description, link, date);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String message = null;

        // Check if selected option is a favorite, if not favorite
        switch(item.getItemId())
        {
            case R.id.favorite_item:
                ApplicationDao dao = new ApplicationDao();
                if(listViewItem.getId() == -1){
                    long dbId = dao.addItem(listViewItem, getContext());
                    if(dbId > -1){
                        message = "Added to favorites";
                        listViewItem.setId(dbId);
                        item.setIcon(R.drawable.ic_favorite);
                    }
                    else{
                        message = "Failed to add to favorites";
                    }
                }
                else{
                    int affectedraws = dao.deleteItem(listViewItem.getId(), getContext());
                    if(affectedraws > 0){
                        message = "Removed from favorites!";
                        item.setIcon(R.drawable.ic_not_favorite);
                    }
                    else{
                        message = "Failed to remove from favorites";
                    }
                }
                break;
            case R.id.help_item:
                message = "Your listed favorites";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataFromActivity = getArguments();


        // Read data from activity and inflate view with retrieved data.
        View result =  inflater.inflate(R.layout.fragment_details, container, false);
        id = dataFromActivity.getLong(ITEM_ID);
        title = dataFromActivity.getString(ITEM_TITLE);
        link = dataFromActivity.getString(ITEM_LINK);
        description = dataFromActivity.getString(ITEM_Description);
        date = dataFromActivity.getString(ITEM_Date);

        // Rebuild link from retrieve data.
        String title_hyper_link = "<a href=\"" + link + "\">"
                        + title + "</a>";
        TextView textViewTitle = (TextView)result.findViewById(R.id.txtView_Title);
        textViewTitle.setText(Html.fromHtml(title_hyper_link));
        textViewTitle.setMovementMethod(LinkMovementMethod.getInstance());

        // Set description from retrieve data
        TextView textViewDescription = (TextView)result.findViewById(R.id.txtView_Description);
        textViewDescription.setText(description);


        TextView textViewDate = (TextView)result.findViewById(R.id.txtView_Date);
        textViewDate.setText("Publication date: " + date);

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (AppCompatActivity)context;
    }
}