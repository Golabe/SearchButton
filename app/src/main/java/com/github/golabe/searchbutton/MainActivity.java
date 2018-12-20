package com.github.golabe.searchbutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.golabe.searchbutton.library.SearchButton;

public class MainActivity extends AppCompatActivity {

    private SearchButton searchButton;
    private Button startSearch, endSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchButton = findViewById(R.id.search_button);
        startSearch = findViewById(R.id.btn_start);
        endSearch = findViewById(R.id.btn_end);

        startSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchButton.isSearching()){
                    searchButton.start();
                }

            }
        });
        endSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchButton.isSearching()){
                    searchButton.searchOver();
                }

            }
        });
    }
}
