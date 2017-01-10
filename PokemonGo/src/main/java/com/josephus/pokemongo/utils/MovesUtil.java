package com.josephus.pokemongo.utils;

import com.pokegoapi.api.pokemon.Pokemon;

/**
 * Created by josephus on 22/12/2016.
 */

public class MovesUtil {

    private static final String TAG = MovesUtil.class.getSimpleName();

    public static final String getMove1String(Pokemon pokemon) {
        String move1Raw = pokemon.getMove1().toString().replace("_FAST", "");

        String[] tokens = move1Raw.split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : tokens) {
            sb.append(lowercaseAllExceptFirst(s)).append(" ");
        }
        return sb.toString().trim() + " " + pokemon.getMove1().getNumber();
    }

    public static final String getMove2String(Pokemon pokemon) {
        String move2Raw = pokemon.getMove2().toString();

        String[] tokens = move2Raw.split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : tokens) {
            sb.append(lowercaseAllExceptFirst(s)).append(" ");
        }
        return sb.toString().trim() + " " + pokemon.getMove2().getNumber();
    }

    private static final String lowercaseAllExceptFirst(String s) {
        return s.substring(0, 1).toUpperCase().concat(s.substring(1, s.length()).toLowerCase());
    }

}
