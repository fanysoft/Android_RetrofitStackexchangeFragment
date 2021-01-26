package cz.vancura.retrofitstackexchangefragment.model.room;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import cz.vancura.retrofitstackexchangefragment.model.UserPOJO;
import cz.vancura.retrofitstackexchangefragment.view.MainActivity;

import static cz.vancura.retrofitstackexchangefragment.viewmodel.MainActivityViewModel.userPojoList;
import static cz.vancura.retrofitstackexchangefragment.viewmodel.MainActivityViewModel.userPojoListLiveData;


public class RoomUserRepository {

    private static String TAG = "myTAG-RoomUserRepository";

    // db
    static RoomDatabase db;
    // dao for interaction with the database
    private RoomUserDao roomUserDao;


    // constructor
    public RoomUserRepository(Context context) {
        Log.d(TAG, "constructor");
        db = RoomDatabase.getDatabase(context);
        roomUserDao = db.userDao();

    }

    // db read wrapper
    public void getAllUsers(Context context) {
        new ReadAsyncTask((MainActivity)context).execute();
    }


    // insert wrapper
    public void insertUser (Context context, RoomUserPOJO user) {
        new InsertAsyncTask((MainActivity)context, user).execute();
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
            if (users != null && users.size() > 0) {

                Log.d(TAG, "Room dB - size="+users.size());

                // loop via List and fill in List
                // convert from type RoomUserPOJO to type UserPOJO
                // TODO zabranit aby se data duplikovala - po refresh se vytvori uz existujici znova
                for (RoomUserPOJO user : users) {
                    userPojoList.add(new UserPOJO(user.getUser_id(), user.getUser_name(), user.getUser_icon_url()));
                }
                userPojoListLiveData.setValue(userPojoList);

                // UpdateGUI - ok
                Log.d(TAG, "Room finished, now refresh GUI");
                MainActivity.ShowDataGUI(true, "");


            }else{
                Log.d(TAG, "Room dB is empty");

                // UpdateGUI
                Log.d(TAG, "Room finished, now refresh GUI");
                MainActivity.ShowDataGUI(false, "No data in dB");

            }

        }
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

        }
    }



}
