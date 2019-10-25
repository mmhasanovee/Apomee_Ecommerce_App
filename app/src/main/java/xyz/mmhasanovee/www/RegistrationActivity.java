package xyz.mmhasanovee.www;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    private Button CreateAccountButton;
    private EditText InputName, InputPhoneNumber,InputPassword;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        CreateAccountButton=findViewById(R.id.register_btn);
        InputName=findViewById(R.id.register_username_input);
        InputPhoneNumber=findViewById(R.id.register_with_phone_number_input);
        InputPassword=findViewById(R.id.register_password_input);

        loadingbar = new ProgressDialog(this);


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {


        String name= InputName.getText().toString();
        String phone= InputPhoneNumber.getText().toString();

        String password= InputPassword.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please enter your name",
                    Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please enter your phone number",
                    Toast.LENGTH_LONG).show();
        }

        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password",
                    Toast.LENGTH_LONG).show();
        }

        else{
            loadingbar.setTitle("Create Account");
            loadingbar.setMessage("Please wait, information are being checked");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            ValidatePhoneNumber(name, phone, password);
        }

    }

    private void ValidatePhoneNumber(final String name, final String phone, final String password) {

        final DatabaseReference RootRef;

        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("Users").child(phone).exists()){

                    HashMap<String,Object>  userrdataMap= new HashMap<>(); //Firebase creates own hashmap for input data, though shows phone number as id. Key-value pairs.
                    userrdataMap.put("phone",phone);
                    userrdataMap.put("password",password);

                    userrdataMap.put("name",name);

                    RootRef.child("Users").child(phone).updateChildren(userrdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        Toast.makeText(RegistrationActivity.this, "Congrats, account created successfully", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();

                                        Intent gologinactivity =new Intent(RegistrationActivity.this,LoginActivity.class);
                                        startActivity(gologinactivity);


                                    }
                                    else{
                                        loadingbar.dismiss();
                                        Toast.makeText(RegistrationActivity.this, "Failed to create account, please try again", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }

                else{

                    Toast.makeText(RegistrationActivity.this, "The number:\n"+phone+"\n already exits. Please try again with different phone number",Toast.LENGTH_LONG).show();
                    loadingbar.dismiss();
                    /*Toast.makeText(RegistrationActivity.this, "",Toast.LENGTH_LONG).show();
*/
                    /*Intent GoToMainActivity =new Intent(RegistrationActivity.this,MainActivity.class);
                    startActivity(GoToMainActivity);*/

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
