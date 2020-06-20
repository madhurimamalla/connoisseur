package mmalla.android.com.connoisseur.login;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmalla.android.com.connoisseur.BaseActivity;
import mmalla.android.com.connoisseur.R;
import mmalla.android.com.connoisseur.SplashActivityNew;
import mmalla.android.com.connoisseur.model.User;
import mmalla.android.com.connoisseur.recommendations.engine.DatabaseUtils;
import timber.log.Timber;

public class EmailPasswordActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = EmailPasswordActivity.class.getSimpleName();

    @BindView(R.id.status)
    TextView mStatusTextView;

    @BindView(R.id.newUserMessage)
    TextView newUserMessage;

    @BindView(R.id.fieldEmail_new)
    EditText mEmailField;

    @BindView(R.id.fieldPassword_new)
    EditText mPasswordField;

    @BindView(R.id.spacer)
    Space mSpace;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    /**
     * Creating instance of DatabaseUtils for interacting with DB
     */
    private DatabaseUtils du;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * Set up Timber for logging
         */
        Timber.plant(new Timber.DebugTree());

        /**
         * Setup needed for Butter Knife
         */
        ButterKnife.bind(this);

        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.verifyEmailButton).setOnClickListener(this);
        findViewById(R.id.newcontent_two).setOnClickListener(this);
        findViewById(R.id.new_ui_flow).setOnClickListener(this);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        /**
         * Make sure the password field is hidden and the auto-complete feature of the keyboard isn't visible.
         */
        mPasswordField.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        disableOldUI();
        updateUI(currentUser);

    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        Timber.d(TAG, "createAccount:%s", email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Timber.d(TAG, "Also adding the authenticated user into Firebase Realtime DB");
                            /**
                             * Add the entry into the Firebase Realtime database as the account is getting created
                             * and it's for the first time!
                             */
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            User userObj = new User(firebaseUser.getEmail());
                            du = new DatabaseUtils();
                            du.writeNewUser(mAuth.getCurrentUser().getUid(), userObj);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.w(task.getException(), TAG, "createUserWithEmail:failure");
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        Timber.d(TAG, "signIn:%s", email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.w(task.getException(), TAG, "signInWithEmail:failure");
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]

    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verifyEmailButton).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.verifyEmailButton).setEnabled(true);
                        findViewById(R.id.newcontent_two).setEnabled(false);
                        findViewById(R.id.new_ui_flow).setEnabled(false);
                        disableOldUI();

                        if (task.isSuccessful()) {
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Timber.e(task.getException(), TAG, "sendEmailVerification");
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(getString(R.string.Required_error_content));
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getString(R.string.Required_error_content));
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            newUserMessage.setText(R.string.welcome_back);
            mStatusTextView.setText(user.getEmail());
            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
            mSpace.setVisibility(View.VISIBLE);
            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.newcontent_two).setVisibility(View.VISIBLE);
            findViewById(R.id.newcontent_two).setEnabled(user.isEmailVerified());
            findViewById(R.id.new_ui_flow).setVisibility(View.VISIBLE);
            findViewById(R.id.new_ui_flow).setEnabled(user.isEmailVerified());
            disableOldUI();
            findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
        } else {
            newUserMessage.setVisibility(View.VISIBLE);
            newUserMessage.setText(R.string.welcome);
            mStatusTextView.setText(R.string.new_user_create_account);
            mSpace.setVisibility(View.GONE);
            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
            findViewById(R.id.signedInButtons).setVisibility(View.GONE);
            findViewById(R.id.new_ui_flow).setVisibility(View.GONE);
            disableOldUI();
        }
    }

    private void disableOldUI() {
        findViewById(R.id.newcontent_two).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.emailCreateAccountButton:
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.emailSignInButton:
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.signOutButton:
                signOut();
                break;
            case R.id.verifyEmailButton:
                sendEmailVerification();
                break;
            case R.id.new_ui_flow:
                Intent homeIntent = new Intent(this, SplashActivityNew.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(homeIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                } else {
                    startActivity(homeIntent);
                }
                break;
        }
    }


}
