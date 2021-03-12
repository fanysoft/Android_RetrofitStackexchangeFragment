package cz.vancura.retrofitstackexchangefragment.model.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/*
Retrofit Http - own Http call - with Url params
 */

public interface RetrofitAPIInterface {

    /*
    Url https://api.stackexchange.com/2.2/answers?page=2&pagesize=5&site=stackoverflow
    returns list of users

    Url params :
    - page
    - pagesize
    - site
     */
    @GET("2.2/answers")
    Call<RetrofitUserPOJO> doGetUserList(
            @Query("page") int page,
            @Query("pagesize") int pagesize,
            @Query("site") String site
    );

}
