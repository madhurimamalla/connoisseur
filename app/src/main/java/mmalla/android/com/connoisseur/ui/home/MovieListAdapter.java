package mmalla.android.com.connoisseur.ui.home;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import mmalla.android.com.connoisseur.R;
import mmalla.android.com.connoisseur.model.Movie;
import timber.log.Timber;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    private static final String TAG = MovieListAdapter.class.getSimpleName();

    private Context mContext;
    private List<Movie> mMoviesList = new ArrayList<>();
    private final static String IMAGE_MOVIE_URL = "https://image.tmdb.org/t/p/w780/";
    private final MovieListAdapter.MoviesListOnClickListener mListener;


    public MovieListAdapter(@NonNull Context context, MovieListAdapter.MoviesListOnClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    public interface MoviesListOnClickListener {
        void onClick(Movie movie, int position);
    }

    public void setMovies(List<Movie> movies) {
        Timber.d(TAG + "Setting the movies in the MovieListAdapter... ");
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

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder movieViewHolder, final int i) {
        Movie movie = this.mMoviesList.get(movieViewHolder.getAdapterPosition());

        ImageView movie_thumbnail = (ImageView) movieViewHolder.movie_thumbnail.findViewById(R.id.movie_item_poster);
        Glide.with(mContext.getApplicationContext()).load(IMAGE_MOVIE_URL + movie.getmPoster()).into(movie_thumbnail);

        movie_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(mMoviesList.get(movieViewHolder.getAdapterPosition()), movieViewHolder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        public ImageView movie_thumbnail;

        public MovieViewHolder(View view) {
            super(view);
            movie_thumbnail = (ImageView) view.findViewById(R.id.movie_item_poster);
        }
    }

}
