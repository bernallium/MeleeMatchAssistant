package com.example.android.meleematchassistant.Activities;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.meleematchassistant.Fragments.StageStrikingStepFragment;
import com.example.android.meleematchassistant.Game;
import com.example.android.meleematchassistant.R;
import com.example.android.meleematchassistant.ViewModels.GameViewModel;
import com.example.android.meleematchassistant.ViewModels.StageSelectionViewModel;

public class StageSelectionActivity extends AppCompatActivity {

    private static final String TAG = StageSelectionActivity.class.getSimpleName();

    private GameViewModel gameViewModel;
    private StageSelectionViewModel stageSelectionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_selection);
        gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
        stageSelectionViewModel = ViewModelProviders.of(this).get(StageSelectionViewModel.class);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.stage_selection_fragment_container, new StageStrikingStepFragment())
                .commit();

        // Add up button to ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.stage_selection_string);

        final Button continueButton = findViewById(R.id.start_button);
        continueButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StageSelectionActivity.this, GameSummaryActivity.class);
                intent.putExtra("intentTag", TAG);
                intent.putExtra("stageIndex", stageSelectionViewModel.getChosenStageIndex());
                intent.putExtra("stageName", stageSelectionViewModel.getChosenStageName());
                startActivity(intent);
            }
        });

        continueButton.setEnabled(false);
        continueButton.setAlpha(0.25f);
        stageSelectionViewModel.isStartButtonEnabled().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean playerHasChosenStage) {
                continueButton.setEnabled(false);
                ObjectAnimator objectDisabledAnimator = ObjectAnimator.ofFloat(continueButton,"alpha",0.25f);
                objectDisabledAnimator.setDuration(300)
                        .start();
                if (playerHasChosenStage) {
                    continueButton.setEnabled(true);
                    ObjectAnimator objectEnabledAnimator = ObjectAnimator.ofFloat(continueButton,"alpha",1);
                    objectEnabledAnimator.setDuration(300)
                            .start();
                }
                else continueButton.setEnabled(false);
            }
        });

        // Updates the UI whenever changes to the previous game occur
        gameViewModel.getCurrentGame().observe(this, new Observer<Game>() {
            @Override
            public void onChanged(@Nullable Game currentGame) {
                if (currentGame != null) { // Prevents NullPointerException when the user presses back from and there is currently only 1 game stored in the database
                    // Dynamic action bar
                    int gameNumber;
                    if (currentGame != null) gameNumber = currentGame.getGameNumber() + 1;
                    else gameNumber = 1;
                    stageSelectionViewModel.setActionBarString(getString(R.string.game) + " " + gameNumber); // Increase by 1 since it's for the game to be inserted
                    getSupportActionBar().setTitle(stageSelectionViewModel.getActionBarString());

                    // Set instructions
                    TextView banPickInstructionsTextView = findViewById(R.id.ban_pick_instructions_text_view);
                    banPickInstructionsTextView.setText(
                            "1. " + currentGame.getWinnerOfGame() + " bans a stage" +
                                    "\n" + "2. " + currentGame.getLoserOfGame() + " picks a remaining stage");
                    TextView characterSelectionOrderInstructionsTextView = findViewById(R.id.character_selection_order_instructions_text_view);
                    characterSelectionOrderInstructionsTextView.setText(
                            "3. " + currentGame.getWinnerOfGame() + " selects their character first" +
                                    "\n" + "4. " + currentGame.getLoserOfGame() + " selects their character second");
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.active_game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                        .setTitle(R.string.stage_selection_alert_dialog_title)
                        .setMessage(R.string.stage_selection_alert_dialog_body)
                        .show();
                return true;
            case R.id.quit_match:
                Intent intent = new Intent(StageSelectionActivity.this, MainActivity.class);
                gameViewModel.deleteAllGames();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gameViewModel.delete(gameViewModel.getCurrentGame().getValue());
        Log.v(TAG, "Current game deleted");
    }
}