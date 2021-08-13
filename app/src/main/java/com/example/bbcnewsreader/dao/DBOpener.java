package com.example.bbcnewsreader.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "BBC_News_DB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "BBC_NEWS";

    public final static String COL_ID = "_id";
    public final static String COL_TITLE = "TITLE";
    public final static String COL_DESCRIPTION = "DESCRIPTION";
    public final static String COL_LINK = "LINK";
    public final static String COL_DATE = "DATE";
    public final static String COL_IS_FAVORITE = "IS_FAVORITE";

    public DBOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT,"
                + COL_DESCRIPTION + " TEXT,"
                + COL_LINK + " TEXT,"
                + COL_DATE + " TEXT,"
                + COL_IS_FAVORITE  + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }
}