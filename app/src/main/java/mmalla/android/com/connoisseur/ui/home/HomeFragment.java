package mmalla.android.com.connoisseur.ui.home;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmalla.android.com.connoisseur.R;
import timber.log.Timber;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FeaturePagerAdapter featurePagerAdapter;
    private final static String TAG = HomeFragment.class.getSimpleName();

    @BindView(R.id.view_pager_features)
    ViewPager featureViewPager;

    @BindView(R.id.featureTabsLayout)
    TabLayout tabsLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Timber.d("Entered the HomeFragment onCreateView()....");

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        View homeRootView = inflater.inflate(R.layout.fragment_home, container, false);

        /**
         * Binding using ButterKnife
         */
        ButterKnife.bind(this, homeRootView);

        featurePagerAdapter = new FeaturePagerAdapter(getContext(), getChildFragmentManager());
        featureViewPager.setAdapter(featurePagerAdapter);
        tabsLayout.setupWithViewPager(featureViewPager);
        return homeRootView;
    }
}