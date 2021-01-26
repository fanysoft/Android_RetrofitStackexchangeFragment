package cz.vancura.retrofitstackexchangefragment.model;

import android.util.Log;


public class UserPOJO {

    private static String TAG = "myTAG-UserPOJO";

    private int user_id;
    private String user_name;
    private String user_icon_url;


    public UserPOJO(int user_id, String user_name, String user_icon_url) {
        Log.d(TAG, "new UserPOJO created");
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_icon_url = user_icon_url;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_icon_url() {
        return user_icon_url;
    }
}
