package mmalla.android.com.connoisseur.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class User {

    public String emailAddress;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(/*String username,*/ String email) {
        // TODO Add support for username too later
        //this.username = username;
        emailAddress = email;
        List<Movie> movies = new ArrayList<Movie>();
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}

