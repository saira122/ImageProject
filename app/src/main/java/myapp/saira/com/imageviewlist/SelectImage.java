package myapp.saira.com.imageviewlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import static android.graphics.Bitmap.CompressFormat.PNG;

public class SelectImage extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private static final String FIREBASE_KEY_IMAGES = "images";
    public static final String FIREBASE_KEY_STORAGE = "images/";

    private DatabaseReference myRef;
    private StorageReference mStorageRef;

    Image mImage;
    Uri mImageUri;
    int mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        mImage=new Image();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(FIREBASE_KEY_IMAGES);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        Button btnSelect= findViewById(R.id.btn_select);
        Button btnImageList=findViewById(R.id.btn_list_view);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            }
        });

        btnImageList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(SelectImage.this,   MainActivity.class);
                startActivity(myIntent);
            }
        });
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
        ImageView imageView = findViewById(R.id.image_view);
        if (requestCode == PICK_IMAGE) {
            //TODO: action

            InputStream imageStream = null;
            try {

                mImageUri = data.getData();
                Log.d("img-uri", mImageUri.toString());
                Log.d("img-uri-path", mImageUri.getPath());
                imageStream = getContentResolver().openInputStream(mImageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                int width = 780;
                int height = 1280;
                Bitmap resizedImage = Bitmap.createScaledBitmap(selectedImage, width, height, true);
                imageView.setImageBitmap(resizedImage);

                //Image objImage=new Image();

                mImage.setmImageUrl(mImageUri.toString());
                uploadToStorage();
                //getting the new uri

                File tempDir = Environment.getExternalStorageDirectory();
                tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
                tempDir.mkdir();
                File tempFile = File.createTempFile("tempimage", ".jpg", tempDir);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                byte[] bitmapData = bytes.toByteArray();

                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(bitmapData);
                fos.flush();
                fos.close();
                Uri uri = Uri.fromFile(tempFile);
                Toast.makeText(SelectImage.this, "The Image uri: " + uri, Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                Toast.makeText(SelectImage.this, "The Image not Found", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(SelectImage.this, "The File not Found", Toast.LENGTH_SHORT).show();
            }
        }


    }


    void uploadToStorage() {


        int c=mCount++;
        final StorageReference imgRef = mStorageRef.child(FIREBASE_KEY_STORAGE +c  );

        final StorageReference imgRef600 = mStorageRef.child(FIREBASE_KEY_STORAGE + c+"_640x960" );
        imgRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri imageFirebaseUrl) {
                        //mImage.setmImageUrl(imageFirebaseUrl.toString());
                        // add to Firebase
                        //addImageToFirebase(imageObj);
                        getURL200(imageFirebaseUrl);
                        Log.d("imageUrl", imageFirebaseUrl.toString());
                        Toast.makeText(SelectImage.this, imageFirebaseUrl.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                //addImageToFirebase(imageObj);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                // ...
                Log.d("failToUpload", exception.toString());
            }
        });









    }

    private void getURL200(final Uri url1) {

        int c=mCount;
        Toast.makeText(this, "C is:"+c+" and the url1 is "+url1, Toast.LENGTH_SHORT).show();
        final StorageReference imgRef200 = mStorageRef.child(FIREBASE_KEY_STORAGE + c+"_240x320" );
        imgRef200.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri imageFirebaseUrl200) {

                mImage.setmImageUrl(url1.toString());
                mImage.setmImageUrl200(imageFirebaseUrl200.toString());
                Toast.makeText(SelectImage.this, "URL1 is: "+ mImage.getmImageUrl() +" and URL200 is : "+mImage.getmImageUrl200(), Toast.LENGTH_SHORT).show();
                // add to Firebase

                addImageToFirebase(mImage);

                Log.d("imageUrl200", imageFirebaseUrl200.toString());
            }
        });

    }

    // save/ write new Image to Firebase
        private void addImageToFirebase(final Image imageObj) {


//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference(FIREBASE_KEY_IMAGES);
            String key = myRef.push().getKey();
            imageObj.setImageFbId(key);
            myRef.child(key).setValue(imageObj);
        }

    }




