package com.example.android.perfectperfume.ui;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.data.Cart;
import com.example.android.perfectperfume.data.Perfume;
import com.example.android.perfectperfume.ui.checkoutActivity.CheckOutActivity;

public class DetailActivity extends AppCompatActivity implements Cart.CartCallbacks {

    private FloatingActionButton cartFab;
    private Toolbar toolbar;
    private LinearLayout detailContainer;
    private TextView brandSexTextView;
    private TextView nameTextView;
    private TextView sizeTextView;
    private TextView priceTextView;
    private TextView descriptionTextView;
    private ImageView cartImageView;

    Perfume perfume;
    private Cart cart;
    private Animation cartAnimation;


    private final static String PERFUME_SAVE_TAG = "perfume_tag";

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
    public void animateCartIcon() {
        if (cartImageView != null) cartImageView.startAnimation(cartAnimation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        cart = new Cart(this);
        cartAnimation = AnimationUtils
                .loadAnimation(this, R.anim.cart_animation);

        if (savedInstanceState != null) {
            perfume = savedInstanceState.getParcelable(PERFUME_SAVE_TAG);
        } else {
            perfume = getIntent().getParcelableExtra(StoreActivity.DETAIL_INTENT_TAG);
        }

        bindViews();
        cartFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart.addItemToCart(perfume.getId());
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        brandSexTextView.setText("By " + perfume.getBrand() + " For " + perfume.getSex());
        nameTextView.setText(perfume.getName());
        sizeTextView.setText(perfume.getSize());
        priceTextView.setText("€" + perfume.getPrice());
        descriptionTextView.setText(perfume.getDescription());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PERFUME_SAVE_TAG, perfume);
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

    private void bindViews() {
        cartFab = findViewById(R.id.cart_fab);
        toolbar = findViewById(R.id.toolbar_detail);
        detailContainer = findViewById(R.id.detail_text_background_ll);
        brandSexTextView = findViewById(R.id.brand_sex_tv);
        nameTextView = findViewById(R.id.name_tv);
        sizeTextView = findViewById(R.id.size_tv);
        priceTextView = findViewById(R.id.price_tv);
        descriptionTextView = findViewById(R.id.description_tv);
    }

    private void openCheckOutActivity() {
        Intent intent = new Intent(this, CheckOutActivity.class);
        startActivity(intent);
    }
}

