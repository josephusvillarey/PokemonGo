package com.josephus.pokemongo.activities.options;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.josephus.pokemongo.R;
import com.josephus.pokemongo.activities.options.dummy.DummyContent.DummyItem;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link BatchEvolveFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyBatchEvolveRecyclerViewAdapter
    extends RecyclerView.Adapter<MyBatchEvolveRecyclerViewAdapter.ViewHolder> {

  private final List<DummyItem> mValues;
  private final BatchEvolveFragment.OnListFragmentInteractionListener mListener;

  public MyBatchEvolveRecyclerViewAdapter(List<DummyItem> items,
      BatchEvolveFragment.OnListFragmentInteractionListener listener) {
    mValues = items;
    mListener = listener;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_batch_evolve, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(final ViewHolder holder, int position) {
  }

  @Override public int getItemCount() {
    return mValues.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public ViewHolder(View itemView) {
      super(itemView);
    }
  }
}
