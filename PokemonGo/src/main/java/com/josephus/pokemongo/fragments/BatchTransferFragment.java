package com.josephus.pokemongo.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.josephus.pokemongo.PokemonGo;
import com.josephus.pokemongo.R;
import com.josephus.pokemongo.adapters.MyBatchTransferRecyclerViewAdapter;
import com.josephus.pokemongo.comparators.CPComparator;
import com.josephus.pokemongo.comparators.FavoriteComparator;
import com.josephus.pokemongo.comparators.HPComparator;
import com.josephus.pokemongo.comparators.IVComparator;
import com.josephus.pokemongo.comparators.NameComparator;
import com.josephus.pokemongo.comparators.NumComparator;
import com.josephus.pokemongo.interfaces.ItemSelectable;
import com.josephus.pokemongo.interfaces.OnListFragmentInteractionListener;
import com.josephus.pokemongo.services.BatchTransferService;
import com.pokegoapi.api.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class BatchTransferFragment extends Fragment implements ItemSelectable {

    private static final String TAG = BatchTransferFragment.class.getSimpleName();

    @BindView(R.id.list)
    RecyclerView recyclerView;
    @BindView(R.id.sort)
    Spinner sortSpinner;
    @BindView(R.id.in_list_tv)
    TextView inList;
    @BindView(R.id.selected_tv)
    TextView selectedTv;

    private BroadcastReceiver transferStatusReceiver;
    private HashSet<Integer> checkedItemsIndex = null;
    private MaterialDialog progressDialog;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyBatchTransferRecyclerViewAdapter batchTransferRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BatchTransferFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BatchTransferFragment newInstance(int columnCount) {
        BatchTransferFragment fragment = new BatchTransferFragment();
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
        View view = inflater.inflate(R.layout.fragment_batchtransfer_list, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the adapter
        if (view instanceof LinearLayout) {
            Context context = view.getContext();
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            batchTransferRecyclerViewAdapter =
                    new MyBatchTransferRecyclerViewAdapter(PokemonGo.pokemonList, mListener,
                            checkedItemsIndex);
            recyclerView.setAdapter(batchTransferRecyclerViewAdapter);

            ArrayAdapter<CharSequence> spinnerAdapter =
                    ArrayAdapter.createFromResource(getActivity(), R.array.sort_by_options,
                            android.R.layout.simple_spinner_dropdown_item);
            sortSpinner.setAdapter(spinnerAdapter);
        }

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

        inList.setText(getString(R.string.in_list, batchTransferRecyclerViewAdapter.getItemCount()));
        selectedTv.setText(getString(R.string.selected, checkedItemsIndex.size()));
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

        List<Pokemon> pokemonToTransfer = new ArrayList<>();
        int[] indices = new int[checkedItemsIndex.size()];

        int index = 0;
        for (Integer i : checkedItemsIndex) {
            indices[index++] = i;
        }

        Intent i = new Intent(getActivity(), BatchTransferService.class);
        i.putExtra(BatchTransferService.KEY_INDICES, indices);
        getActivity().startService(i);
    }

    @Override
    public void onItemSelected(int value) {
        modifySelectedCount(value);
    }

    @Override
    public void onResume() {
        super.onResume();
        transferStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getStringExtra(BatchTransferService.KEY_TRANSFER_STATUS)) {
                    case BatchTransferService.TRANSFER_RUNNING:
                        if (progressDialog == null) {
                            progressDialog = new MaterialDialog.Builder(getActivity()).build();
                        }
                        progressDialog.setProgress(intent.getIntExtra(BatchTransferService.TRANSFER_COUNT, 0));
                        progressDialog.setMaxProgress(intent.getIntExtra(BatchTransferService.TRANSFER_TOTAL, 0));
                        progressDialog.show();
                        break;
                    case BatchTransferService.TRANSFER_COMPLETE:
                        break;
                }
            }
        };
        getActivity().registerReceiver(transferStatusReceiver, new IntentFilter(BatchTransferService.KEY_TRANSFER_STATUS));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(transferStatusReceiver);
    }

    //    class TransferTask extends AsyncTask<List<Pokemon>, Boolean, Void> {
//
//
//        private MaterialDialog transferProgressDialog;
//        private int success = 0, fail = 0;
//
//        @Override
//        protected Void doInBackground(List<Pokemon>... list) {
//            for (Pokemon p : list[0]) {
//                boolean successful = true;
//                Log.d(TAG, "calling transfer");
//                try {
//                    p.transferPokemon();
//                } catch (LoginFailedException e) {
//                    e.printStackTrace();
//                    successful = false;
//                } catch (RemoteServerException e) {
//                    e.printStackTrace();
//                    successful = false;
//                }
//                publishProgress(successful);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            transferProgressDialog = new MaterialDialog.Builder(getActivity()).title(R.string.transfer_dialog_title).content(getString(R.string.transfer_dialog_content, checkedItemsIndex.size())).progress(false, checkedItemsIndex.size(), true).show();
//        }
//
//        @Override
//        protected void onProgressUpdate(Boolean... values) {
//            super.onProgressUpdate(values);
//            if (values[0]) {
//                success++;
//            } else {
//                fail++;
//            }
//            transferProgressDialog.incrementProgress(1);
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            transferProgressDialog.dismiss();
//            new MaterialDialog.Builder(getActivity()).title(R.string.transfer_dialog_success_title).content(getString(R.string.transfer_dialog_success_content, success, fail)).positiveText(R.string.transfer_dialog_success_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
//                @Override
//                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                    dialog.dismiss();
//                    getActivity().finish();
//                }
//            }).show();
//        }
//    }

    public void modifySelectedCount(int count) {
        selectedTv.setText(getString(R.string.selected, count));
    }

}
