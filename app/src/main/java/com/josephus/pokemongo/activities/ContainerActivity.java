package com.josephus.pokemongo.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.josephus.pokemongo.PokemonDetailsDialog;
import com.josephus.pokemongo.R;
import com.josephus.pokemongo.fragments.BatchEvolveFragment;
import com.josephus.pokemongo.fragments.BatchTransferFragment;
import com.josephus.pokemongo.interfaces.ItemSelectable;
import com.josephus.pokemongo.interfaces.OnListFragmentInteractionListener;
import com.pokegoapi.api.pokemon.Pokemon;

import butterknife.ButterKnife;

public class ContainerActivity extends FragmentActivity
        implements OnListFragmentInteractionListener {

    private static final String TAG = ContainerActivity.class.getSimpleName();

    public static final String KEY_ACTION = "ACTION";

    public static final String ACTION_TYPE_BATCH_TRANFER = "batch_transfer";
    public static final String ACTION_TYPE_BATCH_EVOLVE = "batch_evolve";

    private String action;
    private Fragment fragment;

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
                fragment = BatchTransferFragment.newInstance(0);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                break;
            case ACTION_TYPE_BATCH_EVOLVE:
                fragment = BatchEvolveFragment.newInstance(0);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                break;
        }
    }

    @Override
    public void onListFragmentInteraction(Pokemon pokemon) {
        PokemonDetailsDialog pokemonDetailsDialog = new PokemonDetailsDialog(ContainerActivity.this, pokemon);
        pokemonDetailsDialog.show();
    }

    @Override
    public void onSecondaryListFragmentInteraction(int value) {
        ((ItemSelectable) fragment).onItemSelected(value);
    }
}
