package com.josephus.pokemongo.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by josephus on 04/01/2017.
 */

public class BatchEvolveService extends IntentService {

    private static final String TAG = BatchEvolveService.class.getSimpleName();

    public BatchEvolveService() {
        super((TAG));
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
