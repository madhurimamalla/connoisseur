package mmalla.android.com.connoisseur.moviedbclient;

/**
 * @author mmalla
 */

import org.json.JSONException;
import org.json.JSONObject;

import mmalla.android.com.connoisseur.model.Movie;

/**
 * Description: This class is created to assist in parsing and understanding the JSON
 * response given by the GET movie details of TMDB
 * This class is used to parse through the API which gives the movie details
 */
class MovieDetailsJsonUtils {

    public static Movie getMovieInformationFromJson(String moviesJsonStr) throws JSONException {

        /**
         * Movie release date
         */
        final String PM_MOVIE_RELEASE_DATE = "release_date";

        /**
         * Movie id
         */
        final String PM_MOVIE_ID = "id";

        /**
         * Movie title
         */
        final String PM_MOVIE_TITLE = "original_title";

        /**
         * Image path
         */
        final String PM_IMG_PATH = "poster_path";

        /**
         * Overview of the movie
         */
        final String PM_OVERVIEW = "overview";

        /**
         * user rating for the movie
         */
        final String PM_VOTE_AVG = "vote_average";

        final String PM_TAGLINE = "tagline";

        final String PM_VOTE_COUNT = "vote_count";

        Movie parsedMovieDetails = new Movie();

        JSONObject movieJson = new JSONObject(moviesJsonStr);

        String releaseDate = movieJson.getString(PM_MOVIE_RELEASE_DATE);
        String overview = movieJson.getString(PM_OVERVIEW);
        String movieTitle = movieJson.getString(PM_MOVIE_TITLE);
        String movieImgPath = movieJson.getString(PM_IMG_PATH);
        String userRating = movieJson.getString(PM_VOTE_AVG);
        String movieId = movieJson.getString(PM_MOVIE_ID);
        String tagline = movieJson.getString(PM_TAGLINE);
        String voteCount = movieJson.getString(PM_VOTE_COUNT);

        parsedMovieDetails.setmTitle(movieTitle);
        parsedMovieDetails.setmId(movieId);
        parsedMovieDetails.setmReleaseYear(releaseDate);
        parsedMovieDetails.setmPoster(movieImgPath);
        parsedMovieDetails.setmOverview(overview);
        parsedMovieDetails.setmRating(userRating);
        parsedMovieDetails.setmTagline(tagline);
        parsedMovieDetails.setmVoteCount(voteCount);

        return parsedMovieDetails;
    }
}
