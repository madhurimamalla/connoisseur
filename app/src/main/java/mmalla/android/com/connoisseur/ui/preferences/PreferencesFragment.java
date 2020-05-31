package mmalla.android.com.connoisseur.ui.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmalla.android.com.connoisseur.R;
import timber.log.Timber;

public class PreferencesFragment extends Fragment {

    private PreferencesViewModel preferencesViewModel;

    @BindView(R.id.ratingBar)
    SeekBar ratingBar;

    @BindView(R.id.adult_content)
    CheckBox adultContentCB;

    int ratingValue;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        preferencesViewModel =
                ViewModelProviders.of(this).get(PreferencesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_preferences, container, false);

        ButterKnife.bind(this, root);

        final TextView textView = root.findViewById(R.id.text_preferences);
        preferencesViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        /**
         * Add code to gather data when the slider is moved by the user.
         */
        ratingBar.setMax(10);
        /**
         * Fetch the value of rating set from the Shared Preferences and set the seek bar
         */
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.connoisseur_preferences_file), Context.MODE_PRIVATE);
        ratingBar.setProgress(sharedPreferences.getInt(getString(R.string.rating_string), 5));
        ratingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Timber.d("There is some progress");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Timber.d("There is tracking touch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Timber.d("There is a stop in tracking touch.");
                ratingValue = seekBar.getProgress();
                sharedPreferences.edit().putInt(getString(R.string.rating_string), ratingValue).apply();
            }
        });

        boolean adult_content = sharedPreferences.getBoolean(getString(R.string.adult_content), false);
        adultContentCB.setChecked(adult_content);

        adultContentCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(getString(R.string.adult_content), isChecked).apply();
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}