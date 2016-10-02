package com.josephus.pokemongocompanionapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.josephus.pokemongocompanionapp.PokemonGo;
import com.josephus.pokemongocompanionapp.R;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    try {
      if (PokemonGo.provider == null) {
        PokemonGo.provider = new GoogleUserCredentialProvider(new OkHttpClient());
      }
      if (PokemonGo.provider.isTokenIdExpired()) {
        clearTokenAndLaunchLogin();
      }
    } catch (LoginFailedException e) {
      e.printStackTrace();
    } catch (RemoteServerException e) {
      e.printStackTrace();
    }
  }

  private void clearTokenAndLaunchLogin() {
    PokemonGo.clear();
    startActivity(new Intent(MainActivity.this, LoginActivity.class));
    finish();
  }
}
