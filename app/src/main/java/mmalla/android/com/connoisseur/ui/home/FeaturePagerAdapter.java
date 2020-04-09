package mmalla.android.com.connoisseur.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import mmalla.android.com.connoisseur.R;
import mmalla.android.com.connoisseur.model.Movie;
import timber.log.Timber;

public class FeaturePagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = FeaturePagerAdapter.class.getSimpleName();

    @StringRes
    private static final int[] FEATURE_TITLES = new int[]{R.string.discover, R.string.watchlist, R.string.history};
    private final static String FEATURE = "FEATURE";
    private Context mContext;
    private List<Movie> sentMovieList;

    public FeaturePagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    public void setMovies(List<Movie> movies) {
        sentMovieList = movies;
        Timber.d("The movies are set!");
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                MovieListFragment discoveredListFragment = new MovieListFragment();
                Bundle discoverBundle = new Bundle();
                discoverBundle.putString(FEATURE, mContext.getResources().getString(FEATURE_TITLES[0]));
                discoveredListFragment.setArguments(discoverBundle);
                return discoveredListFragment;
            case 1:
                MovieListFragment watchlistListFragment = new MovieListFragment();
                Bundle watchlistBundle = new Bundle();
                watchlistBundle.putString(FEATURE, mContext.getResources().getString(FEATURE_TITLES[1]));
                watchlistListFragment.setArguments(watchlistBundle);
                return watchlistListFragment;
            case 2:
                MovieListFragment historyListFragment = new MovieListFragment();
                Bundle historyBundle = new Bundle();
                historyBundle.putString(FEATURE, mContext.getResources().getString(FEATURE_TITLES[2]));
                historyListFragment.setArguments(historyBundle);
                return historyListFragment;
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(FEATURE_TITLES[position]);
    }

    @Override
    public int getCount() {
        return FEATURE_TITLES.length;
    }
}
