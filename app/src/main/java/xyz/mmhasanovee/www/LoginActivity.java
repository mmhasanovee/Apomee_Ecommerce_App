package xyz.mmhasanovee.www;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {


    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingbar;

    private TextView Adminlink, NotAdminlink;
    private String parentDbName="Users";
    private CheckBox checkBoxRememberme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton=findViewById(R.id.login_btn);
        InputPhoneNumber=findViewById(R.id.login_with_phone_number_input);
        InputPassword=findViewById(R.id.login_password_input);

        Adminlink=(TextView)findViewById(R.id.admin_panel_btn);
        NotAdminlink=(TextView)findViewById(R.id.not_admin_panel_btn);

        loadingbar = new ProgressDialog(this);

        checkBoxRememberme=(CheckBox) findViewById(R.id.remember_me_checkbox);
        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginOfUser();
            }
        });

        Adminlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login Admin");
                Adminlink.setVisibility(View.INVISIBLE);
                NotAdminlink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        NotAdminlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login");
                Adminlink.setVisibility(View.VISIBLE);
                NotAdminlink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });

    }

    private void LoginOfUser() {

        String phone= InputPhoneNumber.getText().toString();

        String password= InputPassword.getText().toString();

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please enter your phone number",
                    Toast.LENGTH_LONG).show();
        }

        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password",
                    Toast.LENGTH_LONG).show();
        }

        else{

            loadingbar.setTitle("Login Account");
            loadingbar.setMessage("Please wait, information are being checked");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
            
            AllowAccessToAccount(phone,password);

        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if(checkBoxRememberme.isChecked()){

            Paper.book().write(Prevalent.UserPhoneID,phone);
            Paper.book().write(Prevalent.UserPasswordId,password);
        }

        final DatabaseReference RootRef; //Creating a root reference for database.

        RootRef= FirebaseDatabase.getInstance().getReference(); //Gets the instance of Firebase Database, to access read/write getreference

        RootRef.addListenerForSingleValueEvent((new ValueEventListener() { // listens exactly once and provides the data
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //readable data are received as data snapshot.
                if(dataSnapshot.child(parentDbName).child(phone).exists()){ //whether given phone number exits

                    User usersData=dataSnapshot.child(parentDbName).child(phone).getValue(User.class); //get the values from firebase and write on user class

                    if(usersData.getPhone().equals(phone)){  //if input phone number matches any phone number from database

                        if(usersData.getPassword().equals(password)){ //and the password matches too
                            /*Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                            Intent gohomeactivity=new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(gohomeactivity);*/


                            if(parentDbName.equals("Admins")){

                                Toast.makeText(LoginActivity.this, "Welcome Admin, Login Successful", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                            Intent goaddactivity=new Intent(LoginActivity.this,AdminCategory.class);
                            startActivity(goaddactivity);
                            }

                            else if(parentDbName.equals("Users")){

                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Intent gohomeactivity=new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.CurrentonlineUser=usersData;
                                startActivity(gohomeactivity);
                            }



                        }

                        else{

                            Toast.makeText(LoginActivity.this, "Incorrect login details", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                }

                else{
                    Toast.makeText(LoginActivity.this, "Invalid phone number:" +
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
