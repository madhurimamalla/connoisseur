package mmalla.android.com.connoisseur.recommendations.engine;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public abstract class FirebaseDatabaseRepository<Model> {

    protected DatabaseReference databaseReference;
    protected FirebaseDatabaseRepositoryCallback<Model> firebaseCallback;
    private BaseValueEventListener listener;
    private FirebaseMapper mapper;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private final static String USERS = "users";
    private final static String MOVIES = "movies";

    protected abstract String getRootNode();

    public FirebaseDatabaseRepository(FirebaseMapper mapper) {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(USERS).child(mUser.getUid()).child(MOVIES).getRef();
        this.mapper = mapper;
    }

    public void addListener(FirebaseDatabaseRepositoryCallback<Model> firebaseCallback) {
        this.firebaseCallback = firebaseCallback;
        listener = new BaseValueEventListener(mapper, firebaseCallback);
        databaseReference.addValueEventListener(listener);
    }

    public void removeListener() {
        databaseReference.removeEventListener(listener);
    }

    public interface FirebaseDatabaseRepositoryCallback<T> {
        void onSuccess(List<T> result);

        void onError(Exception e);
    }

}
