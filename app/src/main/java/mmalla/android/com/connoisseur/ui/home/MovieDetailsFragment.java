package mmalla.android.com.connoisseur.ui.home;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

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
    private final static String IMAGE_MOVIE_URL = "https://image.tmdb.org/t/p/w780/";

    private Movie movie;

    private MovieDetailsViewModel movieDetailsViewModel;

    @BindView(R.id.movie_poster_discover_screen)
    ImageView imageView;

    @BindView(R.id.plot_summary)
    TextView mPlotSummary;

    @BindView(R.id.like_movie_button)
    ImageView likedMovieView;

    @BindView(R.id.add_to_watchlist)
    ImageView watchlistView;

    @BindView(R.id.dislike_movie_button)
    ImageView dislikedMovieView;

    @BindView(R.id.movie_details_title)
    TextView movieDetailTitle;

    @BindView(R.id.movie_rating_value)
    TextView movieRatingValue;

    @BindView(R.id.movie_tagline)
    TextView movieTagline;

    @BindView(R.id.movie_year_of_release_value)
    TextView movieReleaseYear;

    @BindView(R.id.movie_vote_count)
    TextView movie_vote_count;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DiscoverFragment.
     */
    public static MovieDetailsFragment newInstance(Movie movie) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(DISCOVERED_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.movie = savedInstanceState.getParcelable(getString(R.string.PARCELED_MOVIE));
        } else if (getArguments() != null) {
            this.movie = getArguments().getParcelable(DISCOVERED_MOVIE);
        }

        Timber.d(TAG, movie.toString());
    }

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
                        .load(IMAGE_MOVIE_URL + s).error(R.drawable.ic_404).into(imageView));

        movieDetailsViewModel.getMovieTitle().observe(this, s -> movieDetailTitle.setText(s));

        movieDetailsViewModel.getMovieSummary().observe(this, s ->
                mPlotSummary.setText("Plot Summary: " + "\n" + s));

        movieDetailsViewModel.getRating().observe(this, s -> movieRatingValue.setText(s));

        movieDetailsViewModel.getTagline().observe(this, s -> movieTagline.setText(s));

        movieDetailsViewModel.getReleaseYear().observe(this, s -> movieReleaseYear.setText(s));

        movieDetailsViewModel.getVoteCount().observe(this, s -> movie_vote_count.setText("(" + s + ")"));

        watchlistView.setOnClickListener(v -> {
            movieDetailsViewModel.updateMovie(Movie.PREFERENCE.WISHLISTED);
            watchlistView.setEnabled(false);
            likedMovieView.setAlpha((float) 1.0);
            dislikedMovieView.setAlpha((float) 1.0);
            likedMovieView.setVisibility(View.GONE);
            dislikedMovieView.setVisibility(View.GONE);
            Timber.d(TAG, getString(R.string.Disabling_the_liked_dislike_buttons));
        });

        /**
         * This is a general toast message that shows up
         * once the movie preference entered by the user is
         * updated in the backend
         */
        movieDetailsViewModel.showToast.observe(this, aBoolean -> {
            if (aBoolean) {
                Toast.makeText(getContext(), "Your preference is updated", Toast.LENGTH_SHORT).show();
            }
        });

        likedMovieView.setOnClickListener(v -> {
            movieDetailsViewModel.updateMovie(Movie.PREFERENCE.LIKED);
            watchlistView.setAlpha((float) 1.0);
            dislikedMovieView.setAlpha((float) 1.0);
            likedMovieView.setEnabled(true);
            dislikedMovieView.setVisibility(View.GONE);
            watchlistView.setVisibility(View.GONE);
            Timber.d(TAG, getString(R.string.Disabling_the_watchlist_dislike_buttons));
        });

        dislikedMovieView.setOnClickListener(v -> {
            movieDetailsViewModel.updateMovie(Movie.PREFERENCE.DISLIKED);
            dislikedMovieView.setEnabled(true);
            likedMovieView.setAlpha((float) 1.0);
            watchlistView.setAlpha((float) 1.0);
            likedMovieView.setVisibility(View.GONE);
            watchlistView.setVisibility(View.GONE);
            Timber.d(TAG, getString(R.string.Disabling_the_liked_watchlist_buttons));
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.PARCELED_MOVIE), movie);
    }


}
