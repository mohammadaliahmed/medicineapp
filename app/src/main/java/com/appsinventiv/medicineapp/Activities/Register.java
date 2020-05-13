package com.appsinventiv.medicineapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appsinventiv.medicineapp.Models.ChatModel;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;

public class Register extends AppCompatActivity {
    Button signup;
    TextView login;
    DatabaseReference mDatabase;
    EditText name, phone, password, address;
    HashMap<String, Customer> customerHashMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

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


        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        address = findViewById(R.id.address);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.signin);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this, Login.class);
                startActivity(i);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (name.getText().toString().length() == 0) {
                    name.setError("Cannot be null");
                } else if (password.getText().toString().length() == 0) {
                    password.setError("Cannot be null");
                } else if (phone.getText().toString().length() == 0) {
                    phone.setError("Cannot be null");
                } else if (address.getText().toString().length() == 0) {
                    address.setError("Cannot be null");
                } else {
                    checkUser();
                }

            }

        });


    }

    private void checkUser() {
        if (customerHashMap.containsKey(phone.getText().toString())) {
            CommonUtils.showToast("Phone number already exists. Please signup");
        } else {
            final Customer customer = new Customer(phone.getText().toString(),
                    name.getText().toString(),
                    password.getText().toString(),
                    phone.getText().toString(),
                    address.getText().toString(),
                    FirebaseInstanceId.getInstance().getToken(),
                    System.currentTimeMillis()
            );
            mDatabase.child("customers").child(phone.getText().toString()).setValue(customer).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    CommonUtils.showToast("Successfully registered");
                    SharedPrefs.setUserModel(customer);

                    startActivity(new Intent(Register.this, MainActivity.class));
                    finish();
                }
            });
        }

    }


}
