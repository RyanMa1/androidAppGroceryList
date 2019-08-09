package com.example.myfirstapplication;

import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.ProgressDialog;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import javax.xml.transform.Templates;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //ProgressDialog is here
    @VisibleForTesting
    public ProgressDialog progressDialog;


    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;


    //authentication for firebase
    private FirebaseAuth mAuth;


    private Button btnLogin;



    private static final String TAG = "EmailPassword";
    public static final String EXTRA_MESSAGE = "com.example.myfirstApplication.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mAuth.getInstance().signOut();
        //Views

        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        //Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);


        //findView for signout Button
        //findView for Verify method button
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        //check to see if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.getInstance().signOut();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if(user != null){
            Intent intent = new Intent(this, DisplayMessageActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }



    private void createAccount(String email, String password){
        Log.d(TAG, "createAccount: " + email);
        Toast.makeText(MainActivity.this,"I am in here", Toast.LENGTH_SHORT).show();
        if(!validateForm()){
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Sign in success, update UI with the signed-In's user information
                    Log.d(TAG, "createAccount:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }else{
                    //If sign in fails, display a message to the user
                    Log.w(TAG, "createAccount:Failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
                hideProgressDialog();
            }
        });
    }

    private void signIn(String email, String password){
        Log.d(TAG, "signIn:" + email);
        if(!validateForm()){
            return ;
        }
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                }else{
                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }

                if(!task.isSuccessful()){
                    mStatusTextView.setText(R.string.auth_failed);
                }
                hideProgressDialog();
            }
        });

    }
    public void signOut(){
        mAuth.getInstance().signOut();
        updateUI(null);
    }

    private boolean validateForm() {

        boolean valid = true;

        String email = mEmailField.getText().toString();
        if(TextUtils.isEmpty(email)){
            mEmailField.setError("Required.");
            valid = false;
        }else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if(TextUtils.isEmpty(password) || password.length() < 5){
            mPasswordField.setError("Must be atleast 5 characters long");
            valid = false;
        }else {
            mPasswordField.setError(null);
        }
        return valid;
    }


    //Progress Bar Methods Defined below here (should be the base class to be inherited from ...
    public void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);

        }
        progressDialog.show();
    }

    public void hideProgressDialog(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.emailCreateAccountButton) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());

        } else if (i == R.id.emailSignInButton) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if( i == R.id.signOutButton){
            signOut();
        }
    }
//
//    public void hideKeyboard(View view){
//        final InputManager imm = (InputManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        if(imm != null){
//            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
//        }
//    }
    @Override
    public void onStop(){
        super.onStop();
        hideProgressDialog();
    }


}
