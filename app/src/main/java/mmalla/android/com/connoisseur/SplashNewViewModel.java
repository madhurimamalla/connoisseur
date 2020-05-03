package mmalla.android.com.connoisseur;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mmalla.android.com.connoisseur.model.Movie;
import mmalla.android.com.connoisseur.moviedbclient.MovieDBClient;
import mmalla.android.com.connoisseur.moviedbclient.MovieDBClientException;
import mmalla.android.com.connoisseur.recommendations.engine.DatabaseUtils;
import timber.log.Timber;

public class SplashNewViewModel extends ViewModel {

    private static final String TAG = SplashNewViewModel.class.getSimpleName();

    private DatabaseUtils databaseUtils;
    private MutableLiveData<List<Movie>> searchResults = new MutableLiveData<>();
    private MutableLiveData<String> userEmailId = new MutableLiveData<>();

    private FirebaseAuth mAuth;

    public void init() {
        mAuth = FirebaseAuth.getInstance();
        databaseUtils = new DatabaseUtils();
        userEmailId.setValue(mAuth.getCurrentUser().getEmail());
    }

    public MutableLiveData<String> getUserEmailId() {
        return userEmailId;
    }

    public boolean removeAllSavedMoviesFromFirebase() {
        return databaseUtils.removeFullMoviesListFromTheUser(mAuth.getCurrentUser().getUid());
    }

    public void retrieveSearchResults(String query) {
        searchResults = new MutableLiveData<>();
        try {
            searchResults.setValue(new fetchSearchResults().execute(query).get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Timber.d("The search results are here........");
    }

    public MutableLiveData<List<Movie>> getSearchResults() {
        return searchResults;
    }

    /**
     * Fetch Search results
     */
    class fetchSearchResults extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... strings) {
            final MovieDBClient movieDBClient = new MovieDBClient();
            List<Movie> results = new ArrayList<>();
            /**
             * Take in the query string and fetch the search results from TMDB
             */
            try {
                results = movieDBClient.getLimitedSearchResults(strings[0], 20);
            } catch (MovieDBClientException e) {
                e.printStackTrace();
            }
            return results;
        }
    }


}
