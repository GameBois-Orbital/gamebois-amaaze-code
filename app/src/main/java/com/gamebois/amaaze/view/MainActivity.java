package com.gamebois.amaaze.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.gamebois.amaaze.R;
import com.gamebois.amaaze.view.createmaze.MazifyActivity;
import com.gamebois.amaaze.view.viewmaze.ViewMazeActivity;
import com.gamebois.amaaze.viewmodel.MainActivityViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 2323;
    private Toolbar mToolbar;
    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Sets up a toolbar at the top of the screen
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        //View model
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
    }


    @Override
    protected void onStart() {
        super.onStart();
        //Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }
    }

    private void startSignIn() {
        //Enables sign in through email and Google
        AuthUI.IdpConfig googleIdp = new AuthUI.IdpConfig.GoogleBuilder()
                .setSignInOptions(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        Intent signInIntent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        googleIdp
                ))
                .setIsSmartLockEnabled(true)
                .build();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);
    }

    private boolean shouldStartSignIn() {
        //Checks if user has been taken to the sign in activity or is already signed in
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);

            if (resultCode != RESULT_OK && shouldStartSignIn()) {
                startSignIn();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectViewMazes(View v) {
        Intent viewMazeIntent = new Intent(this, ViewMazeActivity.class);
        startActivity(viewMazeIntent);
    }

    private void showTodoToast() {
        Toast.makeText(this, "TODO: Implement", Toast.LENGTH_SHORT).show();
    }

    public void launchCameraCaptureActivity(View view) {
        Log.d(LOG_TAG, "Create maze click");
        Intent intent = new Intent(this, MazifyActivity.class);
        startActivity(intent);
    }

}
