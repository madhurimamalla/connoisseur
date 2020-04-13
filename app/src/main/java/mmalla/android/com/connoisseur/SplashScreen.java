package mmalla.android.com.connoisseur;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import mmalla.android.com.connoisseur.login.EmailPasswordActivity;

public class SplashScreen extends AppCompatActivity {

    /**
     * The splash screen of this app is on for SPLASH_TIMEOUT in ms.
     */
    private static int SPLASH_TIMEOUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashScreen.this, EmailPasswordActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, SPLASH_TIMEOUT);

    }
}
