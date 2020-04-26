package mmalla.android.com.connoisseur.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collection;
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
    private final int LIKED_MOVIES_THRESHOLD = 2;
    private boolean initiateRefresh = false;

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
                    loadHistory();
                    break;
                case "WATCHLIST":
                    loadWatchlist();
                    break;
                case "DISCOVER":
                    discoverMovies(Movie.PREFERENCE.IGNORED);
                    break;
            }
        }
        return mMoviesList;
    }

    private void loadHistory() {
        movieRepository.addListener(new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Movie>() {
            @Override
            public void onSuccess(List<Movie> result) {
                List<Movie> shortListedMovies = new ArrayList<>();
                for (Movie movie :
                        result) {
                    if (movie.getmPref() == Movie.PREFERENCE.DISLIKED || movie.getmPref() == Movie.PREFERENCE.LIKED) {
                        shortListedMovies.add(movie);
                    }
                }
                Timber.d("Shortlisted movies: " + mTypeOfList.getValue() + " are here!" + " And its size is: " + shortListedMovies.size());
                mMoviesList.setValue(shortListedMovies);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void loadWatchlist() {
        movieRepository.addListener(new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Movie>() {
            @Override
            public void onSuccess(List<Movie> result) {
                List<Movie> shortListedMovies = new ArrayList<>();
                for (Movie movie :
                        result) {
                    if (movie.getmPref() == Movie.PREFERENCE.WISHLISTED) {
                        shortListedMovies.add(movie);
                    }
                }
                Timber.d("Shortlisted movies: " + mTypeOfList.getValue() + " are here!" + " And its size is: " + shortListedMovies.size());
                mMoviesList.setValue(shortListedMovies);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * The method used to load Movies in the feature tabs
     *
     * @param mPref
     */
    private void discoverMovies(final Movie.PREFERENCE mPref) {
        movieRepository.addListener(new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Movie>() {
            @Override
            public void onSuccess(List<Movie> result) {
                if (mPref == Movie.PREFERENCE.IGNORED) {
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
                    if (likedMovies.size() <= LIKED_MOVIES_THRESHOLD) {
                        discoveredMovies = loadPopularMovies();
                    } else if (mMoviesList != null) {
                        if (mMoviesList.getValue() == null || initiateRefresh) {
                            discoveredMovies.clear();
                            initiateRefresh = false;
                            final MovieDBClient movieDBClient = new MovieDBClient();
                            int luckyNum = movieDBClient.getRandomNumber(0, likedMovies.size() - 1);
                            Movie luckyMovie = likedMovies.get(luckyNum);
                            Timber.d("Discovered movies based on movie: " + likedMovies.get(luckyNum).getmTitle());
                            try {
                                discoveredMovies = new fetchInterestingMovies().execute(luckyMovie.getmId()).get();
                                Timber.d(TAG, "Discovered movies are found based on similar movies with size: " + discoveredMovies.size());
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
                    Timber.d("Loading final discovered movies with size: " + discoveredMovies.size());
                    mMoviesList.setValue(discoveredMovies);
                }
            }

            @Override
            public void onError(Exception e) {
                mMoviesList.setValue(null);
            }
        });
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

    @Override
    protected void onCleared() {
        movieRepository.removeListener();
    }

    /**
     * This method is used to retrieve the popular movies
     */
    public void getPopularMovies() {
        if (mTypeOfList.getValue().equals("DISCOVER")) {
            Timber.d("Getting popular movies.....");
            mMoviesList.setValue(loadPopularMovies());
        }
    }

    public void initiateRefresh() {
        if (mTypeOfList.getValue().equals("DISCOVER")) {
            initiateRefresh = true;
            discoverMovies(Movie.PREFERENCE.IGNORED);
        }
    }

    /**
     * Loads more movies in the Discover tab
     * @param page
     * @param totalItemsCount
     * @param view
     *//*
    public void getMoreMovies(int page, int totalItemsCount, RecyclerView view) {
        try {
            List<Movie> listOfMovies = new fetchInterestingMovies().execute("").get();
            mMoviesList.getValue().addAll(listOfMovies);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

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
