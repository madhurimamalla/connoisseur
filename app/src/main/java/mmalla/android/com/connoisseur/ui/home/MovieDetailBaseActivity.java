package mmalla.android.com.connoisseur.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.transition.Explode;
import android.transition.Fade;
import android.view.WindowManager;

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

    private static final String MOVIE_DETAIL_FRAGMENT_LIST = "MOVIE_DETAIL_FRAGMENT_LIST";
    private static final String MOVIE_POSITION = "MOVIE_POSITION";
    private static final String CLICK_POSITION = "CLICK_POSITION";
    private static final String LIST_TYPE = "LIST_TYPE";

    @BindView(R.id.view_pager_movies_details)
    ViewPager movieDetailsViewPager;

    private MovieDetailsPagerAdapter movieDetailsPagerAdapter;
    private int moviePosition = 0;
    private String listType = "";

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

        /**
         * For enabling fullscreen mode to this activity
         */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        Intent previousIntent = getIntent();
        if (savedInstanceState != null) {
            Timber.d(TAG, "Retrieving parcelable array list from savedInstance");
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_DETAIL_FRAGMENT_LIST);
            moviePosition = savedInstanceState.getInt(MOVIE_POSITION);
            listType = savedInstanceState.getString(LIST_TYPE);
        } else {
            Timber.d(TAG, "Retrieving the parcelable array list from the previous intent");
            movieList = previousIntent.getExtras().getParcelableArrayList("LIST_MOVIES");
            moviePosition = previousIntent.getExtras().getInt(CLICK_POSITION);
            listType = previousIntent.getExtras().getString(LIST_TYPE);
        }

        movieDetailsPagerAdapter = new MovieDetailsPagerAdapter(getSupportFragmentManager());
        movieDetailsPagerAdapter.setTypeOfList(listType);
        movieDetailsPagerAdapter.setList(movieList);
        movieDetailsViewPager.setAdapter(movieDetailsPagerAdapter);
        movieDetailsViewPager.setCurrentItem(moviePosition);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_DETAIL_FRAGMENT_LIST, (ArrayList<? extends Parcelable>) movieList);
        outState.putInt(MOVIE_POSITION, moviePosition);
        outState.putString(LIST_TYPE, listType);
        super.onSaveInstanceState(outState);
    }
}
