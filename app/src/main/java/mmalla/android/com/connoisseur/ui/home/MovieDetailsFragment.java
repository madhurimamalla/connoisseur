package mmalla.android.com.connoisseur.ui.home;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmalla.android.com.connoisseur.R;
import mmalla.android.com.connoisseur.model.Movie;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailsFragment extends Fragment {

    private final static String TAG = MovieDetailsFragment.class.getSimpleName();
    private final static String DISCOVERED_MOVIE = "CREATED_MOVIE";
    private final static String LIST_TYPE = "LIST_TYPE";
    private final static String IMAGE_MOVIE_URL = "https://image.tmdb.org/t/p/w780/";
    private Movie movie;
    private String typeOfList = "";

    private MovieDetailsViewModel movieDetailsViewModel;

    @BindView(R.id.movie_poster_discover_screen)
    ImageView moviePoster;

    @BindView(R.id.plot_summary)
    TextView mPlotSummary;

    @BindView(R.id.add_to_watchlist)
    FloatingActionButton watchlistView;

    @BindView(R.id.like_movie_button)
    FloatingActionButton likedMovieView;

    @BindView(R.id.dislike_movie_button)
    FloatingActionButton dislikedMovieView;

    @BindView(R.id.movie_details_title)
    TextView movieDetailTitle;

    @BindView(R.id.movie_rating_value)
    TextView movieRatingValue;

    /*
    @BindView(R.id.movie_tagline)
    TextView movieTagline;
*/

    @BindView(R.id.movie_year_of_release_value)
    TextView movieReleaseYear;

    @BindView(R.id.minutes_of_the_movie)
    TextView runtimeMovie;

    @BindView(R.id.movie_vote_count)
    TextView movieVoteCount;

    @BindView(R.id.like_dislike)
    FloatingActionButton likeDislike;

    @BindView(R.id.viewToHelpButton)
    View fadeOutView;

    @BindView(R.id.genresList)
    TextView genresList;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DiscoverFragment.
     */
    public static MovieDetailsFragment newInstance(Movie movie, String listType) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(DISCOVERED_MOVIE, movie);
        args.putString(LIST_TYPE, listType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.movie = savedInstanceState.getParcelable(getString(R.string.PARCELED_MOVIE));
            this.typeOfList = savedInstanceState.getString(LIST_TYPE);
        } else if (getArguments() != null) {
            this.movie = getArguments().getParcelable(DISCOVERED_MOVIE);
            this.typeOfList = getArguments().getString(LIST_TYPE);
        }

        Timber.d(TAG, movie.toString());
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Timber.d("The MovieDetailsViewModel is set here... ");

        movieDetailsViewModel = ViewModelProviders.of(this).get(MovieDetailsViewModel.class);
        /**
         * Init method is invoked so the basic configurations are initialized
         */
        movieDetailsViewModel.init();
        /**
         * Send data to the viewModel
         */
        movieDetailsViewModel.setMovieLiveData(movie);

        View rootView = inflater.inflate(R.layout.fragment_details_movie_new, container, false);

        /**
         * Binding view using ButterKnife
         */
        ButterKnife.bind(this, rootView);

        /**
         * Glide the image rendering!
         */
        movieDetailsViewModel.getPosterPath().observe(this, s ->
                Glide.with(getActivity().getApplicationContext())
                        .load(IMAGE_MOVIE_URL + s).error(R.drawable.ic_404_new).into(moviePoster));

        movieDetailsViewModel.getMovieTitle().observe(this, s -> {
                    movieDetailTitle.setBreakStrategy(Layout.BREAK_STRATEGY_BALANCED);
                    movieDetailTitle.setText(s);
                }
        );

        movieDetailsViewModel.getMovieSummary().observe(this, s ->
                mPlotSummary.setText(s));

        movieDetailsViewModel.getRating().observe(this, s -> movieRatingValue.setText(s));

        movieDetailsViewModel.getRuntime().observe(this, s -> {
            if (s == null) {
                runtimeMovie.setVisibility(View.GONE);
            } else {
                if (Integer.parseInt(s) == 0) {
                    runtimeMovie.setVisibility(View.GONE);
                } else {
                    runtimeMovie.setText(s + " mins");
                }
            }
        });

/*
        movieDetailsViewModel.getTagline().observe(this, s -> movieTagline.setText(s));

*/
        movieDetailsViewModel.getReleaseYear().observe(this, s -> movieReleaseYear.setText(s));

        movieDetailsViewModel.getVoteCount().observe(this, s -> {
            if (s != null) {
                if (Integer.parseInt(s) == 0) {
                    movieVoteCount.setVisibility(View.GONE);
                } else {
                    movieVoteCount.setText(" (" + s + ")");
                }
            } else {
                movieVoteCount.setVisibility(View.GONE);
            }
        });

        movieDetailsViewModel.getGenresList().observe(this, s -> {
            if (s != null) {
                genresList.setText(s);
                genresList.setVisibility(View.VISIBLE);
            } else {
                genresList.setVisibility(View.INVISIBLE);
            }
        });

        Timber.d(TAG + "The type of list is: " + typeOfList);
        if (typeOfList.equals("WATCHLIST")) {
            Glide.with(getActivity().getApplicationContext())
                    .load(R.drawable.ic_playlist_add_check_white_36dp).into(watchlistView);
            watchlistView.setTag("added");
        }

        watchlistView.setOnClickListener(v -> {
            if (watchlistView.getTag().equals("added")) {
                watchlistView.setTag("removed");
                movieDetailsViewModel.updateMovie(Movie.PREFERENCE.IGNORED, "removed");
                Glide.with(getActivity().getApplicationContext()).load(R.drawable.ic_playlist_add_white_36dp).into(watchlistView);
            } else {
                Glide.with(getActivity().getApplicationContext())
                        .load(R.drawable.ic_playlist_add_check_white_36dp).into(watchlistView);
                watchlistView.setTag("added");
                movieDetailsViewModel.updateMovie(Movie.PREFERENCE.WISHLISTED, "added");
            }
            closeSubMenusFab();
        });

        /**
         * This is a general toast message that shows up
         * once the movie preference entered by the user is
         * updated in the backend
         */
        movieDetailsViewModel.showToast.observe(this, str -> {
            switch (str) {
                case "":
                    break;
                case "liked":
                    Toast.makeText(getContext(), "Added to your liked movies", Toast.LENGTH_SHORT).show();
                    break;
                case "disliked":
                    Toast.makeText(getContext(), "Added to your disliked movies", Toast.LENGTH_SHORT).show();
                    break;
                case "added":
                    Toast.makeText(getContext(), "Added to your watchlist", Toast.LENGTH_SHORT).show();
                    break;
                case "removed":
                    Toast.makeText(getContext(), "Removed from your watchlist", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        likeDislike.setOnClickListener(view -> {
            Timber.d("Clicked on the fabSettings....");
            openSubMenusFab();
        });

        moviePoster.setOnClickListener(v -> {
            if (likeDislike.getVisibility() == View.INVISIBLE) {
                closeSubMenusFab();
            }
        });

        likedMovieView.setOnClickListener(v -> {
            movieDetailsViewModel.updateMovie(Movie.PREFERENCE.LIKED, "liked");
            watchlistView.setVisibility(View.INVISIBLE);
            dislikedMovieView.setVisibility(View.INVISIBLE);
            closeSubMenusFab();
        });

        dislikedMovieView.setOnClickListener(v -> {
            movieDetailsViewModel.updateMovie(Movie.PREFERENCE.DISLIKED, "disliked");
            likedMovieView.setVisibility(View.VISIBLE);
            watchlistView.setVisibility(View.INVISIBLE);
            closeSubMenusFab();
        });
        return rootView;
    }

    //closes FAB submenus
    @SuppressLint("RestrictedApi")
    private void closeSubMenusFab() {
        likedMovieView.animate().translationY(0);
        dislikedMovieView.animate().translationY(0);
        watchlistView.animate().translationY(0);
        likedMovieView.setVisibility(View.INVISIBLE);
        dislikedMovieView.setVisibility(View.INVISIBLE);
        watchlistView.setVisibility(View.INVISIBLE);
        likeDislike.setVisibility(View.VISIBLE);
        moviePoster.setAlpha((float) (1.0));
    }

    //Opens FAB submenus
    @SuppressLint("RestrictedApi")
    private void openSubMenusFab() {
        moviePoster.setAlpha((float) 0.5);
        likedMovieView.animate().translationY(-getResources().getDimension(R.dimen.standard_150));
        dislikedMovieView.animate().translationY(-getResources().getDimension(R.dimen.standard_75));
        watchlistView.setVisibility(View.VISIBLE);
        likedMovieView.setVisibility(View.VISIBLE);
        dislikedMovieView.setVisibility(View.VISIBLE);
        likeDislike.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.PARCELED_MOVIE), movie);
        outState.putString(LIST_TYPE, typeOfList);
    }
}
