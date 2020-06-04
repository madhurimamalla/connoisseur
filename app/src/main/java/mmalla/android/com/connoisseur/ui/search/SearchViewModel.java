package mmalla.android.com.connoisseur.ui.search;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mmalla.android.com.connoisseur.model.Movie;
import mmalla.android.com.connoisseur.moviedbclient.MovieDBClient;
import mmalla.android.com.connoisseur.moviedbclient.MovieDBClientException;
import timber.log.Timber;

public class SearchViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<List<Movie>> searchResults = new MutableLiveData<>();
    private boolean adultContentFlag = false;

    public SearchViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Search for movies now!");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public boolean getAdultContentFlag() {
        return this.adultContentFlag;
    }

    public void setAdultContentFlag(boolean adultContentFlag) {
        this.adultContentFlag = adultContentFlag;
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
                results = movieDBClient.getLimitedSearchResults(strings[0], 20, adultContentFlag);
            } catch (MovieDBClientException e) {
                e.printStackTrace();
            }
            return results;
        }
    }
}