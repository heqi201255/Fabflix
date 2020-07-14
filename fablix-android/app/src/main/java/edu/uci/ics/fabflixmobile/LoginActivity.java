package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends ActionBarActivity {

    private EditText username;
    private EditText password;
    private TextView message;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.loginview);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        message = findViewById(R.id.message);

        Button loginButton = findViewById(R.id.login);

        url = "https://52.53.195.116:8443/cs122b-spring20-team-29/api/";

        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    public void login() {

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is POST
        final StringRequest loginRequest = new StringRequest(Request.Method.POST, url + "login",
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try{
                        Log.d("loginView.success", response);
                        JSONObject jsobject = new JSONObject(response);
                        if(jsobject.getString("status").equals("success")) {
                            Intent goToIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(goToIntent);
                        }
                        else{
                            message.setText("Wrong username or password");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error

                        Log.d("loginView.error", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Post request form data
                final Map<String, String> params = new HashMap<>();
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());

                return params;
            }
        };

        // !important: queue.add is where the loginView request is actually sent
        queue.add(loginRequest);

    }
}