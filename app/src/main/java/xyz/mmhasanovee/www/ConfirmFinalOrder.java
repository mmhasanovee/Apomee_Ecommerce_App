package xyz.mmhasanovee.www;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rey.material.widget.SnackBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import xyz.mmhasanovee.www.Prevalent.Prevalent;

public class ConfirmFinalOrder extends AppCompatActivity {


    private EditText nameEditText, phoneEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn;
    private String totalAmount = "";
    private TextView confirmFinalOrderTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        confirmFinalOrderTotalPrice = (TextView) findViewById(R.id.confirm_final_total_amount);




        totalAmount = getIntent().getStringExtra("Total Price");

        confirmFinalOrderTotalPrice.setText("Total Price="+totalAmount+"BDT");


        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        nameEditText = (EditText) findViewById(R.id.shipment_name);
        phoneEditText = (EditText) findViewById(R.id.shipment_phone_number);
        addressEditText = (EditText) findViewById(R.id.shipment_address);
        cityEditText = (EditText) findViewById(R.id.shipment_city);


        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckFields();
            }
        });


    }

    private void CheckFields() {

        if(TextUtils.isEmpty(nameEditText.getText().toString())
        || TextUtils.isEmpty(phoneEditText.getText().toString())
                || TextUtils.isEmpty(addressEditText.getText().toString())
                || TextUtils.isEmpty(cityEditText.getText().toString())
                ){

            Toast.makeText(this, "Please fill up all the fields", Toast.LENGTH_SHORT).show();
        }
        
        else {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {

        final String saveCurrentTime, saveCurrentDate;

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(callForDate.getTime());

        final DatabaseReference orders = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.CurrentonlineUser.getPhone());

        HashMap<String, Object> ordersMap = new HashMap<>();

        ordersMap.put("totalAmount",totalAmount);
        ordersMap.put("name",nameEditText.getText().toString());
        ordersMap.put("phone",phoneEditText.getText().toString());
        ordersMap.put("address",addressEditText.getText().toString());
        ordersMap.put("city",cityEditText.getText().toString());
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentTime);
        ordersMap.put("state","not shipped");

        final DatabaseReference ordersDB = FirebaseDatabase.getInstance().getReference()
                .child("OrdersDB")
                .child(Prevalent.CurrentonlineUser.getPhone());

        HashMap<String, Object> ordersMapDB = new HashMap<>();

        ordersMapDB.put("totalAmount",totalAmount);
        ordersMapDB.put("name",nameEditText.getText().toString());
        ordersMapDB.put("phone",phoneEditText.getText().toString());
        ordersMapDB.put("address",addressEditText.getText().toString());
        ordersMapDB.put("city",cityEditText.getText().toString());
        ordersMapDB.put("date",saveCurrentDate);
        ordersMapDB.put("time",saveCurrentTime);
        ordersMapDB.put("state","not shipped");

        orders.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(Prevalent.CurrentonlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        Toast.makeText(ConfirmFinalOrder.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrder.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK); //user cant come back to the previous activity
                                        startActivity(intent);
                                        finish(); //Call this when your activity is done and should be closed, when starting an activity calling
                                    }
                                }
                            });
                }
            }
        });


        ordersDB.updateChildren(ordersMapDB).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){


                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ConfirmFinalOrder.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
