package com.marketbng.marketbng;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class SignInActivity extends Activity {
    EditText loginEmail, loginPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(View view){
        ParseUser.logInInBackground(loginEmail.getText().toString(), loginPassword.getText().toString(), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    Toast.makeText(SignInActivity.this, "Logged In!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignInActivity.this, SelectSurveyActivity.class);
                    startActivity(intent);
                } else {
                    if(e.getCode() == ParseException.CONNECTION_FAILED || e.getCode() == ParseException.TIMEOUT){
                        Toast.makeText(SignInActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    }else if(e.getCode() == ParseException.EMAIL_NOT_FOUND){
                        Toast.makeText(SignInActivity.this, "No User with the specified E-mail", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(SignInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
