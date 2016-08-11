package app.com.iotdroid.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by masfajar on 6/9/2016.
 */
public class AlertUtility {

    private Activity activity;
    public AlertUtility(Activity activity) {
        this.activity = activity;
    }

    /**
     * Method untuk alert Yes only
     * @param message
     * @param dialogInterface
     */
    public void alertYes(String message,DialogInterface.OnClickListener dialogInterface) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", dialogInterface);
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Method alert confirm
     * @param message
     * @param dialogPositive
     * @param dialogNegative
     */
    public void alertConfirm(String message, DialogInterface.OnClickListener dialogPositive, DialogInterface.OnClickListener dialogNegative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", dialogPositive);
        builder.setNegativeButton("No", dialogNegative);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
