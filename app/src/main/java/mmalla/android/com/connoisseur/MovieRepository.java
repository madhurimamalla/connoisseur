package mmalla.android.com.connoisseur;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import mmalla.android.com.connoisseur.model.Movie;
import mmalla.android.com.connoisseur.recommendations.engine.FirebaseDatabaseRepository;
import mmalla.android.com.connoisseur.recommendations.engine.FBRepository;

/**
 * This serves as the layer between the ViewModels and the FirebaseMoviesRepository
 */
public class MovieRepository extends Observable {

    private static final String TAG = MovieRepository.class.getSimpleName();
    private FBRepository fbRepository;
    private List<Movie> watchlistedMovies;
    private List<Movie> historyMovies;
    private List<Movie> likedMovies;
    private List<Movie> dislikedMovies;
    private List<List<Movie>> allLists;

    public MovieRepository() {
        fbRepository = new FBRepository();
        historyMovies = new ArrayList<>();
        watchlistedMovies = new ArrayList<>();
        likedMovies = new ArrayList<>();
        dislikedMovies = new ArrayList<>();
        allLists = new ArrayList<>();
    }

    public void addListenerOnFirebaseRep() {
        fbRepository.addListener(new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Movie>() {
            @Override
            public void onSuccess(List<Movie> result) {
                if (result.size() == 0) {
                    for (List l : allLists) {
                        l.clear();
                    }
                } else {
                    likedMovies.clear();
                    dislikedMovies.clear();
                    historyMovies.clear();
                    watchlistedMovies.clear();
                    for (Movie movie :
                            result) {
                        if (movie.getmPref() == Movie.PREFERENCE.DISLIKED || movie.getmPref() == Movie.PREFERENCE.LIKED) {
                            if (movie.getmPref() == Movie.PREFERENCE.LIKED) {
                                likedMovies.add(movie);
                            } else {
                                dislikedMovies.add(movie);
                            }
                            historyMovies.add(movie);
                        } else if (movie.getmPref() == Movie.PREFERENCE.WISHLISTED) {
                            watchlistedMovies.add(movie);
                        }
                    }
                    allLists.add(likedMovies);  // 0 : Liked Movies
                    allLists.add(dislikedMovies); // 1 : Disliked Movies
                    allLists.add(historyMovies); // 2 : History Movies
                    allLists.add(watchlistedMovies); // 3 : Watchlisted Movies
                }
                updateMoviesList();
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    public void addObserver(Observer o) {
        super.addObserver(o);
        /*
         * First notification to bring observers up to speed
         * with the current state of the cache.
         */
        if (allLists != null && allLists.size() > 0) {
            new Thread(() -> {
                setChanged();
                notifyObservers(allLists);
            }).start();
        }
    }

    public void updateMoviesList() {
        setChanged();
        notifyObservers(allLists);
    }

    public void removeListenerOnFirebaseRep() {
        fbRepository.removeListener();
    }

}
