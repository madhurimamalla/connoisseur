package mmalla.android.com.connoisseur;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.transition.Explode;
import android.transition.Fade;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmalla.android.com.connoisseur.model.Movie;
import mmalla.android.com.connoisseur.ui.home.MovieDetailBaseActivity;
import mmalla.android.com.connoisseur.ui.search.SearchFragment;
import timber.log.Timber;

public class SplashActivityNew extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SplashNewViewModel splashNewViewModel;
    List<Movie> searchedList = new ArrayList<>();

    private final static String TAG = SplashActivityNew.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        setContentView(R.layout.activity_splash_new);

        /**
         * Configuring the ViewModel class for the activity
         */
        ViewModelProviders.of(this).get(SplashNewViewModel.class);
        splashNewViewModel = new SplashNewViewModel();
        splashNewViewModel.init();

        /**
         * Binding views using ButterKnife
         */
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_search, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        /**
         * Setting the user's email address in the navigational drawer
         */
        View headerView = navigationView.getHeaderView(0);
        TextView tv = (TextView) headerView.findViewById(R.id.emailAddressTv);
        splashNewViewModel.getUserEmailId().observe(this, s -> tv.setText(s));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash_screen_menu, menu);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        SearchView mySearchView = (SearchView) menu.getItem(2).getActionView();
        //mySearchView.setQueryHint("Coming soon...");
        mySearchView.setQueryHint("Search for a movie...");
        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Timber.d("Started text: " + query);
                //TODO
                Bundle searchBundle = new Bundle();
                searchBundle.putString("QUERY_STRING", query);
                navController.navigate(R.id.nav_search, searchBundle);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.edit_account_details:
                Toast.makeText(getApplicationContext(), "This feature will be available soon", Toast.LENGTH_LONG).show();
                return true;
            case R.id.clear_movie_list:
                if (splashNewViewModel.removeAllSavedMoviesFromFirebase()) {
                    Toast.makeText(getApplicationContext(), R.string.Cleared_movie_list, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
