package mmalla.android.com.connoisseur.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmalla.android.com.connoisseur.R;
import mmalla.android.com.connoisseur.model.Movie;
import timber.log.Timber;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    private static final String TAG = MovieListAdapter.class.getSimpleName();

    private Context mContext;
    private List<Movie> mMoviesList = new ArrayList<>();
    private final static String IMAGE_MOVIE_URL = "https://image.tmdb.org/t/p/w780";
    private final MovieListAdapter.MoviesListOnClickListener mListener;
    private String typeOfList;


    public MovieListAdapter(@NonNull Context context, MovieListAdapter.MoviesListOnClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setTypeOfList(String bundleTypeStr) {
        this.typeOfList = bundleTypeStr;
    }

    public interface MoviesListOnClickListener {
        void onClick(Movie movie, int position);
    }

    public void setMovies(List<Movie> movies) {
        Timber.d("%sSetting the movies in the MovieListAdapter... ", TAG);
        this.mMoviesList = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_list_row, viewGroup, false);

        return new MovieViewHolder(itemView);
    }

    @SuppressLint({"RestrictedApi", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder movieViewHolder, final int i) {
        Movie movie = this.mMoviesList.get(movieViewHolder.getAdapterPosition());

        Glide.with(mContext.getApplicationContext()).load(IMAGE_MOVIE_URL + movie.getmPoster()).error(R.drawable.ic_404_movie_poster_not_found).into(movieViewHolder.movie_thumbnail);

        movieViewHolder.movie_thumbnail.setOnClickListener(view -> mListener.onClick(mMoviesList.get(movieViewHolder.getAdapterPosition()), movieViewHolder.getAdapterPosition()));

        if (typeOfList.equals("HISTORY")) {
            if (movieViewHolder.moviePrefOverlay != null) {
                if (movie.getmPref() == Movie.PREFERENCE.LIKED) {
                    movieViewHolder.moviePrefOverlay.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.likeMovieGreenColor));
                    Glide.with(mContext)
                            .load(R.drawable.ic_thumb_up_white_36dp).into(movieViewHolder.moviePrefOverlay);
                } else if (movie.getmPref() == Movie.PREFERENCE.DISLIKED) {
                    movieViewHolder.moviePrefOverlay.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.dislikeMovieRedColor));
                    Glide.with(mContext)
                            .load(R.drawable.ic_thumb_down_white_36dp).into(movieViewHolder.moviePrefOverlay);
                }
            }
            movieViewHolder.moviePrefOverlay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_item_poster)
        ImageView movie_thumbnail;

        /**
         * To be shown only if the list is a History tab
         */
        @Nullable
        @BindView(R.id.movie_preference_overlay)
        FloatingActionButton moviePrefOverlay;

        public MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
