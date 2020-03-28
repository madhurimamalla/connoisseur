package mmalla.android.com.connoisseur;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.transition.Explode;
import android.transition.Fade;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import mmalla.android.com.connoisseur.model.Movie;
import mmalla.android.com.connoisseur.recommendations.engine.DatabaseUtils;

public class SplashActivityNew extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;

    /**
     * Creating instance of DatabaseUtils for interacting with DB
     */
    private DatabaseUtils databaseUtils = new DatabaseUtils();
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private static final String ARE_THERE_MOVIES_UNDER_USER = "ARE_THERE_MOVIES_UNDER_USER";
    private static final String MOVIE_WISHLIST_PARCELED = "MOVIE_WISHLIST_PARCELED";
    private static final String MOVIE_DISCOVER_FEATURE = "MOVIE_DISCOVER_FEATURE";
    private static final String MOVIE_HISTORY_PARCELED = "MOVIE_HISTORY_PARCELED";
    private static final String MOVIE_POPULAR_PARCELED = "MOVIE_POPULAR_PARCELED";
    private static final String MOVIES_DISLIKED = "MOVIES_DISLIKED";
    private static final String MOVIES_LIKED = "MOVIES_LIKED";

    private int num = 0;
    private Long[] size;

    private List<Movie> presentLikedMovies;
    private List<Movie> dislikedMovies;
    private List<Movie> wishlistedMovies;

    private final static String TAG = SplashActivityNew.class.getSimpleName();

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
         /*
          Check if there are any movies under the authenticated user, if so, send a message to the next
          activity so the discovery process can be easy.
         */

        this.presentLikedMovies = new ArrayList<Movie>();
        this.dislikedMovies = new ArrayList<Movie>();

        this.size = databaseUtils.checkIfMoviesExist(mAuth.getCurrentUser().getUid());
        this.presentLikedMovies = databaseUtils.getLikedMovies(mAuth.getCurrentUser().getUid());
        this.dislikedMovies = databaseUtils.getDislikedMovies(mAuth.getCurrentUser().getUid());

        // TODO Added this below in case we want to play around with the options showing up in Recommendations
        this.wishlistedMovies = databaseUtils.getWishlistedMovies(mAuth.getCurrentUser().getUid());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
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
}