package mmalla.android.com.connoisseur.ui.home;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.ExecutionException;

import mmalla.android.com.connoisseur.model.Movie;
import mmalla.android.com.connoisseur.moviedbclient.MovieDBClient;
import mmalla.android.com.connoisseur.moviedbclient.MovieDBClientException;
import mmalla.android.com.connoisseur.recommendations.engine.DatabaseUtils;
import timber.log.Timber;

public class MovieDetailsViewModel extends ViewModel {

    private static final String TAG = MovieDetailsViewModel.class.getSimpleName();

    /**
     * Movie data needs to be set to these variables
     */
    private MutableLiveData<String> mPlotSummary = new MutableLiveData<>();
    private MutableLiveData<String> mTitle = new MutableLiveData<>();
    private MutableLiveData<String> mPosterPath = new MutableLiveData<>();
    private MutableLiveData<String> mRating = new MutableLiveData<>();
    private MutableLiveData<String> mReleaseYear = new MutableLiveData<>();
    private MutableLiveData<String> mVoteCount = new MutableLiveData<>();
    private MutableLiveData<String> mTagline = new MutableLiveData<>();
    private Movie mMovie;
    private Movie tmdbMovie;
    private DatabaseUtils databaseUtils;
    private FirebaseAuth mAuth;
    private MovieDBClient movieDBClient;

    public void init() {
        mAuth = FirebaseAuth.getInstance();
        databaseUtils = new DatabaseUtils();
        movieDBClient = new MovieDBClient();
    }

    public void setMovieLiveData(Movie movie) {
        mMovie = movie;
        try {
            tmdbMovie = new fetchMovieDetails().execute(movie.getmId()).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPlotSummary.setValue(movie.getmOverview());
        mTitle.setValue(movie.getmTitle());
        mPosterPath.setValue(movie.getmPoster());
        mRating.setValue(movie.getmRating());
        mVoteCount.setValue(movie.getmVoteCount());
        mReleaseYear.setValue(movie.getmReleaseYear());
        mTagline.setValue(tmdbMovie.getmTagline());
        Timber.d("Setting the known data....");
    }

    public LiveData<String> getMovieSummary() {
        return mPlotSummary;
    }

    public LiveData<String> getMovieTitle() {
        return mTitle;
    }

    public LiveData<String> getPosterPath() {
        return mPosterPath;
    }

    public LiveData<String> getRating() {
        return mRating;
    }

    public LiveData<String> getTagline() {
        return mTagline;
    }

    public LiveData<String> getReleaseYear() {
        return mReleaseYear;
    }

    public LiveData<String> getVoteCount() {
        return mVoteCount;
    }

    /**
     * Method to update movie preference in the Database
     *
     * @param preference
     */
    public void updateMovie(Movie.PREFERENCE preference) {
        databaseUtils.updateMovie(mAuth.getCurrentUser().getUid(), mMovie, preference);
    }

    class fetchMovieDetails extends AsyncTask<String, Void, Movie> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tmdbMovie = new Movie();
        }

        @Override
        protected Movie doInBackground(String... strings) {
            String mId = strings[0];
            try {
                tmdbMovie = movieDBClient.getMovieDetailsById(mId);
            } catch (MovieDBClientException e) {
                e.printStackTrace();
            }
            return tmdbMovie;
        }
    }
}