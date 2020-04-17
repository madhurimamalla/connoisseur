package mmalla.android.com.connoisseur;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmalla.android.com.connoisseur.recommendations.engine.DatabaseUtils;
import timber.log.Timber;

public class SplashActivityNew extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private DatabaseUtils databaseUtils;

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
         * Binding views using ButterKnife
         */
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        /**
         * Retrieving mAuth from Firebase
         */
        mAuth = FirebaseAuth.getInstance();

        databaseUtils = new DatabaseUtils();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
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
        tv.setText(mAuth.getCurrentUser().getEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash_screen_menu, menu);
        return true;
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
                Toast.makeText(getApplicationContext(), R.string.Edit_account_details_clicked, Toast.LENGTH_LONG).show();
                return true;
            case R.id.clear_movie_list:
                if (databaseUtils.removeFullMoviesListFromTheUser(mAuth.getCurrentUser().getUid())) {
                    Toast.makeText(getApplicationContext(), R.string.Cleared_movie_list, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.search_button:
                SearchView searchView = (SearchView) item.getActionView();

                /**
                 * TODO Implement the search feature for future extension
                 */
                /**
                 * When text is entered into the searchView, start a new SearchActivity
                 */
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        Timber.d(TAG, "Text entered : " + s);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        Timber.d(TAG, "Text changed : " + s);
                        return false;
                    }

                });
                /**
                 * TODO When the searchView is closed, return to SplashActivity with the options
                 */
                searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        return false;
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
