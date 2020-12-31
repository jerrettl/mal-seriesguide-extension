package com.malseriesguideextension;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/**
 * Adapts the search results from MAL into views that can be processed into the RecyclerView in
 * SearchActivity.
 */
public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.ViewHolder> {
    // ==== Fields ====
    private ArrayList<AnimeSearchResult> results;
    private Context context;

    // ==== Constructors ====
    public AnimeAdapter(ArrayList<AnimeSearchResult> results, Context context) {
        this.results = results;
        this.context = context;
    }

    // ==== Methods ====

    /**
     * This is called by the RecyclerView. This creates a new ViewHolder to represent our result
     * items. The new ViewHolder is used to display items of the adapter using onBindViewHolder().
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an
     *               adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given type.
     */
    @NonNull
    @Override
    public AnimeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view from the given XML layout file

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.anime_result, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Called by RecyclerView to bind the right data to the right ViewHolder. Based on the position
     * provided, we can get the data from that index of the results and place it in the right spot.
     *
     * @param holder The ViewHolder that should be updated to represent the contents of the item at
     *               the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull AnimeAdapter.ViewHolder holder, int position) {
        final AnimeSearchResult result = results.get(position);
        holder.title.setText(result.getName());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new onClick listener that will bring the user to the URL as provided
                // from the search result.
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(result.getUrl()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    /**
     * This ViewHolder is what is created for the purpose of holding a search result. We need to
     * also make sure we also have some parts of the View exposed so we can manipulate its
     * contents in onBindViewHolder().
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public LinearLayout item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            item = itemView.findViewById(R.id.item);
        }
    }
}
