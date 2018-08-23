package com.example.android.meleematchassistant.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.android.meleematchassistant.AppRepository;
import com.example.android.meleematchassistant.Game;

import java.util.List;

public class GameViewModel extends AndroidViewModel { // Use AndroidViewModel in this case since we require the context (in all other cases use ViewModel instead)

    // Hold a reference to the repository
    private AppRepository appRepository;

    // Add a private LiveData member variable to cache the current and previous game
    private LiveData<Game> currentGame;
    private LiveData<Game> previousGame;
    private LiveData<List<Game>> stagesPlayerHasWonOn;

    // Temporary values before the database gets updated with a new game
    private int tempPlayerAScore;
    private int tempPlayerBScore;
    private String tempWinnerOfGame;
    private String tempLoserOfGame;

    // Constructor gets the reference to the repository
    // and gets the current and previous game from the repository
    public GameViewModel(Application application) {
        super(application);
        appRepository = new AppRepository(application);
        currentGame = appRepository.getCurrentGame();
        // stagesPlayerHasWonOn = appRepository.getStagesPlayerHasWonOn();
    }

    ///// METHODS /////

    // Add a "getter" method -- completely hides the implementation from the UI.
    public LiveData<Game> getCurrentGame() {
        return currentGame;
    }

    public LiveData<Game> getPreviousGame() {
        return previousGame;
    }

    public LiveData<List<Game>> getStagesPlayerHasWonOn() {
        return stagesPlayerHasWonOn;
    }


    public int getTempPlayerAScore() {
        return tempPlayerAScore;
    }

    public void setTempPlayerAScore(int tempPlayerAScore) {
        this.tempPlayerAScore = tempPlayerAScore;
    }

    public int getTempPlayerBScore() {
        return tempPlayerBScore;
    }

    public void setTempPlayerBScore(int tempPlayerBScore) {
        this.tempPlayerBScore = tempPlayerBScore;
    }

    public String getTempWinnerOfGame() {
        return tempWinnerOfGame;
    }

    public void setTempWinnerOfGame(String tempWinnerOfGame) {
        this.tempWinnerOfGame = tempWinnerOfGame;
    }

    public String getTempLoserOfGame() {
        return tempLoserOfGame;
    }

    public void setTempLoserOfGame(String tempLoserOfGame) {
        this.tempLoserOfGame = tempLoserOfGame;
    }

    // Create wrapper methods that calls the Repository's corresponding method.
    // In this way, the implementation is completely hidden from the UI.
    public void insert(Game game) {
        appRepository.insert(game);
    }
    public void delete(Game game) {
        appRepository.delete(game);
    }
    public void deleteAllGames() {
        appRepository.deleteAllGames();
    }
}
