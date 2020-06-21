package mmalla.android.com.connoisseur.ui.home;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    private final static String WATCHLIST_LIST = "WATCHLIST";
    private final static String WATCHLIST_TAG_ADDED = "added";
    private final static String WATCHLIST_TAG_REMOVED = "removed";
    private final static String MOVIE_LIKED = "liked";
    private final static String MOVIE_DISLIKED = "disliked";
    private final static String DISCOVERED_MOVIE = "CREATED_MOVIE";
    private final static String LIST_TYPE = "LIST_TYPE";
    private final static String IMAGE_MOVIE_URL = "https://image.tmdb.org/t/p/w780";
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

    @BindView(R.id.close_menu_fab)
    FloatingActionButton closeMenuFabView;

    @BindView(R.id.movie_details_title)
    TextView movieDetailTitle;

    @BindView(R.id.movie_rating_value)
    TextView movieRatingValue;

    @BindView(R.id.genre_layout)
    LinearLayout genreLayout;

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

    @BindView(R.id.options_menu)
    FloatingActionButton optionsMenu;

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
                        .load(IMAGE_MOVIE_URL + s).error(R.drawable.ic_404_movie_poster_not_found).into(moviePoster));

        movieDetailsViewModel.getMovieTitle().observe(this, s -> {
                    movieDetailTitle.setBreakStrategy(Layout.BREAK_STRATEGY_BALANCED);
                    movieDetailTitle.setText(s);
                }
        );

        movieDetailsViewModel.getMovieSummary().observe(this, s ->
                mPlotSummary.setText(s));

        movieDetailsViewModel.getRating().observe(this, s -> movieRatingValue.setText(s));

        movieDetailsViewModel.getRuntime().observe(this, s -> {
            if (s.equals("null")) {
                runtimeMovie.setVisibility(View.INVISIBLE);
            } else {
                if (Integer.parseInt(s) == 0) {
                    runtimeMovie.setVisibility(View.INVISIBLE);
                } else {
                    runtimeMovie.setText(s + " mins");
                }
            }
        });

        movieDetailsViewModel.getMoviePreference().observe(this, preference -> {
            if (preference.equals(Movie.PREFERENCE.LIKED)) {
                likedMovieView.setAlpha((float) 0.6);
                likedMovieView.setElevation((float) 32.0);
                dislikedMovieView.setAlpha((float) 1.0);
            } else if (preference.equals(Movie.PREFERENCE.DISLIKED)) {
                dislikedMovieView.setAlpha((float) 0.6);
                dislikedMovieView.setElevation((float) 32.0);
                likedMovieView.setAlpha((float) 1.0);
            }
        });

