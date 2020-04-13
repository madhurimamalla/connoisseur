package mmalla.android.com.connoisseur.features.watchlist;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import mmalla.android.com.connoisseur.R;
import mmalla.android.com.connoisseur.model.Movie;
import mmalla.android.com.connoisseur.recommendations.engine.DatabaseUtils;
import timber.log.Timber;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class WatchlistFragment extends Fragment {

    private final static String TAG = WatchlistFragment.class.getSimpleName();

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String MOVIES_LIST = "MOVIES_LIST";

    private List<Movie> movies;

    private int mColumnCount = 1;

    public void setMoviesList(List<Movie> movies) {
        this.movies = movies;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WatchlistFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static WatchlistFragment newInstance(int columnCount) {
        WatchlistFragment fragment = new WatchlistFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.movies = savedInstanceState.getParcelableArrayList("MOVIES_LIST");
        } else if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        this.movies = databaseUtils.getWishlistedMovies(mAuth.getCurrentUser().getUid());
        Timber.d(TAG, " Got the movies! ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wishlist_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyWatchlistedItemRecyclerViewAdapter(movies, getActivity().getApplicationContext()));
        }
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIES_LIST, new ArrayList<Movie>(movies));
    }
}
