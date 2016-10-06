package com.josephus.pokemongo.activities.options;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.josephus.pokemongo.PokemonGo;
import com.josephus.pokemongo.R;
import com.josephus.pokemongo.activities.options.dummy.DummyContent.DummyItem;
import com.pokegoapi.api.pokemon.Pokemon;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A fragment representing a list of Items.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class BatchTransferFragment extends Fragment {

  private static final String TAG = BatchTransferFragment.class.getSimpleName();

  @BindView(R.id.list) RecyclerView recyclerView;

  private HashSet<Integer> checkedItemsIndex = null;

  // TODO: Customize parameter argument names
  private static final String ARG_COLUMN_COUNT = "column-count";
  // TODO: Customize parameters
  private int mColumnCount = 1;
  private OnListFragmentInteractionListener mListener;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public BatchTransferFragment() {
  }

  // TODO: Customize parameter initialization
  @SuppressWarnings("unused") public static BatchTransferFragment newInstance(int columnCount) {
    BatchTransferFragment fragment = new BatchTransferFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_COLUMN_COUNT, columnCount);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
    }

    checkedItemsIndex = new HashSet<Integer>();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_batchtransfer_list, container, false);
    ButterKnife.bind(this, view);

    // Set the adapter
    if (view instanceof LinearLayout) {
      Context context = view.getContext();
      if (mColumnCount <= 1) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
      } else {
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
      }
      recyclerView.setAdapter(
          new MyBatchTransferRecyclerViewAdapter(PokemonGo.pokemonList, mListener,
              checkedItemsIndex));
    }
    return view;
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnListFragmentInteractionListener) {
      mListener = (OnListFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(
          context.toString() + " must implement OnListFragmentInteractionListener");
    }
  }

  @Override public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @OnClick(R.id.transfer) public void transfer(View view) {
    Log.d(TAG, "transfer");
  }

  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   * <p/>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface OnListFragmentInteractionListener {
    // TODO: Update argument type and name
    void onListFragmentInteraction(Pokemon pokemon);
  }
}