/*
        movieDetailsViewModel.getTagline().observe(this, s -> movieTagline.setText(s));

*/
        movieDetailsViewModel.getReleaseYear().observe(this, s -> movieReleaseYear.setText(s));

        movieDetailsViewModel.getVoteCount().observe(this, s -> {
            if (s.equals("null")) {
                movieVoteCount.setVisibility(View.GONE);
            } else {
                if (Integer.parseInt(s) == 0) {
                    movieVoteCount.setVisibility(View.GONE);
                } else {
                    movieVoteCount.setText(" (" + s + ")");
                }
            }
        });

        movieDetailsViewModel.getGenresList().observe(this, s -> {
            if (!s.equals("null")) {
                genreLayout.setVisibility(View.VISIBLE);
                genresList.setText(s);
                genresList.setVisibility(View.VISIBLE);
            } else {
                genreLayout.setVisibility(View.GONE);
                genresList.setVisibility(View.GONE);
            }
        });

        Timber.d(TAG + "The type of list is: " + typeOfList);
        if (typeOfList.equals(WATCHLIST_LIST)) {
            Glide.with(getActivity().getApplicationContext())
                    .load(R.drawable.ic_playlist_add_check_white_36dp).into(watchlistView);
            watchlistView.setTag(WATCHLIST_TAG_ADDED);
        }

        watchlistView.setOnClickListener(v -> {
            if (watchlistView.getTag().equals(WATCHLIST_TAG_ADDED)) {
                watchlistView.setTag(WATCHLIST_TAG_REMOVED);
                movieDetailsViewModel.updateMovie(Movie.PREFERENCE.IGNORED, WATCHLIST_TAG_REMOVED);
                Glide.with(getActivity().getApplicationContext())
                        .load(R.drawable.ic_playlist_add_white_36dp).into(watchlistView);
            } else {
                Glide.with(getActivity().getApplicationContext())
                        .load(R.drawable.ic_playlist_add_check_white_36dp).into(watchlistView);
                watchlistView.setTag(WATCHLIST_TAG_ADDED);
                movieDetailsViewModel.updateMovie(Movie.PREFERENCE.WISHLISTED, WATCHLIST_TAG_ADDED);
            }
            likedMovieView.setAlpha((float) 1.0);
            dislikedMovieView.setAlpha((float) 1.0);
            likedMovieView.setElevation((float) 0);
            dislikedMovieView.setElevation((float) 0);
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
                case MOVIE_LIKED:
                    Toast toast = Toast.makeText(getContext(), Html.fromHtml("<font color='#ffffff' >" + "Added to your liked movies" + "</font>"), Toast.LENGTH_SHORT);
                    View toastView = toast.getView();
                    toastView.setAlpha((float) 0.9);
                    toastView.setBackgroundResource(R.drawable.toast_drawable);
                    toast.show();
                    break;
                case MOVIE_DISLIKED:
                    Toast toast2 = Toast.makeText(getContext(), Html.fromHtml("<font color='#ffffff' >" + "Added to your disliked movies" + "</font>"), Toast.LENGTH_SHORT);
                    View toastView2 = toast2.getView();
                    toastView2.setAlpha((float) 0.9);
                    toastView2.setBackgroundResource(R.drawable.toast_drawable);
                    toast2.show();
                    break;
                case WATCHLIST_TAG_ADDED:
                    Toast toast3 = Toast.makeText(getContext(), Html.fromHtml("<font color='#ffffff' >" + "Added to your watchlist" + "</font>"), Toast.LENGTH_SHORT);
                    View toastView3 = toast3.getView();
                    toastView3.setAlpha((float) 0.9);
                    toastView3.setBackgroundResource(R.drawable.toast_drawable);
                    toast3.show();
                    break;
                case WATCHLIST_TAG_REMOVED:
                    Toast toast4 = Toast.makeText(getContext(), Html.fromHtml("<font color='#ffffff' >" + "Removed from your watchlist" + "</font>"), Toast.LENGTH_SHORT);
                    View toastView4 = toast4.getView();
                    toastView4.setAlpha((float) 0.9);
                    toastView4.setBackgroundResource(R.drawable.toast_drawable);
                    toast4.show();
                    break;
            }
        });

        optionsMenu.setOnClickListener(view -> {
            Timber.d("Clicked on the fabSettings....");
            openSubMenusFab();
        });

        moviePoster.setOnClickListener(v -> {
            if (optionsMenu.getVisibility() == View.INVISIBLE) {
                closeSubMenusFab();
            }
        });

        likedMovieView.setOnClickListener(v -> {
            movieDetailsViewModel.updateMovie(Movie.PREFERENCE.LIKED, MOVIE_LIKED);
            watchlistView.setVisibility(View.INVISIBLE);
            dislikedMovieView.setVisibility(View.INVISIBLE);
            closeSubMenusFab();
        });

        dislikedMovieView.setOnClickListener(v -> {
            movieDetailsViewModel.updateMovie(Movie.PREFERENCE.DISLIKED, MOVIE_DISLIKED);
            likedMovieView.setVisibility(View.VISIBLE);
            watchlistView.setVisibility(View.INVISIBLE);
            closeSubMenusFab();
        });

        closeMenuFabView.setOnClickListener(v -> closeSubMenusFab());

        /**
         * Return the updated rootView
         */
        return rootView;
    }

    //closes FAB submenus
    @SuppressLint("RestrictedApi")
    private void closeSubMenusFab() {
        likedMovieView.animate().translationY(0);
        dislikedMovieView.animate().translationY(0);
        watchlistView.animate().translationY(0);
        closeMenuFabView.animate().translationY(0);
        likedMovieView.setVisibility(View.INVISIBLE);
        dislikedMovieView.setVisibility(View.INVISIBLE);
        watchlistView.setVisibility(View.INVISIBLE);
        closeMenuFabView.setVisibility(View.INVISIBLE);
        optionsMenu.setVisibility(View.VISIBLE);
        moviePoster.setAlpha((float) (1.0));
    }

    //Opens FAB submenus
    @SuppressLint("RestrictedApi")
    private void openSubMenusFab() {
        moviePoster.setAlpha((float) 0.5);
        likedMovieView.animate().translationY(-getResources().getDimension(R.dimen.standard_225));
        dislikedMovieView.animate().translationY(-getResources().getDimension(R.dimen.standard_150));
        watchlistView.animate().translationY(-getResources().getDimension(R.dimen.standard_75));
        watchlistView.setVisibility(View.VISIBLE);
        likedMovieView.setVisibility(View.VISIBLE);
        dislikedMovieView.setVisibility(View.VISIBLE);
        closeMenuFabView.setVisibility(View.VISIBLE);
        optionsMenu.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.PARCELED_MOVIE), movie);
        outState.putString(LIST_TYPE, typeOfList);
    }
}
