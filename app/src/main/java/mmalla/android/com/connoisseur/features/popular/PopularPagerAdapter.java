package mmalla.android.com.connoisseur.features.popular;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import mmalla.android.com.connoisseur.model.Movie;

public class PopularPagerAdapter extends FragmentPagerAdapter {

    private List<Movie> movieList;

    public PopularPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setList(List<Movie> list){
        this.movieList = list;
    }

    @Override
    public Fragment getItem(int i) {
        return PopularFragment.newInstance(movieList.get(i));
    }

    @Override
    public int getCount() {
        return movieList.size();
    }
}
