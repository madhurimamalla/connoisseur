package mmalla.android.com.connoisseur.ui.home;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmalla.android.com.connoisseur.R;
import mmalla.android.com.connoisseur.model.Movie;
import timber.log.Timber;

public class MovieListFragment extends Fragment implements MovieListAdapter.MoviesListOnClickListener {

    private final static String TAG = MovieListFragment.class.getSimpleName();

    @BindView(R.id.view_pager_movies_details)
    ViewPager mMoviesDetailsViewPager;

    @BindView(R.id.loading_icon)
    View loadingIcon;

    @BindView(R.id.no_movies_message)
    View noMoviesMessageView;

    private MovieListViewModel movieListViewModel;
    private final static String FEATURE = "FEATURE";
    private int SCALING_FACTOR = 120;
    private String bundleTypeStr;
    private List<Movie> moviesList = new ArrayList<Movie>();
    private MovieListAdapter movieListAdapter = null;

    public MovieListFragment() {
        // Empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Timber.d(TAG, "The movieListViewModel is set here...");

        movieListViewModel =
                ViewModelProviders.of(this).get(MovieListViewModel.class);

        bundleTypeStr = getArguments().getString(FEATURE);

        /**
         * Inflating the view of this fragment
         */
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        /**
         * Binding the views using ButterKnife
         */
        ButterKnife.bind(this, rootView);

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
                Timber.d(TAG, "Setting the movies in the MovieListAdapter...");
                moviesList = movies;
                recyclerView.setAdapter(movieListAdapter);
                Timber.d(TAG, "Loading the recyclerView with the MovieListAdapter...");
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
                Timber.d(TAG, "The tab on which it was clicked is : " + bundleTypeStr);
                movieDetailsPagerAdapter.setList(moviesList);
                break;
            default:
                Timber.d(TAG, "The switch case has come into default");
                movieDetailsPagerAdapter.setList(moviesList);
                break;
        }
        mMoviesDetailsViewPager.setAdapter(movieDetailsPagerAdapter);
        mMoviesDetailsViewPager.setCurrentItem(position);
    }
}
