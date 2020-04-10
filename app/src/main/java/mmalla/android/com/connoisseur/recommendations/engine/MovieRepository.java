package mmalla.android.com.connoisseur.recommendations.engine;

import mmalla.android.com.connoisseur.model.Movie;

public class MovieRepository extends FirebaseDatabaseRepository<Movie> {

    public MovieRepository() {
        super(new MovieMapper());
    }

    @Override
    protected String getRootNode() {
        return "MOVIES";
    }
}
