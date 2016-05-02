package com.example.parichay.assignment2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//called when a new user has to added to the database
public class AddUSerActivity extends AppCompatActivity {

    String user_type;
    private EditText Name;
    private EditText Email;
    private EditText Phone;
    private EditText Hostel;
    private EditText Username;
    private EditText Password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //feeds all the details of the new user
        Name = (EditText) findViewById(R.id.name);
        Email = (EditText) findViewById(R.id.email);
        Phone = (EditText) findViewById(R.id.phone);
        Hostel = (EditText) findViewById(R.id.hostel);
        Username = (EditText) findViewById(R.id.username);
        Password = (EditText) findViewById(R.id.password);

        //calls attemptSubmit on clicking the submit button
        Button SubmitButton = (Button) findViewById(R.id.button);


        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        List<String> categories = new ArrayList<String>();
        Bundle extras2 = getIntent().getExtras();
        String usertype = extras2.getString("usertype");
        if(usertype.equals("dean")){
            categories.add("Student");
            categories.add("Warden");
        }
        else {
            categories.add("Student");
        }

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();
                if (item == "Student") {
                    user_type = "student";

                } else if (item == "Warden") {
                    user_type = "warden";


                }

                // Showing selected spinner item
                // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }

        });




        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSubmit();
            }
        });
    }
    private void attemptSubmit() {


        // Reset errors.
        Name.setError(null);
        Email.setError(null);
        Password.setError(null);
        Username.setError(null);

        // Store values at the time of the thread creation
        String name_ = Name.getText().toString();
        String email_ = Email.getText().toString();
        String password_ = Password.getText().toString();
        String username_ = Username.getText().toString();
        String hostel_ = Hostel.getText().toString();
        String phone_ = Phone.getText().toString();
        String usertype_ = user_type;
        try {
            String name = URLEncoder.encode(name_, "UTF-8");
            String email = URLEncoder.encode(email_, "UTF-8");
            String password = URLEncoder.encode(password_, "UTF-8");
            String username = URLEncoder.encode(username_, "UTF-8");
            String usertype = URLEncoder.encode(usertype_, "UTF-8");
            String hostel = URLEncoder.encode(hostel_, "UTF-8");
            String phone = URLEncoder.encode(phone_, "UTF-8");
            boolean cancel = false;
            View focusView = null;


            // Check for empty TextFields to show error.
            if (TextUtils.isEmpty(name_)) {
                Name.setError(getString(R.string.error_field_required));
                focusView = Name;
                cancel = true;
            } else if (TextUtils.isEmpty(email_)) {
                Email.setError(getString(R.string.error_field_required));
                focusView = Email;
                cancel = true;
            }
            else if (TextUtils.isEmpty(username_)) {
                Username.setError(getString(R.string.error_field_required));
                focusView = Username;
                cancel = true;
            }
            else if (TextUtils.isEmpty(password_)) {
                Password.setError(getString(R.string.error_field_required));
                focusView = Password;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt user creation and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // perform the thread creation by parsing in case of no error.
                parsing(usertype,username,hostel,password,email,phone,name);

            }
        }
        catch(Exception e){
            ;
        }


    }

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

    //calls the new user api if all fields are filled
    public void parsing(String usertype,String username,String hostel,String password,String email,String phone,String name) {
        final String url = "http://192.168.43.218/assignment2/default/new.json?username=" + username +"&password=" + password + "&email=" +email+"&phone="+phone+"&usertype="+usertype+"&hostel="+hostel+"&name="+name;
        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest s1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Dialog("User Added", "Success");

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dialog(error.toString(),"error");
                    }
                }
        ) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> getHeader = new HashMap<String, String>();
                getHeader.put("Cookie", LoginActivity.use.getString("Set-Cookie", ""));
                return getHeader;
            }
        };
        rq.add(s1);
    }

}
