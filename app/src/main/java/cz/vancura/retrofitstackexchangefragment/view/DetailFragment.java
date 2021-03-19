package cz.vancura.retrofitstackexchangefragment.view;

import android.app.Activity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cz.vancura.retrofitstackexchangefragment.R;
import cz.vancura.retrofitstackexchangefragment.viewmodel.DetailFragmentViewModel;


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link DetailActivity}
 * on handsets.
 */
public class DetailFragment extends Fragment {

    private static String TAG = "myTAG-DetailFragment";

    private DetailFragmentViewModel detailFragmentViewModel;

    public static final String ARG_ITEM_ID = "item_id";

    int recievedPosition = 0;

    static View rootView;

    static Activity activity;

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
            recievedPosition = Integer.valueOf(recievedPositionString);
            Log.d(TAG, "received position ="  + recievedPosition);

        }else{
            Log.d(TAG, "not received position");
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

        // ViewModel
        detailFragmentViewModel = new ViewModelProvider(this).get(DetailFragmentViewModel.class);
        DetailFragmentViewModel.GiveUserDetails(recievedPosition);


    }


    public static void RefreshGUIFragment(String title, String body, String iconUrl){

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