package com.josephus.pokemongo.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.josephus.pokemongo.PokemonGo;
import com.josephus.pokemongo.R;
import com.josephus.pokemongo.adapters.MyBatchEvolveRecyclerViewAdapter;
import com.josephus.pokemongo.adapters.MyBatchTransferRecyclerViewAdapter;
import com.josephus.pokemongo.comparators.CPComparator;
import com.josephus.pokemongo.comparators.FavoriteComparator;
import com.josephus.pokemongo.comparators.HPComparator;
import com.josephus.pokemongo.comparators.IVComparator;
import com.josephus.pokemongo.comparators.NameComparator;
import com.josephus.pokemongo.comparators.NumComparator;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class BatchEvolveFragment extends Fragment {

    private static final String TAG = BatchTransferFragment.class.getSimpleName();

    @BindView(R.id.list)
    RecyclerView recyclerView;
    @BindView(R.id.sort)
    Spinner sortSpinner;

    private HashSet<Integer> checkedItemsIndex = null;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyBatchEvolveRecyclerViewAdapter batchTransferRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BatchEvolveFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BatchEvolveFragment newInstance(int columnCount) {
        BatchEvolveFragment fragment = new BatchEvolveFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        checkedItemsIndex = new HashSet<Integer>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_batchevolve_list, container, false);
        ButterKnife.bind(this, view);

        // Set the adapter
        if (view instanceof LinearLayout) {
            Context context = view.getContext();
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            batchTransferRecyclerViewAdapter =
                    new MyBatchEvolveRecyclerViewAdapter(PokemonGo.pokemonList, mListener,
                            checkedItemsIndex);
            recyclerView.setAdapter(batchTransferRecyclerViewAdapter);

            ArrayAdapter<CharSequence> spinnerAdapter =
                    ArrayAdapter.createFromResource(getActivity(), R.array.sort_by_options,
                            android.R.layout.simple_spinner_dropdown_item);
            sortSpinner.setAdapter(spinnerAdapter);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: // Fav
                        Collections.sort(PokemonGo.pokemonList, new FavoriteComparator());
                        break;
                    case 1: // Num
                        Collections.sort(PokemonGo.pokemonList, new NumComparator());
                        break;
                    case 2: //HP
                        Collections.sort(PokemonGo.pokemonList, new HPComparator());
                        break;
                    case 3: // Name
                        Collections.sort(PokemonGo.pokemonList, new NameComparator());
                        break;
                    case 4: // CP
                        Collections.sort(PokemonGo.pokemonList, new CPComparator());
                        break;
                    case 5: // IV
                        Collections.sort(PokemonGo.pokemonList, new IVComparator());
                        break;
                }
                batchTransferRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.transfer)
    public void transfer(View view) {

        List<Pokemon> pokemonToEvolve = new ArrayList<>();

        for (Integer i : checkedItemsIndex) {
            Log.d(TAG, "item: " + i);
            pokemonToEvolve.add(PokemonGo.pokemonList.get(i));
        }

        new TransferTask().execute(pokemonToEvolve);

    }

    class TransferTask extends AsyncTask<List<Pokemon>, Boolean, Void> {


        private MaterialDialog transferProgressDialog;
        private int success = 0, fail = 0;

        @Override
        protected Void doInBackground(List<Pokemon>... list) {
            for (Pokemon p : list[0]) {
                boolean successful = true;
                Log.d(TAG, "calling transfer");
                try {
                    p.transferPokemon();
                } catch (LoginFailedException e) {
                    e.printStackTrace();
                    successful = false;
                } catch (RemoteServerException e) {
                    e.printStackTrace();
                    successful = false;
                }
                publishProgress(successful);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            transferProgressDialog = new MaterialDialog.Builder(getActivity()).title(R.string.transfer_dialog_title).content(getString(R.string.transfer_dialog_content, checkedItemsIndex.size())).progress(false, checkedItemsIndex.size(), true).show();
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
            if (values[0]) {
                success++;
            } else {
                fail++;
            }
            transferProgressDialog.incrementProgress(1);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            transferProgressDialog.dismiss();
            new MaterialDialog.Builder(getActivity()).title(R.string.transfer_dialog_success_title).content(getString(R.string.transfer_dialog_success_content, success, fail)).positiveText(R.string.transfer_dialog_success_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                    getActivity().finish();
                }
            }).show();
        }
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
