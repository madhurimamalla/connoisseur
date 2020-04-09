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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    private List<Movie> movieList = new ArrayList<Movie>();
    private MovieListAdapter movieListAdapter = null;

    /**
     * Required for Firebase configuration
     */
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private final static String USERS = "users";
    private final static String MOVIES = "movies";

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

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        /**
         * Inflating the view of this fragment
         */
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        final TextView textView = (TextView) rootView.findViewById(R.id.text_feature);
        mMoviesDetailsViewPager = rootView.findViewById(R.id.view_pager_movies_details);

        loadingIcon = (View) rootView.findViewById(R.id.loading_icon);

        movieListViewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);
        movieListViewModel.setIndex(bundleTypeStr);

        /**
         * Adjusting the number of items on the screen based on the DisplayMetrics and scaling factor
         */
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / SCALING_FACTOR);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), noOfColumns, GridLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.movies_recycleview);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        switch (bundleTypeStr) {
            case "DISCOVER":
                loadDiscover();
                Timber.d("The movies being set are of type : " + bundleTypeStr);
                break;
            case "HISTORY":
                loadHistory();
                Timber.d("The movies being set are of type : " + bundleTypeStr);
                break;
            case "WATCHLIST":
                loadWatchlist();
                Timber.d("The movies being set are of type : " + bundleTypeStr);
                break;
            default:
                loadDiscover();
                Timber.d("The switch case has come into default");
        }

        movieListAdapter = new MovieListAdapter(getContext(), movieList, this);
        recyclerView.setAdapter(movieListAdapter);

//        movieListViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
//            @Override
//            public void onChanged(@Nullable List<Movie> movies) {
//                movieListAdapter.setMovies(movies);
//            }
//        });

        return rootView;
    }

    /**
     * Load the watchlist in the Watchlist tab
     */
    private void loadWatchlist() {
        showLoadingIcon();
        movieList = new ArrayList<Movie>();
        final FirebaseDatabase moviesList = FirebaseDatabase.getInstance();
        DatabaseReference moviesListRef = moviesList.getReference().child(USERS).child(mUser.getUid()).child(MOVIES);
        /**
         * Get list of movie id from the Firebase
         */

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Movie movie = ds.getValue(Movie.class);
                    if (movie != null && movie.getmPref().equals(Movie.PREFERENCE.WISHLISTED)) {
                        movieList.add(movie);
                    }
                }
                if (movieList.size() > 0) {
                    movieListAdapter.setMovies(movieList);
                }
                movieListAdapter.notifyDataSetChanged();
                hideLoadingIcon();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.e(TAG, databaseError.getMessage());
            }
        };

        moviesListRef.addValueEventListener(valueEventListener);
    }

    private void loadDiscover() {
        /**
         * TODO Implement the discover function here to retrieve the discover list from TMDB
         */
        Movie movie = new Movie("75", "Mars Attacks!");
        movie.setmPoster("/gaTuHICwavPUmqQzPZFEXKSRwsC.jpg");
        movieList.add(movie);
        Movie movie1 = new Movie("120", "The Lord of the Rings: The Fellowship of the Ring");
        movie1.setmPoster("/6oom5QYQ2yQTMJIbnvbkBL9cHo6.jpg");
        movieList.add(movie1);
        Movie movie2 = new Movie("155", "The Dark Knight");
        movie2.setmPoster("/pKKvCaL1TPTVtbI6EeliyND3api.jpg");
        movieList.add(movie2);
        Movie movie3 = new Movie("272", "Batman Begins");
        movie3.setmPoster("/dr6x4GyyegBWtinPBzipY02J2lV.jpg");
        movieList.add(movie3);
        movieList.add(movie1);
        movieList.add(movie2);
        movieList.add(movie3);
        Movie movie4 = new Movie("278", "The Shawshank Redemption");
        movie4.setmPoster("/9O7gLzmreU0nGkIB6K3BsJbzvNv.jpg");
        movieList.add(movie4);
        movieList.add(movie);
        movieList.add(movie1);
        movieList.add(movie3);
        movieList.add(movie2);
        movieList.add(movie1);
        movieList.add(movie);
        movieList.add(movie3);
        movieList.add(movie4);
        movieList.add(movie);
        movieList.add(movie1);
        movieList.add(movie3);
    }

    /**
     * Load the history list in the History tab
     */
    private void loadHistory() {
        showLoadingIcon();
        movieList = new ArrayList<Movie>();
        final FirebaseDatabase moviesList = FirebaseDatabase.getInstance();
        DatabaseReference moviesListRef = moviesList.getReference().child(USERS).child(mUser.getUid()).child(MOVIES);
        /**
         * Get list of movie id from the Firebase
         */

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Movie movie = ds.getValue(Movie.class);
                    if (movie != null && movie.getmPref().equals(Movie.PREFERENCE.LIKED)) {
                        movieList.add(movie);
                    }
                }
                if (movieList.size() > 0) {
                    movieListAdapter.setMovies(movieList);
                }
                movieListAdapter.notifyDataSetChanged();
                hideLoadingIcon();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.e(TAG, databaseError.getMessage());
            }
        };
        moviesListRef.addValueEventListener(valueEventListener);
    }

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
        Toast.makeText(getContext(), "Clicked on the movie:" + movie.getmTitle(), Toast.LENGTH_SHORT).show();
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
                movieDetailsPagerAdapter.setList(movieList);
                break;
            default:
                Timber.d("The switch case has come into default");
                movieDetailsPagerAdapter.setList(movieList);
                break;
        }
        mMoviesDetailsViewPager.setAdapter(movieDetailsPagerAdapter);
        mMoviesDetailsViewPager.setCurrentItem(position);
    }
}
