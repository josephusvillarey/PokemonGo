package com.josephus.pokemongo.utils;

import android.util.Log;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.main.PokemonMeta;

import POGOProtos.Enums.PokemonMoveOuterClass;
import POGOProtos.Settings.Master.MoveSettingsOuterClass;

/**
 * Created by josephus on 22/12/2016.
 */

public class MoveUtil {

    private static final String TAG = MoveUtil.class.getSimpleName();

    public static final int getMove1Power(Pokemon pokemon) {
        MoveSettingsOuterClass.MoveSettings moveSettings = PokemonMeta.getMoveSettings(pokemon.getMove1());

        return (int) moveSettings.getPower();
    }

    public static final int getMove2Power(Pokemon pokemon) {
        MoveSettingsOuterClass.MoveSettings moveSettings = PokemonMeta.getMoveSettings(pokemon.getMove2());
        return (int) moveSettings.getPower();
    }

    public static final String getMove1String(Pokemon pokemon) {


        PokemonMoveOuterClass.PokemonMove quickMove = pokemon.getMove1();
        MoveSettingsOuterClass.MoveSettings moveSettings = PokemonMeta.getMoveSettings(quickMove);

        Log.d(TAG, "---move 1 details---");
        Log.d(TAG, "accuracy chance: " + moveSettings.getAccuracyChance());
        Log.d(TAG, "type: " + moveSettings.getPokemonType().toString());
        Log.d(TAG, "crit chance: " + moveSettings.getCriticalChance());
        Log.d(TAG, "duration: " + moveSettings.getDurationMs());
        Log.d(TAG, "energy delta: " + moveSettings.getEnergyDelta());
        Log.d(TAG, "heal scalar: " + moveSettings.getHealScalar());
        Log.d(TAG, "power: " + moveSettings.getPower());
        Log.d(TAG, "vfxname: " + moveSettings.getVfxName());

        String move1Raw = moveSettings.getVfxName().toUpperCase().replace("_FAST", "");

        String[] tokens = move1Raw.split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : tokens) {
            sb.append(lowercaseAllExceptFirst(s)).append(" ");
        }
        return sb.toString().trim() + " (" + TypeUtil.formatTypeText(moveSettings.getPokemonType().toString() + ")");
    }

    public static final String getMove2String(Pokemon pokemon) {

        PokemonMoveOuterClass.PokemonMove specialMove = pokemon.getMove2();
        MoveSettingsOuterClass.MoveSettings moveSettings = PokemonMeta.getMoveSettings(specialMove);

        Log.d(TAG, "---move 2 details---");
        Log.d(TAG, "accuracy chance: " + moveSettings.getAccuracyChance());
        Log.d(TAG, "type: " + moveSettings.getPokemonType().toString());
        Log.d(TAG, "crit chance: " + moveSettings.getCriticalChance());
        Log.d(TAG, "duration: " + moveSettings.getDurationMs());
        Log.d(TAG, "energy delta: " + moveSettings.getEnergyDelta());
        Log.d(TAG, "heal scalar: " + moveSettings.getHealScalar());
        Log.d(TAG, "power: " + moveSettings.getPower());
        Log.d(TAG, "vfxname: " + moveSettings.getVfxName());

        String move2Raw = moveSettings.getVfxName().toUpperCase();

        String[] tokens = move2Raw.split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : tokens) {
            sb.append(lowercaseAllExceptFirst(s)).append(" ");
        }
        return sb.toString().trim() + " (" + TypeUtil.formatTypeText(moveSettings.getPokemonType().toString() + ")");
    }

    private static final String lowercaseAllExceptFirst(String s) {
        return s.substring(0, 1).toUpperCase().concat(s.substring(1, s.length()).toLowerCase());
    }

}
