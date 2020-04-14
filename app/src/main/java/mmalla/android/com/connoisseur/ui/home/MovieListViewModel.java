package mmalla.android.com.connoisseur.ui.home;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mmalla.android.com.connoisseur.model.Movie;
import mmalla.android.com.connoisseur.moviedbclient.MovieDBClient;
import mmalla.android.com.connoisseur.moviedbclient.MovieDBClientException;
import mmalla.android.com.connoisseur.recommendations.engine.FirebaseDatabaseRepository;
import mmalla.android.com.connoisseur.recommendations.engine.MovieRepository;
import timber.log.Timber;

public class MovieListViewModel extends ViewModel {
    private final static String TAG = MovieListViewModel.class.getSimpleName();

    private MutableLiveData<List<Movie>> mMoviesList;
    private MutableLiveData<String> mTypeOfList = new MutableLiveData<>();
    private MovieRepository movieRepository = new MovieRepository();

    /**
     * This method is used to set the type of list : WATCHLIST, HISTORY, DISCOVER here
     *
     * @param index
     */
    public void setIndex(String index) {
        mTypeOfList.setValue(index);
    }

    /**
     * Returns movies to MovieListFragment for respective tabs
     *
     * @return
     */
    public LiveData<List<Movie>> getMovies() {

        if (mMoviesList == null) {
            mMoviesList = new MutableLiveData<List<Movie>>();
            switch (mTypeOfList.getValue()) {
                case "HISTORY":
                    loadMovies(Movie.PREFERENCE.LIKED);
                    break;
                case "WATCHLIST":
                    loadMovies(Movie.PREFERENCE.WISHLISTED);
                    break;
                case "DISCOVER":
                    loadMovies(Movie.PREFERENCE.IGNORED);
                    break;
            }
        }
        return mMoviesList;
    }

    private void loadMovies(final Movie.PREFERENCE mPref) {
        movieRepository.addListener(new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Movie>() {
            @Override
            public void onSuccess(List<Movie> result) {
                if (mPref == Movie.PREFERENCE.WISHLISTED | mPref == Movie.PREFERENCE.LIKED) {
                    List<Movie> shortListedMovies = new ArrayList<>();
                    for (Movie movie :
                            result) {
                        if (movie.getmPref() == mPref) {
                            shortListedMovies.add(movie);
                        }
                    }
                    Timber.d("Shortlisted movies: " + mTypeOfList.getValue() + " are here!");
                    mMoviesList.setValue(shortListedMovies);
                    /**
                     * This is when we are trying to retrieve movies for the discovered tab
                     */
                } else if (mPref == Movie.PREFERENCE.IGNORED) {
                    List<Movie> likedMovies = new ArrayList<>();
                    List<Movie> discoveredMovies = new ArrayList<>();
                    List<Movie> dislikedMovies = new ArrayList<>();
                    for (Movie movie :
                            result) {
                        if (movie.getmPref() == Movie.PREFERENCE.LIKED) {
                            likedMovies.add(movie);
                        } else if (movie.getmPref() == Movie.PREFERENCE.DISLIKED) {
                            dislikedMovies.add(movie);
                        }
                    }
                    if (likedMovies.size() > 0) {
                        final MovieDBClient movieDBClient = new MovieDBClient();
                        int luckyNum = movieDBClient.getRandomNumber(0, likedMovies.size() - 1);

                        try {
                            Movie luckyMovie = likedMovies.get(luckyNum);
                            discoveredMovies = new fetchInterestingMovies().execute(luckyMovie.getmId()).get();
                            Timber.d(TAG, "Discovered movies are here!");
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            discoveredMovies = new fetchMovies().execute("").get();
                            Timber.d(TAG, "Discovered movies are here!");
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    /**
                     * Remove the liked/disliked movies
                     */
                    for (Movie m :
                            likedMovies) {
                        if (discoveredMovies.contains(m)) {
                            discoveredMovies.remove(m);
                        }
                    }
                    for (Movie m :
                            dislikedMovies) {
                        if (discoveredMovies.contains(m)) {
                            discoveredMovies.remove(m);
                        }
                    }
                    mMoviesList.setValue(discoveredMovies);
                }
            }

            @Override
            public void onError(Exception e) {
                mMoviesList.setValue(null);
            }
        });
    }

    @Override
    protected void onCleared() {
        movieRepository.removeListener();
    }

    /**
     * This the string shown in mText changes according to the index
     */
    private LiveData<String> mText = Transformations.map(mTypeOfList, new Function<String, String>() {
        @Override
        public String apply(String input) {
            return "This is the " + input.toLowerCase() + " fragment !";
        }
    });

    public void initiateRefresh() {
        if (mTypeOfList.getValue().equals("DISCOVER")) {
            loadMovies(Movie.PREFERENCE.IGNORED);
        }
    }

    /**
     * Description: fetchInterestingMovies can only fetch 20 movies at a time for a given movie Id.
     */
    class fetchInterestingMovies extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... strings) {
            List<Movie> movielist2 = new ArrayList<Movie>();
            final MovieDBClient movieDBClient = new MovieDBClient();
            try {
                /**
                 * TODO Validate that this is a logic which can be used or exploited to
                 * get different results on similar movies
                 */
                int page = movieDBClient.getRandomNumber(1, 4);
                movielist2 = movieDBClient.getSimilarMovies(strings[0], page);
            } catch (MovieDBClientException e) {
                e.printStackTrace();
            }
            return movielist2;
        }
    }


    /**
     * Fetch Popular movies
     */
    class fetchMovies extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... strings) {
            List<Movie> movieList1 = new ArrayList<Movie>();
            final MovieDBClient movieDBClient = new MovieDBClient();

            try {
                movieList1 = movieDBClient.getSomePopularMovies(20);
            } catch (MovieDBClientException e) {
                e.printStackTrace();
            }
            return movieList1;
        }
    }

}
