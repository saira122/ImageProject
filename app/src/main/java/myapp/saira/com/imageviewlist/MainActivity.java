package myapp.saira.com.imageviewlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private static final int PICK_IMAGE = 100;
    private static final String FIREBASE_KEY_IMAGES = "images";
    public static final String FIREBASE_KEY_STORAGE = "images/";
    ImageAdapter mAdapter;
    ArrayList<Image> imageArrayList;

    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    //Image mImage;
    Uri mImageUri;
    int mCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(FIREBASE_KEY_IMAGES);

        mStorageRef = FirebaseStorage.getInstance().getReference();


        ListView lvListView = findViewById(R.id.lv_container);
        mAdapter = new ImageAdapter(MainActivity.this);
        //imageArrayList = new ArrayList<>();
        readImageFromFirebase();
        lvListView.setAdapter(mAdapter);

//             FloatingActionButton btn=findViewById(R.id.fab);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addImage();
//                readImageFromFirebase();
//
////                imageArrayList.add(mImageObj);
////                mAdapter.updateImageArray(imageArrayList);
//            }
//        });





    }

    private void addImage() {

        Intent intentObj = new Intent();
        intentObj.setType("image/*");
        intentObj.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentObj, "Select Picture"), PICK_IMAGE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            //TODO: action


                mImageUri = data.getData();
                Log.d("img-uri", mImageUri.toString());
                Log.d("img-uri-path", mImageUri.getPath());

//                InputStream imageStream = getContentResolver().openInputStream(mImageUri);
//                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Image imageObj= new Image();
                imageObj.setmImageUrl(mImageUri.toString());

                //uploadToStorage(imageObj);


        }
    }



     // read from firebase
    public void readImageFromFirebase() {

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference(FIREBASE_KEY_IMAGES);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageArrayList= new ArrayList<>();
                //imageArrayList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Image value = snapshot.getValue(Image.class);
                        imageArrayList.add(value);
                    }

                    mAdapter.updateImageArray(imageArrayList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
