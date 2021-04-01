package cz.vancura.retrofitstackexchangefragment.view.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cz.vancura.retrofitstackexchangefragment.R;
import cz.vancura.retrofitstackexchangefragment.model.UserPOJO;

/*
RecyclerView Adapter
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{

    private static String TAG = "myTAG-RecyclerAdapter";
    private List<UserPOJO> dataClassList = new ArrayList<>();
    private Context context;


    // 1 - View Holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView1, mTextView2;
        public ImageView mImageView;
        public MyViewHolder(View view) {
            super(view);
            mTextView1 = (TextView) view.findViewById(R.id.textView1);
            mTextView2 = (TextView) view.findViewById(R.id.textView2);
            mImageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }

    // 2 - contructor
    public RecyclerAdapter(Context context) {
        this.context = context;
    }

    // 3 - Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_one_row, parent, false);
        return new MyViewHolder(itemView);
    }

    // 4 - Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {

        //Log.d(TAG, "onBindViewHolder - position=" + position);
        UserPOJO userPOJO = dataClassList.get(position);

        // GUI - text
        holder.mTextView1.setText("row " + position);
        holder.mTextView2.setText(userPOJO.getUser_name());

        // GUI - pict
        Glide.with(context)
                .load(userPOJO.getUser_icon_url())
                .into(holder.mImageView);


    }


    // 5 - Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int size = dataClassList.size();
        return size;
    }

    // 6 - import list
    public void importData(List<UserPOJO> inList){
        dataClassList = inList;
    }


}
