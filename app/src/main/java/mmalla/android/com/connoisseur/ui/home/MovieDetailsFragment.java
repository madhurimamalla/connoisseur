package mmalla.android.com.connoisseur.ui.home;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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

    private BottomSheetBehavior mBottomSheetBehavior;

    private Movie movie;

    private MovieDetailsViewModel movieDetailsViewModel;

    @BindView(R.id.bottom_sheet)
    View viewBottom;

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

        View rootView = inflater.inflate(R.layout.fragment_movie_details_old, container, false);

        /**
         * Binding view using ButterKnife
         */
        ButterKnife.bind(this, rootView);

        /**
         * Configurations needed for the BottomSheetBehavior
         */
        mBottomSheetBehavior = BottomSheetBehavior.from(viewBottom);
        mBottomSheetBehavior.setHideable(true);

        /**
         * Glide the image rendering!
         */
        movieDetailsViewModel.getPosterPath().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Glide.with(getActivity().getApplicationContext()).load(IMAGE_MOVIE_URL + s).into(imageView);
            }
        });

        movieDetailsViewModel.getMovieSummary().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mPlotSummary.setText(s);
            }
        });


        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                return true;
            }
        });

        /**
         * The options become invisible as we pull the plot summary drawer
         */
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        likedMovieView.setVisibility(View.INVISIBLE);
                        dislikedMovieView.setVisibility(View.INVISIBLE);
                        watchlistView.setVisibility(View.INVISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_HIDDEN:
                    default:
                        likedMovieView.setVisibility(View.VISIBLE);
                        dislikedMovieView.setVisibility(View.VISIBLE);
                        watchlistView.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                likedMovieView.setVisibility(View.INVISIBLE);
                dislikedMovieView.setVisibility(View.INVISIBLE);
                watchlistView.setVisibility(View.INVISIBLE);
            }
        });

        watchlistView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieDetailsViewModel.updateMovie(Movie.PREFERENCE.WISHLISTED);
                Toast.makeText(getContext(), R.string.The_movie_is_added_to_your_wishlist, Toast.LENGTH_LONG).show();
                watchlistView.setEnabled(false);
                likedMovieView.setAlpha((float) 1.0);
                dislikedMovieView.setAlpha((float) 1.0);
                likedMovieView.setVisibility(View.GONE);
                dislikedMovieView.setVisibility(View.GONE);
                Timber.d(TAG, getString(R.string.Disabling_the_liked_dislike_buttons));
            }
        });

        likedMovieView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieDetailsViewModel.updateMovie(Movie.PREFERENCE.LIKED);
                Toast.makeText(getContext(), R.string.The_movie_is_added_to_your_liked_list, Toast.LENGTH_LONG).show();
                watchlistView.setAlpha((float) 1.0);
                dislikedMovieView.setAlpha((float) 1.0);
                likedMovieView.setEnabled(true);
                dislikedMovieView.setVisibility(View.GONE);
                watchlistView.setVisibility(View.GONE);
                Timber.d(TAG, getString(R.string.Disabling_the_watchlist_dislike_buttons));
            }
        });

        dislikedMovieView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieDetailsViewModel.updateMovie(Movie.PREFERENCE.DISLIKED);
                Toast.makeText(getContext(), R.string.The_movie_is_added_to_your_dislike_list, Toast.LENGTH_LONG).show();
                dislikedMovieView.setEnabled(true);
                likedMovieView.setAlpha((float) 1.0);
                watchlistView.setAlpha((float) 1.0);
                likedMovieView.setVisibility(View.GONE);
                watchlistView.setVisibility(View.GONE);
                Timber.d(TAG, getString(R.string.Disabling_the_liked_watchlist_buttons));
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.PARCELED_MOVIE), movie);
    }
}
