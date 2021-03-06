package mmalla.android.com.connoisseur.ui.home;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmalla.android.com.connoisseur.recommendations.engine.MovieRepository;
import mmalla.android.com.connoisseur.R;
import mmalla.android.com.connoisseur.model.Movie;
import timber.log.Timber;

public class MovieListFragment extends Fragment implements MovieListAdapter.MoviesListOnClickListener {

    private final static String TAG = MovieListFragment.class.getSimpleName();
    private MovieRepository movieRepository;
    private static final String CLICK_POSITION = "CLICK_POSITION";
    private static final String LIST_MOVIES = "LIST_MOVIES";
    private static final String LIST_TYPE = "LIST_TYPE";
    private static final String MOVIE_LIST_FRAGMENT = "MOVIE_LIST_FRAGMENT";

    @BindView(R.id.loading_icon)
    View loadingIcon;

    @BindView(R.id.no_movies_message)
    View noMoviesMessageView;

    @BindView(R.id.no_movie_image)
    ImageView noMoviesImageView;

    @Nullable
    @BindView(R.id.swipe_refresh_list)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.movies_recyclerview)
    RecyclerView recyclerView;

    @Nullable
    @BindView(R.id.popular_switch)
    SwitchCompat mSwitchCompat;

    private MovieListViewModel movieListViewModel;
    private final static String FEATURE = "FEATURE";
    private String bundleTypeStr;
    private List<Movie> moviesList = new ArrayList<>();
    private MovieListAdapter movieListAdapter = null;

    public MovieListFragment(MovieRepository mRepo) {
        this.movieRepository = mRepo;
        // Empty constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Timber.d(TAG, "The movieListViewModel is set here...");

        movieListViewModel =
                ViewModelProviders.of(this).get(MovieListViewModel.class);

        if (savedInstanceState != null) {
            moviesList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_FRAGMENT);
            bundleTypeStr = savedInstanceState.getString(LIST_TYPE);
        } else {
            bundleTypeStr = Objects.requireNonNull(getArguments()).getString(FEATURE);
        }

        /*
          Inflating the view of this fragment
         */
        View rootView;
        switch (Objects.requireNonNull(bundleTypeStr)) {
            case "DISCOVER":
                rootView = inflater.inflate(R.layout.fragment_discover_movie_list, container, false);
                break;
            default:
                rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        }

        ButterKnife.bind(this, rootView);

        movieListViewModel.init(bundleTypeStr, movieRepository);
        if (bundleTypeStr.equals("DISCOVER") && mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(true);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);

            mSwipeRefreshLayout.setOnRefreshListener(() -> initiateRefresh());
            if (mSwitchCompat != null) {
                mSwitchCompat.setVisibility(View.VISIBLE);
            }

            mSwitchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    buttonView.setEnabled(true);
                    Timber.d("Opting to get Popular movies....");
                    populatePopularMovies();
                } else {
                    Timber.d("Getting movies from recommendation algorithm");
                    initiateRefresh();
                }
            });

        }

        movieListAdapter = new MovieListAdapter(getContext(), this);
        /**
         * Setting the type of list in the adapter
         */
        movieListAdapter.setTypeOfList(bundleTypeStr);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int SCALING_FACTOR = 120;
        int noOfColumns = (int) (dpWidth / SCALING_FACTOR);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), noOfColumns, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        showLoadingIcon();

        movieListViewModel.getMovies().observe(this, movies -> {
            movieListAdapter.setMovies(movies);
            if (movies.size() == 0) {
                showNoMoviesTextView();
                hideLoadingIcon();
            } else {
                hideNoMoviesTextView();
                Timber.d(TAG, "Setting the movies in the MovieListAdapter...");
                moviesList.clear();
                moviesList.addAll(movies);
                recyclerView.setAdapter(movieListAdapter);
                Timber.d(TAG, "Loading the recyclerView with the MovieListAdapter...");
                hideLoadingIcon();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Objects.requireNonNull(bundleTypeStr).equals("DISCOVER")) {
                    if (dy <= 0) {
                        ViewGroup.LayoutParams p = mSwitchCompat.getLayoutParams();
                        mSwitchCompat.animate().start();
                        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        mSwitchCompat.setLayoutParams(p);
                        mSwitchCompat.setText(getResources().getString(R.string.want_to_see_popular_movies));
                    } else if (dy > 60) {
                        ViewGroup.LayoutParams p = mSwitchCompat.getLayoutParams();
                        mSwitchCompat.animate().start();
                        p.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        mSwitchCompat.setLayoutParams(p);
                        mSwitchCompat.setText("Popular");
                    }
                }
            }
        });

        return rootView;
    }

    private void populatePopularMovies() {
        movieListViewModel.getPopularMovies();
    }

    private void initiateRefresh() {
        Timber.d(TAG, "Initiating refresh....");
        if (mSwitchCompat.isChecked()) {
            movieListViewModel.getPopularMovies();
        } else {
            movieListViewModel.initiateRefresh();
        }
        Objects.requireNonNull(mSwipeRefreshLayout).setRefreshing(false);
    }

    public void showNoMoviesTextView() {
        noMoviesImageView.setVisibility(View.VISIBLE);
        noMoviesMessageView.setVisibility(View.VISIBLE);
    }

    public void hideNoMoviesTextView() {
        noMoviesImageView.setVisibility(View.INVISIBLE);
        noMoviesMessageView.setVisibility(View.INVISIBLE);
    }

    /**
     * Show the loading icon
     */
    private void showLoadingIcon() {
        loadingIcon.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the loading icon
     */
    private void hideLoadingIcon() {
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
        Timber.d("The movie clicked was at position: " + position + " in the movie list of type: " + bundleTypeStr);

        if (mSwitchCompat != null) {
            mSwitchCompat.setVisibility(View.INVISIBLE);
        }

        Intent movieDetailsIntent = new Intent(getActivity(), MovieDetailBaseActivity.class);
        movieDetailsIntent.putExtra(LIST_TYPE, bundleTypeStr);
        movieDetailsIntent.putExtra(CLICK_POSITION, position);
        movieDetailsIntent.putParcelableArrayListExtra(LIST_MOVIES, (ArrayList<? extends Parcelable>) new ArrayList<Movie>(this.moviesList));
        startActivity(movieDetailsIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.swipe_refresh_list:
                if (bundleTypeStr.equals("DISCOVER") && mSwipeRefreshLayout != null) {
                    initiateRefresh();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mSwitchCompat != null) {
            mSwitchCompat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST_FRAGMENT, (ArrayList<? extends Parcelable>) moviesList);
        outState.putString(LIST_TYPE, bundleTypeStr);
    }
}
