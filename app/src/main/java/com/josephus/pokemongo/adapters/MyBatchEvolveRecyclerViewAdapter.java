package com.josephus.pokemongo.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
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
import com.josephus.pokemongo.interfaces.OnListFragmentInteractionListener;
import com.pokegoapi.api.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import POGOProtos.Enums.PokemonFamilyIdOuterClass;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Pokemon} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyBatchEvolveRecyclerViewAdapter
        extends RecyclerView.Adapter<MyBatchEvolveRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = MyBatchTransferRecyclerViewAdapter.class.getSimpleName();

    private final List<Pokemon> mValues;
    private final HashSet<Integer> checkedItemsIndex;
    private final OnListFragmentInteractionListener mListener;
    private HashMap<PokemonFamilyIdOuterClass.PokemonFamilyId, Integer> candyCount;
    private RecyclerView recyclerView;
    private Snackbar snackbar;

    public MyBatchEvolveRecyclerViewAdapter(List<Pokemon> items,
                                            OnListFragmentInteractionListener listener,
                                            HashSet<Integer> checkedItemsIndex) {
        mValues = new ArrayList<Pokemon>();
        candyCount = new HashMap<PokemonFamilyIdOuterClass.PokemonFamilyId, Integer>();
        for (Pokemon item : items) {
            if (item.canEvolve()) {
                if (!candyCount.containsKey(item.getPokemonFamily())) {
                    candyCount.put(item.getPokemonFamily(), item.getCandy());
                }
                if (item.getCandy() >= item.getCandiesToEvolve()) {
                    mValues.add(item);
                }
            }
        }
        this.mListener = listener;
        this.checkedItemsIndex = checkedItemsIndex;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public List<Pokemon> getmValues() {
        return mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_batch_transfer, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bindTo(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Pokemon pokemon;
        LinearLayout parentPanel;
        ImageView image, fav;
        TextView cp_name, iv, atk, def, sta, totalCandies, toEvolve;
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
            totalCandies = (TextView) itemView.findViewById(R.id.total_candies_tv);
            toEvolve = (TextView) itemView.findViewById(R.id.candies_to_evolve);
        }

        public void bindTo(final Pokemon pokemon) {
            this.pokemon = pokemon;
            final Context context = parentPanel.getContext();
            String number = ("000" + String.valueOf(pokemon.getPokemonId().getNumber())).substring(
                    String.valueOf(pokemon.getPokemonId().getNumber()).length());
            int resId = context.getResources()
                    .getIdentifier("image_" + number, "drawable", context.getPackageName());
            image.setImageResource(resId);
            fav.setImageResource(pokemon.isFavorite() ? R.drawable.ic_fav_pressed : R.drawable.ic_fav);
            cp_name.setText(
                    context.getString(R.string.cp_name_text, String.valueOf(pokemon.getCp()),
                            pokemon.getPokemonId().toString()));
            iv.setText(
                    context.getString(R.string.iv_text, String.format("%.2f", pokemon.getIvInPercentage())));
            atk.setText(context.getString(R.string.atk_text, pokemon.getIndividualAttack()));
            def.setText(context.getString(R.string.def_text, pokemon.getIndividualDefense()));
            sta.setText(context.getString(R.string.sta_text, pokemon.getIndividualStamina()));
            totalCandies.setText(context.getString(R.string.total_candies_text, pokemon.getCandy()));
            if (pokemon.getCandiesToEvolve() > 0) {
                toEvolve.setVisibility(View.VISIBLE);
                toEvolve.setText(context.getString(R.string.to_evolve, pokemon.getCandiesToEvolve()));
            } else {
                toEvolve.setVisibility(View.GONE);
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int previousCheckedItemsCount = checkedItemsIndex.size();
                    if (b) {
                        checkedItemsIndex.add(getAdapterPosition());
                        if (previousCheckedItemsCount != checkedItemsIndex.size()) {
                            candyCount.put(mValues.get(getAdapterPosition()).getPokemonFamily(), candyCount.get(mValues.get(getAdapterPosition()).getPokemonFamily()) - mValues.get(getAdapterPosition()).getCandiesToEvolve());
                        }

                    } else {
                        checkedItemsIndex.remove(getAdapterPosition());
                        if (previousCheckedItemsCount != checkedItemsIndex.size()) {
                            candyCount.put(mValues.get(getAdapterPosition()).getPokemonFamily(), candyCount.get(mValues.get(getAdapterPosition()).getPokemonFamily()) + mValues.get(getAdapterPosition()).getCandiesToEvolve());
                        }

                    }

                    if (previousCheckedItemsCount != checkedItemsIndex.size()) {
                        if (snackbar == null) {
                            snackbar = Snackbar.make(recyclerView, context.getString(R.string.remaining_candies_text, pokemon.getPokemonId().toString(), candyCount.get(pokemon.getPokemonFamily())), Snackbar.LENGTH_INDEFINITE);
                        } else {
                            snackbar.setText(context.getString(R.string.remaining_candies_text, pokemon.getPokemonId().toString(), candyCount.get(pokemon.getPokemonFamily())));
                        }
                        if (!snackbar.isShown()) {
                            snackbar.show();
                        }
                    }

                    mListener.onSecondaryListFragmentInteraction(checkedItemsIndex.size());
                }
            });


            checkBox.setChecked(checkedItemsIndex.contains(getAdapterPosition()));

            parentPanel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onListFragmentInteraction(mValues.get(getAdapterPosition()));
                }
            });
        }
    }
}
