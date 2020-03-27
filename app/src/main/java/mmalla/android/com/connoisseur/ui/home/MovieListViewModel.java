package mmalla.android.com.connoisseur.ui.home;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

public class MovieListViewModel extends ViewModel {

    private MutableLiveData<String> mTitle = new MutableLiveData<>();

    /**
     * This the string shown in mText changes according to the index
     */
    private LiveData<String> mText = Transformations.map(mTitle, new Function<String, String>() {
        @Override
        public String apply(String input) {
            return "This is the " + input.toLowerCase() + " fragment !";
        }
    });

    public void setIndex(String index) {
        mTitle.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }
}
