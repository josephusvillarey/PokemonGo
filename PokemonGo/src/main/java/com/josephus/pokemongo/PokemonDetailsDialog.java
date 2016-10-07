package com.josephus.pokemongo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.pokemon.PokemonType;
import com.pokegoapi.exceptions.NoSuchItemException;
import org.w3c.dom.Text;

/**
 * Created by josephus on 04/10/2016.
 */

public class PokemonDetailsDialog extends Dialog {

  private static final String TAG = PokemonDetailsDialog.class.getSimpleName();

  @BindView(R.id.image) ImageView image;
  @BindView(R.id.is_fav_iv) ImageView isFav;
  @BindView(R.id.name_tv) TextView name;
  @BindView(R.id.cp_tv) TextView cp;
  @BindView(R.id.type_tv) TextView type;
  @BindView(R.id.iv_tv) TextView iv;
  @BindView(R.id.atk_tv) TextView atk;
  @BindView(R.id.def_tv) TextView def;
  @BindView(R.id.sta_tv) TextView sta;
  @BindView(R.id.height_tv) TextView height;
  @BindView(R.id.weight_tv) TextView weight;
  @BindView(R.id.candy_tv) TextView candy;
  @BindView(R.id.maxcp_tv) TextView maxCp;

  private Pokemon pokemon;

  public PokemonDetailsDialog(Context context) {
    super(context);
  }

  public PokemonDetailsDialog(Context context, Pokemon pokemon) {
    super(context);
    getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    setCanceledOnTouchOutside(false);
    this.pokemon = pokemon;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
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

    isFav.setImageResource(pokemon.isFavorite() ? R.drawable.ic_fav_pressed : R.drawable.ic_fav);
    name.setText(pokemon.getPokemonId().toString());
    cp.setText(
        getContext().getString(R.string.cp_text, String.valueOf(pokemon.getProto().getCp())));
    type.setText(
        pokemon.getMeta().getType1().toString() + (pokemon.getMeta().getType2() == PokemonType.NONE
            ? "" : "/" + pokemon.getMeta().getType2().toString()));
    iv.setText(getContext().getString(R.string.iv_text,
        String.format("%.2f", pokemon.getIvInPercentage())));
    atk.setText(
        getContext().getString(R.string.atk_text, pokemon.getProto().getIndividualAttack()));
    def.setText(
        getContext().getString(R.string.def_text, pokemon.getProto().getIndividualDefense()));
    sta.setText(
        getContext().getString(R.string.sta_text, pokemon.getProto().getIndividualStamina()));
    height.setText(getContext().getString(R.string.height_text,
        String.format("%.2f", pokemon.getProto().getHeightM())));
    weight.setText(getContext().getString(R.string.weight_text,
        String.format("%.2f", pokemon.getProto().getWeightKg())));
    candy.setText(getContext().getString(R.string.candy_text, pokemon.getCandy()));
    try {
      maxCp.setText(getContext().getString(R.string.max_cp_text, pokemon.getMaxCpForPlayer()));
    } catch (NoSuchItemException e) {
      maxCp.setText("Not available");
      e.printStackTrace();
    }
  }
}
