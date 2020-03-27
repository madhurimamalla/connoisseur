package mmalla.android.com.connoisseur.ui.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mmalla.android.com.connoisseur.R;
import timber.log.Timber;

public class MovieListFragment extends Fragment {

    private MovieListViewModel movieListViewModel;

    private final static String TAG = MovieListFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Timber.d("The movieListViewModel is set here...");
        movieListViewModel =
                ViewModelProviders.of(this).get(MovieListViewModel.class);

        String bundleTypeStr = getArguments().getString("FEATURE");

        /**
         * Inflating the view of this fragment
         */
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        final TextView textView = (TextView) rootView.findViewById(R.id.text_feature);

        movieListViewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);
        movieListViewModel.setIndex(bundleTypeStr);

        movieListViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return rootView;
    }
}
