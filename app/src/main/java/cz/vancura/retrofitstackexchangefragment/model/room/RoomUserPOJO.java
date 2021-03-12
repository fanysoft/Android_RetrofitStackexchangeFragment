package cz.vancura.retrofitstackexchangefragment.model.room;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/*
Room dB - POJO class
 */

@Entity
public class RoomUserPOJO {

    private static String TAG = "myTAG-RoomUserPOJO";

    @PrimaryKey(autoGenerate = true)
    private int my_user_id;

    @ColumnInfo(name = "user_id")
    private int user_id;

    @ColumnInfo(name = "user_name")
    private String user_name;

    @ColumnInfo(name = "user_icon_url")
    private String user_icon_url;

    // constructor
    public RoomUserPOJO(int user_id, String user_name, String user_icon_url) {
        Log.d(TAG, "New RoomUserPOJO created.. " + user_name);
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_icon_url = user_icon_url;
    }


    // Getters Setters ..

    public int getMy_user_id() {
        return my_user_id;
    }

    public void setMy_user_id(int my_user_id) {
        this.my_user_id = my_user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_icon_url() {
        return user_icon_url;
    }

    public void setUser_icon_url(String user_icon_url) {
        this.user_icon_url = user_icon_url;
    }
}
