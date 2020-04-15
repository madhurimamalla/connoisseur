package mmalla.android.com.connoisseur.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import mmalla.android.com.connoisseur.model.Movie;
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
    private Movie mMovie;
    private DatabaseUtils databaseUtils;
    private FirebaseAuth mAuth;

    public void init() {
        mAuth = FirebaseAuth.getInstance();
        databaseUtils = new DatabaseUtils();
    }

    public void setMovieLiveData(Movie movie) {
        mMovie = movie;
        mPlotSummary.setValue(movie.getmOverview());
        mTitle.setValue(movie.getmTitle());
        mPosterPath.setValue(movie.getmPoster());
        mRating.setValue(movie.getmRating());
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

    /**
     * Method to update movie preference in the Database
     *
     * @param preference
     */
    public void updateMovie(Movie.PREFERENCE preference) {
        databaseUtils.updateMovie(mAuth.getCurrentUser().getUid(), mMovie, preference);
    }
}
