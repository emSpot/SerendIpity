package com.example.bel.loginregister;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Bel on 18.02.2016.
 */
public class Login extends AppCompatActivity{

    EditText etLogin;
    EditText etPassword;
    ImageButton ibLogin;
    //Button bRegister;
    TextView tvForgetPassword;
    TextView tvRegistration;
    CheckBox cbRememerMe;

    UserLocalStore userLocalStore;

    @Override
    protected void onStart() {
        super.onStart();

        if(userLocalStore.getUserLoggedIn() && userLocalStore.isRememberMe() ){
            displayUserDetails();
            cbRememerMe.setChecked(userLocalStore.isRememberMe());
        }
    }

    private void displayUserDetails(){
        User user = userLocalStore.getLoggedInUserData();

        etLogin.setText(user.email);
        etPassword.setText(user.password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //set edittext fields from to variables
        etLogin = (EditText)findViewById(R.id.etLogin);
        etPassword = (EditText)findViewById(R.id.etPassword);

        //set ImageButton and CheckBox to variables
        ibLogin = (ImageButton) findViewById(R.id.ibLogin);
        cbRememerMe = (CheckBox)findViewById(R.id.cbRememberMe);

        //bRegister = (Button) findViewById(R.id.bRegister);
        tvForgetPassword = (TextView)findViewById(R.id.tvForgetPassword);
        tvRegistration = (TextView) findViewById(R.id.tvRegistration);

        //centralize text in edittexts
        etLogin.setGravity(Gravity.CENTER);
        etPassword.setGravity(Gravity.CENTER);

        //get reference to local store
        userLocalStore = new UserLocalStore(this);

        //set listener to LOGIN button
        ibLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etLogin.getText().toString();
                String password = etPassword.getText().toString();

                User user = new User(email, password);

                authenticate(user);

                userLocalStore.setRememberUser(cbRememerMe.isChecked());
            }
        });

        tvRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });

        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ResetPassword.class);
                startActivity(intent);
            }
        });
    }


    private void authenticate(User user){
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if(returnedUser == null) {
                    showErrorMessage();
                }
                else{
                    logUserIn(returnedUser);
                }
            }
        });
    }

    private void logUserIn(User user){
        userLocalStore.storeUserData(user);
        userLocalStore.setUserLoggedIn(true);
    }

    private void showErrorMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this);
        dialogBuilder.setMessage("Incorrect Email/Password Combination");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

}
