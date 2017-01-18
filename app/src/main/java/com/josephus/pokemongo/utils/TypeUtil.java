package com.josephus.pokemongo.utils;

import android.util.Log;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.pokemon.PokemonType;
import com.pokegoapi.main.PokemonMeta;

import POGOProtos.Enums.PokemonTypeOuterClass;

/**
 * Created by josephus on 18/01/2017.
 */

public class TypeUtil {

    private static final String TAG = TypeUtil.class.getSimpleName();

    public static final String getPokemonTypeDisplayText(Pokemon pokemon) {
        String type1 = getFormattedPokemonType(pokemon, 1);
        String type2 = getFormattedPokemonType(pokemon, 2);
        if (type2.equalsIgnoreCase("")) {
            return type1;
        } else {
            return type1 + "/" + type2;
        }
    }

    private static final String getFormattedPokemonType(Pokemon pokemon, int index) {
        switch (index) {
            case 1:
                // get type1 move
                return formatTypeText(PokemonMeta.getPokemonSettings(pokemon.getPokemonId()).getType().toString());
            case 2:
                if (PokemonMeta.getPokemonSettings(pokemon.getPokemonId()).getType2() == PokemonTypeOuterClass.PokemonType.POKEMON_TYPE_NONE) {
                    return "";
                } else {
                    return formatTypeText(PokemonMeta.getPokemonSettings(pokemon.getPokemonId()).getType2().toString());
                }
        }

        return null;
    }

    public static String formatTypeText(String rawTypeText) {
        String typeText = rawTypeText.split("_")[2];
        typeText = typeText.toLowerCase();
        typeText = typeText.substring(0, 1).toUpperCase() + typeText.substring(1, typeText.length());
        return typeText;
    }
}
