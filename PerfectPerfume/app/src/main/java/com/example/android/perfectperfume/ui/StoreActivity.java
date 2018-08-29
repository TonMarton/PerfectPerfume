package com.example.android.perfectperfume.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.data.Cart;
import com.example.android.perfectperfume.data.Perfume;
import com.example.android.perfectperfume.data.PerfumeDbHelper;
import com.example.android.perfectperfume.data.StoreAdapter;
import com.example.android.perfectperfume.ui.checkoutActivity.CheckOutActivity;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity implements Cart.CartCallbacks, PerfumeDbHelper.PerfumeDataCallbacks, StoreAdapter.StoreAdapterCallbacks {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ImageView cartImageView;

    private List<Perfume> mPerfumes = new ArrayList<>();
    private Cart cart;
    private PerfumeDbHelper perfumeDbHelper;
    private Animation cartAnimation;
    private boolean uiReady = false;

    public final static String DETAIL_INTENT_TAG = "detail_tag";
    final static String SAVE_PERFUMES_KEY = "perfumes_save";
    private static final String LOG_DATA_LOAD_TYPE = "StoreActivity loaded";

    @Override
    public void animateCartIcon() {
        if (uiReady) cartImageView.startAnimation(cartAnimation);
    }

    @Override
    public void deliverPerfumes(List<Perfume> items) {
        setUpToolbar();
        setUpRecyclerView(items);
    }

    @Override
    public void sendDatabaseError() {
        WarningMessage.createConnectionWarning(this,
                this.getResources().getString(R.string.default_database_warning));
    }

    @Override
    public void initialImageLoadingReady() {
        swapLayouts();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        }

        cart = new Cart(this);
        cartAnimation = AnimationUtils
                .loadAnimation(this, R.anim.cart_animation);

        if (savedInstanceState != null) {
            mPerfumes = savedInstanceState.getParcelableArrayList(SAVE_PERFUMES_KEY);
            setUpRecyclerView(mPerfumes);
            Log.d(LOG_DATA_LOAD_TYPE, "SavedInstanceState");
        } else {
            initLoadingScreen();

            //starts downloading the perfume data as soon as constructed, then returns the findings
            perfumeDbHelper = new PerfumeDbHelper(this);
            Log.d(LOG_DATA_LOAD_TYPE, "PerfumeDbHelper");
        }
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
        if (cart != null) cart.activityPaused();
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

    private void swapLayouts() {
        ViewGroup rootLayout = findViewById(R.id.store_root);
        FrameLayout loadingLayout = rootLayout.findViewById(R.id.store_loading_layout);
        FrameLayout uiLayout = rootLayout.findViewById(R.id.store_ui_layout);
        LayoutSwapAnimator.swap(loadingLayout, uiLayout, rootLayout.getWidth());
    }

    private void initLoadingScreen() {
        LoadingAnimationLayout layout = findViewById(R.id.loading_animation_layout_fl);
        layout.startAnimation();
    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar_store);
        setSupportActionBar(toolbar);
    }

    private void setUpRecyclerView(List<Perfume> items) {
        StoreAdapter adapter = new StoreAdapter(StoreActivity.this, items);
        ViewGroup container = findViewById(R.id.rv_container_rl);
        RecyclerView recyclerView = (RecyclerView) getLayoutInflater()
                .inflate(R.layout.store_recycler_view, container, false);
        GridLayoutManager lm = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);
        container.addView(recyclerView);
        uiReady = true;
    }

    private void openCheckOutActivity() {
        Intent intent = new Intent(this, CheckOutActivity.class);
        startActivity(intent);
    }
}