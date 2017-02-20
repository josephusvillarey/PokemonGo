package com.josephus.pokemongo.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.josephus.pokemongo.PokemonGo;
import com.josephus.pokemongo.R;
import com.nineoldandroids.animation.Animator;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.CaptchaActiveException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.exceptions.hash.HashException;
import com.pokegoapi.main.PokemonMeta;
import com.pokegoapi.util.hash.HashProvider;
import com.pokegoapi.util.hash.legacy.LegacyHashProvider;

import okhttp3.OkHttpClient;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int LOGIN_ACTIVITY_REQUEST_CODE = 2234;
    private OkHttpClient httpClient;

    private MaterialDialog progressDialog;

    @BindView(R.id.activity_login)
    LinearLayout parent;
    @BindView(R.id.ptc_username)
    EditText ptcUsername;
    @BindView(R.id.ptc_password)
    EditText ptcPassword;
    @BindView(R.id.ptc_login_btn)
    Button ptcLoginBtn;
    private HashProvider hasher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (PokemonGo.containsString(PokemonGo.KEY_REFRESH_TOKEN)) {
            Snackbar.make(parent, "Already logged in.", Snackbar.LENGTH_SHORT).show();
            new ReloginTask().execute(PokemonGo.getString(PokemonGo.KEY_REFRESH_TOKEN));
        } else if (PokemonGo.containsString(PokemonGo.KEY_PTC_USERNAME) && PokemonGo.containsString(PokemonGo.KEY_PTC_PASSWORD)) {
            Snackbar.make(parent, "Signing in as " + PokemonGo.getString(PokemonGo.KEY_PTC_USERNAME), Snackbar.LENGTH_SHORT).show();
            new PtcLoginTask().execute(new Pair<String, String>(PokemonGo.getString(PokemonGo.KEY_PTC_USERNAME), PokemonGo.getString(PokemonGo.KEY_PTC_PASSWORD)));
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

    @OnClick(R.id.ptcLogin)
    public void ptcLogin(View view) {
        // animate show ptc fields

        YoYo.with(Techniques.FadeInDown).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ptcUsername.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(ptcUsername);
        YoYo.with(Techniques.FadeInDown).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ptcPassword.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(ptcPassword);
        YoYo.with(Techniques.Bounce.FadeInDown).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ptcLoginBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(ptcLoginBtn);
    }

    @OnClick(R.id.ptc_login_btn)
    public void ptcLoginBtn(View view) {
        new PtcLoginTask().execute(new Pair<String, String>(ptcUsername.getText().toString(), ptcPassword.getText().toString()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_ACTIVITY_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    String key = data.getStringExtra("key");
                    new NewGoogleLoginTask().execute(key);
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
            } catch (HashException e) {
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

    class PtcLoginTask extends AsyncTask<Pair<String, String>, Void, Pair<String, String>> {

        @Override
        protected void onPreExecute() {
            progressDialog = new MaterialDialog.Builder(LoginActivity.this).progress(true, 0).title("Logging in").content("Please wait").cancelable(false).build();
            progressDialog.show();
        }

        @Override
        protected Pair<String, String> doInBackground(Pair<String, String>... params) {
            try {
                httpClient = new OkHttpClient();
                PokemonGo.go = new com.pokegoapi.api.PokemonGo(httpClient);
                hasher = new LegacyHashProvider();
                PokemonGo.go.login(new PtcCredentialProvider(httpClient, params[0].first, params[0].second), hasher);
            } catch (RemoteServerException e) {
                e.printStackTrace();
            } catch (CaptchaActiveException e) {
                e.printStackTrace();
            } catch (LoginFailedException e) {
                e.printStackTrace();
            } catch (HashException e) {
                e.printStackTrace();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(Pair<String, String> pair) {
            super.onPostExecute(pair);
            persistUserPassAndLaunchMain(pair);
        }
    }

    class NewGoogleLoginTask extends AsyncTask<String, Void, String> {

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
                hasher = new LegacyHashProvider();
                PokemonGo.go.login(PokemonGo.provider, hasher);
                return PokemonGo.provider.getRefreshToken();
            } catch (LoginFailedException e) {
                e.printStackTrace();
            } catch (RemoteServerException e) {
                e.printStackTrace();
            } catch (CaptchaActiveException e) {
                e.printStackTrace();
            } catch (HashException e) {
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

    private void persistUserPassAndLaunchMain(Pair<String, String> pair) {
        if (pair != null) {
            Snackbar.make(parent, "Login successful!", Snackbar.LENGTH_SHORT).show();
            PokemonGo.putString(PokemonGo.KEY_PTC_USERNAME, pair.first);
            PokemonGo.putString(PokemonGo.KEY_PTC_PASSWORD, pair.second);
            launchMainActivity();
        }
    }

    private void persistTokenAndLaunchMain(String s) {
        if (s != null) {
            Snackbar.make(parent, "Login successful!", Snackbar.LENGTH_SHORT).show();
            PokemonGo.putString(PokemonGo.KEY_REFRESH_TOKEN, s);
            launchMainActivity();
        }
    }
}
