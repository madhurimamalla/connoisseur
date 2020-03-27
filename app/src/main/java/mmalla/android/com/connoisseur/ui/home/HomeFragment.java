package mmalla.android.com.connoisseur.ui.home;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;

import mmalla.android.com.connoisseur.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View homeRootView = inflater.inflate(R.layout.fragment_home, container, false);

        FeaturePagerAdapter featurePagerAdapter = new FeaturePagerAdapter(getContext(), getChildFragmentManager());
        ViewPager featureViewPager = (ViewPager) homeRootView.findViewById(R.id.view_pager_features);
        featureViewPager.setAdapter(featurePagerAdapter);

        TabLayout tabsLayout = homeRootView.findViewById(R.id.featureTabsLayout);
        tabsLayout.setupWithViewPager(featureViewPager);
        return homeRootView;
    }
}