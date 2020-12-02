package com.budget.buddy.moneytracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    Button login,register;
    EditText username,password;
    String Username,Password;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    boolean emailchecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.register);
        register = findViewById(R.id.login);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging User...");
        progressDialog.setCanceledOnTouchOutside(false);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
                
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
            Toast.makeText(LoginActivity.this,"You Are Logged In!!", Toast.LENGTH_SHORT).show();
            sendUserToMain();
        }

        else
        {
            Toast.makeText(this,"Please Verify Your Account...",Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }

    }

    private void loginUser() {
        Username = username.getText().toString().trim();
        Password = password.getText().toString().trim();
        progressDialog.show();

        if(Username.equals("")){
            progressDialog.dismiss();
            Toast.makeText(this,"Please Enter Email",Toast.LENGTH_SHORT).show();
        }
        else if(Password.equals("")){
            progressDialog.dismiss();
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show();
        }
        else{
            firebaseAuth.signInWithEmailAndPassword(Username,Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful()) {
                                progressDialog.dismiss();
                                verifyEmail();
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this,"Login Error!! Please Check Your Email And Password ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    private void goToRegisterActivity() {
        Intent loginIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void sendUserToMain() {
        Intent mainIntent=new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
