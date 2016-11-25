package com.josephus.pokemongo.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.josephus.pokemongo.PokemonDetailsDialog;
import com.josephus.pokemongo.R;
import com.josephus.pokemongo.fragments.BatchEvolveFragment;
import com.josephus.pokemongo.fragments.BatchTransferFragment;
import com.pokegoapi.api.pokemon.Pokemon;

import butterknife.ButterKnife;

public class ContainerActivity extends FragmentActivity
        implements BatchTransferFragment.OnListFragmentInteractionListener,
        BatchEvolveFragment.OnListFragmentInteractionListener {

    private static final String TAG = ContainerActivity.class.getSimpleName();

    public static final String KEY_ACTION = "ACTION";

    public static final String ACTION_TYPE_BATCH_TRANFER = "batch_transfer";
    public static final String ACTION_TYPE_BATCH_EVOLVE = "batch_evolve";

    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        ButterKnife.bind(ContainerActivity.this);

        if (getIntent().hasExtra(KEY_ACTION)) {
            action = getIntent().getStringExtra(KEY_ACTION);
        }

        switch (action) {
            case ACTION_TYPE_BATCH_TRANFER:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, BatchTransferFragment.newInstance(0))
                        .commit();
                break;
            case ACTION_TYPE_BATCH_EVOLVE:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, BatchEvolveFragment.newInstance(0))
                        .commit();
                break;
        }
    }

    @Override
    public void onListFragmentInteraction(Pokemon pokemon) {
        PokemonDetailsDialog pokemonDetailsDialog = new PokemonDetailsDialog(ContainerActivity.this, pokemon);
        pokemonDetailsDialog.show();
    }
}
