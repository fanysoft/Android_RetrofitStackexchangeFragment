package cz.vancura.retrofitstackexchangefragment.view.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/*
RecyclerView Touch Listener
 */

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    private static String TAG = "myTAG-RecyclerTouchListener";

    private GestureDetector gestureDetector;
    // interface instance
    private RecyclerClickInterface clickListener;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final RecyclerClickInterface clickListener) {

        // interf
        this.clickListener = clickListener;

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            // pro OnLongClick
            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress");
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    // interface method
                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                }
            }
        });
    }

    // pro OnClick
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            Log.d(TAG, "onClick");
            // interface method
            clickListener.onClick(child, rv.getChildPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        // empty
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // empty
    }
}
