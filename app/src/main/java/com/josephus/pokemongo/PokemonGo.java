package com.josephus.pokemongo;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.auth.GoogleUserCredentialProvider;

import io.fabric.sdk.android.Fabric;

import java.util.List;

/**
 * Created by josephus on 26/09/2016.
 */

public class PokemonGo extends MultiDexApplication {

    private static final String TAG = PokemonGo.class.getSimpleName();

    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_PTC_USERNAME = "ptc_username";
    public static final String KEY_PTC_PASSWORD = "ptc_password";
    public static com.pokegoapi.api.PokemonGo go;
    public static GoogleUserCredentialProvider provider;
    public static List<Pokemon> pokemonList;
    private static SharedPreferences sharedPreferences;
    private boolean isBatchEvolveServiceRunning, isBatchTransferServiceRunning;

    public boolean isBatchEvolveServiceRunning() {
        return isBatchEvolveServiceRunning;
    }

    public void setBatchEvolveServiceRunning(boolean batchEvolveServiceRunning) {
        isBatchEvolveServiceRunning = batchEvolveServiceRunning;
    }

    public boolean isBatchTransferServiceRunning() {
        return isBatchTransferServiceRunning;
    }

    public void setBatchTransferServiceRunning(boolean batchTransferServiceRunning) {
        isBatchTransferServiceRunning = batchTransferServiceRunning;
    }

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

    public static void clear() {
        sharedPreferences.edit().clear().commit();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
}