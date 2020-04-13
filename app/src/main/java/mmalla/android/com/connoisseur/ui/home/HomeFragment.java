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

import mmalla.android.com.connoisseur.R;
import timber.log.Timber;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FeaturePagerAdapter featurePagerAdapter;
    private final static String TAG = HomeFragment.class.getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Timber.d("Entered the HomeFragment onCreateView()....");

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        View homeRootView = inflater.inflate(R.layout.fragment_home, container, false);

        featurePagerAdapter = new FeaturePagerAdapter(getContext(), getChildFragmentManager());
        ViewPager featureViewPager = (ViewPager) homeRootView.findViewById(R.id.view_pager_features);
        featureViewPager.setAdapter(featurePagerAdapter);

        TabLayout tabsLayout = homeRootView.findViewById(R.id.featureTabsLayout);
        tabsLayout.setupWithViewPager(featureViewPager);
        return homeRootView;
    }
}