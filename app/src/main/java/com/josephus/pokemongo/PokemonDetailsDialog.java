package com.josephus.pokemongo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.josephus.pokemongo.utils.MoveUtil;
import com.josephus.pokemongo.utils.TypeUtil;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.CaptchaActiveException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.main.PokemonMeta;

import POGOProtos.Enums.PokemonMoveOuterClass;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by josephus on 04/10/2016.
 */

public class PokemonDetailsDialog extends Dialog {

    private static final String TAG = PokemonDetailsDialog.class.getSimpleName();

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.is_fav_iv)
    ImageView isFav;
    @BindView(R.id.name_tv)
    TextView name;
    @BindView(R.id.type_tv)
    TextView type;
    @BindView(R.id.iv_tv)
    TextView iv;
    @BindView(R.id.atk_tv)
    TextView atk;
    @BindView(R.id.def_tv)
    TextView def;
    @BindView(R.id.sta_tv)
    TextView sta;
    @BindView(R.id.height_tv)
    TextView height;
    @BindView(R.id.weight_tv)
    TextView weight;
    @BindView(R.id.candy_tv)
    TextView candy;
    @BindView(R.id.move1_tv)
    TextView move1;
    @BindView(R.id.move2_tv)
    TextView move2;
    @BindView(R.id.move1_pow_tv)
    TextView move1Pow;
    @BindView(R.id.move2_pow_tv)
    TextView move2Pow;
    @BindView(R.id.cp_pb)
    ProgressBar cpProgressBar;
    @BindView(R.id.current_max_cp_tv)
    TextView currentMaxCp;

    private Pokemon pokemon;

    public PokemonDetailsDialog(Context context, Pokemon pokemon) {
        super(context);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.pokemon = pokemon;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pokemon_details);
        ButterKnife.bind(this);

        initDetails();
    }

    private void initDetails() {

        String number = ("000" + String.valueOf(pokemon.getPokemonId().getNumber())).substring(
                String.valueOf(pokemon.getPokemonId().getNumber()).length());

        int resId = getContext().getResources()
                .getIdentifier("image_" + number, "drawable", getContext().getPackageName());
        image.setImageResource(resId);

        setFavoriteDisplay();
//        isFav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    pokemon.setFavoritePokemon(!pokemon.isFavorite());
//                    setFavoriteDisplay();
//                } catch (LoginFailedException e) {
//                    e.printStackTrace();
//                } catch (CaptchaActiveException e) {
//                    e.printStackTrace();
//                } catch (RemoteServerException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        name.setText(pokemon.getPokemonId().toString());

        try {
            currentMaxCp.setText(pokemon.getCp() + "/" + pokemon.getMaxCpForPlayer() + " CP");
            cpProgressBar.setMax(pokemon.getMaxCpForPlayer());
            cpProgressBar.setProgress(pokemon.getCp());
        } catch (NoSuchItemException e) {
            e.printStackTrace();
        }

        type.setText(TypeUtil.getPokemonTypeDisplayText(pokemon));
        iv.setText(getContext().getString(R.string.iv_text,
                String.format("%.2f", pokemon.getIvInPercentage())));
        atk.setText(
                getContext().getString(R.string.atk_text, pokemon.getIndividualAttack()));
        def.setText(
                getContext().getString(R.string.def_text, pokemon.getIndividualDefense()));
        sta.setText(
                getContext().getString(R.string.sta_text, pokemon.getIndividualStamina()));
        height.setText(getContext().getString(R.string.height_text,
                String.format("%.2f", pokemon.getHeightM())));
        weight.setText(getContext().getString(R.string.weight_text,
                String.format("%.2f", pokemon.getWeightKg())));
        candy.setText(getContext().getString(R.string.candy_text, pokemon.getCandy()));

        move1.setText(MoveUtil.getMove1String(pokemon));
        move2.setText(MoveUtil.getMove2String(pokemon));
        move1Pow.setText(String.valueOf(MoveUtil.getMove1Power(pokemon)));
        move2Pow.setText(String.valueOf(MoveUtil.getMove2Power(pokemon)));
    }

    private void setFavoriteDisplay() {
        isFav.setImageResource(pokemon.isFavorite() ? R.drawable.ic_fav_pressed : R.drawable.ic_fav);
    }
}
