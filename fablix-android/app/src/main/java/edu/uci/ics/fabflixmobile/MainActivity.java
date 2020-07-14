package edu.uci.ics.fabflixmobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.view.KeyEvent.KEYCODE_ENTER;

public class MainActivity extends Activity {
    public static final String SEARCH = "search";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        EditText searchbox = findViewById(R.id.searchBox);
        Button searchbutton = findViewById(R.id.searchButton);

        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String words = searchbox.getText().toString();
                Intent intent = new Intent(MainActivity.this, ListViewActivity.class);
                intent.putExtra(SEARCH, words);
                startActivity(intent);
            }
        });
        searchbox.setOnKeyListener(
            new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode==KEYCODE_ENTER){
                        String words = searchbox.getText().toString();
                        Intent intent = new Intent(MainActivity.this, ListViewActivity.class);
                        intent.putExtra(SEARCH, words);
                        startActivity(intent);
                    }
                    return false;
                }
            }
        );
    }
}
