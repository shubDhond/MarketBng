package com.marketbng.marketbng;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUpActivity extends Activity {
    EditText signUpEmail, signUpPassword, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpEmail = (EditText) findViewById(R.id.signUpEmail);
        signUpPassword = (EditText) findViewById(R.id.signUpPassword);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

    public void signUp(View view){
        if (fieldIsEmpty(signUpEmail)) {
            Toast.makeText(this, "Please enter an e-mail address", Toast.LENGTH_SHORT).show();
            return;
        } else if (!signUpEmail.getText().toString().matches("(.*)@(.*)")) {
            Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(fieldIsEmpty(signUpPassword) || fieldIsEmpty(confirmPassword)){
            Toast.makeText(this, "Both password field are required.", Toast.LENGTH_SHORT).show();
            return;
        } else if (!signUpPassword.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(this, "The passwords you entered do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        ParseUser user = new ParseUser();
        user.setEmail(signUpEmail.getText().toString());
        user.setPassword(signUpPassword.getText().toString());
        user.setUsername(signUpEmail.getText().toString());
        user.put("corporate", false);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    if (e.getCode() == com.parse.ParseException.EMAIL_TAKEN || e.getCode() == com.parse.ParseException.USERNAME_TAKEN) {
                        Toast.makeText(SignUpActivity.this, "The E-mail you entered is linked to another account", Toast.LENGTH_SHORT).show();
                    } else if (e.getCode() == com.parse.ParseException.TIMEOUT||e.getCode() == com.parse.ParseException.CONNECTION_FAILED) {
                        Toast.makeText(SignUpActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public boolean fieldIsEmpty(EditText field) {
        if (field.getText().toString().matches("")) {
            return true;
        }
        return false;
    }
}
