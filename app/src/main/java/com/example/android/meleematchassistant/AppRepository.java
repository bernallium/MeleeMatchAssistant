package com.example.android.meleematchassistant;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

// The repository acts as a mediator between different data sources
// Acts as a single point of entry for the data

public class AppRepository {

    private PlayersDatabase playersDatabase;
    private GameDao gameDao;
    private LiveData<Game> currentGame;
    private LiveData<Game> previousGame;
    private LiveData<List<Game>> allGames;
    // private LiveData<List<Game>> stagesPlayerHasWonOn;

    public AppRepository() {
    }

    // Add a constructor that gets a handle to the database and initializes the member variables.
    public AppRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        playersDatabase = new PlayersDatabaseImpl();
        gameDao = appDatabase.gameDao();
        currentGame = gameDao.getCurrentGame();
        previousGame = gameDao.getPreviousGame();
        allGames = gameDao.getAllGames();
    /*    if (getCurrentGame() != null)
            stagesPlayerHasWonOn = gameDao.getStagesPlayerHasWonOn(getCurrentGame().getValue().getWinnerOfGame());
        else stagesPlayerHasWonOn = null;*/
    }

    public PlayersDatabase getPlayersDatabase() {
        return playersDatabase;
    }

    // Wrap in LiveData to notify any observers of changes
    public LiveData<Game> getCurrentGame() {
        return currentGame;
    }

    public LiveData<Game> getPreviousGame() {
        return previousGame;
    }

    public LiveData<List<Game>> getAllGames() {
        return allGames;
    }

/*
    public LiveData<List<Game>> getStagesPlayerHasWonOn() {
        return stagesPlayerHasWonOn;
    }
*/

    // You must call these methods on a non-UI thread or your app will crash
    // Room ensures that you don't do any long-running operations on the main thread, blocking the UI
    public void insert(Game game) {
        new insertAsyncTask(gameDao).execute(game);
    }

    public void delete(Game game) {
        new deleteAsyncTask(gameDao).execute(game);
    }

    public void deleteAllGames() {
        new deleteAllGamesAsyncTask(gameDao).execute();
    }

    // Async tasks:
    private static class insertAsyncTask extends AsyncTask<Game, Void, Void> {

        private GameDao gameDao;

        insertAsyncTask(GameDao gameDao) {
            this.gameDao = gameDao;
        }

        // Parameters of the AsyncTask are passed here
        @Override
        protected Void doInBackground(final Game... params) {
            gameDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Game, Void, Void> {

        private GameDao gameDao;

        deleteAsyncTask(GameDao gameDao) {
            this.gameDao = gameDao;
        }

        // Parameters of the AsyncTask are passed here
        @Override
        protected Void doInBackground(final Game... params) {
            gameDao.delete(params[0]);
            return null;
        }
    }

    private static class deleteAllGamesAsyncTask extends AsyncTask<Void, Void, Void> {

        private GameDao gameDao;

        deleteAllGamesAsyncTask(GameDao gameDao) {
            this.gameDao = gameDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            gameDao.deleteAllGames();
            return null;
        }
    }
}
