package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SingleMovieActivity extends Activity{
    private TextView title;
    private TextView year;
    private TextView director;
    private TextView stars;
    private TextView genres;
    private Button backButton;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemovieview);
        url = "https://52.53.195.116:8443/cs122b-spring20-team-29/api/";
        String mid = getIntent().getStringExtra(ListViewActivity.MID);
        title = findViewById(R.id.mtitle);
        year = findViewById(R.id.myear);
        director = findViewById(R.id.mdirector);
        stars = findViewById(R.id.mstars);
        genres = findViewById(R.id.mgenres);
        fillInfo(mid);

        backButton = findViewById(R.id.singleMovieBackButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fillInfo(String mid){
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final JsonArrayRequest searchRequest = new JsonArrayRequest(
                url + "single-movie?id="+mid,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("single-movie response: ", response.toString());
                        try{
                            JSONObject movie = response.getJSONObject(0);
                            title.setText(movie.getString("movie_title"));
                            year.setText(movie.getString("movie_year"));
                            director.setText(movie.getString("movie_director"));
                            genres.setText(movie.getString("movie_genre"));
                            stars.setText(movie.getString("movie_stars"));
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("single-movie error:", error.toString());
                        error.printStackTrace();
                    }
                });

        queue.add(searchRequest);
    }


}
