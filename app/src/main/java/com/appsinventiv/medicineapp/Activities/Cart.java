package com.appsinventiv.medicineapp.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.medicineapp.Adapters.CartAdapter;
import com.appsinventiv.medicineapp.Adapters.MainSliderAdapter;
import com.appsinventiv.medicineapp.Interface.AddToCartInterface;
import com.appsinventiv.medicineapp.Models.Product;
import com.appsinventiv.medicineapp.Models.ProductCountModel;
import com.appsinventiv.medicineapp.R;
import com.appsinventiv.medicineapp.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Cart extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    public static ArrayList<ProductCountModel> userCartProductList = new ArrayList<>();
    DatabaseReference mDatabase;
    CartAdapter adapter;
    TextView subtotal, totalAmount, grandTotal;
    long total;
    int items;
    public static long grandTotalAmount;
    RelativeLayout checkout, wholeLayout, noItemInCart;
    Button startShopping;
    public static long deliveryChargess;
    public static long productTotal;
    TextView deliveryCharges;
    MainSliderAdapter mViewPagerAdapter;
    ViewPager viewPager;
    int currentPic = MainActivity.currentPic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        this.setTitle("My Cart");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        subtotal = findViewById(R.id.subtotal);
        totalAmount = findViewById(R.id.totalAmount);
        grandTotal = findViewById(R.id.totalPrice);
        checkout = findViewById(R.id.checkout);
        startShopping = findViewById(R.id.startShopping);
        wholeLayout = findViewById(R.id.wholeLayout);
        noItemInCart = findViewById(R.id.noItemInCart);
        deliveryCharges = findViewById(R.id.deliveryCharges);

        startShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Cart.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Cart.this, Checkout.class);
                i.putExtra("grandTotal", grandTotalAmount);
                startActivity(i);
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference();


        recyclerView = findViewById(R.id.recycler);
        calculateTotal();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CartAdapter(Cart.this, userCartProductList, new AddToCartInterface() {
            @Override
            public void addedToCart(final Product product, final int quantity, int position) {
                mDatabase.child("customers").child(SharedPrefs.getUserModel().getPhone())
                        .child("cart").child(product.getId()).setValue(new ProductCountModel(product, quantity, System.currentTimeMillis()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                calculateTotal();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void deletedFromCart(final Product product, final int position) {
                userCartProductList.remove(position);
                if (userCartProductList.isEmpty()) {
                    wholeLayout.setVisibility(View.GONE);
                    noItemInCart.setVisibility(View.VISIBLE);

                }

                mDatabase.child("customers").child(SharedPrefs.getUserModel().getPhone())
                        .child("cart").child(product.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        getDeliveryChargesFromDB();
                        calculateTotal();

//                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void quantityUpdate(Product product, final int quantity, int position) {
                mDatabase.child("customers").child(SharedPrefs.getUserModel().getPhone())
                        .child("cart").child(product.getId()).child("quantity").setValue(quantity).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        calculateTotal();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });
        recyclerView.setAdapter(adapter);
        getUserCartProductsFromDB();


    }

    public void calculateTotal() {

        total = 0;
        items = 0;
        grandTotalAmount = 0;
        mDatabase.child("customers").child(SharedPrefs.getUserModel().getPhone()).child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    noItemInCart.setVisibility(View.GONE);
                    wholeLayout.setVisibility(View.VISIBLE);
                    total = 0;
                    items = 0;
                    grandTotalAmount = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductCountModel model = snapshot.getValue(ProductCountModel.class);
                        if (model != null) {
                            total = total + (model.getQuantity() * model.getProduct().getPrice());
                            subtotal.setText("Rs " + total);
//                            if (deliveryChargess != 0) {
                            productTotal = total;
                            grandTotalAmount = total + deliveryChargess;
                            totalAmount.setText("Rs " + grandTotalAmount);
                            grandTotal.setText("Rs " + grandTotalAmount);
//                            }


                        }
                    }
                } else {
                    total = 0;
                    items = 0;
                    grandTotalAmount = 0;
                    noItemInCart.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserCartProductsFromDB() {
//        mDatabase.child("Settings").child("DeliveryCharges").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//                    deliveryChargess = dataSnapshot.getValue(Long.class);
//                    deliveryCharges.setText("Rs " + deliveryChargess);
//                    calculateTotal();
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        mDatabase.child("customers").child(SharedPrefs.getUserModel().getPhone()).child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userCartProductList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductCountModel product = snapshot.getValue(ProductCountModel.class);
                        if (product != null) {
                            userCartProductList.add(product);
                            Collections.sort(userCartProductList, new Comparator<ProductCountModel>() {
                                @Override
                                public int compare(ProductCountModel listData, ProductCountModel t1) {
                                    Long ob1 = listData.getTime();
                                    Long ob2 = t1.getTime();

                                    return ob2.compareTo(ob1);

                                }
                            });
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
