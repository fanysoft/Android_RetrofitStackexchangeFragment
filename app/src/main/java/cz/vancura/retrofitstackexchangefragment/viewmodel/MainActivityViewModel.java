package cz.vancura.retrofitstackexchangefragment.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;


import cz.vancura.retrofitstackexchangefragment.model.UserPOJO;
import cz.vancura.retrofitstackexchangefragment.model.retrofit.RetrofitAPIClient;
import cz.vancura.retrofitstackexchangefragment.model.retrofit.RetrofitAPIInterface;
import cz.vancura.retrofitstackexchangefragment.model.retrofit.RetrofitUserPOJO;
import cz.vancura.retrofitstackexchangefragment.model.room.RoomUserPOJO;
import cz.vancura.retrofitstackexchangefragment.model.room.RoomUserRepository;
import cz.vancura.retrofitstackexchangefragment.view.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cz.vancura.retrofitstackexchangefragment.view.MainActivity.sharedPrefEditor;


public class MainActivityViewModel extends ViewModel {

    // Variables
    private static String TAG = "myTAG-MainActivityViewModel";

    RetrofitAPIInterface retrofitAPIInterface = RetrofitAPIClient.getClient().create(RetrofitAPIInterface.class);
    static RoomUserRepository roomUserRepository;

    // retrofit config
    public static int retrofitUrlPage = 1;
    int retrofitUrlPagesize = 15;
    String retrofitUrlSite = "stackoverflow";
    public static boolean retrofitShouldLoadMore = true;

    // Lists
    public static List<UserPOJO> userPojoList = new ArrayList<>(); // List
    public static MutableLiveData<List<UserPOJO>> userPojoListLiveData; // LiveData List


    // LiveData getter
    public MutableLiveData<List<UserPOJO>> getLiveData() {
        if (userPojoListLiveData == null) {
            userPojoListLiveData = new MutableLiveData<List<UserPOJO>>();
        }
        return userPojoListLiveData;
    }


    // if online - get data from Retrofit
    public void gimeMeRetrofitData(int urlPage) {

        Log.d(TAG, "gimeMeRetrofitData - page=" + urlPage);

        roomUserRepository = new RoomUserRepository(MainActivity.context);

        retrofitShouldLoadMore = false;

        // Retrofit HTTP call
        Call<RetrofitUserPOJO> call = retrofitAPIInterface.doGetUserList(urlPage, retrofitUrlPagesize, retrofitUrlSite);
            call.enqueue(new Callback<RetrofitUserPOJO>() {
            @Override
            public void onResponse(Call<RetrofitUserPOJO> call, Response<RetrofitUserPOJO> response) {

                // response code {200 is OK)
                String urlResponseCode = "HTTP Response code=" + String.valueOf(response.code() + "\n");
                Log.d(TAG, urlResponseCode);

                if (response.code() == 200){
                    // ok

                    RetrofitUserPOJO resource = response.body();
                    // Log.d(TAG, "resource=" + resource); // cz.vancura.retrofitstackexchange.model.UserPOJO@53db750

                    // Data level 1
                    //Log.d(TAG, "data - level 1 - hasMore=" + resource.hasMore);
                    //Log.d(TAG, "data - level 1 - backoff=" + resource.backoff);
                    //Log.d(TAG, "data - level 1 - quota_max=" + resource.quotaMax);
                    Log.d(TAG, "data - level 1 - quota_remaining=" + resource.quotaRemaining);

                    List<RetrofitUserPOJO.Item> itemList = resource.items;
                    //Log.d(TAG, "data - level 1 - items=" + itemList);

                    // Data level 2 - pod Items

                    int i = 0;
                    for (RetrofitUserPOJO.Item item:itemList) {

                        Log.d(TAG, "data - level 2 - loop in item  ________________________________________");
                        //Log.d(TAG, "data - level 2 - loop in item - isAccepted=" + item.isAccepted);
                        //Log.d(TAG, "data - level 2 - loop in itme - score=" + item.score);
                        //Log.d(TAG, "data - level 2 - loop in item - lastEditDate=" + item.lastEditDate);
                        //Log.d(TAG, "data - level 2 - loop in item - creationDate=" + item.creationDate);
                        // more params here ...

                        // Data level 3 - pod Items - Owner
                        RetrofitUserPOJO.Item.Owner owner = item.owner;
                        //Log.d(TAG, "data - level 3 - loop in owner - reputation=" + owner.reputation);
                        //Log.d(TAG, "data - level 3 - loop in owner - user_id=" + owner.userId);
                        //Log.d(TAG, "data - level 3 - loop in owner - userType=" + owner.userType);
                        //Log.d(TAG, "data - level 3 - loop in owner - profileImage=" + owner.profileImage);
                        Log.d(TAG, "data - level 3 - loop in owner - displayName=" + owner.displayName);
                        //Log.d(TAG, "data - level 3 - loop in owner - link=" + owner.link);


                        // add new User to Master List
                        Log.d(TAG, "Adding new user to List ..");
                        UserPOJO userPOJO = new UserPOJO(owner.userId, owner.displayName, owner.profileImage);
                        userPojoList.add(userPOJO);


                        // add new User to RoomDB for later offline use
                        Log.d(TAG, "Adding new user to Room DB ..");
                        RoomUserPOJO roomUserPOJO = new RoomUserPOJO(owner.userId, owner.displayName, owner.profileImage);
                        roomUserRepository.insertUser(MainActivity.context, roomUserPOJO);
                        // write to SharedPred that we have offline data
                        sharedPrefEditor.putBoolean("roomDataExists", true);
                        sharedPrefEditor.apply();


                        i++;

                    }


                    retrofitShouldLoadMore = true;

                    // ok
                    Log.d(TAG, "Retrofit finished, now refresh GUI");
                    MainActivity.ShowDataGUI(true, "");
                    userPojoListLiveData.setValue(userPojoList);

                }else{

                    retrofitShouldLoadMore = true;

                    // ng - response Code is not 200
                    // example 400 Bad Request - server vraci "Violation of backoff parameter","error_name":"throttle_violation" - kdyz je tam moc requestu za cas
                    Log.e(TAG, "Retrofit has troubles .. ");
                    MainActivity.ShowDataGUI(false, "Retrofit ERROR " + urlResponseCode);

                }


            }

            @Override
            public void onFailure(Call<RetrofitUserPOJO> call, Throwable t) {
                String ErrorMessage = "ERROR " + t.getLocalizedMessage();
                Log.e(TAG, ErrorMessage);
                MainActivity.ShowDataGUI(false, ErrorMessage);
                call.cancel();
            }
        });

    }

    // if offline - get data from backup in Room dB
    public void gimeMeRoomData() {

        RoomUserRepository roomUserRepository = new RoomUserRepository(MainActivity.context);
        roomUserRepository.getAllUsers(MainActivity.context);

    }


}
