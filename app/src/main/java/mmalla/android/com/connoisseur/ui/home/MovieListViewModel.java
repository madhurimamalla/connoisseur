package mmalla.android.com.connoisseur.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

import mmalla.android.com.connoisseur.recommendations.engine.MovieRepository;
import mmalla.android.com.connoisseur.model.Movie;
import mmalla.android.com.connoisseur.moviedbclient.MovieDBClient;
import mmalla.android.com.connoisseur.moviedbclient.MovieDBClientException;
import timber.log.Timber;

public class MovieListViewModel extends ViewModel implements Observer {
    private final static String TAG = MovieListViewModel.class.getSimpleName();

    private MutableLiveData<List<Movie>> mMoviesList = new MutableLiveData<>();
    private MutableLiveData<String> mTypeOfList = new MutableLiveData<>();
    private boolean initiateRefresh = false;
    private MovieRepository movieRepository;
    private List<Movie> likedMovies = new ArrayList<>();
    private List<Movie> dislikedMovies = new ArrayList<>();
    private List<Movie> discoveredMovies = new ArrayList<>();

    /**
     * This method is used to set the type of list : WATCHLIST, HISTORY, DISCOVER here
     *
     * @param index
     */
    public void init(String index, MovieRepository mRepo) {
        /**
         * This sets the type of  fragment tab that is using this viewModel
         */
        mTypeOfList.setValue(index);
        /**
         * Other initiations to be done here
         */
        movieRepository = mRepo;
        movieRepository.addObserver(this);
    }

    /**
     * Returns movies to MovieListFragment for respective tabs
     *
     * @return
     */
    public LiveData<List<Movie>> getMovies() {
        if (mMoviesList == null) {
            mMoviesList = new MutableLiveData<List<Movie>>();
        }
        return mMoviesList;
    }

    private List<Movie> loadPopularMovies() {
        List<Movie> popularMovies = new ArrayList<>();
        try {
            popularMovies = new fetchPopularMovies().execute().get();
            Timber.d(TAG, "Popular movies are here!");
            return popularMovies;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return popularMovies;
    }

    /**
     * This method is used to retrieve the popular movies
     */
    public void getPopularMovies() {
        if (mTypeOfList.getValue().equals("DISCOVER")) {
            Timber.d("Getting popular movies.....");
            mMoviesList.postValue(loadPopularMovies());
        }
    }

    public void initiateRefresh() {
        if (mTypeOfList.getValue().equals("DISCOVER")) {
            initiateRefresh = true;
            discoverMovies(Movie.PREFERENCE.IGNORED);
        }
    }

    /**
     * The method used to discover movies in the Discover tab
     *
     * @param mPref
     */
    private void discoverMovies(final Movie.PREFERENCE mPref) {
        if (mPref == Movie.PREFERENCE.IGNORED) {
            int LIKED_MOVIES_THRESHOLD = 2;
            if (likedMovies.size() <= LIKED_MOVIES_THRESHOLD) {
                discoveredMovies = loadPopularMovies();
            } else if (mMoviesList != null) {
                if (mMoviesList.getValue() == null || initiateRefresh) {
                    discoveredMovies.clear();
                    initiateRefresh = false;
                    final MovieDBClient movieDBClient = new MovieDBClient();
                    int luckyNum = movieDBClient.getRandomNumber(0, likedMovies.size() - 1);
                    Movie luckyMovie = likedMovies.get(luckyNum);
                    Timber.d("Discovered movies based on movie: %s", likedMovies.get(luckyNum).getmTitle());
                    try {
                        discoveredMovies = new fetchInterestingMovies().execute(luckyMovie.getmId()).get();
                        Timber.d(TAG, "Discovered movies are found based on similar movies with size: %s", discoveredMovies.size());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Timber.d("As the mMoviesList already has data, return the same one...");
                    discoveredMovies = mMoviesList.getValue();
                }
            }
            if (discoveredMovies.size() == 0) {
                discoveredMovies = loadPopularMovies();
            }
            /**
             * Remove the liked/disliked movies
             */
            Timber.d("Removing the liked movies from the discovered list of movies");
            for (Movie m :
                    likedMovies) {
                if (discoveredMovies.contains(m)) {
                    discoveredMovies.remove(m);
                }
            }
            Timber.d("Removing the disliked movies from the discovered list of movies");
            for (Movie m :
                    dislikedMovies) {
                if (discoveredMovies.contains(m)) {
                    discoveredMovies.remove(m);
                }
            }
            Timber.d("Loading final discovered movies with size: %s", discoveredMovies.size());
            mMoviesList.postValue(discoveredMovies);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MovieRepository) {
            int size = ((List<List<Movie>>) arg).size();
            if (mTypeOfList.getValue().equals("HISTORY")) {
                if (size == 0) {
                    mMoviesList.postValue(new ArrayList<Movie>(0));
                } else {
                    mMoviesList.postValue(((List<List<Movie>>) arg).get(2));
                }
            } else if (mTypeOfList.getValue().equals("WATCHLIST")) {
                if (size == 0) {
                    mMoviesList.postValue(new ArrayList<Movie>(0));
                } else {
                    mMoviesList.postValue(((List<List<Movie>>) arg).get(3));
                }
            } else if (mTypeOfList.getValue().equals("DISCOVER")) {
                if (size == 0) {
                    this.likedMovies.clear();
                    this.dislikedMovies.clear();
                } else {
                    this.likedMovies = ((List<List<Movie>>) arg).get(0);
                    this.dislikedMovies = ((List<List<Movie>>) arg).get(1);
                }
                discoverMovies(Movie.PREFERENCE.IGNORED);
            }
        }

    }

    @Override
    protected void onCleared() {
        movieRepository.deleteObserver(this);
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
    class fetchPopularMovies extends AsyncTask<Void, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(Void... voids) {
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
