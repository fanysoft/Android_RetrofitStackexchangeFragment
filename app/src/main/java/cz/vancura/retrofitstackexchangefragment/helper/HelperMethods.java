package cz.vancura.retrofitstackexchangefragment.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Class with Helper methods
 * Could be singelton pattern
 */

public class HelperMethods {

    // is device online ?
    // returns true/false
    // Idea : add listener for internet status change
    // see https://stackoverflow.com/questions/25678216/android-internet-connectivity-change-listener
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    // convert EPOCH time to human-readable
    /*
    public static String ConvertEPOCH(String inputDate){

        String result = "no date";

        if (inputDate != "null") {

            Date date = new Date(Integer.parseInt(inputDate));
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.GERMAN);
            format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            result = format.format(date);

        }

        return result;
    }
    */



}
