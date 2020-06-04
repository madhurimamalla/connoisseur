package mmalla.android.com.connoisseur.ui.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmalla.android.com.connoisseur.R;
import mmalla.android.com.connoisseur.model.Movie;
import mmalla.android.com.connoisseur.ui.home.MovieDetailBaseActivity;
import timber.log.Timber;

public class SearchFragment extends Fragment implements MovieListAdapterSearch.MoviesListOnClickListener {

    private final static String TAG = SearchFragment.class.getSimpleName();

    private SearchViewModel searchViewModel;
    private String queryStr;
    private boolean wantAdultContent = false;
    private SharedPreferences sharedPreferences;

    private static final String ADULT_CONTENT_FLAG = "ADULT_CONTENT_FLAG";
    private static final String LIST_OF_MOVIES = "LIST_MOVIES";
    private static final String QUERY_STRING = "QUERY_STRING";

    @BindView(R.id.search_results)
    RecyclerView searchResultRV;

    @BindView(R.id.search_illustration)
    ImageView searchIllustration;

    private List<Movie> searchList = new ArrayList<>();
    private MovieListAdapterSearch movieListAdapterSearch = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        searchViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);

        wantAdultContent = doesUserWantAdultContent();

        if (savedInstanceState != null) {
            queryStr = savedInstanceState.getString(QUERY_STRING);
            searchList = savedInstanceState.getParcelableArrayList(LIST_OF_MOVIES);
            wantAdultContent = savedInstanceState.getBoolean(ADULT_CONTENT_FLAG);
        } else {
            Bundle receivedBundle = getArguments();
            if (receivedBundle != null && receivedBundle.getString(QUERY_STRING) != null) {
                queryStr = getArguments().getString(QUERY_STRING);
                searchViewModel.retrieveSearchResults(queryStr);
                wantAdultContent = getArguments().getBoolean(ADULT_CONTENT_FLAG);
            }
        }

        View root = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, root);

        movieListAdapterSearch = new MovieListAdapterSearch(getContext(), this);

        final TextView textView = root.findViewById(R.id.text_search);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        searchResultRV.setLayoutManager(mLayoutManager);
        searchResultRV.setItemAnimator(new DefaultItemAnimator());
        searchResultRV.setHasFixedSize(true);

        searchViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        searchViewModel.getSearchResults().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if (movies.size() == 0) {
                    textView.setVisibility(View.VISIBLE);
                    searchIllustration.setVisibility(View.VISIBLE);
                    searchResultRV.setVisibility(View.INVISIBLE);
                } else {
                    textView.setVisibility(View.GONE);
                    searchIllustration.setVisibility(View.GONE);
                    searchList.clear();
                    searchList.addAll(movies);
                    movieListAdapterSearch.setMovies(movies);
                    Timber.d(TAG, "Setting the movies in the MovieListAdapterSearch...");
                    searchResultRV.setAdapter(movieListAdapterSearch);
                    Timber.d(TAG, "Loading the recyclerView with the MovieListAdapterSearch...");
                }
            }
        });

        return root;
    }

    private boolean doesUserWantAdultContent() {
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.connoisseur_preferences_file), Context.MODE_PRIVATE);
        boolean wantAdultMovies = false;
        if (sharedPreferences.contains(getString(R.string.adult_content))) {
            wantAdultMovies = sharedPreferences.getBoolean(getString(R.string.adult_content), false);
        }
        return wantAdultMovies;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(QUERY_STRING, queryStr);
        outState.putBoolean(ADULT_CONTENT_FLAG, wantAdultContent);
        outState.putParcelableArrayList(LIST_OF_MOVIES, (ArrayList<? extends Parcelable>) new ArrayList<>(this.searchList));
    }

    @Override
    public void onClick(int position, Movie movie) {
        Timber.d("Movie clicked:" + movie.getmTitle());
        Intent movieDetailsIntent = new Intent(getActivity(), MovieDetailBaseActivity.class);
        movieDetailsIntent.putExtra("LIST_TYPE", "search");
        movieDetailsIntent.putExtra("CLICK_POSITION", position);
        movieDetailsIntent.putParcelableArrayListExtra("LIST_MOVIES",
                (ArrayList<? extends Parcelable>) new ArrayList<Movie>(this.searchList));
        startActivity(movieDetailsIntent);
    }


}