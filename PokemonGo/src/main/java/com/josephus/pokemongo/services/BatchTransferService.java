package com.josephus.pokemongo.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.josephus.pokemongo.PokemonGo;
import com.josephus.pokemongo.R;
import com.josephus.pokemongo.activities.ContainerActivity;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by josephus on 04/01/2017.
 */

public class BatchTransferService extends IntentService {

    private static final String TAG = BatchTransferService.class.getSimpleName();

    public static final String KEY_INDICES = "indices";
    public static final String KEY_TRANSFER_STATUS = "com.josephus.pokemongo.services.TRANSFER_STATUS";
    public static final String TRANSFER_RUNNING = "running";
    public static final String TRANSFER_COMPLETE = "complete";
    public static final String TRANSFER_COUNT = "transfer_count";
    public static final String TRANSFER_TOTAL = "transfer_total";
    private static final int TRANSFER_NOTIFICATION_ID = 240857;

    private int[] indices;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    public BatchTransferService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent.hasExtra(KEY_INDICES)) {
            indices = intent.getIntArrayExtra(KEY_INDICES);

            List<Pokemon> pokemonToTransfer = new ArrayList<>();

            for (Integer i : indices) {
                pokemonToTransfer.add(PokemonGo.pokemonList.get(i));
            }

            executeTransferTask(pokemonToTransfer);
        } else {
            throw new IllegalArgumentException("Must pass KEY_INDICES when using this service");
        }

    }

    private void executeTransferTask(List<Pokemon> pokemonList) {
        int progress = 0;

        setupNotification(progress);

        for (Pokemon p : pokemonList) {
            boolean successful = true;
            Log.d(TAG, "calling transfer");
            try {
                p.transferPokemon();
            } catch (LoginFailedException e) {
                e.printStackTrace();
            } catch (RemoteServerException e) {
                e.printStackTrace();
            }
            publishProgress(++progress);
        }

        finished(progress);
    }

    private void publishProgress(int progress) {
        mBuilder.setProgress(indices.length, progress, false);
        mNotificationManager.notify(TRANSFER_NOTIFICATION_ID, mBuilder.build());

        Intent i = new Intent(KEY_TRANSFER_STATUS);
        i.putExtra(KEY_TRANSFER_STATUS, TRANSFER_RUNNING);
        i.putExtra(TRANSFER_COUNT, progress);
        i.putExtra(TRANSFER_TOTAL, indices.length);
        sendBroadcast(i);
    }

    private void finished(int progress) {
        mBuilder.setProgress(indices.length, progress, false);
        mBuilder.setOngoing(false);
        mBuilder.setContentTitle(getString(R.string.transfer_dialog_complete_title));
        mBuilder.setContentText(getString(R.string.transfer_dialog_complete_content, indices.length));
        mNotificationManager.notify(TRANSFER_NOTIFICATION_ID, mBuilder.build());

        ((PokemonGo)getApplication()).setBatchTransferServiceRunning(false);

        Intent i = new Intent(KEY_TRANSFER_STATUS);
        i.putExtra(KEY_TRANSFER_STATUS, TRANSFER_COMPLETE);
        sendBroadcast(i);
    }

    private void setupNotification(int progress) {
        mBuilder = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.transfer_dialog_title)).setContentText(getString(R.string.transfer_dialog_content, indices.length));

        Intent resultIntent = new Intent(getApplicationContext(), ContainerActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(ContainerActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setProgress(indices.length, progress, false);
        mBuilder.setOngoing(true);
        mNotificationManager.notify(TRANSFER_NOTIFICATION_ID, mBuilder.build());

        ((PokemonGo)getApplication()).setBatchTransferServiceRunning(true);

        Intent i = new Intent(KEY_TRANSFER_STATUS);
        i.putExtra(KEY_TRANSFER_STATUS, TRANSFER_RUNNING);
        sendBroadcast(i);
    }

    class BatchTransferTask extends AsyncTask<List<Pokemon>, Boolean, Void> {

        private NotificationManager mNotificationManager;
        private NotificationCompat.Builder mBuilder;
        private int progress = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // build and show notification about transfer

            mBuilder = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.transfer_dialog_title)).setContentText(getString(R.string.transfer_dialog_content, indices.length));

            Intent resultIntent = new Intent(getApplicationContext(), ContainerActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addParentStack(ContainerActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder.setProgress(indices.length, progress, false);
            mNotificationManager.notify(TRANSFER_NOTIFICATION_ID, mBuilder.build());
        }

        @Override
        protected Void doInBackground(List<Pokemon>... lists) {
            for (Pokemon p : lists[0]) {
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
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);

            // update notification to reflect progress
            mBuilder.setProgress(indices.length, ++progress, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // update notification to reflect completion, make notification dismissable

        }
    }


}
