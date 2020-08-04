package com.malseriesguideextension;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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
    private RecyclerView recyclerView;

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
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        results = new ArrayList<>();

        // Grab the information to be searched from the intent.
        Intent intent = getIntent();
        String text = intent.getStringExtra(SEARCH_QUERY);
        if (text == null) {
            // If this activity was opened without this extra data, we're actually done here.
            // There is no search query to give MAL.
            finish();
        }

        makeApiRequest(text, this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


    @Override
    protected void onStop() {
        super.onStop();
        queue.stop();
    }


    private void makeApiRequest(String query, final Context context) {
        query = "https://api.jikan.moe/v3/search/anime?q=" + query + "&page=1?limit=10";

        // Set up the request to the internet
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, query, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                results = parseJson(response);
                displayResults(context);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayError();
            }
        });

        // Actually make the request.
        this.queue.add(jsonObjectRequest);
    }


    /**
     * Convert the JSON received from MAL into a usable ArrayList of AnimeSearchResult objects.
     *
     * @param json The JSON data to be parsed.
     * @return A new ArrayList of AnimeSearchResult objects based on the contents of the JSON data.
     */
    private ArrayList<AnimeSearchResult> parseJson(JSONObject json) {
        JSONArray inList;
        ArrayList<AnimeSearchResult> outList = new ArrayList<>();

        try {
            inList = json.getJSONArray("results");

            for (int i = 0; i < inList.length(); i++) {
                JSONObject currentItem = inList.getJSONObject(i);
                AnimeSearchResult newItem = new AnimeSearchResult(
                        currentItem.getString("title"),
                        currentItem.getString("url"));
                outList.add(newItem);
            }
        }
        catch (Exception exception) {
            outList = null;
        }

        return outList;
    }

    private void finishLoading() {
        progressOverlay.setVisibility(View.GONE);
    }

    private void displayResults(Context context) {
        // Set the contents of the RecyclerView to the search results.
        finishLoading();
        if (results != null) {
            recyclerView.setAdapter(new AnimeAdapter(results, context));
        }
        else {
            displayError();
        }
    }

    private void displayError() {
        finishLoading();
        errorOverlay.setVisibility(View.VISIBLE);
    }
}