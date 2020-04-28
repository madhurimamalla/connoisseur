package mmalla.android.com.connoisseur.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import mmalla.android.com.connoisseur.MovieRepository;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MovieRepository movieRepository;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public MovieRepository getMRepository() {
        if(movieRepository == null){
            movieRepository = new MovieRepository();
            movieRepository.addListenerOnFirebaseRep();
        }
        return movieRepository;
    }

    public LiveData<String> getText() {
        return mText;
    }

    @Override
    protected void onCleared() {
        movieRepository.removeListenerOnFirebaseRep();
        super.onCleared();
    }
}