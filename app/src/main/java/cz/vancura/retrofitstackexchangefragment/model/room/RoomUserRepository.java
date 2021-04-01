package cz.vancura.retrofitstackexchangefragment.model.room;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cz.vancura.retrofitstackexchangefragment.model.UserPOJO;
import cz.vancura.retrofitstackexchangefragment.view.MainActivity;

import static cz.vancura.retrofitstackexchangefragment.viewmodel.MainActivityViewModel.errorLiveData;
import static cz.vancura.retrofitstackexchangefragment.viewmodel.MainActivityViewModel.userPojoListLiveData;

/*
Room dB - repository - encapsulation of CRUD methods - here only ready and insert data from/to dB
 */


public class RoomUserRepository {

    private static String TAG = "myTAG-RoomUserRepository";

    Context context;

    // db instance
    static RoomDatabase db;
    // dao for interaction with the database
    private RoomUserDao roomUserDao;

    // constructor
    public RoomUserRepository(Context context) {
        Log.d(TAG, "constructor");
        this.context = context;
        db = RoomDatabase.getDatabase(context);
        roomUserDao = db.userDao();
    }


    ////////////////////////////////////////////////////////////////////////////// READ

    public void getAllUsers() {

        Log.d(TAG, "getAllUsers");

        // TODO AsyncTask is depreciated - replace by ExecutorService or RxJava
        /*
        db.databaseWriteExecutor.execute(() -> {
            roomUserDao.getAllUsers();
        });
*/

        // 1. get all users from db - returns List<RoomUserPOJO>
        // 2. convert to List<UserPOJO>
        // 3. pass to LiveData List in VM


        // old - AsyncTask
        new ReadAsyncTask((MainActivity)context).execute();
    }

    // db read - async job
    private static class ReadAsyncTask extends AsyncTask<Void, Void, List<RoomUserPOJO>> {

        private WeakReference<MainActivity> activityReference;

        // only retain a weak reference to the activity
        ReadAsyncTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<RoomUserPOJO> doInBackground(Void... voids) {
            if (activityReference.get() != null)
                return db.userDao().getAllUsers();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<RoomUserPOJO> users) {

            Log.d(TAG, "ReadAsyncTask onPostExecute");

            if (users != null && users.size() > 0) {

                Log.d(TAG, "Room dB - size="+users.size());

                List<UserPOJO> list = new ArrayList<>();

                // loop via List and fill in List
                // conversion of RoomUserPOJO to UserPOJO - for GUI use
                for (RoomUserPOJO user : users) {
                    list.add(new UserPOJO(user.getUser_id(), user.getUser_name(), user.getUser_icon_url()));
                }

                Log.d(TAG, "Room finished with data, now refresh GUI");
                // LiveData update - will trigger GUI update
                userPojoListLiveData.setValue(list);

            }else{

                // teoretically

                // UpdateGUI
                Log.d(TAG, "Room finished without data, now refresh GUI");

                // LiveData update - will trigger GUI update
                errorLiveData.setValue("No data in dB - sorry :(");

            }

        }
    }


    ////////////////////////////////////////////////////////////////////////////// INSERT

    public void insertUser (RoomUserPOJO user) {

        Log.d(TAG, "insertUser via ExecutorService");

        RoomDatabase.databaseWriteExecutor.execute(() -> {
            roomUserDao.insertUser(user);
        });

    }

    ////////////////////////////////////////////////////////////////////////////// DELETE

    public void deleteAll () {

        Log.d(TAG, "deleteAll via ExecutorService");

        RoomDatabase.databaseWriteExecutor.execute(() -> {
            roomUserDao.deleteAllData();
        });

    }

}
