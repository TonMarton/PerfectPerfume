package com.example.android.perfectperfume.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.utilities.SignInHandler;

public class LoginActivity extends AppCompatActivity implements SignInHandler.SignInHelper {

    private SignInHandler signInHandler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureTransitions();

        setContentView(R.layout.activity_login);
        signInHandler = new SignInHandler(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignInHandler.RC_SIGN_IN) signInHandler.signInWithGoogle(data);
    }

    @Override
    public void startActivityForAuthentication(Intent signInIntent) {
        startActivityForResult(signInIntent, SignInHandler.RC_SIGN_IN);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void signInReady() {
        Intent intent = new Intent(this, StoreActivity.class);
        ImageView sharedElement = findViewById(R.id.login_shared_element_iv);
        if (sharedElement == null) {
            startActivity(intent);
        } else {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    sharedElement, "shared_element_bottle");
            startActivity(intent, options.toBundle());
        }
        finish();
    }

    @Override
    public void showLoginInterface() {
        FrameLayout root = findViewById(R.id.login_activity_root);
        FrameLayout interfaceLayout = (FrameLayout) this.getLayoutInflater()
                .inflate(R.layout.login_interface_layout, root, false);
        root.addView(interfaceLayout);
        FrameLayout loadingLayout = findViewById(R.id.loading_root_fl);
        LayoutSwapAnimator.swap(loadingLayout, interfaceLayout, root.getWidth());
        setUpForLogIn();
    }

    private void configureTransitions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setAllowEnterTransitionOverlap(false);
            getWindow().setExitTransition(null);
        }
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
}