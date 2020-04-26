package mmalla.android.com.connoisseur.moviedbclient;

/**
 * @author mmalla
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mmalla.android.com.connoisseur.model.Genre;
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

        /**
         * The tagline of the movie
         */
        final String PM_TAGLINE = "tagline";

        /**
         * The number of votes a movie has received
         */
        final String PM_VOTE_COUNT = "vote_count";

        /**
         * Runtime of the movie
         */
        final String PM_RUNTIME = "runtime";

        /**
         * The list of genres
         */
        final String PM_GENRES = "genres";

        /**
         * Genre name
         */
        final String PM_GENRE = "name";

        /**
         * Genre id
         */
        final String PM_GENRE_ID = "id";


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
        String runTime = movieJson.getString(PM_RUNTIME);

        JSONArray genreArray = movieJson.getJSONArray(PM_GENRES);
        if (genreArray.length() > 0) {
            List<Genre> genres = new ArrayList<>();
            for (int i = 0; i < genreArray.length(); i++) {
                Genre genre = new Genre(genreArray.getJSONObject(i).getString(PM_GENRE_ID),
                        genreArray.getJSONObject(i).getString(PM_GENRE));
                genres.add(genre);
            }
            parsedMovieDetails.setmGenres(genres);
        }

        parsedMovieDetails.setmTitle(movieTitle);
        parsedMovieDetails.setmId(movieId);
        parsedMovieDetails.setmReleaseYear(releaseDate);
        parsedMovieDetails.setmPoster(movieImgPath);
        parsedMovieDetails.setmOverview(overview);
        parsedMovieDetails.setmRating(userRating);
        parsedMovieDetails.setmTagline(tagline);
        parsedMovieDetails.setmVoteCount(voteCount);
        parsedMovieDetails.setmRuntime(runTime);
        return parsedMovieDetails;
    }
}
