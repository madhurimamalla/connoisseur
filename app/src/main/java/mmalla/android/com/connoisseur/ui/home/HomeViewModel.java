package mmalla.android.com.connoisseur.ui.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import mmalla.android.com.connoisseur.model.Movie;
import mmalla.android.com.connoisseur.recommendations.engine.DatabaseUtils;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    /**
     * Creating instance of DatabaseUtils for interacting with DB
     */
    private DatabaseUtils databaseUtils = new DatabaseUtils();
    // [START declare_auth]
    private FirebaseAuth mAuth;
    Long[] size;
    List<Movie> presentLikedMovies = new ArrayList<Movie>();

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public List<Movie> fetchMovies() {
        mAuth = FirebaseAuth.getInstance();
        this.size = databaseUtils.checkIfMoviesExist(mAuth.getCurrentUser().getUid());
        this.presentLikedMovies = databaseUtils.getLikedMovies(mAuth.getCurrentUser().getUid());
        return this.presentLikedMovies;
    }
}