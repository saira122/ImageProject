package myapp.saira.com.imageviewlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private final Context mContext;
    ArrayList<Image> mImageArrayList ;

    public ImageAdapter (Context context){
        mImageArrayList= new ArrayList<>();
        mContext = context;
    }

    public void updateImageArray(ArrayList<Image> items){
        mImageArrayList = items;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return mImageArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return mImageArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View itemView, ViewGroup viewGroup) {

        if (itemView==null){
            itemView= LayoutInflater.from(mContext).inflate(R.layout.image_list_view,viewGroup,false);

        }

        ImageView ivImage = itemView.findViewById(R.id.image);

        Image objImage = mImageArrayList.get(i);

        Glide.with(mContext)                          // Bcoz we are now in same activity, we need the main activity which is saved in mContext
                .load(objImage.getmImageUrl200())            //bring the Image
                .placeholder(R.mipmap.ic_launcher)  // during the default period display this image
                .into(ivImage);                     // save the image into ivImage

        return itemView;
    }
}
