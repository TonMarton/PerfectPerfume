package com.example.android.perfectperfume.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.ui.WarningMessage;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInHandler implements FirebaseAuth.AuthStateListener{

    private SignInHelper signInCallbacks;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    private Context context;

    public static final int RC_SIGN_IN = 1;
    private final int PASSWORD_LENGTH = 6;
    private static final String LOG_IN_TAG = "Log-in info";

    public interface SignInHelper {
        void startActivityForAuthentication(Intent signInIntent);
        void signInReady();
        void showLoginInterface();
    }

    public SignInHandler(Context context) {
        try {
            signInCallbacks = (SignInHelper) context;

        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement SignInHelper");
        }
        this.context = context;
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
        auth.removeAuthStateListener(this);
        final FirebaseUser user = auth.getCurrentUser();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (user == null) {
                    signInCallbacks.showLoginInterface();
                } else {
                    Log.d("UserEmail" ,user.getEmail());
                    signInCallbacks.signInReady();
                }
            }
        }, 2000);
    }

    public void authenticateWithGoogle() {
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);
        signInCallbacks.startActivityForAuthentication(googleSignInClient.getSignInIntent());
    }

    public void signInWithGoogle(Intent data) {

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        GoogleSignInAccount account;
        try {
            // Only Google Sign is done, do Firebase auth to get the data of the user
            account = task.getResult(ApiException.class);
        } catch (ApiException e) {
            Log.w(LOG_IN_TAG, "Google sign in failed", e);
            return;
            // TODO: do something, like log in again and return
        }

        Log.d(LOG_IN_TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleResult(task, 1);
                    }
                });
    }

    public void signInWithEmailAndPassword(String email, String password) {
        if (!isValidEmailPassword(email, password)) return;
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleResult(task, 0);
                    }
                });
    }

    public void signUpWithEmail(String email, String password) {
        if (!isValidEmailPassword(email, password)) return;
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleResult(task, 2);
                    }
                });
    }

    private void handleResult(final Task<AuthResult> task, final int type) {
        final String logText;
        if (type == 0) {
            logText = "signInWithEmail";
        } else if (type == 1) {
            logText = "signInWithCredential";
        } else {
            logText = "signUpWithEmail";
        }
        task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(LOG_IN_TAG,  logText + " succeeded");
                FirebaseUser user = auth.getCurrentUser();
                signInCallbacks.signInReady();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LOG_IN_TAG, logText + " failed", task.getException());
                String warningText;
                Resources res = context.getResources();
                if (type == 0) {
                    warningText = res.getString(R.string.email_signin_error_warning);
                } else if (type == 1) {
                    warningText = res.getString(R.string.google_signin_error_warning);
                } else {
                    warningText = res.getString(R.string.email_signup_error_warning);
                }
                WarningMessage.createConnectionWarning(context, warningText);
            }
        });
    }

    public static FirebaseUser getCurrentUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) return user;
        else throw new NullPointerException("There is no currently signed in user...");
    }

    public boolean isValidEmailPassword(String email, String password) {
        if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.length() >= PASSWORD_LENGTH) {
            return true;
        } else {
            String text = context.getResources().getString(R.string.validation_warning);
            WarningMessage.createConnectionWarning(context, text);
            return false;
        }

    }
}
