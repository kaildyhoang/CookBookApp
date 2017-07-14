package com.example.kaildyhoang.mycookbookapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaildyhoang.mycookbookapplication.view.MainActivity_View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText _edtTEmail, _edtTPassword;
    private ProgressDialog progressDialog;
    private ImageView _imgVLogo;

    private FirebaseAuth mAuth;
    private static final String TAG = "SignInActivityWithEMAI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //EditText
        _edtTEmail = (EditText) findViewById(R.id.editTextEmailSI);
        _edtTPassword = (EditText) findViewById(R.id.editTextPasswordSI);

        //Button
        findViewById(R.id.buttonSignUpSI).setOnClickListener(this);
        findViewById(R.id.buttonSignInSI).setOnClickListener(this);
        findViewById(R.id.buttonSignInFacebookSI).setOnClickListener(this);
        findViewById(R.id.buttonSignInGmailSI).setOnClickListener(this);

        //ImageView
        _imgVLogo = (ImageView) findViewById(R.id.imageViewLogo);
        //Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //Initialize the progressDialog
        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v){
        int i = v.getId();
        if(i == R.id.buttonSignInSI){
            doSignIn(_edtTEmail.getText().toString(),_edtTPassword.getText().toString());
        }else if(i == R.id.buttonSignUpSI){
            startActivity(new Intent(this,SignUpActivity.class));
        }else if(i == R.id.buttonSignInFacebookSI){

        }else if(i == R.id.buttonSignInGmailSI){

        }
    }

    private void doSignIn(String email,String password){
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        //[Start Sign in]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG,"SignInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(SignInActivity.this,"Welcome." + user.getEmail(),Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity_View.class));
                        }else {
                            Log.w(TAG,"SignInWithEmail:failure",task.getException());
                            Toast.makeText(SignInActivity.this,"SignInWithEmail failed.",Toast.LENGTH_SHORT).show();
                        }
                        if(!task.isSuccessful()){
                            Toast.makeText(SignInActivity.this,"Account not be exist, Please create new account.",Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });
    }



    private boolean validateForm (){
        boolean valid = true;

        String email = _edtTEmail.getText().toString();
        if(TextUtils.isEmpty(email)){
            _edtTEmail.setError("Require.");
            valid = false;
        }else{
            _edtTEmail.setError(null);
        }

        String password = _edtTPassword.getText().toString();
        if(TextUtils.isEmpty(password)){
            _edtTPassword.setError("Require.");
            valid = false;
        }else{
            _edtTPassword.setError(null);
        }

        return  valid;
    }


    private void showProgressDialog(){
        progressDialog.setMessage("Waiting...");
        progressDialog.show();
    }
    private void hideProgressDialog(){
        progressDialog.dismiss();
    }
}
