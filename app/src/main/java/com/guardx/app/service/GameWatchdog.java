package com.guardx.app.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.guardx.app.scanner.AppScanner;
import com.guardx.app.utils.Constants;

public class GameWatchdog {

    private Context context;
    private AppScanner appScanner;
    private Handler handler;
    private Runnable watchRunnable;
    private GameListener listener;
    private String lastActiveGame = null;

    public interface GameListener {
        void onGameDetected(String packageName);
        void onGameClosed();
    }

    public GameWatchdog(Context context, GameListener listener) {
        this.context = context;
        this.appScanner = new AppScanner(context);
        this.handler = new Handler(Looper.getMainLooper());
        this.listener = listener;
    }

    public void startWatching() {
        watchRunnable = new Runnable() {
            @Override
            public void run() {
                String activeGame = appScanner.getActiveGame(Constants.GAMES);

                if (activeGame != null && !activeGame.equals(lastActiveGame)) {
                    lastActiveGame = activeGame;
                    if (listener != null) {
                        listener.onGameDetected(activeGame);
                    }
                } else if (activeGame == null && lastActiveGame != null) {
                    lastActiveGame = null;
                    if (listener != null) {
                        listener.onGameClosed();
                    }
                }
                handler.postDelayed(this, Constants.SCAN_GAME_ACTIVE);
            }
        };
        handler.post(watchRunnable);
    }

    public void stopWatching() {
        if (handler != null && watchRunnable != null) {
            handler.removeCallbacks(watchRunnable);
        }
    }
}
