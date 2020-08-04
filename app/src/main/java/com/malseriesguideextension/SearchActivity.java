package com.malseriesguideextension;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "MALSeriesGuideExtension";
    public static final String SEARCH_QUERY = "com.malseriesguideextension.SEARCH_QUERY";

    private LinearLayout progressOverlay;
    private LinearLayout errorOverlay;
    private RequestQueue queue;
    private AnimeAdapter animeAdapter;

    private ArrayList<AnimeSearchResult> results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize Volley RequestQueue (to make GET request for search).
        queue = Volley.newRequestQueue(this);
    }


    @Override
    protected void onStart() {
        super.onStart();

        progressOverlay = findViewById(R.id.progress_overlay);
        errorOverlay = findViewById(R.id.error_overlay);

        // Set up RecyclerView (list of anime results).
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create results array and attach the data to an adapter, then the adapter to the RecyclerView.
        results = new ArrayList<>();
        animeAdapter = new AnimeAdapter(results, this);
        recyclerView.setAdapter(animeAdapter);


        // Grab the information to be searched from the intent.
        Intent intent = getIntent();
        String text = intent.getStringExtra(SEARCH_QUERY);
        if (text == null) {
            // If this activity was opened without this extra data, we're actually done here.
            // There is no search query to give MAL.
            finish();
        }

        makeApiRequest(text);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        queue.stop();
    }


    private void makeApiRequest(String query) {
        query = "https://api.jikan.moe/v3/search/anime?q=" + query + "&page=1?limit=10";

        // Set up the request to the internet
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, query, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // We got a list of results, let's do something with them.
                try {
                    // Parse the JSON and display it.
                    parseJson(results, response);
                    displayResults();
                }
                catch (Exception exception) {
                    // If something goes wrong during parsing, assume an error.
                    displayError();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // We weren't able to get the results. Tell the user as such.
                displayError();
            }
        });

        // Actually make the request.
        this.queue.add(jsonObjectRequest);
    }


    /**
     * Parse JSON results into AnimeSearchResult objects, then add them to a provided ArrayList.
     *
     * @param list The ArrayList to add items to.
     * @param json The JSON data to be parsed.
     */
    private void parseJson(ArrayList<AnimeSearchResult> list, JSONObject json) throws Exception {
        JSONArray inList = json.getJSONArray("results");

        for (int i = 0; i < inList.length(); i++) {
            JSONObject currentItem = inList.getJSONObject(i);
            AnimeSearchResult newItem = new AnimeSearchResult(
                    currentItem.getString("title"),
                    currentItem.getString("url"));
            list.add(newItem);
        }
    }

    /**
     * Remove anything on the screen that indicates we were loading something.
     */
    private void finishLoading() {
        progressOverlay.setVisibility(View.GONE);
    }

    /**
     * Update the RecyclerView's adapter with the newly-added search results.
     */
    private void displayResults() {
        finishLoading();
        animeAdapter.notifyDataSetChanged();
    }

    /**
     * Something wrong happened, so display a message saying so.
     */
    private void displayError() {
        finishLoading();
        errorOverlay.setVisibility(View.VISIBLE);
    }
}