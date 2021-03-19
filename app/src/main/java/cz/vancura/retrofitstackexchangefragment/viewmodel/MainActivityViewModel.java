package cz.vancura.retrofitstackexchangefragment.viewmodel;


import android.app.Application;
import android.content.Context;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static cz.vancura.retrofitstackexchangefragment.view.MainActivity.sharedPrefEditor;

/*
MVVM - ViewModel for MainActivity
 */

public class MainActivityViewModel extends ViewModel {

    // Variables
    private static String TAG = "myTAG-MainActivityViewModel";

    // room dB
    private static RoomUserRepository roomUserRepository;

    // retrofit Http
    private static RetrofitAPIInterface retrofitAPIInterface = RetrofitAPIClient.getClient().create(RetrofitAPIInterface.class);
    public static int retrofitUrlPage = 1;
    static int retrofitUrlPagesize = 15;
    static String retrofitUrlSite = "stackoverflow";
    public static boolean retrofitShouldLoadMore = true;

    // Lists
    public static List<UserPOJO> userPojoList = new ArrayList<>(); // List
    public static MutableLiveData<List<UserPOJO>> userPojoListLiveData; // LiveData List
    // Error
    public static MutableLiveData<String> errorLiveData; // LiveData String


    // LiveData getter - List of UserPOJO
    public MutableLiveData<List<UserPOJO>> getLiveDataUsers() {
        if (userPojoListLiveData == null) {
            userPojoListLiveData = new MutableLiveData<List<UserPOJO>>();
        }
        return userPojoListLiveData;
    }


    // LiveData getter
    public MutableLiveData<String> getLiveDataError() {
        if (errorLiveData == null) {
            errorLiveData = new MutableLiveData<String>();
        }
        return errorLiveData;
    }

    // workaround - unable to create non-zero-constructor for ViewModel when extends ViewModel, was possible with extends AndroidViewModel
    public void init(Context context) {
        Log.d(TAG, "ViewModel init");
        roomUserRepository = new RoomUserRepository(context);
    }


    // if online - get data from Retrofit, store it in List and LiveDataList
    public static void gimeMeRetrofitData(int urlPage) {

        Log.d(TAG, "gimeMeRetrofitData - page=" + urlPage);


        //roomUserRepository = new RoomUserRepository(MainActivity.context);

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
                    Log.d(TAG, "data - level 1 - quota_remaining=" + resource.quotaRemaining); // server returns quota info

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
                        //Log.d(TAG, "data - level 3 - loop in owner - displayName=" + owner.displayName);
                        //Log.d(TAG, "data - level 3 - loop in owner - link=" + owner.link);


                        // add new User to Master List
                        //Log.d(TAG, "Adding new user to List ..");
                        UserPOJO userPOJO = new UserPOJO(owner.userId, owner.displayName, owner.profileImage);
                        userPojoList.add(userPOJO);


                        // add new User to RoomDB for later offline use
                        Log.d(TAG, "Adding new user to Room DB ..");
                        RoomUserPOJO roomUserPOJO = new RoomUserPOJO(owner.userId, owner.displayName, owner.profileImage);

                        roomUserRepository.insertUser(roomUserPOJO);

                        // write to SharedPred that we have offline data
                        sharedPrefEditor.putBoolean("roomDataExists", true);
                        sharedPrefEditor.apply();

                        i++;

                    }


                    retrofitShouldLoadMore = true;
                    
                    Log.d(TAG, "Retrofit finished, List size=" + userPojoList.size() + "  now refresh GUI");

                    // LiveData update - will trigget GUI update via Observer
                    userPojoListLiveData.setValue(userPojoList);

                }else{

                    retrofitShouldLoadMore = true;

                    // ng - response Code is not 200
                    // example 400 Bad Request - server vraci "Violation of backoff parameter","error_name":"throttle_violation" - ochrana na strane server - moc requestu za cas
                    Log.e(TAG, "Retrofit has troubles - response code in not 200 " + urlResponseCode);

                    // LiveData update - will trigger GUI update
                    String error = "Retrofit ERROR " + urlResponseCode + " Too many server requests - try it again later";
                    errorLiveData.setValue(error);


                }


            }

            @Override
            public void onFailure(Call<RetrofitUserPOJO> call, Throwable t) {
                String ErrorMessage = "Retrofit ERROR " + t.getLocalizedMessage();
                Log.e(TAG, ErrorMessage);

                // LiveData update - will trigger GUI update
                errorLiveData.setValue(ErrorMessage);

                // cancel retrofit call
                call.cancel();
            }
        });

    }

    // if offline and persistence data exists in Room dB - get data from backup
    public static void gimeMeRoomData() {

        //RoomUserRepository roomUserRepository = new RoomUserRepository(MainActivity.context);
        roomUserRepository.getAllUsers();

    }


}
