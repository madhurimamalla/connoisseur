package mmalla.android.com.connoisseur.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mmalla.android.com.connoisseur.R;
import mmalla.android.com.connoisseur.model.Movie;

import timber.log.Timber;

class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = WidgetDataProvider.class.getSimpleName();
    private static final String MOVIE_DISCOVER_FEATURE = "MOVIE_DISCOVER_FEATURE";
    private static final String MOVIE_PARCELED = "MOVIE_PARCELED";
    private static final String MOVIES = "movies";
    private static final String USERS = "users";

    private Context mContext;
    private Intent mIntent;

    private List<Movie> movieList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userId;

    /**
     * TODO Needs to be changed to reach and fetch the watchlist directly
     * @throws NullPointerException
     */
    private void initData() throws NullPointerException {
        try {
            this.movieList.clear();
            this.mAuth = FirebaseAuth.getInstance();
            this.user = mAuth.getCurrentUser();
            this.userId = user.getUid();
            Timber.d(TAG, "The user name is: %s", this.user.getDisplayName().toString());

            /**
             * Getting the Firebase database for this application
             */
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

            FirebaseDatabase moviesListDB = FirebaseDatabase.getInstance();
            DatabaseReference moviesListRef = moviesListDB.getReference().child(USERS).child(this.userId).child(MOVIES);

            /**
             * Get list of movie id from the Firebase
             */

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Movie movie = ds.getValue(Movie.class);
                        if (movie != null && movie.getmPref().equals(Movie.PREFERENCE.WISHLISTED)) {
                            movieList.add(movie);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Timber.e(TAG, databaseError.getMessage());
                }
            };

            moviesListRef.addValueEventListener(valueEventListener);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public WidgetDataProvider(Context mContext, Intent intent) {
        this.mContext = mContext;
        this.mIntent = intent;
    }

    @Override
    public void onCreate() {
        Timber.d(TAG, "Starting onCreate() in WidgetDataProvider.......");
        initData();

    }

    /**
     * TODO Check why the movies aren't getting displayed in the widget
     */
    @Override
    public void onDataSetChanged() {
        Timber.d(TAG, "Starting onDataSetChanged() in WidgetDataProvider.......");
        initData();
        Timber.d(TAG, "Completed onDataSetChanged() in WidgetDataProvider.......");
    }

    @Override
    public void onDestroy() {
        Timber.d(TAG, "Starting onDestroy() in WidgetDataProvider.......");

    }

    @Override
    public int getCount() {
        Timber.d(TAG, "Starting onCount() in WidgetDataProvider.......");
        return movieList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Timber.d(TAG, "Starting getViewAt() in WidgetDataProvider.......");

        Movie movie = movieList.get(position);
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.wishlist_widget_item_row);
        remoteViews.setTextViewText(R.id.widget_movie_title, movie.getmTitle());
        remoteViews.setTextViewText(R.id.widget_movie_year, movie.getmReleaseYear());
        remoteViews.setImageViewUri(R.id.widget_movie_poster, Uri.parse("http://image.tmdb.org/t/p/w185/" + movie.getmPoster()));

        Bundle extras = new Bundle();
        extras.putParcelable(MOVIE_PARCELED, movie);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(MOVIE_DISCOVER_FEATURE, movie);
        fillInIntent.putExtras(extras);
        remoteViews.setOnClickFillInIntent(R.id.widget_item_row, fillInIntent);

        Timber.d(TAG, "Completing getViewAt() in WidgetDataProvider.......");
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        Timber.d(TAG, "Starting getViewTypeCount() in WidgetDataProvider.......");
        return 1;
    }

    @Override
    public long getItemId(int position) {
        Timber.d(TAG, "Starting getItemId() in WidgetDataProvider.......");
        return position;
    }

    @Override
    public boolean hasStableIds() {
        Timber.d(TAG, "Starting hasStableIds() in WidgetDataProvider.......");
        return true;
    }
}
