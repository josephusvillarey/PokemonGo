package com.josephus.pokemongo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.josephus.pokemongo.R;
import com.josephus.pokemongo.fragments.BatchTransferFragment;
import com.pokegoapi.api.pokemon.Pokemon;
import java.util.HashSet;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Pokemon} and makes a call to the
 * specified {@link BatchTransferFragment.OnListFragmentInteractionListener}.
 */
public class MyBatchTransferRecyclerViewAdapter
    extends RecyclerView.Adapter<MyBatchTransferRecyclerViewAdapter.ViewHolder> {

  private static final String TAG = MyBatchTransferRecyclerViewAdapter.class.getSimpleName();

  private final List<Pokemon> mValues;
  private final HashSet<Integer> checkedItemsIndex;
  private final BatchTransferFragment.OnListFragmentInteractionListener mListener;

  public MyBatchTransferRecyclerViewAdapter(List<Pokemon> items,
      BatchTransferFragment.OnListFragmentInteractionListener listener,
      HashSet<Integer> checkedItemsIndex) {
    this.mValues = items;
    this.mListener = listener;
    this.checkedItemsIndex = checkedItemsIndex;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_batch_transfer, parent, false));
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    holder.bindTo(mValues.get(position));
  }

  @Override public int getItemCount() {
    return mValues.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    Pokemon pokemon;
    LinearLayout parentPanel;
    ImageView image, fav;
    TextView cp_name, iv, atk, def, sta;
    CheckBox checkBox;

    public ViewHolder(View itemView) {
      super(itemView);
      parentPanel = (LinearLayout) itemView;
      image = (ImageView) itemView.findViewById(R.id.image);
      fav = (ImageView) itemView.findViewById(R.id.fav_icon);
      cp_name = (TextView) itemView.findViewById(R.id.cp_name_tv);
      iv = (TextView) itemView.findViewById(R.id.iv);
      atk = (TextView) itemView.findViewById(R.id.atk);
      def = (TextView) itemView.findViewById(R.id.def);
      sta = (TextView) itemView.findViewById(R.id.sta);
      checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
    }

    public void bindTo(final Pokemon pokemon) {
      this.pokemon = pokemon;
      Context context = parentPanel.getContext();
      String number = ("000" + String.valueOf(pokemon.getPokemonId().getNumber())).substring(
          String.valueOf(pokemon.getPokemonId().getNumber()).length());
      int resId = context.getResources()
          .getIdentifier("image_" + number, "drawable", context.getPackageName());
      image.setImageResource(resId);
      fav.setImageResource(pokemon.isFavorite() ? R.drawable.ic_fav_pressed : R.drawable.ic_fav);
      cp_name.setText(
          context.getString(R.string.cp_name_text, String.valueOf(pokemon.getProto().getCp()),
              pokemon.getPokemonId().toString()));
      iv.setText(
          context.getString(R.string.iv_text, String.format("%.2f", pokemon.getIvInPercentage())));
      atk.setText(context.getString(R.string.atk_text, pokemon.getProto().getIndividualAttack()));
      def.setText(context.getString(R.string.def_text, pokemon.getProto().getIndividualDefense()));
      sta.setText(context.getString(R.string.sta_text, pokemon.getProto().getIndividualStamina()));

      checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
          if (b) {
            checkedItemsIndex.add(getAdapterPosition());
          } else {
            checkedItemsIndex.remove(getAdapterPosition());
          }
        }
      });
      checkBox.setChecked(checkedItemsIndex.contains(getAdapterPosition()));

      parentPanel.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          mListener.onListFragmentInteraction(mValues.get(getAdapterPosition()));
        }
      });
    }
  }
}
