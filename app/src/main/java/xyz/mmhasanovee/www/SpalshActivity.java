package xyz.mmhasanovee.www;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SpalshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                Intent i = new Intent(SpalshActivity.this, MainActivity.class);
                startActivity(i);
                finish(); } }, 2000);
    }
}
