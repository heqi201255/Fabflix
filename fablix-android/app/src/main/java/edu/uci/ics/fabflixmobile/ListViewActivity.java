package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class ListViewActivity extends Activity {
    public static final String MID = "mid";
    private String url;
    private int offset = 0;
    private int total_movie = 20;
    private final int pagenum = 20;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movielistview);
        query = getIntent().getStringExtra(MainActivity.SEARCH);
        url = "https://52.53.195.116:8443/cs122b-spring20-team-29/api/";
        search();
        ((Button) findViewById(R.id.prevButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goPrev();
            }
        });

        ((Button) findViewById(R.id.nextButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNext();
            }
        });
        ((Button) findViewById(R.id.movieListBackButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListViewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void search(){
        String words = query;
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final JsonArrayRequest searchRequest = new JsonArrayRequest(
                url + "new-search?search=" + words + "&sort=r_dsc_t_asc&pagenum=20&offset=" + offset,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("search response: ", response.toString());
                        update_movie_list(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("search error:", error.toString());
                        error.printStackTrace();
                    }
                });

        queue.add(searchRequest);
    }

    private void update_movie_list(JSONArray responseArray){
        ArrayList<Movie> movies = new ArrayList<Movie>();
        ArrayList<String> midList = new ArrayList<>();
        try{
            if (responseArray.length()!=0){
                total_movie = Integer.parseInt(responseArray.getJSONObject(0).getString("movie_count"));
                for (int i=0;i<responseArray.length();i++){
                    JSONObject movie = responseArray.getJSONObject(i);
                    String title = movie.getString("movie_name");
                    String year = movie.getString("movie_year");
                    String director = movie.getString("movie_director");
                    String id = movie.getString("movie_id");
                    String genres = movie.getString("movie_genre");
                    String stars = movie.getString("movie_stars");
                    Movie m = new Movie(title,year,director,genres,stars);
                    movies.add(m);
                    midList.add(id);
                }
                MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);
                ListView listView = findViewById(R.id.movieList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View v, int position,
                                            long arg3) {
                        Intent intent = new Intent(ListViewActivity.this, SingleMovieActivity.class);
                        intent.putExtra(MID, midList.get(position));
                        startActivity(intent);
                    }
                });
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private void goNext(){
        int new_offset;
        new_offset = offset + pagenum;
        if (new_offset < total_movie){
            offset = new_offset;
            search();
        }
    }

    private void goPrev(){
        int new_offset;
        new_offset = offset - pagenum;
        if(new_offset >= 0){
            offset = new_offset;
            search();
        }
    }

}