package mmalla.android.com.connoisseur.moviedbclient;

import android.net.Uri;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import mmalla.android.com.connoisseur.BuildConfig;
import mmalla.android.com.connoisseur.model.Movie;
import timber.log.Timber;

/**
 * A simple client which does the job of creating URLs and executing them
 */
public class MovieDBClient {

    private static final String TAG = MovieDBClient.class.getSimpleName();

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie";

    private static final String SEARCH_URL = "https://api.themoviedb.org/3/search/movie";

    private static final String PATH_POPULAR_PARAM = "popular";

    private static final String SEARCH_PARAM = "similar";

    private final static String QUERY_PARAM = "api_key";

    private final static String LANG_PARAM = "language";

    private final static String PAGE_PARAM = "page";

    private final static String LANG_VALUE = "en_US";

    private final static String PAGE_VALUE = "1";

    private final static String QUERY_STRING_PARAM = "query";

    private final static String INCLUDE_ADULT_PARAM = "include_adult";

    private final static String API_KEY = BuildConfig.THE_MOVIE_DB_API_TOKEN;

    public MovieDBClient() {
    }

    /**
     * General method needed to get response from any API URL built above
     *
     * @param url
     * @return
     * @throws IOException
     */
    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private static URL buildUrl(String path, String page) {

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(QUERY_PARAM, API_KEY)
                .appendQueryParameter(LANG_PARAM, LANG_VALUE)
                .appendQueryParameter(PAGE_PARAM, page)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Timber.v(TAG, "Built URL %s", url);
        return url;
    }

    /**
     * Description: https://api.themoviedb.org/3/movie/19404?api_key=<<api-key>></>&language=en-US
     *
     * @param id
     * @return
     */
    private static URL buildGetMovieDetailsUrl(String id) {

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(id)
                .appendQueryParameter(QUERY_PARAM, API_KEY)
                .appendQueryParameter(LANG_PARAM, LANG_VALUE)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Timber.v(TAG, "Built URL %s", url);
        return url;
    }


    /**
     * Description: https://api.themoviedb.org/3/movie/338952/similar?api_key=<<api-key>></>&language=en-US&page=1
     *
     * @param id
     * @return
     */
    private static URL buildGetSimilarMoviesUrl(String id, int page) {

        Uri builtUri = Uri.parse(BASE_URL).buildUpon().appendPath(id)
                .appendPath(SEARCH_PARAM)
                .appendQueryParameter(QUERY_PARAM, API_KEY)
                .appendQueryParameter(LANG_PARAM, LANG_VALUE)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(page))
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Timber.v(TAG, "Built URL %s", url);

        return url;
    }

    /**
     * Description: This method builds a search query URL
     * This is by default doesn't include adult content
     *
     * @param queryString
     * @return
     */
    private static URL buildSearchQueryUrl(String queryString) {
        Uri builtUri = Uri.parse(SEARCH_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, API_KEY)
                .appendQueryParameter(LANG_PARAM, LANG_VALUE)
                .appendQueryParameter(QUERY_STRING_PARAM, queryString)
                .appendQueryParameter(PAGE_PARAM, PAGE_VALUE)
                .appendQueryParameter(INCLUDE_ADULT_PARAM, "false")
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Timber.v(TAG, "Built search URL %s", url);

        return url;
    }

    /**
     * Method returns details of a movie by id
     *
     * @param id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public Movie getMovieDetailsById(String id) throws MovieDBClientException {

        Movie movieDetails = new Movie();
        URL movieDetailUrl = buildGetMovieDetailsUrl(id);
        Timber.d(TAG, movieDetailUrl);
        try {
            String jsonMovieDetailsResponse = getResponseFromHttpUrl(movieDetailUrl);
            movieDetails = MovieDetailsJsonUtils.getMovieInformationFromJson(jsonMovieDetailsResponse);
        } catch (IOException | JSONException e) {
            throw new MovieDBClientException(e);
        }

        return movieDetails;
    }

    /**
     * Method returns all popular movies
     *
     * @return
     * @throws MovieDBClientException
     */
    private List<Movie> getPopularMovies() throws MovieDBClientException {
        List<Movie> movieList = null;

        int randomNumber = getRandomNumber(1, 100);

        URL moviesListUrl = buildUrl(PATH_POPULAR_PARAM, Integer.toString(randomNumber));
        Timber.d(TAG, moviesListUrl);

        try {
            String jsonPopularMoviesResponse = getResponseFromHttpUrl(moviesListUrl);
            return MoviesListJsonUtils.getSimpleMoviesInformationFromJson(jsonPopularMoviesResponse);
        } catch (IOException | JSONException e) {
            throw new MovieDBClientException(e);
        }
    }


    /**
     * Method returns all similar movies compared to the movie id sent as the parameter
     * This similar movie endpoint only returns 20 movies
     *
     * @param id
     * @return
     * @throws MovieDBClientException
     */
    public List<Movie> getSimilarMovies(String id, int page) throws MovieDBClientException {

        URL moviesListUrl = buildGetSimilarMoviesUrl(id, page);
        Timber.d(TAG, moviesListUrl);

        try {
            String jsonSimilarMoviesResponse = getResponseFromHttpUrl(moviesListUrl);
            return MoviesListJsonUtils.getSimpleMoviesInformationFromJson(jsonSimilarMoviesResponse);
        } catch (IOException | JSONException e) {
            throw new MovieDBClientException(e);
        }

    }

    /**
     * @param length
     * @return
     * @throws MovieDBClientException
     */
    public List<Movie> getSomePopularMovies(int length) throws MovieDBClientException {

        List<Movie> movieList = new ArrayList<Movie>();
        movieList = getPopularMovies();
        if (movieList.size() > length) {
            return movieList.subList(0, length - 1);
        } else {
            return movieList;
        }
    }

    /**
     * Get random number between a range
     */
    public int getRandomNumber(int fromNum, int toNum) {

        return new Random().nextInt((toNum - fromNum) + 1) + fromNum;
    }

    /**
     * Description: Method to get a limited number of search results
     * which were in turn fetched from TMDB
     *
     * @param queryStr
     * @param length
     * @return
     * @throws MovieDBClientException
     */
    public List<Movie> getLimitedSearchResults(String queryStr, int length) throws MovieDBClientException {

        List<Movie> movieList = new ArrayList<>();
        movieList = getSearchResults(queryStr);
        if (movieList.size() > length) {
            return movieList.subList(0, length - 1);
        } else {
            return movieList;
        }
    }

    /**
     * Description: Method to get search results based on a query string from TMDB
     *
     * @param queryStr
     * @return
     * @throws MovieDBClientException
     */
    private List<Movie> getSearchResults(String queryStr) throws MovieDBClientException {
        List<Movie> movies = new ArrayList<>();

        URL moviesSearchURL = buildSearchQueryUrl(queryStr);
        try {
            String jsonSearchMoviesResponse = getResponseFromHttpUrl(moviesSearchURL);
            return MoviesListJsonUtils.getSimpleMoviesInformationFromJson(jsonSearchMoviesResponse);
        } catch (IOException | JSONException e) {
            throw new MovieDBClientException(e);
        }
    }
}
