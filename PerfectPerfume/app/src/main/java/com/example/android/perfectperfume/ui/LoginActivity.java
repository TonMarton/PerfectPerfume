package com.example.android.perfectperfume.ui;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.utilities.SignInHandler;

public class LoginActivity extends AppCompatActivity implements SignInHandler.SignInHelper {

    // this is a completely different branch - this is feature - behold

    private LoadingAnimationLayout loadingAnimationLayout;

    private EditText emailEditText;
    private EditText passwordEditText;

    private SignInHandler signInHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signInHandler = new SignInHandler(this);
        //loadingAnimationLayout = findViewById(R.id.loading_animation_layout_fl);
        /*loadingAnimationLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            loadingAnimationLayout.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        } else {
                            loadingAnimationLayout.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }
                        loadingAnimationLayout.invalidate();
                        loadingAnimationLayout.startAnimation();
                    }
                });*/
    }

    //if there is a user signed in, there is no need for further authentication.
    @Override
    protected void onStart() {
        super.onStart();

        //TODO: on google login make the activity disappear on success
        if (SignInHandler.getCurrentUser() != null) {
            signInReady();
            //startLoadingAnimation();
        } else {
            setUpForLogIn();
        }
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

    private void setUpForLogIn() {
        emailEditText = findViewById(R.id.sign_in_email_edittext);
        passwordEditText = findViewById(R.id.sign_in_password_edittext);

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

        // create client with options
        Button mGoogleSignInButton = findViewById(R.id.sign_in_google_btn);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInHandler.authenticateWithGoogle();
            }
        });
    }
}