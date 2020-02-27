package mmalla.android.com.connoisseur;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import mmalla.android.com.connoisseur.features.FeatureActivity;
import mmalla.android.com.connoisseur.model.Movie;
import mmalla.android.com.connoisseur.recommendations.engine.DatabaseUtils;
import timber.log.Timber;

public class SplashActivity extends BaseActivity implements View.OnClickListener {

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


    private final static String TAG = SplashActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_splash);
        findViewById(R.id.wishlist).setOnClickListener(this);
        findViewById(R.id.history).setOnClickListener(this);
        findViewById(R.id.discover).setOnClickListener(this);
        findViewById(R.id.popular).setOnClickListener(this);
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
    }

    /**
     * Creates a menu for various options
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash_screen_menu, menu);
        return true;
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
                 * When the searchView is closed, return to SplashActivity with the options
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

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.wishlist) {
            Intent wishlistIntent = new Intent(this, FeatureActivity.class);
            wishlistIntent.putExtra(MOVIE_WISHLIST_PARCELED, "");
            startActivity(wishlistIntent);

        } else if (i == R.id.discover) {
            /*
              Initiating the Discover fragment via Feature Activity
             */
            Intent discoverIntent = new Intent(this, FeatureActivity.class);
            discoverIntent.putExtra(MOVIE_DISCOVER_FEATURE, "");
            discoverIntent.putExtra(ARE_THERE_MOVIES_UNDER_USER, size[0]);
            discoverIntent.putParcelableArrayListExtra(MOVIES_LIKED, (ArrayList<? extends Parcelable>) this.presentLikedMovies);
            discoverIntent.putParcelableArrayListExtra(MOVIES_DISLIKED, (ArrayList<? extends Parcelable>) this.dislikedMovies);
            startActivity(discoverIntent);

        } else if (i == R.id.popular) {

            Intent popularIntent = new Intent(this, FeatureActivity.class);
            popularIntent.putExtra(MOVIE_POPULAR_PARCELED, "");
            popularIntent.putParcelableArrayListExtra(MOVIES_LIKED, (ArrayList<? extends Parcelable>) this.presentLikedMovies);
            popularIntent.putParcelableArrayListExtra(MOVIES_DISLIKED, (ArrayList<? extends Parcelable>) this.dislikedMovies);
            startActivity(popularIntent);

        } else if (i == R.id.history) {

            Intent historyIntent = new Intent(this, FeatureActivity.class);
            historyIntent.putExtra(MOVIE_HISTORY_PARCELED, "");
            startActivity(historyIntent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
