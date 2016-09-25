package com.josephus.pokemongocompanionapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.josephus.pokemongocompanionapp.PokemonGo;
import com.josephus.pokemongocompanionapp.R;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import okhttp3.OkHttpClient;

public class LoginActivity extends AppCompatActivity {

  private static final String TAG = LoginActivity.class.getSimpleName();

  private static final int LOGIN_ACTIVITY_REQUEST_CODE = 2234;
  private OkHttpClient httpClient;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);

    if (PokemonGo.containsString(PokemonGo.KEY_REFRESH_TOKEN)) {
      Toast.makeText(LoginActivity.this, "Already logged in.", Toast.LENGTH_SHORT).show();
      launchMainActivity();
    } else {
      Toast.makeText(LoginActivity.this, "Not yet logged in", Toast.LENGTH_SHORT).show();
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
          Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
          break;
      }
    }
  }
}
