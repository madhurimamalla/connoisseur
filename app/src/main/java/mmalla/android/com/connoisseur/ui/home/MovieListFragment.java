package mmalla.android.com.connoisseur.ui.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mmalla.android.com.connoisseur.R;
import mmalla.android.com.connoisseur.model.Movie;
import timber.log.Timber;

public class MovieListFragment extends Fragment implements MovieListAdapter.MoviesListOnClickListener {

    private final static String TAG = MovieListFragment.class.getSimpleName();

    private MovieListViewModel movieListViewModel;
    private final static String FEATURE = "FEATURE";
    private int SCALING_FACTOR = 120;

    private String bundleTypeStr;

    private ViewPager mMoviesDetailsViewPager;
    private View loadingIcon;
    private View noMoviesMessageView;
    private List<Movie> moviesList = new ArrayList<Movie>();

    private MovieListAdapter movieListAdapter = null;

    public MovieListFragment() {
        // Empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Timber.d(TAG + "The movieListViewModel is set here...");
        movieListViewModel =
                ViewModelProviders.of(this).get(MovieListViewModel.class);

        bundleTypeStr = getArguments().getString(FEATURE);

        /**
         * Inflating the view of this fragment
         */
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        final TextView textView = (TextView) rootView.findViewById(R.id.text_feature);
        mMoviesDetailsViewPager = rootView.findViewById(R.id.view_pager_movies_details);

        noMoviesMessageView = (View) rootView.findViewById(R.id.no_movies_message);
        loadingIcon = (View) rootView.findViewById(R.id.loading_icon);

        movieListViewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);
        movieListViewModel.setIndex(bundleTypeStr);

        movieListAdapter = new MovieListAdapter(getContext(), this);

        /**
         * Adjusting the number of items on the screen based on the DisplayMetrics and scaling factor
         */
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / SCALING_FACTOR);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), noOfColumns, GridLayoutManager.VERTICAL, false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.movies_recycleview);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        showLoadingIcon();

        movieListViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                movieListAdapter.setMovies(movies);
                Timber.d("Setting the movies in the MovieListAdapter...");
                moviesList = movies;
                recyclerView.setAdapter(movieListAdapter);
                Timber.d("Loading the recyclerView with the MovieListAdapter...");
                hideLoadingIcon();
            }
        });

        return rootView;
    }

/*
    public void showNoMoviesTextView() {
        noMoviesMessageView.setVisibility(View.VISIBLE);
    }

    public void hideNoMoviesTextView() {
        noMoviesMessageView.setVisibility(View.INVISIBLE);
    }
*/

    /**
     * Show the loading icon
     */
    public void showLoadingIcon() {
        loadingIcon.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the loading icon
     */
    public void hideLoadingIcon() {
        loadingIcon.setVisibility(View.INVISIBLE);
    }

    /**
     * On clicking on a poster, the MovieDetailsFragment is loaded showing more options
     *
     * @param movie
     * @param position
     */
    @Override
    public void onClick(Movie movie, int position) {
        Timber.d("The movie clicked here is: " + movie.getmId() + ": and title is: " + movie.getmTitle());
        Timber.d("The movie clicked was at position: " + position + " in the movielist of type: " + bundleTypeStr);

        /**
         * Get the discovered movie list and set the adapter list so it can display in the viewpager
         * Keep the starting position of the viewPager based on which movie was clicked
         */
        MovieDetailsPagerAdapter movieDetailsPagerAdapter = new MovieDetailsPagerAdapter(getChildFragmentManager());
        switch (bundleTypeStr) {
            case "HISTORY":
            case "DISCOVER":
            case "WATCHLIST":
                Timber.d("The tab on which it was clicked is : " + bundleTypeStr);
                movieDetailsPagerAdapter.setList(moviesList);
                break;
            default:
                Timber.d("The switch case has come into default");
                movieDetailsPagerAdapter.setList(moviesList);
                break;
        }
        mMoviesDetailsViewPager.setAdapter(movieDetailsPagerAdapter);
        mMoviesDetailsViewPager.setCurrentItem(position);
    }
}
