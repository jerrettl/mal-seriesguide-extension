package com.malseriesguideextension;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.malseriesguideextension.helpers.ThemeHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class SearchActivity extends AppCompatActivity {
    public static final String SEARCH_QUERY = "com.malseriesguideextension.SEARCH_QUERY";
    public static final String STATE_QUERY = "query";
    public static final String TAG = "MALSearchActivity";

    private String query;

    private LinearLayout progressOverlay;
    private LinearLayout errorOverlay;
    private RequestQueue queue;
    private AnimeAdapter animeAdapter;
    private RecyclerView recyclerView;
    private DividerItemDecoration mDividerItemDecoration;

    private ArrayList<AnimeSearchResult> results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyThemePreference(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            query = savedInstanceState.getString(STATE_QUERY);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Initialize Volley RequestQueue (to make GET request for search).
        queue = Volley.newRequestQueue(this);

        progressOverlay = findViewById(R.id.progress_overlay);
        errorOverlay = findViewById(R.id.error_overlay);

        startLoading();
        initializeRecyclerView();

        // Create results array and attach the data to an adapter, then the adapter to the RecyclerView.
        results = new ArrayList<>();
        animeAdapter = new AnimeAdapter(results, this);
        recyclerView.setAdapter(animeAdapter);

        if (query == null) {
            // If the activity was opened without already having restored the query, this must be a
            // fresh instance. In this case, grab the information to be searched from the intent.
            Intent intent = getIntent();
            query = intent.getStringExtra(SEARCH_QUERY);
        }
        if (query == null) {
            // If we open the activity, there's nothing restored, and there was no extra data from
            // the intent, there is nothing to do and we will close.
            finish();
        }

        makeApiRequest();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the query so we can restore it later.
        outState.putString(STATE_QUERY, query);
    }

    @Override
    protected void onStop() {
        super.onStop();
        queue.stop();
    }

    private void resetRecyclerView()
    {
        while (recyclerView.getItemDecorationCount() > 0) {
            recyclerView.removeItemDecorationAt(0);
        }
    }

    private void initializeRecyclerView()
    {
        // Set up RecyclerView (list of anime results).
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        resetRecyclerView();

        // Set up LayoutManager.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Add dividers between items in the LinearLayoutManager.
        mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);
    }

    private void makeApiRequest() {
        // Encode query for HTML.
        String sanitizedQuery;
        try {
            sanitizedQuery = URLEncoder.encode(query, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            sanitizedQuery = query;
        }
        String urlquery = "https://api.jikan.moe/v3/search/anime?q=" + sanitizedQuery + "&page=1?limit=10";

        // Set up the request to the internet
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlquery, null, new Response.Listener<JSONObject>() {
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
                    displayError(exception, "JSON string parsing");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // We weren't able to get the results. Tell the user as such.
                displayError(error, "Volley response");
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

    private void startLoading() {
        progressOverlay.setVisibility(View.VISIBLE);
        errorOverlay.setVisibility(View.GONE);
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

        Button b = findViewById(R.id.button_try_again);
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startLoading();
                makeApiRequest();
            }
        });
    }

    @SuppressLint({"SetTextI18n", "RtlHardcoded"})
    private void displayError(Exception exception, String context)
    {
        Log.e(TAG,
                "Error occurred in " + context + ".\n" + exception.getMessage() + "\n" + Arrays.toString(exception.getStackTrace()),
                exception);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean debuggingMode = sharedPreferences.getBoolean("debugging", false);

        if (debuggingMode) {
            TextView errorText = findViewById(R.id.error_text);
            errorText.setGravity(Gravity.LEFT);
            errorText.setText("App version: " + BuildConfig.VERSION_NAME +
                    "\nSDK version: " + Build.VERSION.SDK_INT + " (" + Build.VERSION.RELEASE + ")" +
                    "\nDevice: " + Build.MANUFACTURER + " " + Build.MODEL + " " + Build.PRODUCT +
                    "\n\nError occurred in " + context + ".\n" + exception.getMessage() + "\n" + Arrays.toString(exception.getStackTrace()));
        }

        displayError();
    }
}
