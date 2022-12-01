package com.example.matricexaminationguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    Boolean signUpModeActive = true;
    TextView loginTextView;
    EditText usernameEditText;
    EditText passwordEditText;

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            signUpClicked(view);
        }

        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginTextView) {

            Button signUpButton = findViewById(R.id.signUpButton);

            if (signUpModeActive) {
                signUpModeActive = false;
                signUpButton.setText("Login");
                loginTextView.setText("or, Sign Up");
            } else {
                signUpModeActive = true;
                signUpButton.setText("Sign Up");
                loginTextView.setText("or, Login");
            }

        } else if (view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void signUpClicked(View view) {

        if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
            Toast.makeText(this, "A username and a password are required.", Toast.LENGTH_SHORT).show();

        } else {
            if (signUpModeActive) {
                ParseUser user = new ParseUser();
                user.setUsername(usernameEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i ("Signup","Success");
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.i ("Signup","Failed");
                        }
                    }
                });
            } else {
                // Login
                ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            Log.i("Login", "ok!");
                            setContentView(R.layout.activity_main);
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        loginTextView = findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(this);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        ImageView logoImageView = findViewById(R.id.logoImageView);
        logoImageView.setOnClickListener(this);

        passwordEditText.setOnKeyListener(this);

        if(ParseUser.getCurrentUser()!=null){
            Intent i = new Intent(getApplicationContext(),ExamsByYear.class);
            startActivity(i);
        }
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    public  void ViewByYear(View v){
        Button clicked= (Button)v;
        String subject=clicked.getText().toString();
        Intent i=new Intent(MainActivity.this,ExamsByYear.class);
        i.putExtra("subject",subject);
        startActivity(i);
    }
}