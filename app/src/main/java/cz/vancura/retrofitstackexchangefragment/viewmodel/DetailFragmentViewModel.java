package cz.vancura.retrofitstackexchangefragment.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import cz.vancura.retrofitstackexchangefragment.model.UserPOJO;

import static cz.vancura.retrofitstackexchangefragment.view.DetailFragment.RefreshGUIFragment;
import static cz.vancura.retrofitstackexchangefragment.viewmodel.MainActivityViewModel.userPojoList;

public class DetailFragmentViewModel extends ViewModel {

    private static String TAG = "myTAG-DetailFragmentViewModel";


    public static void GiveUserDetails(int position) {

        UserPOJO user = userPojoList.get(position);

        String textTitle = user.getUser_name();
        String textBody = "User position=" + position + "\n" + "User name=" + user.getUser_name() + "\n" + "User id=" + user.getUser_id();
        String imgUrl = user.getUser_icon_url();

        // update Fragment GUI
        RefreshGUIFragment(textTitle, textBody, imgUrl);

    }


}
