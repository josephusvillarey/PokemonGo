package com.josephus.pokemongo.comparators;

import com.pokegoapi.api.pokemon.Pokemon;
import java.util.Comparator;

/**
 * Created by josephus on 07/10/2016.
 */

public class CPComparator implements Comparator<Pokemon> {
  private static final String TAG = CPComparator.class.getSimpleName();

  @Override public int compare(Pokemon pokemon, Pokemon t1) {
    int cpDiff = t1.getCp() - pokemon.getCp();
    if (cpDiff == 0) {
      int numResult = pokemon.getMeta().getNumber() - t1.getMeta().getNumber();
      if (numResult == 0) {
        return t1.getCp() - pokemon.getCp();
      } else {
        return numResult;
      }
    } else {
      return cpDiff;
    }
  }
}
