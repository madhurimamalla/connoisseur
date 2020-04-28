package mmalla.android.com.connoisseur.recommendations.engine;

import mmalla.android.com.connoisseur.model.Movie;

public class FBRepository extends FirebaseDatabaseRepository<Movie> {

    public FBRepository() {
        super(new MovieMapper());
    }

    @Override
    protected String getRootNode() {
        return "MOVIES";
    }
}
