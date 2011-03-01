package com.wadsack.android.sheepshead.scorer;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

import java.util.List;

public class ScoreCardActivity extends GuiceActivity
{
    private static final String TAG = ScoreCardActivity.class.getSimpleName();

    @InjectView(R.id.table)
    private TableLayout table;

    @InjectView(R.id.header)
    private TableLayout header;

    @InjectView(R.id.new_hand)
    private Button newHand;

    public static final String EXTRA_SCORECARD = "A Scorecard passed into the activity";
    @InjectExtra(value = EXTRA_SCORECARD, optional = true)
    private ScoreCard scoreCard;

    // Result codes for intent starting
    private static final int PICK_PLAYERS = 1;
    private static final int NEW_HAND = 2;
    private static final int EDIT_HAND = 3;
    private static final int SETTINGS = 4;

    private boolean dataSetChanged = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scorecard);

        // Check savedInstanceState with precedence over the Intent
        if (savedInstanceState != null) {
            scoreCard = savedInstanceState.getParcelable(EXTRA_SCORECARD);
        }

        dataSetChanged = true;

        newHand.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent i = new Intent(ScoreCardActivity.this, ScoreHandActivity.class);
                i.putParcelableArrayListExtra(ScoreHandActivity.EXTRA_PLAYERS, scoreCard.Players);
                i.putExtra(ScoreHandActivity.EXTRA_DEALER_INDEX, scoreCard.Hands.size() % scoreCard.Players.size());
                if ((scoreCard.Hands.size() + 1) % scoreCard.Players.size() == 0) {
                    i.putExtra(ScoreHandActivity.EXTRA_MAY_BE_LAST_HAND, true);
                }
                if (scoreCard.Hands.size() > 0 &&
                    scoreCard.Hands.size() % scoreCard.Players.size() == 0
                    && scoreCard.Hands.get(scoreCard.Hands.size() - 1).lastHand) {
                    scoreCard.Hands.get(scoreCard.Hands.size() - 1).lastHand = false;
                }
                if (scoreCard.Hands.size() > 0 &&
                    (scoreCard.Hands.get(scoreCard.Hands.size() - 1).pickerPoints == Hand.TrickPointsRange.Sixty ||
                     scoreCard.Hands.get(scoreCard.Hands.size() - 1).gameType == Hand.GameType.MauerCheck ||
                     scoreCard.Hands.get(scoreCard.Hands.size() - 1).gameType == Hand.GameType.Leaster)) {
                    i.putExtra(ScoreHandActivity.EXTRA_DOUBLER, true);
                }
                startActivityForResult(i, NEW_HAND);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (dataSetChanged) {
            redrawScorecard();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_SCORECARD, scoreCard);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d(TAG, "Result from activity " + requestCode + ": " + resultCode);
        switch( requestCode ) {
            case PICK_PLAYERS:
                if (intent == null) {
                    // So that a back button press closes the app
                    finish();
                    return;
                }
                final List<Player> list = intent.getParcelableArrayListExtra(PickPlayersActivity.EXTRA_PLAYERS);
                if (list.size() < 5) {
                    // So that a back button press closes the app
                    finish();
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        scoreCard = new ScoreCard();
                        for (Player p : list) {
                            scoreCard.Players.add(p);
                        }
                        notifyDataSetChanged();
                        }
                });
                break;
            case NEW_HAND:
                if (intent != null && intent.hasExtra(ScoreHandActivity.EXTRA_HAND)) {
                    // add returned hand to the score card
                    final Hand hand = intent.getParcelableExtra(ScoreHandActivity.EXTRA_HAND);
                    runOnUiThread( new Runnable() {
                        public void run() {
                            scoreCard.Hands.add(hand);
                            notifyDataSetChanged();
                        }
                    });
                }
                break;
            case EDIT_HAND:
                if (intent != null && intent.hasExtra(ScoreHandActivity.EXTRA_HAND)) {
                    // replace hand at index with one returned
                    if (resultCode != RESULT_CANCELED) {
                        final Hand hand = intent.getParcelableExtra(ScoreHandActivity.EXTRA_HAND);

                        int index = -1;
                        if (intent.hasExtra(ScoreHandActivity.EXTRA_HAND_EDIT_INDEX)) {
                            index = intent.getIntExtra(ScoreHandActivity.EXTRA_HAND_EDIT_INDEX, index);
                            Log.d(TAG, "Changing hand at index " + index);
                        }

                        if (index < 0 || index > scoreCard.Hands.size()) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    scoreCard.Hands.add(hand);
                                    notifyDataSetChanged();
                                }
                            });
                            Log.w(TAG, "Adding new hand in response from EDIT command.");
                        } else {
                            final int i = index;
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    scoreCard.Hands.set(i, hand);
                                    if (i + 1 < scoreCard.Hands.size() &&
                                            (hand.pickerPoints == Hand.TrickPointsRange.Sixty ||
                                             hand.gameType == Hand.GameType.MauerCheck ||
                                             hand.gameType == Hand.GameType.Leaster)) {
                                        scoreCard.Hands.get(i + 1).isDoubled = true;
                                    }
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
                break;
            case SETTINGS:
                if (resultCode == RESULT_OK) {
                    // if settings have changed then we need to start a new game
                    // todo: offer to restart or re-score current game
                    Toast.makeText(this, R.string.new_settings_require_restart, Toast.LENGTH_SHORT);
                    startNewGame();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS);
                return true;
            case R.id.new_game:
                startNewGame();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (scoreCard == null) {
            // todo: also check persistence (SQLite) for an unfinished game (or maybe always load the last game?)
            startActivityForResult(new Intent(this, PickPlayersActivity.class), PICK_PLAYERS);
        }

    }

    private void redrawScorecard() {
        if (scoreCard != null && scoreCard.Players != null) {
            // note: it would be faster if we only did this when the data changed (i.e. when header isn't built)
            drawHeaderRow();

            int[] runningTotal = new int[scoreCard.Players.size()];

            // note: we could make this a little faster by just adding new rows unless we edit a row
            // but we still have to calculate running totals from the start or read the previous row
            table.removeAllViews();
            int handsSize = scoreCard.Hands.size();
            for (int r = 0; r < handsSize; r++) {
                TableRow row = drawScoreRow(runningTotal, r);
                table.addView(row);
            }

            dataSetChanged = false;
        }
    }

    private void drawHeaderRow() {
        header.removeAllViews();
        TableRow headerRow = new TableRow(this);
        for (Player p : scoreCard.Players) {
            TextView playerInitial = new TextView(this);
            playerInitial.setText("  " + p.Initial);
            playerInitial.setTypeface(playerInitial.getTypeface(), Typeface.BOLD);
            headerRow.addView(playerInitial, new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        }
        TextView handDetails = new TextView(this);
        handDetails.setText(R.string.hand_details);
        handDetails.setTypeface(handDetails.getTypeface(), Typeface.BOLD);
        headerRow.addView(handDetails);
        header.addView(headerRow);
    }


    private TableRow drawScoreRow(int[] runningTotal, final int rowIndex) {
        final Hand hand = scoreCard.Hands.get(rowIndex);
        final TableRow row = new TableRow(this);
        List<Player> players = scoreCard.Players;
        for (int p = 0, playersSize = players.size(); p < playersSize; p++) {
            TextView scoreCell = new TextView(this);
            if (rowIndex % scoreCard.Players.size() == p) {
                scoreCell.setBackgroundResource(R.drawable.dealer_mark);
            } else {
                scoreCell.setBackgroundColor(android.R.color.transparent);
            }
            scoreCell.setGravity(Gravity.LEFT);
            if (runningTotal == null) {
                scoreCell.setText(" *  ");
            } else if (hand.isScored()) {
                runningTotal[p] += hand.getScore(p);
                scoreCell.setText(String.format("%3d", runningTotal[p]));
            } else {
                scoreCell.setText(" -  ");
            }
            row.addView(scoreCell, new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        }
        if (!hand.isScored()) {
            // Future hands can't be calculated
            // todo: this doesn't work when used in a method because it doesn't change the original
            runningTotal = null;
        }

        View details = getLayoutInflater().inflate(R.layout.check_cell, null);
        if (hand.isDoubled) {
            details.findViewById(R.id.doubler).setVisibility(View.VISIBLE);
        }
        if (hand.crackerIndex >= 0 ) {
            TextView cracker = (TextView)details.findViewById(R.id.cracker);
            // todo: use a graphic of a fist with a badge of the cracker's initial
            if (hand.recrackerIndex < 0) {
                cracker.setText(
                    String.format(getString(R.string.cracker),
                            scoreCard.Players.get(hand.crackerIndex).Initial));
            } else {
                cracker.setText(
                    String.format(getString(R.string.crack_recrack),
                            scoreCard.Players.get(hand.crackerIndex).Initial,
                            scoreCard.Players.get(hand.recrackerIndex).Initial));
            }
            cracker.setVisibility(View.VISIBLE);
        }

        if (hand.gameType == Hand.GameType.MauerCheck) {
            TextView game = (TextView)details.findViewById(R.id.game);
            game.setText(R.string.mauer_check_short);
            game.setVisibility(View.VISIBLE);
        } else if (hand.gameType == Hand.GameType.Leaster) {
            TextView game = (TextView)details.findViewById(R.id.game);
            game.setText(R.string.leaster_short);
            game.setVisibility(View.VISIBLE);
        } else if (hand.pickerPoints == Hand.TrickPointsRange.Sixty) {
            TextView game = (TextView)details.findViewById(R.id.game);
            game.setText(getResources().getStringArray(R.array.point_taken)[2]);
            game.setVisibility(View.VISIBLE);
        }
        row.addView(details);

        row.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(ScoreCardActivity.this, ScoreHandActivity.class);
                i.putParcelableArrayListExtra(ScoreHandActivity.EXTRA_PLAYERS, scoreCard.Players);
                i.putExtra(ScoreHandActivity.EXTRA_HAND, hand);
                i.putExtra(ScoreHandActivity.EXTRA_HAND_EDIT_INDEX, rowIndex);
                i.putExtra(ScoreHandActivity.EXTRA_DEALER_INDEX, hand.dealerIndex);
                if ((rowIndex + 1) % scoreCard.Players.size() == 0) {
                    i.putExtra(ScoreHandActivity.EXTRA_MAY_BE_LAST_HAND, true);
                }
                startActivityForResult(i, EDIT_HAND);
            }
        });

        if (hand.lastHand) {
            row.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_border_dark));
        }
        else if ((rowIndex + 1) % scoreCard.Players.size() == 0) {
            row.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_border_light));
        }
        return row;
    }


    private void startNewGame() {
        runOnUiThread(new Runnable() {
            public void run() {
                scoreCard.Hands.clear();
                // todo: pass in current players so they can be changed
                startActivityForResult(new Intent(ScoreCardActivity.this, PickPlayersActivity.class), PICK_PLAYERS);
            }
        });
    }
    
    private void notifyDataSetChanged() {
        Log.d(TAG, "Data set changed notification.");
        dataSetChanged = true;
    }
}

