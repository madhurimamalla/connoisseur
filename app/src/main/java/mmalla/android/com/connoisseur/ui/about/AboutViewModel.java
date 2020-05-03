package mmalla.android.com.connoisseur.ui.about;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import mmalla.android.com.connoisseur.BuildConfig;

public class AboutViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AboutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Information about my app will be displayed here. " + "\n " + " \n Connoisseur Version: " + BuildConfig.VERSION_NAME);
    }

    public LiveData<String> getText() {
        return mText;
    }
}
