package com.example.parichay.assignment2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */

//launch activity that asks for login details username and password
public class LoginActivity extends AppCompatActivity {

    // private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView usnm;
    private EditText pswrd;
    private View mProgressView;

    public static SharedPreferences use;

    // Writing data to SharedPreferences
    public static SharedPreferences.Editor editor ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        use = getSharedPreferences("MyPref", MODE_PRIVATE);
        // Set up the login form.

        //stores username entered
        usnm = (AutoCompleteTextView) findViewById(R.id.username);
        usnm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    view.setBackgroundResource(R.drawable.onfocus);
                } else {
                    view.setBackgroundResource(R.drawable.lostfocus);
                }
            }
        });

        //stores password entered
        pswrd = (EditText) findViewById(R.id.password);
        pswrd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    view.setBackgroundResource(R.drawable.onfocus);
                } else {
                    view.setBackgroundResource(R.drawable.lostfocus);
                }
            }
        });


        Button button1 = (Button) findViewById(R.id.loginbutton1);

        //calls attempt login method
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();

            }
        });
        //mProgressView = findViewById(R.id.login_progress);
    }


    //calls the login api to check against the database
    public void parsing(){
        final String url = "http://192.168.43.218/assignment2/default/login.json?userid="+usnm.getText().toString() +"&password="+pswrd.getText().toString();
        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest s1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override

                    public void onResponse(String response) {
                        // JSON Parsing
                        try {
                            JSONObject resp = new JSONObject(response);
                            String r = resp.getString("success"); //success:true only when the user is valid


                            Log.e("logged in", "");
                            if (r.equals("true")) {
                                JSONObject user = resp.getJSONObject("user");
                                String name = user.getString("name");
                                String usertype = user.getString("user_type");
                                String hostel = user.getString("hostel");
                                String validid = user.getString("id");

                                    //next screen opens up when successful login
                                    Intent nextScreen2 = new Intent(getApplicationContext(), MenuActivity.class);
                                    nextScreen2.putExtra("username", name);
                                    nextScreen2.putExtra("usertype", usertype);
                                    nextScreen2.putExtra("hostel",hostel);
                                    nextScreen2.putExtra("validid",validid);
                                    nextScreen2.putExtra("lastactivity",0);
                                    startActivity(nextScreen2);
                                }
                            else {Dialog("invalid entry","");}

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                //giving volley error

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Dialog(error.toString(),"error");
                    }
                }

        ) {
            protected Response<String> parseNetworkResponse(NetworkResponse networkResponse) {
                String sessionId = networkResponse.headers.get("Set-Cookie");
                editor = use.edit();
                editor.putString("Set-Cookie",sessionId);
                editor.apply();
                return super.parseNetworkResponse(networkResponse);
            }
        };

        rq.add(s1);

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    //calls the method parsing after ensuring all the field are filled (UI designing)
    private void attemptLogin() {


        // Reset errors.
        usnm.setError(null);
        pswrd.setError(null);

        // Store values at the time of the login attempt.
        String user = usnm.getText().toString();
        String password = pswrd.getText().toString();

        boolean cancel = false;
        View focusView = null;



        // Check for a all required fields
        if (TextUtils.isEmpty(user)) {
            usnm.setError(getString(R.string.error_field_required));
            focusView = usnm;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            pswrd.setError(getString(R.string.error_field_required));
            focusView = pswrd;
            focusView = pswrd;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            parsing();   //method parsing is called if all fields are filled

        }
    }

  /* private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
       return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;
    }*/

    //customised dialog alert to display error message
    private void Dialog(String msg, String b) {
        AlertDialog.Builder b1 = new AlertDialog.Builder(this);
        b1.setMessage(msg);
        b1.setCancelable(true);

        b1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        b1.setTitle(b);
        AlertDialog alert = b1.create();
        alert.show();
        Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);

    }


}

