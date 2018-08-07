package com.example.android.perfectperfume.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.utilities.SignInHandler;

public class LoginActivity extends AppCompatActivity implements SignInHandler.SignInHelper {

    private SignInHandler signInHandler;


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signInHandler = new SignInHandler(this);
    }

    //if there is a user signed in, there is no need for further authentication.
    @Override
    protected void onStart() {
        super.onStart();

        //TODO: on google login make the activity disappear on success
        // signInReady(); OR setUpForLogIn();
        // based on the authentication status
        // implement a proper auth state listener
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignInHandler.RC_SIGN_IN) signInHandler.signInWithGoogle(data);
    }

    // SignInHelper interface functions
    @Override
    public void startActivityForAuthentication(Intent signInIntent) {
        startActivityForResult(signInIntent, SignInHandler.RC_SIGN_IN);
    }

    @Override
    public void signInReady() {
        Intent intent = new Intent(this, StoreActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showLoginInterface() {
        FrameLayout root = findViewById(R.id.login_activity_root);
        FrameLayout interfaceLayout = (FrameLayout) this.getLayoutInflater()
                .inflate(R.layout.login_interface_layout, root, false);
        root.addView(interfaceLayout);
        FrameLayout loadingLayout = findViewById(R.id.loading_root_fl);
        makeLayoutSwapAnimation(loadingLayout, interfaceLayout, root.getWidth());
        setUpForLogIn();
    }

    private void setUpForLogIn() {
        final EditText emailEditText = findViewById(R.id.sign_in_email_edittext);
        final EditText passwordEditText = findViewById(R.id.sign_in_password_edittext);

        Button mSignUpButton = findViewById(R.id.sign_up_btn);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                signInHandler.signUpWithEmail(email, password);
            }
        });

        Button mSignInButton = findViewById(R.id.sign_in_btn);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                signInHandler.signInWithEmailAndPassword(email, password);
            }
        });

        Button mGoogleSignInButton = findViewById(R.id.sign_in_google_btn);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInHandler.authenticateWithGoogle();
            }
        });
    }

    private void makeLayoutSwapAnimation(FrameLayout leftL, FrameLayout rightL, int width) {
        repositionRightLayout(rightL, width);
        leftL.getLayoutParams().width = width;
        createCombinedAnimationSet(leftL, rightL, width).start();
    }

    private void repositionRightLayout(FrameLayout rightL, int width) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rightL.getLayoutParams();
        params.leftMargin = width;
        params.width = width;
        rightL.setLayoutParams(params);
        rightL.setVisibility(View.VISIBLE);
    }

    private AnimatorSet createCombinedAnimationSet(final FrameLayout leftL, FrameLayout rightL, int width) {
        AnimatorSet combinedSet = new AnimatorSet();
        ValueAnimator outAnim = createSlideLeftAnimation(0, -width, leftL);
        ValueAnimator inAnim = createSlideLeftAnimation( width, 0, rightL);
        combinedSet.play(outAnim).with(inAnim);
        combinedSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                leftL.setVisibility(View.GONE);
                ViewGroup root = (ViewGroup) leftL.getParent();
                root.removeView(leftL);
            }
        });
        combinedSet.setInterpolator(new DecelerateInterpolator());
        return combinedSet;
    }

    private ValueAnimator createSlideLeftAnimation(int start, int end, final FrameLayout layout) {
        final ValueAnimator slideLeft = ValueAnimator.ofInt(start, end);
        slideLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
                layoutParams.leftMargin = val;
                layout.setLayoutParams(layoutParams);
            }
        });
        slideLeft.setDuration(1000);
        return slideLeft;
    }
}