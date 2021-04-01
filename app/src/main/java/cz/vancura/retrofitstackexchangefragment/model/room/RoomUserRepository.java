package cz.vancura.retrofitstackexchangefragment.model.room;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import cz.vancura.retrofitstackexchangefragment.model.UserPOJO;
import cz.vancura.retrofitstackexchangefragment.view.MainActivity;

import static cz.vancura.retrofitstackexchangefragment.viewmodel.MainActivityViewModel.errorLiveData;
import static cz.vancura.retrofitstackexchangefragment.viewmodel.MainActivityViewModel.userPojoList;
import static cz.vancura.retrofitstackexchangefragment.viewmodel.MainActivityViewModel.userPojoListLiveData;

/*
Room dB - repository - encapsulation of CRUD methods - here only ready and insert data from/to dB
 */
// TODO AsyncTask is depreciated - replace

public class RoomUserRepository {

    private static String TAG = "myTAG-RoomUserRepository";

    // db instance
    static RoomDatabase db;
    // dao for interaction with the database
    private RoomUserDao roomUserDao;

    Context context;

    // constructor
    public RoomUserRepository(Context context) {
        Log.d(TAG, "constructor");
        this.context = context;
        db = RoomDatabase.getDatabase(context);
        roomUserDao = db.userDao();
    }


    ////////////////////////////////////////////////////////////////////////////// READ

    // db read wrapper
    public void getAllUsers() {
        Log.d(TAG, "getAllUsers");
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

                // clear List - zabranit aby se data duplikovala po opakovanem Refresh
                userPojoList.clear();

                // loop via List and fill in List
                for (RoomUserPOJO user : users) {
                    userPojoList.add(new UserPOJO(user.getUser_id(), user.getUser_name(), user.getUser_icon_url()));
                }

                // UpdateGUI - ok
                Log.d(TAG, "Room finished with data, now refresh GUI");

                // LiveData update - will trigger GUI update
                userPojoListLiveData.setValue(userPojoList);

            }else{

                // muze nastat pouze teoreticky, protoze pokud apka nema zadna data - zastavi se uz v MainActivity

                // UpdateGUI
                Log.d(TAG, "Room finished without data, now refresh GUI");

                // LiveData update - will trigger GUI update
                errorLiveData.setValue("No data in dB - sorry :(");

            }

        }
    }


    ////////////////////////////////////////////////////////////////////////////// INSERT

    // insert wrapper
    public void insertUser (RoomUserPOJO user) {
        new InsertAsyncTask((MainActivity) context, user).execute();
    }


    // db insert - async job
    private static class InsertAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<MainActivity> activityReference;
        private RoomUserPOJO user;

        // only retain a weak reference to the activity
        InsertAsyncTask(MainActivity context, RoomUserPOJO user) {
            activityReference = new WeakReference<>(context);
            this.user = user;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            db.userDao().insertUser(user);
            return true;
        }

        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(Boolean bool) {
            //empty
            Log.d(TAG, "InsertAsyncTask onPostExecute - user inserted into dB - user name=" + user.getUser_name());
        }
    }



}
