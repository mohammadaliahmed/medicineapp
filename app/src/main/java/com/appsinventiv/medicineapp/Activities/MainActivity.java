package com.appsinventiv.medicineapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.appsinventiv.medicineapp.Adapters.MainSliderAdapter;
import com.appsinventiv.medicineapp.Adapters.ProductsAdapter;
import com.appsinventiv.medicineapp.Interface.AddToCartInterface;
import com.appsinventiv.medicineapp.Models.NewCategoryModel;
import com.appsinventiv.medicineapp.Models.Product;
import com.appsinventiv.medicineapp.Models.ProductCountModel;
import com.appsinventiv.medicineapp.R;
import com.appsinventiv.medicineapp.Utils.CommonUtils;
import com.appsinventiv.medicineapp.Utils.Constants;
import com.appsinventiv.medicineapp.Utils.PrefManager;
import com.appsinventiv.medicineapp.Utils.SharedPrefs;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    DatabaseReference mDatabase;
    long cartItemCountFromDb;
    Toolbar toolbar;
    MainSliderAdapter mViewPagerAdapter;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    ArrayList<String> pics = new ArrayList<>();
    ViewPager viewPager;
    public static int currentPic = 0;
    //    TextView contact;
    private String adminNumber;
    RecyclerView recyclerview;
    private ArrayList<ProductCountModel> userCartProductList = new ArrayList<>();
    private ArrayList<Product> productArrayList = new ArrayList<>();
    ProductsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        splash = findViewById(R.id.splash);
        recyclerview = findViewById(R.id.recyclerview);


        this.setTitle("Medicine Online");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initViewPager();
        getLatestProductsFromDB();
        sendFcmKeyToServer();


        initDrawer();
        getAdminDataFromDB();
    }


    private void getLatestProductsFromDB() {
        final ArrayList<NewCategoryModel> arrayList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ProductsAdapter(MainActivity.this, productArrayList, userCartProductList, new AddToCartInterface() {
            @Override
            public void addedToCart(final Product product, final int quantity, int position) {
                mDatabase.child("customers").child(SharedPrefs.getUserModel().getPhone())
                        .child("cart").child(product.getId()).setValue(new ProductCountModel(product, quantity, System.currentTimeMillis()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void deletedFromCart(final Product product, int position) {
                mDatabase.child("customers").child(SharedPrefs.getUserModel().getPhone())
                        .child("cart").child(product.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getUserCartProductsFromDB();
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
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });
        recyclerView.setAdapter(adapter);


        getProductsFromDB();

    }

    private void getProductsFromDB() {
        productArrayList.clear();
        mDatabase.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            if (product.getIsActive().equals("true")) {

                                productArrayList.add(product);

                            }


                        }
                    }
                    Collections.sort(productArrayList, new Comparator<Product>() {
                        @Override
                        public int compare(Product listData, Product t1) {
                            String ob1 = listData.getTitle();
                            String ob2 = t1.getTitle();

                            return ob1.compareTo(ob2);

                        }
                    });
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getUserCartProductsFromDB() {
        userCartProductList.clear();
        mDatabase.child("customers").child(SharedPrefs.getUserModel().getPhone()).child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
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

                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendFcmKeyToServer() {
        mDatabase.child("customers").child(SharedPrefs.getUserModel().getPhone()).child("fcmKey").setValue(FirebaseInstanceId.getInstance().getToken());
    }


    private void getAdminDataFromDB() {
        mDatabase.child("Settings").child("AdminNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    adminNumber = dataSnapshot.getValue(String.class);
                    SharedPrefs.setAdminPhone(adminNumber);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void initViewPager() {

        pics.add("https://apollo-singapore.akamaized.net:443/v1/files/1iczqqwl0z212-PK/image");
        pics.add("https://apollo-singapore.akamaized.net:443/v1/files/4y85llmhd70q3-PK/image");
        pics.add("https://apollo-singapore.akamaized.net:443/v1/files/b5l7cl2ej97o3-PK/image");
        pics.add("https://apollo-singapore.akamaized.net:443/v1/files/bpdmtu8nipg62-PK/image");
        pics.add("https://apollo-singapore.akamaized.net:443/v1/files/6jllokrluoka2-PK/imagee");
        pics.add("https://apollo-singapore.akamaized.net:443/v1/files/0ti7nda67kl41-PK/image");
        viewPager = findViewById(R.id.slider);
        mViewPagerAdapter = new MainSliderAdapter(this, pics);
        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPic = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Magic here
                if (currentPic >= pics.size()) {
                    currentPic = 0;
                    viewPager.setCurrentItem(currentPic);
                } else {
                    viewPager.setCurrentItem(currentPic);
                    currentPic++;
                }
                new Handler().postDelayed(this, 2000);
            }
        }, 2000); // Millisecond 1000 = 1 sec


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
            }

            mBackPressed = System.currentTimeMillis();
        }

    }

    private void initDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.name_drawer);
        TextView navSubtitle = (TextView) headerView.findViewById(R.id.phone_drawer);


        Menu nav_Menu = navigationView.getMenu();


        if (SharedPrefs.getUserModel().getPhone().equalsIgnoreCase("")) {
            nav_Menu.findItem(R.id.signout).setVisible(false);
            navSubtitle.setText("Welcome to Go Shop");

            navUsername.setText("Login or Signup");
            navUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, Login.class);
                    startActivity(i);
                }
            });
        } else {
            navSubtitle.setText(SharedPrefs.getUserModel().getPhone());

            navUsername.setText(SharedPrefs.getUserModel().getName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);


        View actionView = MenuItemCompat.getActionView(menuItem);
        final TextView textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        mDatabase.child("customers").child(SharedPrefs.getUserModel().getPhone()).child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartItemCountFromDb = dataSnapshot.getChildrenCount();
                textCartItemCount.setText("" + cartItemCountFromDb);
                SharedPrefs.setCartCount("" + cartItemCountFromDb);
                if (dataSnapshot.getChildrenCount() == 0) {
                    SharedPrefs.setCartCount("0");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent i = new Intent(MainActivity.this, Search.class);
            startActivity(i);
        } else if (id == R.id.action_cart) {
            if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
                CommonUtils.showToast("Your Cart is empty");
            } else {
                Intent i = new Intent(MainActivity.this, Cart.class);
                startActivity(i);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getUserCartProductsFromDB();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        int id = item.getItemId();


        if (id == R.id.profile) {
            Intent i = new Intent(MainActivity.this, MyProfile.class);
            startActivity(i);

        } else if (id == R.id.orders) {
            Intent i = new Intent(MainActivity.this, MyOrders.class);
            startActivity(i);

        } else if (id == R.id.chat) {
            Intent i = new Intent(MainActivity.this, LiveChat.class);

            startActivity(i);


        } else if (id == R.id.callus) {


            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + SharedPrefs.getAdminPhone()));
            startActivity(i);
        } else if (id == R.id.privacy) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://grocerryapp-6924e.firebaseio.com/Admin/privacyPolicy.json"));
            startActivity(i);
        } else if (id == R.id.share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Go Shop\n Download Now\n" + "http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName());
            startActivity(Intent.createChooser(shareIntent, "Share App via.."));


        } else if (id == R.id.signout) {

            PrefManager prefManager = new PrefManager(this);
            prefManager.setFirstTimeLaunch(true);
            SharedPrefs.logout();
            Intent i = new Intent(MainActivity.this, Splash.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void deleteAppData() {
        try {
            // clearing app data
            String packageName = getApplicationContext().getPackageName();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear " + packageName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
