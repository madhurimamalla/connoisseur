package mmalla.android.com.connoisseur.ui.about;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AboutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Information about my app will be displayed here.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
