package com.josephus.pokemongo.comparators;

import com.pokegoapi.api.pokemon.Pokemon;
import java.util.Comparator;

/**
 * Created by josephus on 07/10/2016.
 */

public class HPComparator implements Comparator<Pokemon>{
  @Override public int compare(Pokemon pokemon, Pokemon t1) {
    int hpDiff = t1.getStaminaInPercentage() - pokemon.getStaminaInPercentage();
    if (hpDiff == 0) {
      return t1.getCp() - pokemon.getCp();
    } else {
      return hpDiff;
    }
  }
}
