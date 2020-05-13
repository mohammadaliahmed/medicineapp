package com.appsinventiv.medicineapp.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appsinventiv.medicineapp.Adapters.MainSliderAdapter;
import com.appsinventiv.medicineapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderPlaced extends AppCompatActivity {
    TextView total, time, day;
    String amount, tim, da;

    MainSliderAdapter mViewPagerAdapter;
    ViewPager viewPager;
    int currentPic = MainActivity.currentPic;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
        this.setTitle("Success");
        Button home = findViewById(R.id.home);
        Button myOrders = findViewById(R.id.myOrders);
        total = findViewById(R.id.total);
        time = findViewById(R.id.time);
        day = findViewById(R.id.day);

        Intent i = getIntent();

        amount = i.getStringExtra("total");
        tim = i.getStringExtra("time");
        da = i.getStringExtra("day");

        total.setText("Rs " + amount);
        time.setText(tim);
        day.setText(da);
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        initViewPager();


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OrderPlaced.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
        myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OrderPlaced.this, MyOrders.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
    }





    @Override
    public void onBackPressed() {

    }
}
