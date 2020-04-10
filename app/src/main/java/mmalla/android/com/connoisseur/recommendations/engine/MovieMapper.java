package mmalla.android.com.connoisseur.recommendations.engine;

import mmalla.android.com.connoisseur.model.Movie;

public class MovieMapper extends FirebaseMapper<MovieEntity, Movie> {

    @Override
    public Movie map(MovieEntity movieEntity) {
        Movie movie = new Movie();
        movie.setmTitle(movieEntity.getmTitle());
        movie.setmReleaseYear(movieEntity.getmReleaseYear());
        movie.setmPref(movieEntity.getmPref());
        movie.setmRating(movieEntity.getmRating());
        movie.setmOverview(movieEntity.getmOverview());
        movie.setmPoster(movieEntity.getmPoster());
        movie.setmId(movieEntity.getmId());
        return movie;
    }
}
