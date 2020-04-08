package mmalla.android.com.connoisseur.ui.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

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
        //TODO DO remove this hardcoded value once the real data flows through.
        mPlotSummary.setValue("Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency."
                + "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.");
        mTitle.setValue(movie.getmTitle());
        mPosterPath.setValue(movie.getmPoster());
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

    /**
     * Method to update movie preference in the Database
     *
     * @param preference
     */
    public void updateMovie(Movie.PREFERENCE preference) {
        databaseUtils.updateMovie(mAuth.getCurrentUser().getUid(), mMovie, preference);
    }
}
