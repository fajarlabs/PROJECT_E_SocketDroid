package app.com.iotdroid.module.main.sqlite.dao;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Map;

import app.com.iotdroid.library.sqlite.DB_SQLiteUtility;
import app.com.iotdroid.module.main.sqlite.model.SwitchLabelModel;

/**
 * Created by masfajar on 6/23/2016.
 */
public class SwitchLabelDAO extends DB_SQLiteUtility {
    private final String TAG = SwitchLabelDAO.class.getSimpleName();

    public static final String KEYID = "ID";
    public static final String KEYSTACK = "STACK";
    public static final String KEYTITLE = "TITLE";
    public static final String KEYDESC = "DESCRIPTION";

    /**
     * Constructor
     * @param activity
     */
    public SwitchLabelDAO(Activity activity) {
        super(activity);
    }

    /**
     * Insert Data
     * @param switchLabelModel
     */
    public void insertData(SwitchLabelModel switchLabelModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEYID, switchLabelModel.getId());
        contentValues.put(KEYSTACK, switchLabelModel.getStack());
        contentValues.put(KEYTITLE, switchLabelModel.getTitle());
        contentValues.put(KEYDESC, switchLabelModel.getDescription());
        long id = db.insert(TABLE_SWITCH, null, contentValues);
        db.close();
    }

    /**
     * Select Data
     * @param joinMap
     * @return
     */
    public ArrayList<SwitchLabelModel> selectData(Map<String,String> joinMap) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String str_con = "";
        String query = "";
        if(joinMap != null) {
            int join = 0;
            if (joinMap.size() > 0) {
                str_con += "WHERE ";
                join = joinMap.size() - 1;

                int iterate = 0;
                for (Map.Entry<String, String> entry : joinMap.entrySet()) {
                    str_con += entry.getKey() + " = '" + entry.getValue()+"'";
                    if (iterate != join) {
                        str_con += ",";
                    }
                    iterate++;
                }
            }
        }
        query = "SELECT * FROM "+TABLE_SWITCH+" "+str_con;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<SwitchLabelModel> result = new ArrayList<>();
        if (cursor.getCount() > 0) {
            do{
                SwitchLabelModel switchLabelModel = new SwitchLabelModel();
                switchLabelModel.setStack(cursor.getInt(1));
                switchLabelModel.setTitle(cursor.getString(2));
                switchLabelModel.setDescription(cursor.getString(3));
                result.add(switchLabelModel);
            }while(cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();

        return result;
    }

    /**
     * Delete data
     * @param switchLabelModel
     */
    public void deleteData(SwitchLabelModel switchLabelModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(switchLabelModel == null) {
            db.execSQL("DELETE FROM "+ TABLE_SWITCH);
        } else {
            db.delete(TABLE_SWITCH, KEYID+" = ?", new String[]{String.valueOf(switchLabelModel.getId())});
        }
    }

    /**
     * Update data
     * @param switchLabelModel
     */
    public void updateData(SwitchLabelModel switchLabelModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEYSTACK, switchLabelModel.getStack()); //These Fields should be your String values of actual column names
        cv.put(KEYTITLE, switchLabelModel.getTitle());
        cv.put(KEYDESC, switchLabelModel.getDescription());
        db.update(TABLE_SWITCH, cv, KEYSTACK+" = ?", new String[]{String.valueOf(switchLabelModel.getStack())});
    }
}
