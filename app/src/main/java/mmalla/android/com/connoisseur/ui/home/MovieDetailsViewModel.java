package mmalla.android.com.connoisseur.ui.home;

import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mmalla.android.com.connoisseur.model.Genre;
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
    public MutableLiveData<String> showToast = new MutableLiveData<>();
    private MutableLiveData<String> mRuntime = new MutableLiveData<>();
    private MutableLiveData<String> mGenres = new MutableLiveData<>();
    private Movie mMovie;
    private Movie tmdbMovie;
    private DatabaseUtils databaseUtils;
    private FirebaseAuth mAuth;
    private MovieDBClient movieDBClient;
    private static final String FORMAT_USED_BY_TMDB = "yyyy-mm-dd";

    public void init() {
        mAuth = FirebaseAuth.getInstance();
        databaseUtils = new DatabaseUtils();
        movieDBClient = new MovieDBClient();
        showToast.setValue("");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        if (movie.getmReleaseYear() != null) {
            SimpleDateFormat format = new SimpleDateFormat(FORMAT_USED_BY_TMDB);
            try {
                Date date = format.parse(movie.getmReleaseYear());
                int year = date.getYear() + 1900;
                mReleaseYear.setValue(Integer.toString(year));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
        mTagline.setValue(tmdbMovie.getmTagline());
        mRuntime.setValue(tmdbMovie.getmRuntime());
        if (movie.getmVoteCount() == null) {
            mVoteCount.setValue(tmdbMovie.getmVoteCount());
        }

        List<Genre> genresList = tmdbMovie.getmGenres();
        List<String> genresListNames = new ArrayList<>();
        for (Genre genre : genresList) {
            genresListNames.add(genre.getName());
        }
        String genreListStr = TextUtils.join(", ", genresListNames);
        mGenres.setValue(genreListStr);
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

    public LiveData<String> getRuntime() {
        return mRuntime;
    }

    public LiveData<String> getReleaseYear() {
        return mReleaseYear;
    }

    public LiveData<String> getVoteCount() {
        return mVoteCount;
    }

    public LiveData<String> getGenresList() {
        return mGenres;
    }

    /**
     * Method to update movie preference in the Database
     *
     * @param preference
     */
    public void updateMovie(Movie.PREFERENCE preference, String word) {
        databaseUtils.updateMovie(mAuth.getCurrentUser().getUid(), mMovie, preference);
        showToast.setValue(word);
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