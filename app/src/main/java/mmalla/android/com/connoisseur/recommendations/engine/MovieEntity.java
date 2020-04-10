package mmalla.android.com.connoisseur.recommendations.engine;

import com.google.firebase.database.IgnoreExtraProperties;

import mmalla.android.com.connoisseur.model.Movie;

@IgnoreExtraProperties
public class MovieEntity {

    private String mId;
    private Movie.PREFERENCE mPref;
    private String mTitle;
    private String mPoster;
    private String mReleaseYear;
    private String mOverview;
    private String mRating;

    public MovieEntity() {
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public Movie.PREFERENCE getmPref() {
        return mPref;
    }

    public void setmPref(Movie.PREFERENCE mPref) {
        this.mPref = mPref;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmPoster() {
        return mPoster;
    }

    public void setmPoster(String mPoster) {
        this.mPoster = mPoster;
    }

    public String getmReleaseYear() {
        return mReleaseYear;
    }

    public void setmReleaseYear(String mReleaseYear) {
        this.mReleaseYear = mReleaseYear;
    }

    public String getmOverview() {
        return mOverview;
    }

    public void setmOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public String getmRating() {
        return mRating;
    }

    public void setmRating(String mRating) {
        this.mRating = mRating;
    }
}
