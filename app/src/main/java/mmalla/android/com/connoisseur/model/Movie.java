package mmalla.android.com.connoisseur.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.view.View;

import java.util.List;
import java.util.Observer;

/**
 * @author mmalla
 */

public class Movie implements Parcelable {

    protected Movie(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mPoster = in.readString();
        mReleaseYear = in.readString();
        mRating = in.readString();
        mOverview = in.readString();
        mVoteCount = in.readString();
        mRuntime = in.readString();
        int mPrefTmp = in.readInt();
        mPref = mPrefTmp == -1 ? null : PREFERENCE.values()[mPrefTmp];
        in.readList(mGenres, List.class.getClassLoader());
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mPoster);
        dest.writeString(mReleaseYear);
        dest.writeString(mRating);
        dest.writeString(mOverview);
        dest.writeString(mVoteCount);
        dest.writeString(mRuntime);
        int mPrefTmp = mPref == null ? -1 : mPref.ordinal();
        dest.writeInt(mPrefTmp);
        dest.writeList(mGenres);
    }

    public enum PREFERENCE {
        WISHLISTED, DISLIKED, LIKED, IGNORED
    }

    private String mId;
    private PREFERENCE mPref;
    private String mTitle;
    private String mPoster;
    private String mReleaseYear;
    private String mOverview;
    private String mRating;
    private String mTagline;
    private String mVoteCount;
    private String mRuntime;
    private List<Genre> mGenres;

    public List<Genre> getmGenres() {
        return mGenres;
    }

    public void setmGenres(List<Genre> mGenres) {
        this.mGenres = mGenres;
    }

    public String getmRuntime() {
        return mRuntime;
    }

    public void setmRuntime(String mRuntime) {
        this.mRuntime = mRuntime;
    }

    public String getmVoteCount() {
        return mVoteCount;
    }

    public void setmVoteCount(String mVoteCount) {
        this.mVoteCount = mVoteCount;
    }

    public String getmTagline() {
        return mTagline;
    }

    public void setmTagline(String mTagline) {
        this.mTagline = mTagline;
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

    public Movie() {
    }

    public Movie(String mId, String mTitle) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mPref = PREFERENCE.IGNORED;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public PREFERENCE getmPref() {
        return mPref;
    }

    public void setmPref(PREFERENCE mPref) {
        this.mPref = mPref;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        return mId.equals(movie.mId);
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }
}
