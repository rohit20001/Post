package com.example.post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.widget.Toast.LENGTH_SHORT;

public class Home extends AppCompatActivity {
    private static final int REQUESTCODE = 2;
    FirebaseAuth fAuth;
       FirebaseUser currentUser;
       Dialog popAddPost;
       ImageView popPostImage,popAddBtn;
       EditText popTitle,popDescription;
       ProgressBar popProgress;
       private static final int PReqCode=2;
       private Uri pickedImgUri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        iniPop();
        setupPopupImageClick();
        FloatingActionButton fab=(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddPost.show();
            }
        });

    }

    private void setupPopupImageClick() {
        popPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();

            }
        });
    }
    private void checkAndRequestForPermission(){
        if(ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(Home.this,Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Toast.makeText(Home.this,"Please accept for required permssion", LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(Home.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }else{
            openGallery();
        }

    }

    private void openGallery() {
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==REQUESTCODE && data!=null){
            pickedImgUri = data.getData();
            popPostImage.setImageURI(pickedImgUri);
        }
    }

    private void iniPop() {
        popAddPost=new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity= Gravity.TOP;
        popPostImage=popAddPost.findViewById(R.id.imageView2);
        popTitle=popAddPost.findViewById(R.id.title);
        popDescription=popAddPost.findViewById(R.id.description);
        popAddBtn=popAddPost.findViewById(R.id.imageView4);
        popProgress=popAddPost.findViewById(R.id.progressBar8);


        popAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popProgress.setVisibility(View.VISIBLE);
                popAddBtn.setVisibility(View.INVISIBLE);
                if(!popTitle.getText().toString().isEmpty()
                   && ! popDescription.getText().toString().isEmpty()
                   && pickedImgUri !=null){
                    StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("blog_images");
                    StorageReference imageFilePath=storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink=uri.toString();

                                    post Post=new post(popTitle.getText().toString(),
                                            popDescription.getText().toString(),
                                            imageDownloadLink);
                                    addPost(Post);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showMessege(e.getMessage());
                                    popProgress.setVisibility(View.INVISIBLE);
                                    popAddBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });



                }
                else{
                    showMessege("Please verify all input field and choose post image");
                    popAddBtn.setVisibility(View.VISIBLE);
                    popProgress.setVisibility(View.INVISIBLE);
                }
            }

            private void showMessege(String messege) {
                Toast.makeText(Home.this,messege,Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void addPost(post post) {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference("Posts").push();
        String key = myRef.getKey();
        post.setPostKey(key);
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessege("post added Successfully");
                popProgress.setVisibility(View.INVISIBLE);
                popAddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Home.this,"Sorry error", LENGTH_SHORT).show();
            }
        });
    }
    private void showMessege(String messege) {
        Toast.makeText(Home.this,messege,Toast.LENGTH_SHORT).show();

    }
}