package com.appsinventiv.medicineapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appsinventiv.medicineapp.Models.Customer;
import com.appsinventiv.medicineapp.R;
import com.appsinventiv.medicineapp.Utils.CommonUtils;
import com.appsinventiv.medicineapp.Utils.Constants;
import com.appsinventiv.medicineapp.Utils.PrefManager;
import com.appsinventiv.medicineapp.Utils.SharedPrefs;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedTransferQueue;

public class Login extends AppCompatActivity {

    DatabaseReference mDatabase;
    Button login;
    TextView register;
    EditText phone, password;
    private HashMap<String, Customer> customerHashMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);


        mDatabase = FirebaseDatabase.getInstance().getReference();


        mDatabase.child("customers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Customer customer = snapshot.getValue(Customer.class);
                        if (customer != null) {
                            customerHashMap.put(customer.getPhone(), customer);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
                finish();

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone.getText().length() == 0) {
                    phone.setError("Enter phone");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else {
                    checkUser();
                }
            }
        });
    }

    private void checkUser() {
        if (customerHashMap.containsKey(phone.getText().toString())) {
            Customer model = customerHashMap.get(phone.getText().toString());
            if (model.getPassword().equals(password.getText().toString())) {
                CommonUtils.showToast("Logged in successfully");
                SharedPrefs.setUserModel(model);
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            } else {
                CommonUtils.showToast("Wrong password");
            }
        } else {
            CommonUtils.showToast("User does not exists. Please register");
        }
    }

}
