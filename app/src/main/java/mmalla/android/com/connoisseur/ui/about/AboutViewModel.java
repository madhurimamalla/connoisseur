package mmalla.android.com.connoisseur.ui.about;

import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import mmalla.android.com.connoisseur.BuildConfig;

public class AboutViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AboutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("\n Connoisseur Version: " + BuildConfig.VERSION_NAME
                + "\n Model: " + Build.MODEL
                + "\n Manufacturer: " + Build.MANUFACTURER
                + "\n Version SDK Int:" + Build.VERSION.SDK_INT);
    }

    public LiveData<String> getText() {
        return mText;
    }
}
