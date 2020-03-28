package mmalla.android.com.connoisseur.ui.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mmalla.android.com.connoisseur.R;
import mmalla.android.com.connoisseur.model.Movie;
import timber.log.Timber;

import static java.util.Collections.shuffle;

public class MovieListFragment extends Fragment implements MovieListAdapter.MoviesListOnClickListener {

    private final static String TAG = MovieListFragment.class.getSimpleName();

    private MovieListViewModel movieListViewModel;
    private final static String FEATURE = "FEATURE";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Timber.d("The movieListViewModel is set here...");
        movieListViewModel =
                ViewModelProviders.of(this).get(MovieListViewModel.class);

        String bundleTypeStr = getArguments().getString(FEATURE);

        /**
         * Inflating the view of this fragment
         */
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        final TextView textView = (TextView) rootView.findViewById(R.id.text_feature);

        movieListViewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);
        movieListViewModel.setIndex(bundleTypeStr);

        /**
         * TODO Remove this
         */
        /*movieListViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 120;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), noOfColumns, GridLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.movies_recycleview);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        /**
         * Create dummy data to test the UI screens
         */
        List<Movie> movieList = new ArrayList<Movie>();
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

        /**
         * Shuffling the same list to mock actual behavior of the application
         */
        List<Movie> watchlistMovies = new ArrayList<>(movieList);
        Collections.copy(watchlistMovies, movieList);
        Collections.shuffle(movieList);
        List<Movie> discoveredMovies = new ArrayList<>(movieList);
        Collections.copy(discoveredMovies, movieList);
        Collections.shuffle(movieList);
        List<Movie> historyMovies = new ArrayList<>(movieList);
        Collections.copy(historyMovies, movieList);
        final MovieListAdapter movieListAdapter = new MovieListAdapter(getContext(), movieList, this);
        recyclerView.setAdapter(movieListAdapter);

        movieListViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                movieListAdapter.setMovies(movies);
            }
        });

        return rootView;
    }

    @Override
    public void onClick(Movie movie) {
        /**
         * TODO Enter the flow here which goes to open the MovieDetailsContent
         */
        Toast.makeText(getContext(), "Clicked on the movie:" + movie.getmTitle(), Toast.LENGTH_SHORT).show();
        //Intent movieDetailsIntent = new Intent(this, MovieDetailsActivity.class);
        //movieDetailsIntent.putExtra(Intent.EXTRA_TEXT, movie.getMovieId());
        //startActivity(movieDetailsIntent);
    }
}
