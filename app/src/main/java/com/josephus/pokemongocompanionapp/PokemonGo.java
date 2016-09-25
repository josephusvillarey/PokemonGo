package com.josephus.pokemongocompanionapp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import java.util.List;

/**
 * Created by josephus on 26/09/2016.
 */

public class PokemonGo extends MultiDexApplication {

  private static final String TAG = PokemonGo.class.getSimpleName();

  public static final String KEY_REFRESH_TOKEN = "refresh_token";
  public static com.pokegoapi.api.PokemonGo go;
  public static GoogleUserCredentialProvider provider;
  public static List<Pokemon> pokemonList;
  private static SharedPreferences sharedPreferences;

  public static boolean containsString(String key) {
    return sharedPreferences.contains(key) && sharedPreferences.getString(key, null) != null;
  }

  public static String getString(String key) {
    if (containsString(key)) {
      return sharedPreferences.getString(key, null);
    } else {
      return null;
    }
  }

  public static void putString(String key, String string) {
    sharedPreferences.edit().putString(key, string).commit();
  }

  @Override public void onCreate() {
    super.onCreate();
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
  }
}
