package cz.vancura.retrofitstackexchangefragment.view.RecyclerView;

import android.view.View;

public interface RecyclerClickInterface {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}
