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
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//displays information of a particular complaint with list of all comments and options for upvote/downvote/resolve if available
public class ListActivity extends AppCompatActivity {
    private ListView lvProduct;
    private ProductAdapter adapter;
    private List<Product> mProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvProduct = (ListView) findViewById(R.id.listview1_product);
        mProductList = new ArrayList<>();

        //welcome user through a dialog box
        Bundle extras2 = getIntent().getExtras();
        String id = extras2.getString("complaint_id");
        particular_complaint(id);
    }

    public void particular_complaint(final String complaint_id) {

        final String url3 = "http://192.168.43.218/assignment2/complaints/complaint.json/" + complaint_id;
        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest s1 = new StringRequest(Request.Method.GET, url3,
                new Response.Listener<String>() {
                    @Override

                    public void onResponse(String response) {
                        // JSON Parsing
                        try {
                            //reads all information from JSON response received
                            JSONObject resp = new JSONObject(response);
                            JSONArray complaints = resp.getJSONArray("complaints");
                            JSONObject list = complaints.getJSONObject(0);
                            JSONObject complaint = list.getJSONObject("complaints");
                            JSONArray valid = resp.getJSONArray("valid_user");
                            JSONObject list1 = valid.getJSONObject(0);
                            final int validuserid = list1.getInt("user_id");
                            JSONObject user = list.getJSONObject("users");
                            JSONArray concerned_user = resp.getJSONArray("concerned_user");
                            JSONObject concerned = concerned_user.getJSONObject(0);
                            String username = user.getString("name");
                            final int userid = user.getInt("id");
                            JSONObject concernedperson = concerned.getJSONObject("users");
                            String concernedname= concernedperson.getString("name");
                            final int concernedid = concernedperson.getInt("id");
                            String title = complaint.getString("title");
                            String posted_at = complaint.getString("posted_at");
                            int complaint_status = complaint.getInt("complaint_status");
                            String description = complaint.getString("description");
                            final int complaint_type = complaint.getInt("complaint_type");
                            JSONArray upvote_users = complaint.getJSONArray("upvote_users");
                            JSONArray downvote_users = complaint.getJSONArray("downvote_users");
                            int upvotes = (upvote_users).length();
                            int downvotes = (downvote_users).length();
                            String id = complaint.getString("id");
                            String type,status;
                            int upvoted=0,downvoted=0;
                            /*if (Arrays.asList(upvote_users).contains(validuserid)){upvoted=1;}
                            else {upvoted = 0;}
                            if (Arrays.asList(downvote_users).contains(validuserid)){downvoted=1;}
                            else {downvoted=0;}*/
                            //int upvoted,downvoted;
                            for (int i = 0; i < upvote_users.length(); i++) {
                                int value = upvote_users.getInt(i);
                                if (value == validuserid) {upvoted=1;break;}
                            }
                            for (int i = 0; i < downvote_users.length(); i++) {
                                int value = downvote_users.getInt(i);
                                if (value == validuserid) {downvoted=1;break;}
                            }
                            if (complaint_status==1){status ="Status: Resolved";}
                            else {status = "Status: Unresolved";}

                            if(complaint_type == 1){type = "Type: Hostel Level";}
                            else if(complaint_type == 2){type = "Type: Institute Level";}
                            else {type = "Type: Individual Level";}

                            mProductList.add(new Product(1, "Title: " + title , username +" posted to " + concernedname +"\nPosted at: " +posted_at +"\n\n" + type + "\n"+ status , "\nUpvotes: "+ Integer.toString(upvotes)+"  Downvotes: "+ Integer.toString(downvotes)+ "\nDescription:\n" + description, id));
                            JSONArray comments = resp.getJSONArray("comments");
                            JSONArray users = resp.getJSONArray("comment_users");

                            //adds all comments to the list
                            for (int j = 0; j < comments.length(); j++) {
                                JSONObject comment = comments.getJSONObject(j);
                                JSONArray commentuser = users.getJSONArray(j);
                                JSONObject comment_user = commentuser.getJSONObject(0);
                                String description_comment = comment.getString("description");
                                String posted_at1 = comment.getString("posted_at");
                                String name = comment_user.getString("name");
                                String user_name = comment_user.getString("username");

                                mProductList.add(new Product(1, "", name + " (" + user_name + "):" +"\nPosted at: " + posted_at1 , description_comment, ""));


                                //}


                            }
                            //adds upvote/downvote option if the user has not voted before
                            if (upvoted==1 || downvoted==1 || complaint_type==0){;}
                            else {
                                mProductList.add(new Product(10000, "UPVOTE", "", "", String.valueOf(id)));
                                mProductList.add(new Product(10001, "DOWNVOTE", "", "", String.valueOf(id)));
                            }
                            //adds resolve option if unresolved complaint and user has the power to resolve it
                            if ((complaint_type==0 && validuserid==userid) || (validuserid == concernedid))
                                    {
                                        if (complaint_status==0) {
                                            mProductList.add(new Product(10002, "RESOLVE", "", "", String.valueOf(id)));
                                        }
                                    }
                            else {}
                            //adds add comment option to all complaints
                            mProductList.add(new Product(10003, "ADD COMMENT", "", "", String.valueOf(id)));


                            adapter = new ProductAdapter(getApplicationContext(), mProductList);
                            lvProduct.setAdapter(adapter);


                            //on item click
                            lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                     //action to be taken
                                                                     // eg
                                                                     if (mProductList.get(position).getId() == 10003) {
                                                                         //pops up a dialog that asks the user to enter the comment
                                                                         popup(mProductList.get(position).getCredits());

                                                                     }
                                                                     if (mProductList.get(position).getId() == 10002) {
                                                                         resolve(complaint_id);//calls resolve method

                                                                     }

                                                                     if (mProductList.get(position).getId() == 10000) {
                                                                         upvote(complaint_id);//calls upvote method
                                                                     }

                                                                     if (mProductList.get(position).getId() == 10001) {
                                                                         downvote(complaint_id);//calls downvote downvote
                                                                     }

                                                                     else {
                                                                         //Toast.makeText(getApplicationContext(), "Click back to return to the menu", Toast.LENGTH_SHORT).show();
                                                                     }
                                                                 }
                                                             }
                            );


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                //giving volley error

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


//calls upvote api
    public void upvote(String complaint_id) {

        RequestQueue rq = Volley.newRequestQueue(this);
        String url = "http://192.168.43.218/assignment2/complaints/upvote.json/" + complaint_id;
        StringRequest s1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Successfully upvoted", Toast.LENGTH_SHORT).show();
                        // Dialog("Comment posted", "Success");
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Dialog(error.toString(),"error");
                    }
                }
        ) {


        };
        rq.add(s1);

    }


    //calls downvote api
    public void downvote(String complaint_id) {

        RequestQueue rq = Volley.newRequestQueue(this);
        String url = "http://192.168.43.218/assignment2/complaints/downvote.json/" + complaint_id;
        StringRequest s1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Successfully downvoted", Toast.LENGTH_SHORT).show();
                        // Dialog("Comment posted", "Success");
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Dialog(error.toString(),"error");
                    }
                }
        ) {


        };
        rq.add(s1);

    }

    //calls add comment api with description as a request.var
    public void post_comment(String url) {

        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest s1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getApplicationContext(), "Successfully Resolved", Toast.LENGTH_SHORT).show();
                        // Dialog("Comment posted", "Success");
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Dialog(error.toString(),"error");
                    }
                }
        ) {


        };
        rq.add(s1);
    }

    //calls resolve api
    public void resolve(String complaint_id) {

        RequestQueue rq = Volley.newRequestQueue(this);
        String url = "http://192.168.43.218/assignment2/complaints/resolve.json/" + complaint_id;
        StringRequest s1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Successfully Resolved", Toast.LENGTH_SHORT).show();
                        // Dialog("Comment posted", "Success");
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Dialog(error.toString(),"error");
                    }
                }
        ) {


        };
        rq.add(s1);
    }

    //asks user to enter the new comment and then calls the method post_comment
    public void popup(final String complaint_id) {
        // Creating alert Dialog with one Button
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ListActivity.this);

        //AlertDialog alertDialog = new AlertDialog.Builder(Main23Activity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Add Comment");

        // Setting Dialog Message
        final EditText input = new EditText(ListActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Enter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        String desc = input.getText().toString();
                        try {
                            String s = URLEncoder.encode(desc, "UTF-8");
                            //Dialog ("entered comment",desc);
                            String url = "http://192.168.43.218/assignment2/complaints/post_comment.json?complaint_id=" + complaint_id + "&description=" + s;
                            post_comment(url);;

                        }
                        catch(Exception e){;}

                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();

                    }
                });

        // closed

        // Showing Alert Message
        alertDialog.show();
    }

    //used whereever a dialog alert is needed
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
