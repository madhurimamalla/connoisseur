package mmalla.android.com.connoisseur.ui.preferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PreferencesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PreferencesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Use the controls below to tweak your app preferences. \n" + "This feature is still in development!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}