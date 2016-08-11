package app.com.iotdroid.module.setting.sqlite.dao;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Map;

import app.com.iotdroid.library.sqlite.DB_SQLiteUtility;
import app.com.iotdroid.module.setting.sqlite.model.SettingModel;


/**
 * Created by masfajar on 6/22/2016.
 */
public class SettingDAO extends DB_SQLiteUtility {
    private final String TAG = SettingDAO.class.getSimpleName();

    public static final String KEYID = "OPTION_NAME";
    public static final String KEYVAL = "OPTION_VALUE";

    /**
     * Constructor
     * @param activity
     */
    public SettingDAO(Activity activity) {
        super(activity);
    }

    /**
     * Insert data
     * @param settingModel
     */
    public void insertData(SettingModel settingModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEYID, settingModel.getOptionName());
        contentValues.put(KEYVAL, settingModel.getOptionValue());
        long id = db.insert(TABLE_SETTING, null, contentValues);
        //db.close();
    }

    /**
     * Select Data
     * @return
     */
    public ArrayList<SettingModel> selectData(Map<String,String> joinMap) {
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

        query = "SELECT * FROM "+TABLE_SETTING+" "+str_con;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<SettingModel> result = new ArrayList<>();
        if (cursor.getCount() > 0) {
            do{
                SettingModel settingModel = new SettingModel();
                settingModel.setOptionName(cursor.getString(0));
                settingModel.setOptionValue(cursor.getString(1));
                result.add(settingModel);
            }while(cursor.moveToNext());
        }
        cursor.close();
        //sqLiteDatabase.close();
        return result;
    }

    /**
     * Delete Data
     * @param settingModel
     */
    public void deleteData(SettingModel settingModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(settingModel == null) {
            db.execSQL("DELETE FROM "+ TABLE_SETTING);
        } else {
            db.delete(TABLE_SETTING, KEYID+" = ?", new String[]{String.valueOf(settingModel.getOptionName())});
        }
    }

    /**
     * Update data
     * @param settingModel
     */
    public void updateData(SettingModel settingModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEYID, settingModel.getOptionName()); //These Fields should be your String values of actual column names
        cv.put(KEYVAL, settingModel.getOptionValue());
        db.update(TABLE_SETTING, cv, KEYID+" = ?", new String[]{settingModel.getOptionName()});
    }
}
