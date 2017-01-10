package com.josephus.pokemongo.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.afollestad.materialdialogs.MaterialDialog;
import com.josephus.pokemongo.PokemonGo;
import com.josephus.pokemongo.R;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.exceptions.CaptchaActiveException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.util.hash.HashProvider;
import com.pokegoapi.util.hash.legacy.LegacyHashProvider;
import com.pokegoapi.util.hash.pokehash.PokeHashProvider;

import okhttp3.OkHttpClient;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int LOGIN_ACTIVITY_REQUEST_CODE = 2234;
    private OkHttpClient httpClient;

    private MaterialDialog progressDialog;

    @BindView(R.id.activity_login)
    LinearLayout parent;
    private HashProvider hasher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (PokemonGo.containsString(PokemonGo.KEY_REFRESH_TOKEN)) {
            Snackbar.make(parent, "Already logged in.", Snackbar.LENGTH_SHORT).show();
            new ReloginTask().execute(PokemonGo.getString(PokemonGo.KEY_REFRESH_TOKEN));
        } else {
            Snackbar.make(parent, "Not yet logged in.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private String getUrl() {
        httpClient = new OkHttpClient();
        try {
            PokemonGo.provider = new GoogleUserCredentialProvider(httpClient);
            return GoogleUserCredentialProvider.LOGIN_URL;
        } catch (RemoteServerException e) {
            e.printStackTrace();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        } catch (CaptchaActiveException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void launchMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    @OnClick(R.id.googleLogin)
    public void googleLogin(View view) {
        Intent i = new Intent(LoginActivity.this, GoogleLoginActivity.class);
        i.putExtra("url", getUrl());
        startActivityForResult(i, LOGIN_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_ACTIVITY_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    String key = data.getStringExtra("key");
                    new NewLoginTask().execute(key);
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    class ReloginTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "relogin preex");
            progressDialog = new MaterialDialog.Builder(LoginActivity.this).progress(true, 0)
                    .title("Logging in")
                    .content("Please wait")
                    .cancelable(false)
                    .build();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                httpClient = new OkHttpClient();
                PokemonGo.go = new com.pokegoapi.api.PokemonGo(httpClient);
                hasher = new LegacyHashProvider();
                PokemonGo.go.login(new GoogleUserCredentialProvider(httpClient, strings[0]), hasher);
                return strings[0];
            } catch (RemoteServerException e) {
                e.printStackTrace();
            } catch (LoginFailedException e) {
                e.printStackTrace();
            } catch (CaptchaActiveException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "launching main now");
            persistTokenAndLaunchMain(s);
        }
    }

    class NewLoginTask extends AsyncTask<String, Void, String> {

        MaterialDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new MaterialDialog.Builder(LoginActivity.this).progress(true, 0)
                    .title("Logging in")
                    .content("Please wait")
                    .cancelable(false)
                    .build();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                PokemonGo.provider.login(strings[0]);
                PokemonGo.go = new com.pokegoapi.api.PokemonGo(httpClient);
                PokemonGo.go.login(PokemonGo.provider, hasher);
                return PokemonGo.provider.getRefreshToken();
            } catch (LoginFailedException e) {
                e.printStackTrace();
            } catch (RemoteServerException e) {
                e.printStackTrace();
            } catch (CaptchaActiveException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            persistTokenAndLaunchMain(s);
        }
    }

    private void persistTokenAndLaunchMain(String s) {
        if (s != null) {
            Snackbar.make(parent, "Login successful!", Snackbar.LENGTH_SHORT).show();
            PokemonGo.putString(PokemonGo.KEY_REFRESH_TOKEN, s);
            launchMainActivity();
        } else {
            Log.d(TAG, "persist else");
        }
    }
}
