package mmalla.android.com.connoisseur;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import mmalla.android.com.connoisseur.recommendations.engine.DatabaseUtils;

public class SplashNewViewModel extends ViewModel {

    private static final String TAG = SplashNewViewModel.class.getSimpleName();

    private DatabaseUtils databaseUtils;
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
}
