package xyz.mmhasanovee.www;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;





public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName, Description, Price,Pname,saveCurrentDate,saveCurrentTime;
    private Button AddNewProductButton;
    private ImageView InputProductImage;
    private EditText InputProductName, InputProductDescription, InputProductPrice;
    private static final int GalleryPick=1;

    private Uri ImageUri;
    private String productrandomkey,downloadImageurl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingbar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);


        CategoryName=getIntent().getExtras().get("category").toString();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products"); //saving new product informations and child is products.
        AddNewProductButton= (Button)findViewById(R.id.add_new_product);
        InputProductImage=(ImageView)findViewById(R.id.select_product_image);
        InputProductName=(EditText) findViewById(R.id.product_name);
        InputProductDescription=(EditText)findViewById(R.id.product_description);
        InputProductPrice=(EditText)findViewById(R.id.product_price);
        loadingbar = new ProgressDialog(this);

        InputProductImage.setOnClickListener(new View.OnClickListener() { //on clicking image button prompt gallery to upload a image
            @Override
            public void onClick(View view) {

                OpenGallery();

            }
        });

        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });




    }


    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        /*startActivityForResult(galleryIntent,GalleryPick);*/
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), GalleryPick);//image uri



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if(requestCode==GalleryPick && requestCode==RESULT_OK  &&  data!=null){

            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);

        }*/

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            ImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));


                InputProductImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void ValidateProductData() {

        Description = InputProductDescription.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();

        if(ImageUri==null){

            Toast.makeText(this, "Image is required", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(Description)) {

            Toast.makeText(this, "Please write description", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(Price)) {

            Toast.makeText(this, "Please write price", Toast.LENGTH_SHORT).show();

        }

        else if(TextUtils.isEmpty(Pname)) {

            Toast.makeText(this, "Please write name of the product", Toast.LENGTH_SHORT).show();

        }

        else{

            StoreProductInformation();

        }



        }

    private void StoreProductInformation() { //getting time function

        loadingbar.setTitle("Add new product");
        loadingbar.setMessage("Please wait, while new products are being added");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        productrandomkey = saveCurrentDate+saveCurrentTime; //unique random key for adding product

        final StorageReference filepath = ProductImagesRef.child(ImageUri.getLastPathSegment()+productrandomkey+".jpg"); //generates unique name for firebase database

        final UploadTask uploadTask = filepath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(AdminAddNewProductActivity.this, "Image uploaded Successfully..", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){

                            throw task.getException();

                        }
                        downloadImageurl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadImageurl=task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product image url fetch successful", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();

                        }
                    }
                });

            }
        });


    }

    private void SaveProductInfoToDatabase() {


        HashMap<String, Object> productMap= new HashMap<>();
        productMap.put("pid", productrandomkey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageurl);
        productMap.put("category", CategoryName);
        productMap.put("price", Price);
        productMap.put("pname", Pname);

        ProductsRef.child(productrandomkey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Intent intent = new Intent(AdminAddNewProductActivity.this,AdminCategory.class);
                    startActivity(intent);
                    loadingbar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "New product upload successful", Toast.LENGTH_SHORT).show();
                }

                else{

                    loadingbar.dismiss();

                    String message= task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}


