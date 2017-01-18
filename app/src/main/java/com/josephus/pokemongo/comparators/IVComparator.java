package com.josephus.pokemongo.comparators;

import com.pokegoapi.api.pokemon.Pokemon;
import java.util.Comparator;

/**
 * Created by josephus on 07/10/2016.
 */

public class IVComparator implements Comparator<Pokemon> {
  @Override public int compare(Pokemon pokemon, Pokemon t1) {
    double ivDiff = t1.getIvInPercentage() - pokemon.getIvInPercentage();
    if (ivDiff == 0) {
      return new CPComparator().compare(pokemon, t1);
    } else {
      return (int) ivDiff;
    }
  }
}
