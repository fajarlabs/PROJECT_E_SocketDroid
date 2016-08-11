package app.com.iotdroid.library.sqlite;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Fajar R.D.P
 */
public abstract class DB_SQLiteUtility extends SQLiteOpenHelper {
    private static final String TAG = "DB_SQLiteUtility";

    // Activity
    Activity activity;

    // Database
    public static final String DATABASE_NAME = "DB_SAKLARIOT";
    public static final int DATABASE_VERSION = 1;

    // Define Table
    public static final String TABLE_SWITCH = "TB_SWITCHLABEL";
    public static final String TABLE_SETTING = "TB_SETTING";

    // Define SQL Create
    public static final String CREATE_TABLE_SWITCH = "CREATE TABLE TB_SWITCHLABEL(\n" +
            "   ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "   STACK INT,\n" +
            "   TITLE TEXT, \n" +
            "   DESCRIPTION TEXT \n" +
            ");";
    public static final String CREATE_TABLE_SETTING = "CREATE TABLE TB_SETTING(\n" +
            "   OPTION_NAME CHAR(25),\n" +
            "   OPTION_VALUE TEXT, \n" +
            "   CONSTRAINT option_name_pk PRIMARY KEY (option_name) \n" +
            ");";

    // Define SQL Drop
    public static final String DROP_TABLE_SWITCH = "DROP TABLE  IF EXISTS TB_SWITCHLABEL;";
    public static final String DROP_TABLE_SETTING = "DROP TABLE  IF EXISTS TB_SETTING;";

    public DB_SQLiteUtility(Activity activity) {
        super(activity, DATABASE_NAME, null, DATABASE_VERSION, null);
        this.activity = activity;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_SETTING);
            db.execSQL(CREATE_TABLE_SWITCH);
            Toast.makeText(activity, "Create", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Database tables created");

        } catch (SQLiteException e) {
            Toast.makeText(activity, "Error Create", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE_SETTING);
            db.execSQL(DROP_TABLE_SWITCH);
            onCreate(db);
        } catch (SQLiteException e) {
            Toast.makeText(activity, "Error drop", Toast.LENGTH_LONG).show();
        }
    }
}