package com.josephus.pokemongo.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.josephus.pokemongo.PokemonGo;
import com.josephus.pokemongo.R;
import com.josephus.pokemongo.activities.ContainerActivity;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.CaptchaActiveException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by josephus on 04/01/2017.
 */

public class BatchEvolveService extends IntentService {

    private static final String TAG = BatchEvolveService.class.getSimpleName();

    public static final String KEY_INDICES = "indices";
    public static final String KEY_EVOLVE = "com.josephus.pokemongo.services.EVOLVE_STATUS";
    private static final int EVOLVE_NOTIFICATION_ID = 2345645;

    private int[] indices;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    public BatchEvolveService() {
        super((TAG));
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent.hasExtra(KEY_INDICES)) {
            indices = intent.getIntArrayExtra(KEY_INDICES);

            List<Pokemon> pokemonToEvolve = new ArrayList<>();
            for (Integer i : indices) {
                pokemonToEvolve.add(PokemonGo.pokemonList.get(i));
            }

            executeEvolveTask(pokemonToEvolve);
        } else {
            throw new IllegalArgumentException("Must pass KEY_INDICES when using this service");
        }
    }

    private void executeEvolveTask(List<Pokemon> pokemonList) {
        int progress = 0;
        setupNotification(progress);

        for (Pokemon p : pokemonList) {
            try {
                p.evolve();
            } catch (LoginFailedException e) {
                e.printStackTrace();
            } catch (RemoteServerException e) {
                e.printStackTrace();
            } catch (CaptchaActiveException e) {
                e.printStackTrace();
            }
            publishProgress(++progress);
        }

        finished(progress);
    }

    private void publishProgress(int progress) {
        mBuilder.setProgress(indices.length, progress, false);
        mBuilder.setContentTitle(getString(R.string.evolve_dialog_title, indices.length)).setContentText(getString(R.string.evolve_dialog_content, progress, indices.length));
        mNotificationManager.notify(EVOLVE_NOTIFICATION_ID, mBuilder.build());

        Intent i = new Intent(KEY_EVOLVE);
        sendBroadcast(i);
    }

    private void finished(int progress) {
        mBuilder.setProgress(indices.length, progress, false);
        mBuilder.setOngoing(false);
        mBuilder.setContentTitle(getString(R.string.evolve_dialog_complete_title));
        mBuilder.setContentText(getString(R.string.evolve_dialog_complete_content, indices.length));
        mNotificationManager.notify(EVOLVE_NOTIFICATION_ID, mBuilder.build());

        Intent i = new Intent(KEY_EVOLVE);
        sendBroadcast(i);
    }

    private void setupNotification(int progress) {
        mBuilder = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.evolve_dialog_title, indices.length)).setContentText(getString(R.string.evolve_dialog_content, progress, indices.length));

        Intent resultIntent = new Intent(getApplicationContext(), ContainerActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(ContainerActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setProgress(indices.length, progress, false);
        mBuilder.setOngoing(true);
        mNotificationManager.notify(EVOLVE_NOTIFICATION_ID, mBuilder.build());

        ((PokemonGo) getApplication()).setBatchEvolveServiceRunning(true);

        Intent i = new Intent(KEY_EVOLVE);
        sendBroadcast(i);
    }

}
