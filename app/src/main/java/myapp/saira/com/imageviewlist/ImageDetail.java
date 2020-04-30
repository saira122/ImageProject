package myapp.saira.com.imageviewlist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        ImageView imgView600= findViewById(R.id.img_detail);
        Image objImage = new Image ();
        Glide.with(this)                          // Bcoz we are now in same activity, we need the main activity which is saved in mContext
                .load(objImage.getmImageUrl200())            //bring the Image
                .placeholder(R.mipmap.ic_launcher)  // during the default period display this image
                .into(imgView600);                     // save the image into ivImage

    }
}
