package xyz.mmhasanovee.www;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;
import xyz.mmhasanovee.www.Model.User;
import xyz.mmhasanovee.www.Prevalent.Prevalent;



public class MainActivity extends AppCompatActivity {


    private Button joinNowButton, loginButton;
    private ProgressDialog loadingbar;
    private TextView appslogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        joinNowButton=findViewById(R.id.main_join_now_btn);
        loginButton=findViewById(R.id.main_login_btn);
        loadingbar=new ProgressDialog(this);




        Paper.init(this);




        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gologinpage =new Intent(MainActivity.this,LoginActivity.class);
                startActivity(gologinpage);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goregisterpage =new Intent(MainActivity.this,RegistrationActivity.class);
                startActivity(goregisterpage);
            }
        });

        String UserPhoneID=Paper.book().read(Prevalent.UserPhoneID);
        String UserPasswordID=Paper.book().read(Prevalent.UserPasswordId);

        if(UserPhoneID!="" && UserPasswordID!=""){

            if(!TextUtils.isEmpty(UserPhoneID) && !TextUtils.isEmpty(UserPasswordID)){

                AllowAccess(UserPhoneID,UserPasswordID);

                loadingbar.setTitle("Already logged in");
                loadingbar.setMessage("Please wait.....");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
            }
        }
    }

    private void AllowAccess(final String phone, final String password) {

        final DatabaseReference RootRef; //Creating a root reference for database.

        RootRef= FirebaseDatabase.getInstance().getReference(); //Gets the instance of Firebase Database, to access read/write getreference

        RootRef.addListenerForSingleValueEvent((new ValueEventListener() { // listens exactly once and provides the data
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //readable data are received as data snapshot.
                if(dataSnapshot.child("Users").child(phone).exists()){ //whether given phone number exits

                    User usersData=dataSnapshot.child("Users").child(phone).getValue(User.class); //get the values from firebase and write on user class

                    if(usersData.getPhone().equals(phone)){  //if input phone number matches any phone number from database

                        if(usersData.getPassword().equals(password)){ //and the password matches too
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                            Prevalent.CurrentonlineUser=usersData;
                            Intent gohomeactivity=new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(gohomeactivity);

                        }

                        else{

                            Toast.makeText(MainActivity.this, "Password is not correct", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                }

                else{
                    Toast.makeText(MainActivity.this, "Invalid phone number:" +
                            phone +"\n Please try again", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));
    }
}
