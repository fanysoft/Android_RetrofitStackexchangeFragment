package cz.vancura.retrofitstackexchangefragment.model.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
POJO class based on JSON structure
 */

public class RetrofitUserPOJO {

    private static String TAG = "myTAG-RetrofitUserPOJO";

    // JSON level 1

    @SerializedName("items")
    @Expose
    public List<Item> items = null;
    @SerializedName("has_more")

    @Expose
    public Boolean hasMore;
    @SerializedName("backoff")

    @Expose
    public Integer backoff;
    @SerializedName("quota_max")

    @Expose
    public Integer quotaMax;
    @SerializedName("quota_remaining")

    @Expose
    public Integer quotaRemaining;


    // JSON level 2 - pod Items
    public class Item {

        @SerializedName("owner")
        @Expose
        //public List<UserPOJO.Item.Owner> owner;
        public Owner owner;

        @SerializedName("is_accepted")
        @Expose
        public Boolean isAccepted;

        @SerializedName("score")
        @Expose
        public Integer score;

        @SerializedName("last_activity_date")
        @Expose
        public Integer lastActivityDate;

        @SerializedName("last_edit_date")
        @Expose
        public String lastEditDate;

        @SerializedName("creation_date")
        @Expose
        public String creationDate;

        @SerializedName("answer_id")
        @Expose
        public Integer answerId;

        @SerializedName("question_id")
        @Expose
        public Integer questionId;

        @SerializedName("content_license")
        @Expose
        public String contentLicense;


        // JSON level 3 - pod owner
        public class Owner {

            @SerializedName("reputation")
            @Expose
            public Integer reputation;

            @SerializedName("user_id")
            @Expose
            public Integer userId;

            @SerializedName("user_type")
            @Expose
            public String userType;

            @SerializedName("profile_image")
            @Expose
            public String profileImage;

            @SerializedName("display_name")
            @Expose
            public String displayName;

            @SerializedName("link")
            @Expose
            public String link;

        }

    }


}
