package com.studybean.studybeanliteapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    String TAG="LogIn";

    int RC_SIGN_IN=1;

    EditText editTextEmail, editTextPassword;

    TextView forgetPass,pricavyPolicy;

    Button goButton;

    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    CallbackManager callbackManager;
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pricavyPolicy = findViewById(R.id.privacyPolicyId);
        String text = "Review our Privacy Policy for this app";

        SpannableString hl = new SpannableString(text);

        final ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String url = "http://sites.google.com/view/solutionpro";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);

                ds.setColor(Color.parseColor("#9f161d"));
                ds.setUnderlineText(false);
            }
        };

        hl.setSpan(clickableSpan1, 11,25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        pricavyPolicy.setText(hl);
        pricavyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        loginButton = findViewById(R.id.fbLoginId);
        loginButton.setReadPermissions("email");

        signInButton = findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

        printKeyHash();

        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        if(mAuth.getCurrentUser() != null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), Home.class));
        }

        goButton = findViewById(R.id.goButtonId);

        editTextEmail = findViewById(R.id.emailId);
        editTextPassword = findViewById(R.id.passwordId);

        forgetPass = findViewById(R.id.forgetPassId);

        progressBar = findViewById(R.id.progressBarId);

        goButton.setOnClickListener(this);
        forgetPass.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode,data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        progressBar.setVisibility(View.VISIBLE);

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this,"Google SignIn Failed", Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user)
    {
        startActivity(new Intent(getApplicationContext(), Home.class));
        finish();

        Toast.makeText(getApplicationContext(), "Google LogIn Successful", Toast.LENGTH_SHORT).show();
    }

    private void printKeyHash() {
        try
        {
            PackageInfo info = getPackageManager().getPackageInfo("com.applicationull.solutionpro", PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures)
            {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.e("KEYHASH", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void registerUser()
    {
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty())
        {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Valid email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6)
        {
            editTextPassword.setError("Atleast 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if(task.isSuccessful())
                {
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();

                    //Intent intent = new Intent(LoginActivity.this, Home.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {

                                    finish();
                                    startActivity(new Intent(getApplicationContext(), Home.class));

                                    //Intent intent = new Intent(LoginActivity.this, Home.class);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Toast.makeText(getApplicationContext(), "Log in successful", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    editTextPassword.setError("Wrong password");
                                    editTextPassword.requestFocus();
                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    //Toast.makeText(getApplicationContext(), "Error occurred",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.goButtonId:
                registerUser();
                break;

            case R.id.forgetPassId:
                startActivity(new Intent(this,ResetPassword.class));
                break;

            case R.id.fbLoginId:
                signIn();
                break;
        }
    }

    private void signIn() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                progressBar.setVisibility(View.VISIBLE);

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }



    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this,"LogIn from google", Toast.LENGTH_LONG).show();
                        Log.e("Error Occured", ""+e.getMessage());

                        LoginManager.getInstance().logOut();
                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Toast.makeText(getApplicationContext(), "Facebook Login Successful", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getApplicationContext(), Home.class));
                finish();
            }
        });
    }

}
