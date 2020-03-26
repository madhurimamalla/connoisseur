package mmalla.android.com.connoisseur.ui.home;

import android.content.Context;
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
        switch (i) {
            case 0:
                return new DiscoverFragmentNew();
            case 1:
                return new WatchlistFragmentNew();
            case 2:
                return new HistoryFragmentNew();
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
