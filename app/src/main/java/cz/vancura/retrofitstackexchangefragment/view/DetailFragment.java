package cz.vancura.retrofitstackexchangefragment.view;

import android.app.Activity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cz.vancura.retrofitstackexchangefragment.R;
import cz.vancura.retrofitstackexchangefragment.model.UserPOJO;

import static cz.vancura.retrofitstackexchangefragment.viewmodel.MainActivityViewModel.userPojoList;


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link DetailActivity}
 * on handsets.
 */
public class DetailFragment extends Fragment {

    private static String TAG = "myTAG-DetailFragment";

    public static final String ARG_ITEM_ID = "item_id";
    int recievedPosition = 0;

    View rootView;
    Activity activity;

    // empty constructor
    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        activity = this.getActivity();

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            String recievedPositionString = getArguments().getString(ARG_ITEM_ID);
            recievedPosition = Integer.parseInt(recievedPositionString);
            Log.d(TAG, "received position ="  + recievedPosition);

        }else{
            Log.e(TAG, "not received position");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");

        rootView = inflater.inflate(R.layout.item_detail, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated");

        // data
        UserPOJO user = userPojoList.get(recievedPosition);

        String textTitle = user.getUser_name();
        String textBody = "User position=" + recievedPosition + "\n" + "User name=" + user.getUser_name() + "\n" + "User id=" + user.getUser_id();
        String imgUrl = user.getUser_icon_url();

        // update Fragment GUI
        RefreshGUIFragment(textTitle, textBody, imgUrl);


    }


    public void RefreshGUIFragment(String title, String body, String iconUrl){

        Log.d(TAG, "RefreshGUIFragment ..");

        // ToolBar text and img
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        // v portrait mode - je videt ToolBar z activity detail layout
        if (appBarLayout != null) {
            // text
            appBarLayout.setTitle(title);
            // pict
            Glide.with(activity)
                    .load(iconUrl)
                    .into((ImageView) appBarLayout.findViewById(R.id.toolbarImage));
        }else{
            Log.d(TAG, "appBarLayout is null .."); // v landscape mode - neni videt ToolBar z activity detail layout
        }

        // Body text
        ((TextView) rootView.findViewById(R.id.textview_detail)).setText(body);

        // Body img
        Glide.with(activity)
                .load(iconUrl)
                .into(((ImageView) rootView.findViewById(R.id.image_detail)));

    }

}