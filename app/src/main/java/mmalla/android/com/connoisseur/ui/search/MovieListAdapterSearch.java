package mmalla.android.com.connoisseur.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmalla.android.com.connoisseur.R;
import mmalla.android.com.connoisseur.model.Movie;
import timber.log.Timber;

public class MovieListAdapterSearch extends RecyclerView.Adapter<MovieListAdapterSearch.MovieViewHolder> {

    private static final String TAG = MovieListAdapterSearch.class.getSimpleName();

    private Context mContext;
    private List<Movie> mMoviesList = new ArrayList<>();
    private final static String IMAGE_MOVIE_URL = "https://image.tmdb.org/t/p/w1280/";
    private final MovieListAdapterSearch.MoviesListOnClickListener mListener;

    public MovieListAdapterSearch(@NonNull Context context, MovieListAdapterSearch.MoviesListOnClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    public interface MoviesListOnClickListener {
        void onClick(int position, Movie movie);
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
                .inflate(R.layout.movie_list_row_search_item, viewGroup, false);

        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder movieViewHolder, final int i) {
        Movie movie = this.mMoviesList.get(movieViewHolder.getAdapterPosition());

        Glide.with(mContext.getApplicationContext()).load(IMAGE_MOVIE_URL
                + movie.getmPoster()).error(R.drawable.ic_404_movie_poster_not_found)
                .into(movieViewHolder.movie_thumbnail);
        movieViewHolder.movieTitle.setText(movie.getmTitle());
        movieViewHolder.movieRatingValue.setText(movie.getmRating());

        movieViewHolder.searchCardItem.setOnClickListener(
                v -> mListener.onClick(movieViewHolder.getAdapterPosition(),
                        mMoviesList.get(movieViewHolder.getAdapterPosition())));

        movieViewHolder.searchCardItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_wide_poster)
        ImageView movie_thumbnail;

        @BindView(R.id.movie_title)
        TextView movieTitle;

        @BindView(R.id.movie_rating_value)
        TextView movieRatingValue;

        @BindView(R.id.card_item_search)
        CardView searchCardItem;

        public MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
