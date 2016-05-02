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


//all details are taken and parsed to the server by this activity
public class NewComplaintActivity extends AppCompatActivity {

    int complaint_type, concerned_user;
    private EditText Title;
    private EditText Description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complaint);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Title = (EditText) findViewById(R.id.editText);
        Description = (EditText) findViewById(R.id.editText2);

        //calls attemptSubmit on clicking the submit button
        Button SubmitButton = (Button) findViewById(R.id.button);


        // Spinner element used to feed complaint_type and concerned person
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> categories = new ArrayList<String>();
        categories.add("Individual Level");
        categories.add("Hostel Level");
        categories.add("Institute Level");
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        List<String> categories1 = new ArrayList<String>();
        categories1.add("Warden");
        categories1.add("Electrician");
        categories1.add("Carpenter");
        categories1.add("Computer Centre");
        categories1.add("Dean");

        final ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories1);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item a customised list is displayed on spinner 2
                String item = parent.getItemAtPosition(position).toString();
                if (item == "Individual Level") {
                    complaint_type = 0;
                    dataAdapter1.clear();
                    dataAdapter1.add("Electrician");
                    dataAdapter1.add("Carpenter");
                    dataAdapter1.add("Computer Centre");
                    dataAdapter1.notifyDataSetChanged();
                } else if (item == "Hostel Level") {
                    complaint_type = 1;
                    dataAdapter1.clear();
                    dataAdapter1.add("Warden");
                    dataAdapter1.notifyDataSetChanged();
                    Bundle extras = getIntent().getExtras();
                    String hostel = extras.getString("hostel");
                    if (hostel.equals("girnar")) {
                        concerned_user = 16;
                    } else if (hostel.equals("kailash")){
                        concerned_user = 17;
                    }
                } else {
                    complaint_type = 2;
                    concerned_user= 19;
                    dataAdapter1.clear();
                    dataAdapter1.add("Dean");
                    dataAdapter1.notifyDataSetChanged();
                }

                // Showing selected spinner item
                // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }

        });

        //customised spinner list is used and the userid of concerned person is retrieved
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();
                if(item == "Electrician"){
                    concerned_user= 18;
                } else if(item == "Carpenter"){
                    concerned_user= 20;
                } else if(item == "Computer Centre"){
                    concerned_user= 21;
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
                attemptSubmit(concerned_user);
            }
        });
    }

    //checks if all fields are filled and calls the method parsing if yes
    private void attemptSubmit(int concerned_user) {


        // Reset errors.
        Title.setError(null);
        Description.setError(null);

        // Store values at the time of the thread creation
        String title = Title.getText().toString();
        String description = Description.getText().toString();
        try {
            String s1 = URLEncoder.encode(title, "UTF-8");
            String s = URLEncoder.encode(description, "UTF-8");

            boolean cancel = false;
            View focusView = null;


            // Check for empty TextFiealds to show error.
            if (TextUtils.isEmpty(title)) {
                Title.setError(getString(R.string.error_field_required));
                focusView = Title;
                cancel = true;
            } else if (TextUtils.isEmpty(description)) {
                Description.setError(getString(R.string.error_field_required));
                focusView = Description;
                focusView = Description;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt thread creation and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // perform the thread creation by parsing in case of no error.
                parsing(concerned_user,s1,s,complaint_type);

            }
        }
        catch(Exception e){
            ;
        }


    }

    //customised dialog alert
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

    //calls the new complaint api and shows a dialog alert on successful submission
    public void parsing(final int concerneduser,String title, final String desc, int type) {
        final String url = "http://192.168.43.218/assignment2/complaints/new.json?title=" + title +"&description=" + desc + "&concerned_user=" +concerneduser+"&complaint_type="+type;
        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest s1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Dialog("Complaint Posted", "Success");
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
