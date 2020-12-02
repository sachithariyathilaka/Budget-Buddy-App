package com.budget.buddy.moneytracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    Button login,register;
    EditText username,password;
    String Username, Password;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    boolean emailchecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currunt_user=firebaseAuth.getCurrentUser();
        if(currunt_user!=null) {
            verifyEmail();
        }
    }

    private void verifyEmail() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        emailchecker=user.isEmailVerified();

        if(emailchecker)
        {
            Toast.makeText(RegisterActivity.this,"You Are Logged In!!", Toast.LENGTH_SHORT).show();
            sendUserToMain();
        }

        else
        {
            Toast.makeText(this,"Please Verify Your Account...",Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }

    }

    private void registerUser() {
        Username = username.getText().toString().trim();
        Password = password.getText().toString().trim();
        progressDialog.show();
        if(Username.equals("")){
            progressDialog.dismiss();
            Toast.makeText(this,"Please Enter Your Email",Toast.LENGTH_SHORT).show();
        }
        else if(Password.equals("")){
            progressDialog.dismiss();
            Toast.makeText(this,"Please Enter Your Password",Toast.LENGTH_SHORT).show();
        }
        else{
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(Username,Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this,"Registered Succusfully!!!",Toast.LENGTH_SHORT).show();
                                sendEmailVerificationMessage();
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this,"Oops Try Again!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    private void sendUserToMain() {
        Intent mainIntent=new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendEmailVerificationMessage() {
        FirebaseUser user=firebaseAuth.getCurrentUser();

        if(user!=null)
        {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(RegisterActivity.this,"Registration Succusfull!! Please Check your inbox to verify your account.....",Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                    }



                    else
                    {
                        Toast.makeText(RegisterActivity.this,"Error Occured!!",Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                    }

                }
            });
        }
    }

    private void goToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
