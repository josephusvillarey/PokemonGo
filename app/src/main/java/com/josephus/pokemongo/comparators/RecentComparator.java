package com.josephus.pokemongo.comparators;

import com.pokegoapi.api.pokemon.Pokemon;

import java.util.Comparator;

/**
 * Created by josephus on 07/10/2016.
 */

public class RecentComparator implements Comparator<Pokemon> {

    private static final String TAG = RecentComparator.class.getSimpleName();

    @Override
    public int compare(Pokemon pokemon, Pokemon t1) {
        int compareResult = (int) (pokemon.getCreationTimeMs() - t1.getCreationTimeMs());
        if (compareResult == 0) {
            return t1.getCp() - pokemon.getCp();
        } else {
            return compareResult;
        }
    }
}
