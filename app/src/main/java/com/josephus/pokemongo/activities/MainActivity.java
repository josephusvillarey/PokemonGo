package com.josephus.pokemongo.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

import com.afollestad.materialdialogs.MaterialDialog;
import com.josephus.pokemongo.PokemonGo;
import com.josephus.pokemongo.R;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PokemonGo.go.getPlayerProfile() == null) {
            clearTokenAndLaunchLogin();
        } else {
            new FetchPokemonTask().execute();
        }
    }

    class FetchPokemonTask extends AsyncTask<Void, Void, Void> {

        MaterialDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = new MaterialDialog.Builder(MainActivity.this).progress(true, 0)
                        .title("Fetching pokemon list")
                        .content("Please wait")
                        .cancelable(false)
                        .build();
            }
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            PokemonGo.pokemonList = PokemonGo.go.getInventories().getPokebank().getPokemons();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    private void clearTokenAndLaunchLogin() {
        PokemonGo.clear();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @OnClick(R.id.batch_evolve)
    public void batchEvolve(View view) {
        Intent i = new Intent(MainActivity.this, ContainerActivity.class);
        i.putExtra(ContainerActivity.KEY_ACTION, ContainerActivity.ACTION_TYPE_BATCH_EVOLVE);
        startActivity(i);
    }

    @OnClick(R.id.batch_transfer)
    public void batchTransfer(View view) {
        Intent i = new Intent(MainActivity.this, ContainerActivity.class);
        i.putExtra(ContainerActivity.KEY_ACTION, ContainerActivity.ACTION_TYPE_BATCH_TRANFER);
        startActivity(i);
    }
}
