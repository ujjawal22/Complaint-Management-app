package com.example.parichay.assignment2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceActivity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
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
import org.w3c.dom.Text;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//second activity that is launched on successful login
public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ListView lvProduct;
    private ProductAdapter adapter;
    private List<Product> mProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lvProduct = (ListView) findViewById(R.id.listview_product);
        mProductList = new ArrayList<>();


        //welcome user through a dialog box
        Bundle extras2 = getIntent().getExtras();
        String usertype = extras2.getString("usertype");
        String name = extras2.getString("username");


        Dialog("You have successfully logged in", "Welcome " + name);


        //navigation drawer activity used where the drawer shows various options that can be selected
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu=navigationView.getMenu();
        View header = navigationView.getHeaderView(0);


        TextView tes=(TextView)header.findViewById(R.id.SetText);
        tes.setText(name+"\n"+usertype);
        menu.findItem(R.id.adduser).setVisible(false);

        //add user option is only availabe for dean and wardens
        if(usertype.equals("warden") || usertype.equals("dean")){
            menu.findItem(R.id.adduser).setVisible(true);
        }

        navigationView.setNavigationItemSelectedListener(this);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    //list of complaints is displayed using custom adapter
    //all objects are clickable and show details of particular complaint by launching ListActivity when clicked.
    public void showcomplaints(String url2){

        lvProduct = (ListView) findViewById(R.id.listview_product);
        mProductList = new ArrayList<>();
        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest s1 = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
                    @Override

                    public void onResponse(String response) {
                        // JSON Parsing
                        try {
                            JSONObject resp = new JSONObject(response);
                            JSONArray complaints = resp.getJSONArray("complaints");

                            if(complaints.length()==0){
                                mProductList.add(new Product(0, "NO COMPLAINTS","","",""));
                            }
                            Bundle extras2 = getIntent().getExtras();
                            final String hostel = extras2.getString("hostel");
                            final String validid = extras2.getString("validid");

                            //list of all complaints is stored to the adarter
                            for (int i = 0; i < complaints.length(); i++) {
                                JSONObject list = complaints.getJSONObject(i);
                                JSONObject complaint = list.getJSONObject("complaints");
                                JSONObject user = list.getJSONObject("users");
                                String name = user.getString("name");
                                String hostel1 = user.getString("hostel");
                                String title = complaint.getString("title");
                                int complaint_status = complaint.getInt("complaint_status");
                                String description = complaint.getString("description");
                                int complaint_type = complaint.getInt("complaint_type");
                                String id = complaint.getString("id");
                                String posted_at = complaint.getString("posted_at");
                                String userid = complaint.getString("user_id");
                                String concernedid = complaint.getString("concerned_user");
                                String type,status;

                                if (complaint_status==1){status ="Status: Resolved";}
                                else {status = "Status: Unresolved";}

                                if(complaint_type == 1){type = "Type: Hostel Level";}
                                else if(complaint_type == 2){type = "Type: Institute Level";}
                                else {type = "Type: Individual Level";}


                                //filters the list of complaints according to the logged in user
                                if(complaint_type==2) {
                                    mProductList.add(new Product(i + 1, "Title: " + title, type + "\n" + status, "Posted by: " + name + "\nPosted at: "+ posted_at, id));
                                }
                                else if(complaint_type==1 && (hostel1.equals(hostel))){
                                    mProductList.add(new Product(i + 1, "Title: " + title, type + "\n" + status, "Posted by: " + name + "\nPosted at: "+ posted_at, id));
                                }
                                else if(complaint_type==0 && (validid.equals(concernedid) || validid.equals(userid)) ) {
                                    mProductList.add(new Product(i + 1, "Title: " + title, type + "\n" + status, "Posted by: " + name + "\nPosted at: "+ posted_at, id));
                                }
                            }
                            adapter = new ProductAdapter(getApplicationContext(), mProductList);
                            lvProduct.setAdapter(adapter);

                            //on item click
                            lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                                    //calls the listactivity to show details of a particular complaint
                                                                     Intent nextScreen = new Intent(getApplicationContext(), ListActivity.class);
                                                                     nextScreen.putExtra("complaint_id",mProductList.get(position).getCredits());
                                                                     startActivity(nextScreen);

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
                        Dialog(error.toString(),"error");
                    }
                }

        ){

            /*public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> getHeader = new HashMap<String, String>();
                getHeader.put("Cookie",LoginActivity.use.getString("Set-Cookie",""));
                return getHeader;
            }*/
        };


        rq.add(s1);

    }

    //list of all notifications from the api are displayed using custom adapter
    public void shownotifications(){

        lvProduct = (ListView) findViewById(R.id.listview_product);
        mProductList = new ArrayList<>();
        final String url2 = "http://192.168.43.218/assignment2/complaints/notifications.json";
        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest s1 = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
                    @Override

                    public void onResponse(String response) {
                        // JSON Parsing
                        try {
                            JSONObject resp = new JSONObject(response);
                            JSONArray notifs = resp.getJSONArray("notifications");

                            if(notifs.length()==0){
                                mProductList.add(new Product(0, "NO NOTIFICATIONS","","",""));
                            }
                            Bundle extras2 = getIntent().getExtras();
                            final String hostel = extras2.getString("hostel");
                            final String validid = extras2.getString("validid");

                            for (int i = 0; i < notifs.length()-2 ; i=i+3) {
                                JSONObject list = notifs.getJSONObject(i);
                                JSONObject complaint = notifs.getJSONObject(i+1);
                                String description = list.getString("description");
                                JSONObject user = notifs.getJSONObject(i + 2);
                                String name = user.getString("name");
                                String hostel1 = user.getString("hostel");
                                String title = complaint.getString("title");
                                int complaint_status = complaint.getInt("complaint_status");
                                int complaint_type = complaint.getInt("complaint_type");
                                String id = complaint.getString("id");
                                String posted_at = complaint.getString("posted_at");
                                String userid = complaint.getString("user_id");
                                String concernedid = complaint.getString("concerned_user");
                                String type,status;
                                if (complaint_status==1){status ="Status: Resolved";}
                                else {status = "Status: Unresolved";}

                                if(complaint_type == 1){type = "Type: Hostel Level";}
                                else if(complaint_type == 2){type = "Type: Institute Level";}
                                else {type = "Type: Individual Level";}
                                if(complaint_type==2) {
                                    mProductList.add(new Product(i + 1, "",description , "", id));
                                }
                                else if(complaint_type==1 && (hostel1.equals(hostel))){
                                    mProductList.add(new Product(i + 1, "", description , "", id));
                                }
                                else if(complaint_type==0 && (validid.equals(concernedid) || validid.equals(userid)) ) {
                                    mProductList.add(new Product(i + 1, "", description , "", id));
                                }
                                //Dialog("coursecode: " + code + "\ndesc: " + description + "\nname: " + name, "Courses registered in " + current_yr + " : ");
                            }
                            adapter = new ProductAdapter(getApplicationContext(), mProductList);
                            lvProduct.setAdapter(adapter);

                            //on item click
                            lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                                                     Intent nextScreen = new Intent(getApplicationContext(), ListActivity.class);
                                                                     nextScreen.putExtra("complaint_id", mProductList.get(position).getCredits());
                                                                     startActivity(nextScreen);

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
                        Dialog(error.toString(),"error");
                    }
                }

        ){

            /*public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> getHeader = new HashMap<String, String>();
                getHeader.put("Cookie",LoginActivity.use.getString("Set-Cookie",""));
                return getHeader;
            }*/
        };

        rq.add(s1);

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


    //calls the different methods depending on the chosen option from the drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.mainlist) {

            String url = "http://192.168.43.218/assignment2/complaints/mainlist.json";
            showcomplaints(url);


        }  else if (id == R.id.resolved) {

            String url = "http://192.168.43.218/assignment2/complaints/resolvedlist.json";
            showcomplaints(url);

        } else if (id == R.id.unresolved) {

            String url = "http://192.168.43.218/assignment2/complaints/unresolvedlist.json";
            showcomplaints(url);

        } else if (id == R.id.notification) {

            shownotifications();

        } else if (id == R.id.newcomplaint) {
            Bundle extras2 = getIntent().getExtras();
            String hostel = extras2.getString("hostel");
            String validid = extras2.getString("validid");

            Intent nextScreen = new Intent(getApplicationContext(), NewComplaintActivity.class);
            nextScreen.putExtra("hostel",hostel);
            nextScreen.putExtra("validid",validid);
            startActivity(nextScreen);

        }else if (id == R.id.adduser){
            Bundle extras2 = getIntent().getExtras();
            String usertype = extras2.getString("usertype");
            if(usertype.equals("warden")||usertype.equals("dean")) {
                Intent nextScreen = new Intent(getApplicationContext(), AddUSerActivity.class);
                nextScreen.putExtra("usertype", usertype);
                startActivity(nextScreen);
            }
            else{
                Dialog("You are not allowed to add","Invalid User");
            }
        }
        else if (id == R.id.logout) {

            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //calls the logout api and deletes all cache and cookies
    public void logout(){
        final String url3 = "http://192.168.43.218/default/logout.json";
        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest s1 = new StringRequest(Request.Method.GET, url3,
                new Response.Listener<String>() {
                    @Override

                    public void onResponse(String response) {

                        LoginActivity.editor = LoginActivity.use.edit();
                        LoginActivity.editor.putString("Set-Cookie","");
                        LoginActivity.editor.apply();
                    }

                },
                //giving volley error

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Dialog(error.toString(),"error");
                    }
                }

        ){

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> getHeader = new HashMap<String, String>();
                getHeader.put("Cookie",LoginActivity.use.getString("Set-Cookie",""));
                return getHeader;
            }
        };
        Intent nextScreen = new Intent(getApplicationContext(), LoginActivity.class);
        //nextScreen.putExtra("type",0);
        startActivity(nextScreen);


        rq.add(s1);


    }

}
