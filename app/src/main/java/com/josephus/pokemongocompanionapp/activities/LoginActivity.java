package com.josephus.pokemongocompanionapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.josephus.pokemongocompanionapp.PokemonGo;
import com.josephus.pokemongocompanionapp.R;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import java.util.concurrent.ExecutionException;
import okhttp3.OkHttpClient;

public class LoginActivity extends AppCompatActivity {

  private static final String TAG = LoginActivity.class.getSimpleName();

  private static final int LOGIN_ACTIVITY_REQUEST_CODE = 2234;
  private OkHttpClient httpClient;
  private String refreshToken;

  @BindView(R.id.activity_login) LinearLayout parent;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);

    if (PokemonGo.containsString(PokemonGo.KEY_REFRESH_TOKEN)) {
      Snackbar.make(parent, "Already logged in.", Snackbar.LENGTH_SHORT).show();
      launchMainActivity();
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
    }
    return null;
  }

  private void launchMainActivity() {

  }

  @OnClick(R.id.googleLogin) public void googleLogin(View view) {
    Intent i = new Intent(LoginActivity.this, GoogleLoginActivity.class);
    i.putExtra("url", getUrl());
    startActivityForResult(i, LOGIN_ACTIVITY_REQUEST_CODE);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == LOGIN_ACTIVITY_REQUEST_CODE) {
      switch (resultCode) {
        case RESULT_OK:
          String key = data.getStringExtra("key");
          try {
            refreshToken = new LoginTask().execute(key).get();
            if (refreshToken != null) {
              Snackbar.make(parent, "Login successful!", Snackbar.LENGTH_SHORT).show();
              PokemonGo.putString(PokemonGo.KEY_REFRESH_TOKEN, refreshToken);
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
          } catch (ExecutionException e) {
            e.printStackTrace();
          }
          break;
      }
    }
  }

  class LoginTask extends AsyncTask<String, Void, String> {
    @Override protected String doInBackground(String... strings) {
      try {
        PokemonGo.provider.login(strings[0]);
        PokemonGo.go = new com.pokegoapi.api.PokemonGo(httpClient);
        PokemonGo.go.login(PokemonGo.provider);

        return PokemonGo.provider.getRefreshToken();
      } catch (LoginFailedException e) {
        e.printStackTrace();
      } catch (RemoteServerException e) {
        e.printStackTrace();
      }
      return null;
    }
  }
}
