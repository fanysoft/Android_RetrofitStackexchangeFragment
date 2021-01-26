package cz.vancura.retrofitstackexchangefragment.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import cz.vancura.retrofitstackexchangefragment.R;
import cz.vancura.retrofitstackexchangefragment.helper.HelperMethods;
import cz.vancura.retrofitstackexchangefragment.model.UserPOJO;
import cz.vancura.retrofitstackexchangefragment.view.RecyclerView.RecyclerAdapter;
import cz.vancura.retrofitstackexchangefragment.view.RecyclerView.RecyclerClickInterface;
import cz.vancura.retrofitstackexchangefragment.view.RecyclerView.RecyclerTouchListener;
import cz.vancura.retrofitstackexchangefragment.viewmodel.MainActivityViewModel;

import java.util.List;

import static cz.vancura.retrofitstackexchangefragment.viewmodel.MainActivityViewModel.retrofitShouldLoadMore;
import static cz.vancura.retrofitstackexchangefragment.viewmodel.MainActivityViewModel.retrofitUrlPage;
import static cz.vancura.retrofitstackexchangefragment.viewmodel.MainActivityViewModel.userPojoList;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "myTAG-MainActivity";

    private static MainActivityViewModel mainActivityViewModel;

    public static Context context;

    private boolean mTwoPane;

    static SharedPreferences sharedPref;
    public static SharedPreferences.Editor sharedPrefEditor;

    private static RecyclerView mRecyclerView;
    private static RecyclerAdapter mAdapter;
    private static ProgressBar mProgressBar;
    private static TextView mTextviewError, mTextviewStatus;
    private static Button mButtonRefresh;

    public static boolean roomDataExitst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "activity started ..");

        // ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // If this view is present, then the activity should be in two-pane mode.
        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
        Log.d(TAG, "after start - screen size detection - mTwoPane=" + mTwoPane);

        // GUI outlets
        mProgressBar = findViewById(R.id.progressBar);
        mTextviewError = findViewById(R.id.textViewError);
        mTextviewStatus = findViewById(R.id.textViewStatus);
        mButtonRefresh = findViewById(R.id.buttonRefresh);
        context = this;

        // ViewModel
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        // SharedPref
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        sharedPrefEditor = sharedPref.edit();

        if(sharedPref.contains("roomDataExists")){
            roomDataExitst = sharedPref.getBoolean("roomDataExists", false);
            Log.d(TAG, "after start - roomDataExitst=" + roomDataExitst);
        }else{
            Log.d(TAG, "after start - roomDataExitst does not exists so far");
        }

        // LiveData Observer
        final Observer<List<UserPOJO>> observer = new Observer<List<UserPOJO>>() {
            @Override
            public void onChanged(List<UserPOJO> userPOJOS) {
                Log.d(TAG, "LiveData changed - observer - can update GUI now..");
            }
        };
        mainActivityViewModel.getLiveData().observe(this, observer);

        // RecyclerView setup
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new RecyclerAdapter(userPojoList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);


        // RecyclerView onCLick
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerClickInterface() {

            @Override
            public void onClick(View view, int position) {
                Log.d(TAG, "onClick position=" + position);
                //Toast.makeText(context, "onClick " + position, Toast.LENGTH_SHORT).show();

                // if wide like tablet - fragments are side-by-side
                if (mTwoPane) {
                    // show Fragment
                    Log.d(TAG, "launching DetailFragment..");
                    Bundle arguments = new Bundle();
                    arguments.putString(DetailFragment.ARG_ITEM_ID, String.valueOf(position)); // param must be String, why ?
                    DetailFragment fragment = new DetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    // if not wide - like phone - Fragments are above/below each other
                    // lauch Activity
                    Log.d(TAG, "launching DetailActivity..");
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(DetailFragment.ARG_ITEM_ID, String.valueOf(position));
                    context.startActivity(intent);
                }

            }

            @Override
            public void onLongClick(View view, int position) {
                //Log.d(TAG, "onLongClick position=" + position);
            }
        }));


        // RecyclerView onScrolled - load more data
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //Log.d(TAG, "onScrolled - dx=" + dx + " dy=" + dy);
                // dx = int: The amount of horizontal scroll.
                // dy = int: The amount of vertical scroll.

                if (dy > 0) {

                    Log.d(TAG, "onScrolled downwards");
                    // Recycle view scrolling downwards...

                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {

                        if (retrofitShouldLoadMore && HelperMethods.isNetworkAvailable(context)) {

                            Log.d(TAG, "load more data now ..");
                            Toast.makeText(context, "Loading more data ... ", Toast.LENGTH_SHORT).show();
                            // page increment - load another data
                            retrofitUrlPage++;
                            // fetch data from Http
                            mainActivityViewModel.gimeMeRetrofitData(retrofitUrlPage);
                        }
                        if (!HelperMethods.isNetworkAvailable(context)) {
                            Toast.makeText(context, "Offline mode - can not get new data ... ", Toast.LENGTH_SHORT).show();
                        }
                    }

                }else{
                    // Recycle view scrolling upwards...
                    Log.d(TAG, "onScrolled upwards");
                    // no action
                }
            }
        });


        // read data after start
        GetData();

    }



    // Give new data
    public static void GetData(){

        Log.d(TAG, "GetData()");

        // GUI - loading
        GUIloading();


        if (HelperMethods.isNetworkAvailable(context)) {

            // if online - fetch data from Http
            Log.d(TAG, "device is online");
            mTextviewStatus.setText("online");
            // fetch data from Http
            mainActivityViewModel.gimeMeRetrofitData(retrofitUrlPage);

        }else {

            // if offline - fetch data from Room dB backup
            mTextviewStatus.setText("offline");

            if (roomDataExitst) {
                Log.d(TAG, "device is offline - we have offline data in room db");
                // fetch data from room dB
                mainActivityViewModel.gimeMeRoomData();
            }else{
                // no data from http or room dB
                Log.d(TAG, "device is offline - we have no data - so sad ..");
                ShowDataGUI(false, "No data - go online to get it ..");
            }
        }


    }


    // GUI - refresh screen based on result
    public static void ShowDataGUI(Boolean status, String what){

        Log.d(TAG, "ShowDataGUI() - userPojoList size=" + userPojoList.size());

        if (status) {
            // result is OK, we have data
            // GUI - stop loading - show data
            GUIvalidData();
            // refresh RecyclerView
            mAdapter.notifyDataSetChanged();

        }else{
            // error
            // GUI - show error and refresh button
            GUIerror();
            mTextviewError.setText(what);

            mButtonRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Refresh data now after ERROR ..");
                    GetData();
                }
            });
        }

    }


    // GUI - show Progress bar only
    private static void GUIloading(){
        mProgressBar.setVisibility(View.VISIBLE);
        mTextviewError.setVisibility(View.INVISIBLE);
        mButtonRefresh.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    // GUI - show RecyclerView only
    private static void GUIvalidData() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextviewError.setVisibility(View.INVISIBLE);
        mButtonRefresh.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    // GUI - show Error and Retry buton
    private static void GUIerror(){
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextviewError.setVisibility(View.VISIBLE);
        mButtonRefresh.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);

    }

    // Menu - init
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Menu - selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.d(TAG, "menu selected " + item);

        if (item.getItemId() == (R.id.action_refresh)) {
           GetData();
        }

        return super.onOptionsItemSelected(item);
    }


}