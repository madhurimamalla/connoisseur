package mmalla.android.com.connoisseur.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmalla.android.com.connoisseur.BaseActivity;
import mmalla.android.com.connoisseur.R;
import mmalla.android.com.connoisseur.model.Movie;
import timber.log.Timber;

public class MovieDetailBaseActivity extends BaseActivity {

    private static final String TAG = MovieDetailBaseActivity.class.getSimpleName();
    private List<Movie> movieList = new ArrayList<>();

    @BindView(R.id.view_pager_movies_details)
    ViewPager movieDetailsViewPager;

    MovieDetailsPagerAdapter movieDetailsPagerAdapter;
    int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Explode explode = new Explode();
            explode.setDuration(300);
            Fade fade = new Fade();
            fade.setDuration(400);
            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(explode);
            getWindow().setReenterTransition(explode);
        }

        setContentView(R.layout.activity_details_movie_new);

        /**
         * Binding views using ButterKnife
         */
        ButterKnife.bind(this);

        Intent previousIntent = getIntent();
        if (savedInstanceState != null) {
            Timber.d(TAG, "Retrieving parcelable array list from savedInstance");
            movieList = savedInstanceState.getParcelableArrayList("SAVED_MOVIES");
            position = 0;
        } else {
            Timber.d(TAG, "Retrieving the parcelable array list from the previous intent");
            movieList = previousIntent.getExtras().getParcelableArrayList("LIST_MOVIES");
            position = previousIntent.getExtras().getInt("CLICK_POSITION");
        }


        movieDetailsPagerAdapter = new MovieDetailsPagerAdapter(getSupportFragmentManager());
        movieDetailsPagerAdapter.setList(movieList);
        movieDetailsViewPager.setAdapter(movieDetailsPagerAdapter);
        movieDetailsViewPager.setCurrentItem(position);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
