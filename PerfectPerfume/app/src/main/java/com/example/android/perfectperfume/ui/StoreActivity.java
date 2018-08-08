package com.example.android.perfectperfume.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.data.Cart;
import com.example.android.perfectperfume.data.Perfume;
import com.example.android.perfectperfume.data.PerfumeDbHelper;
import com.example.android.perfectperfume.data.StoreAdapter;
import com.example.android.perfectperfume.ui.checkoutActivity.CheckOutActivity;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity implements Cart.CartCallbacks, PerfumeDbHelper.PerfumeDataCallbacks {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ImageView cartImageView;

    private List<Perfume> mPerfumes = new ArrayList<>();

    private Cart cart;
    private PerfumeDbHelper perfumeDbHelper;
    private Animation cartAnimation;

    public final static String DETAIL_INTENT_TAG = "detail_tag";
    final static String SAVE_PERFUMES_KEY = "perfumes_save";
    private static final String LOG_DATA_LOAD_TYPE = "StoreActivity loaded";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        initLoadingScreen();

        /*cart = new Cart(this);
        cartAnimation = AnimationUtils
                .loadAnimation(this, R.anim.cart_animation);
        perfumeDbHelper = new PerfumeDbHelper(this);

        toolbar = findViewById(R.id.toolbar_store);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.store_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(StoreActivity.this, 1));

        if (savedInstanceState != null) {
            mPerfumes = savedInstanceState.getParcelableArrayList(SAVE_PERFUMES_KEY);
            setRecyclerViewAdapter(mPerfumes);
            Log.d(LOG_DATA_LOAD_TYPE, "SavedInstanceState");
        }
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.store_menu, menu);
        LayoutInflater inf =
                (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cartImageView =
                (ImageView) inf.inflate(R.layout.cart_icon_imageview, null);
        cartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCheckOutActivity();
            }
        });
        menu.findItem(R.id.cart).setActionView(cartImageView);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        cart.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cart != null) cart.activityResumed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVE_PERFUMES_KEY , (ArrayList<Perfume>) mPerfumes);
    }

    private void initLoadingScreen() {
        LoadingAnimationLayout loadingLayout = findViewById(R.id.loading_animation_layout_fl);
        loadingLayout.startAnimation();
    }

    private void setRecyclerViewAdapter(List<Perfume> items) {
        StoreAdapter adapter = new StoreAdapter(StoreActivity.this, items);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void animateCartIcon() {
        cartImageView.startAnimation(cartAnimation);
    }

    @Override
    public void deliverPerfumes(List<Perfume> items) {
        setRecyclerViewAdapter(items);
        Log.d(LOG_DATA_LOAD_TYPE, "FirebaseDB");
    }

    private void openCheckOutActivity() {
        Intent intent = new Intent(this, CheckOutActivity.class);
        startActivity(intent);
    }
}