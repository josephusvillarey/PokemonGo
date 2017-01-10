package com.josephus.pokemongo.utils;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.main.PokemonMeta;

import POGOProtos.Enums.PokemonMoveOuterClass;
import POGOProtos.Settings.Master.MoveSettingsOuterClass;

/**
 * Created by josephus on 22/12/2016.
 */

public class MovesUtil {

    private static final String TAG = MovesUtil.class.getSimpleName();

    public static final String getMove1String(Pokemon pokemon) {

        PokemonMoveOuterClass.PokemonMove quickMove = pokemon.getMove1();
        MoveSettingsOuterClass.MoveSettings moveSettings = PokemonMeta.getMoveSettings(quickMove);

        String move1Raw = pokemon.getMove1().toString().replace("_FAST", "");

        String[] tokens = move1Raw.split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : tokens) {
            sb.append(lowercaseAllExceptFirst(s)).append(" ");
        }
        return sb.toString().trim() + " Power: " + moveSettings.getPower() + " Crit chance: " + moveSettings.getCriticalChance() + " Energy: " + moveSettings.getEnergyDelta();
    }

    public static final String getMove2String(Pokemon pokemon) {

        PokemonMoveOuterClass.PokemonMove specialMove = pokemon.getMove2();
        MoveSettingsOuterClass.MoveSettings moveSettings = PokemonMeta.getMoveSettings(specialMove);

        String move2Raw = pokemon.getMove2().toString();

        String[] tokens = move2Raw.split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : tokens) {
            sb.append(lowercaseAllExceptFirst(s)).append(" ");
        }
        return sb.toString().trim() + " Power: " + moveSettings.getPower() + " Crit chance: " + moveSettings.getCriticalChance() + " Energy: " + moveSettings.getEnergyDelta();
    }

    private static final String lowercaseAllExceptFirst(String s) {
        return s.substring(0, 1).toUpperCase().concat(s.substring(1, s.length()).toLowerCase());
    }

}
