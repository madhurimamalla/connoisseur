package mmalla.android.com.connoisseur.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mmalla.android.com.connoisseur.R;

public class FeaturePagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] FEATURE_TITLES = new int[]{R.string.discover, R.string.watchlist, R.string.history};
    private Context mContext;

    public FeaturePagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int i) {
        MovieListFragment movieListFragment = new MovieListFragment();
        switch (i) {
            case 0:
                Bundle discoverBundle = new Bundle();
                discoverBundle.putString("FEATURE", mContext.getResources().getString(FEATURE_TITLES[0]));
                movieListFragment.setArguments(discoverBundle);
                return movieListFragment;
            case 1:
                Bundle watchlistBundle = new Bundle();
                watchlistBundle.putString("FEATURE", mContext.getResources().getString(FEATURE_TITLES[1]));
                movieListFragment.setArguments(watchlistBundle);
                return movieListFragment;
            case 2:
                Bundle historyBundle = new Bundle();
                historyBundle.putString("FEATURE", mContext.getResources().getString(FEATURE_TITLES[2]));
                movieListFragment.setArguments(historyBundle);
                return movieListFragment;
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
