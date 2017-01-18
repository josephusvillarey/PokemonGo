package com.josephus.pokemongo.comparators;

import com.pokegoapi.api.pokemon.Pokemon;
import java.util.Comparator;

/**
 * Created by josephus on 07/10/2016.
 */

public class FavoriteComparator implements Comparator<Pokemon>{
  private static final String TAG = FavoriteComparator.class.getSimpleName();

  @Override public int compare(Pokemon pokemon, Pokemon t1) {
    int favResult =
        Boolean.valueOf(t1.isFavorite()).compareTo(Boolean.valueOf(pokemon.isFavorite()));
    if (favResult == 0) {
      return t1.getCp() - pokemon.getCp();
    } else {
      return favResult;
    }
  }
}
