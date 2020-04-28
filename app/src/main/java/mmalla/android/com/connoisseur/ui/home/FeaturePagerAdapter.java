package mmalla.android.com.connoisseur.ui.home;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import mmalla.android.com.connoisseur.MovieRepository;
import mmalla.android.com.connoisseur.R;

public class FeaturePagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = FeaturePagerAdapter.class.getSimpleName();
    private MovieRepository movieRepository;

    @StringRes
    private static final int[] FEATURE_TITLES = new int[]{R.string.discover, R.string.watchlist, R.string.history};
    private final static String FEATURE = "FEATURE";
    private Context mContext;

    public FeaturePagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    public void init(MovieRepository movieRepository1) {
        this.movieRepository = movieRepository1;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                MovieListFragment discoveredListFragment = new MovieListFragment(movieRepository);
                Bundle discoverBundle = new Bundle();
                discoverBundle.putString(FEATURE, mContext.getResources().getString(FEATURE_TITLES[0]));
                discoveredListFragment.setArguments(discoverBundle);
                return discoveredListFragment;
            case 1:
                MovieListFragment watchlistListFragment = new MovieListFragment(movieRepository);
                Bundle watchlistBundle = new Bundle();
                watchlistBundle.putString(FEATURE, mContext.getResources().getString(FEATURE_TITLES[1]));
                watchlistListFragment.setArguments(watchlistBundle);
                return watchlistListFragment;
            case 2:
                MovieListFragment historyListFragment = new MovieListFragment(movieRepository);
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
