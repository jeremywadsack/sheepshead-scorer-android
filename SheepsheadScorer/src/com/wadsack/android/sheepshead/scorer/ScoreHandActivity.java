package com.wadsack.android.sheepshead.scorer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Jeremy Wadsack
 */
public class ScoreHandActivity extends GuiceActivity {
    private static final String TAG = ScoreHandActivity.class.getSimpleName();

    @InjectView(R.id.selectGame)
    private Spinner selectGame;

    @InjectView(R.id.doubler)
    private CheckBox doublerCheck;

    @InjectView(R.id.picker)
    private Spinner picker;

    @InjectView(R.id.partner)
    private Spinner partner;

    @InjectView(R.id.pointsTaken)
    private Spinner pointsTaken;

    @InjectView(R.id.crack)
    private CheckBox crack;

    @InjectView(R.id.recrack)
    private CheckBox recrack;

    @InjectView(R.id.noTrick)
    private CheckBox noTrick;

    @InjectView(R.id.allTricks)
    private CheckBox allTricks;

    @InjectView(R.id.lastHand)
    private CheckBox lastHand;

    @InjectView(R.id.done)
    private Button doneButton;

    @InjectView(R.id.revert)
    private Button revertButton;


    public static final String EXTRA_PLAYERS = "A list of valid players for the hand";
    @InjectExtra(EXTRA_PLAYERS)
    private List<Player> players;

    public static final String EXTRA_HAND_EDIT_INDEX = "Optionally the index of the a hand to edit; returned in result.";
    @InjectExtra(value = EXTRA_HAND_EDIT_INDEX, optional = true)
    private int handEditIndex = -1;

    public static final String EXTRA_HAND = "Optionally a hand to edit; also the returned hand.";
    @InjectExtra(value = EXTRA_HAND, optional = true)
    private Hand hand;

    public static final String EXTRA_MAY_BE_LAST_HAND = "Optional; indicates the end of a round which could be a last hand, which is doubled.";
    @InjectExtra(value = EXTRA_MAY_BE_LAST_HAND, optional = true)
    private boolean mayBeLastHand = false;

    public static final String EXTRA_DOUBLER = "Optional; indicates that the hand should be doubled by default.";
    @InjectExtra(value = EXTRA_DOUBLER, optional = true)
    private boolean doubler = false;

    public static final String EXTRA_DEALER_INDEX = "The index of the dealer in the list of players";
    @InjectExtra(EXTRA_DEALER_INDEX)
    private int dealerIndex;

    // Result codes to return
    public static final int RESULT_SCORED = RESULT_OK;
    public static final int RESULT_NOT_SCORED = RESULT_OK - 1;
    private List<Player> whoIsPlaying;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_hand);

        if (savedInstanceState != null) {
            hand = savedInstanceState.getParcelable(EXTRA_HAND);
        }

        int playerCount = players.size();
        whoIsPlaying = new ArrayList<Player>();
        for (int i = 0; i < playerCount; i++) {
            if (Hand.isPlayerPlaying(i, dealerIndex, playerCount)) {
                whoIsPlaying.add(players.get(i));
            }
        }

        ArrayAdapter playerAdapter =
                new ArrayAdapter<Player>(
                        this,
                        android.R.layout.simple_spinner_item,
                        whoIsPlaying
                );
        playerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        picker.setAdapter(playerAdapter);
        partner.setAdapter(playerAdapter);

        revertButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        if (mayBeLastHand) {
            lastHand.setVisibility(View.VISIBLE);
        }

        doublerCheck.setChecked(doubler);

        crack.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                if (!((CheckBox)view).isChecked()) {
                    hand.crackerIndex = -1;
                    recrack.setVisibility(View.GONE);
                    return;
                }
                recrack.setVisibility(View.VISIBLE);
                AlertDialog.Builder builder = new AlertDialog.Builder(ScoreHandActivity.this);
                builder.setTitle(R.string.who_cracked);

                // Anyone but picker or partner can crack
                int partnerIndex = partner.getSelectedItemPosition();
                int pickerIndex = picker.getSelectedItemPosition();
                final List<Player> whoCanCrack = new ArrayList<Player>();
                for (int i = 0, listSize = whoIsPlaying.size(); i < listSize; i++) {
                    if (i != partnerIndex && i != pickerIndex) {
                        whoCanCrack.add(whoIsPlaying.get(i));
                    }
                }
                int crackerIndex = hand.crackerIndex < 0 ? -1 : whoCanCrack.indexOf(players.get(hand.crackerIndex));

                builder.setSingleChoiceItems(
                        new ArrayAdapter<Player>(ScoreHandActivity.this, android.R.layout.simple_spinner_dropdown_item, whoCanCrack),
                        crackerIndex,
                        new Dialog.OnClickListener(){
                            public void onClick(DialogInterface dialogInterface, int position) {
                                hand.crackerIndex = players.indexOf(whoCanCrack.get(position));
                                dialogInterface.dismiss();
                            }
                }).show();
            }
        });


        recrack.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                if (!((CheckBox) view).isChecked()) {
                    hand.recrackerIndex = -1;
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ScoreHandActivity.this);
                builder.setTitle(R.string.who_recracked);
                // Only picker or partner can crack back, if we know who they are
                int partnerIndex = partner.getSelectedItemPosition();
                int pickerIndex = picker.getSelectedItemPosition();
                final List<Player> whoCanRecrack;
                if (pickerIndex < 0 || partnerIndex < 0) {
                    whoCanRecrack = new ArrayList<Player>(whoIsPlaying);
                } else {
                    whoCanRecrack = new ArrayList<Player>();
                    whoCanRecrack.add(whoIsPlaying.get(pickerIndex));
                    whoCanRecrack.add(whoIsPlaying.get(partnerIndex));
                }
                int recrackerIndex = hand.recrackerIndex < 0 ? -1 : whoCanRecrack.indexOf(players.get(hand.recrackerIndex));

                builder.setSingleChoiceItems(
                        new ArrayAdapter<Player>(ScoreHandActivity.this, android.R.layout.simple_spinner_dropdown_item, whoCanRecrack),
                        recrackerIndex,
                        new Dialog.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int position) {
                                hand.recrackerIndex = players.indexOf(whoCanRecrack.get(position));
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });

        if (hand == null) {
            hand = new Hand(players.size());
        } else {
            switch (hand.gameType) {
                case Regular:
                    selectGame.setSelection(0);
                    break;
                case MauerCheck:
                    selectGame.setSelection(1);
                    break;
                case Leaster:
                    selectGame.setSelection(2);
                    break;
            }
            if(hand.pickerIndex > -1) {
                picker.setSelection(whoIsPlaying.indexOf(players.get(hand.pickerIndex)));
            }
            if(hand.partnerIndex > -1) {
                partner.setSelection(whoIsPlaying.indexOf(players.get(hand.partnerIndex)));
            }
            pointsTaken.setSelection(hand.getPickerPointsIndex());
            doublerCheck.setChecked(hand.isDoubled);
            noTrick.setChecked(hand.pickerTricksTaken == Hand.Option.None);
            allTricks.setChecked(hand.pickerTricksTaken == Hand.Option.All);
            lastHand.setChecked(hand.lastHand);
            crack.setChecked(hand.crackerIndex > -1);
            recrack.setChecked(hand.recrackerIndex > -1);
        }


        doneButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                int rc = scoreHand();
                Intent i = new Intent();
                i.putExtra(EXTRA_HAND, hand);
                if (handEditIndex != -1 ) {
                    Log.d(TAG, "Returning edit hand index: " + handEditIndex);
                    i.putExtra(EXTRA_HAND_EDIT_INDEX, handEditIndex);
                }
                setResult(rc, i);
                finish();
            }
        });

        selectGame.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                switch (parent.getSelectedItemPosition()) {
                    case 1:
                        pointsTaken.setVisibility(View.GONE);
                        partner.setVisibility(View.GONE);
                        crack.setVisibility(View.GONE);
                        pointsTaken.setSelection(-1);
                        partner.setSelection(-1);
                        break;
                    case 2:
                        pointsTaken.setVisibility(View.GONE);
                        partner.setVisibility(View.GONE);
                        crack.setVisibility(View.GONE);
                        pointsTaken.setSelection(-1);
                        partner.setSelection(-1);
                        break;
                    default:
                        pointsTaken.setVisibility(View.VISIBLE);
                        partner.setVisibility(View.VISIBLE);
                        crack.setVisibility(View.VISIBLE);
                        break;
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        pointsTaken.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                if (parent.getSelectedItemPosition() == 0) {
                    noTrick.setVisibility(View.VISIBLE);
                } else {
                    noTrick.setVisibility(View.GONE);
                }
                if (parent.getSelectedItemPosition() == 4) {
                    allTricks.setVisibility(View.VISIBLE);
                } else {
                    allTricks.setVisibility(View.GONE);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        scoreHand();
        outState.putParcelable(EXTRA_HAND, hand);
    }

    private int scoreHand() {
        int returnCode = RESULT_NOT_SCORED;

        hand.dealerIndex = dealerIndex;

        int pickerIndex = picker.getSelectedItemPosition();
        if (pickerIndex >= 0) {
            hand.setPickerIndex(players.indexOf(whoIsPlaying.get(pickerIndex)));
        }
        int partnerIndex = partner.getSelectedItemPosition();
        if (partnerIndex >= 0) {
            hand.setPartnerIndex(players.indexOf(whoIsPlaying.get(partnerIndex)));
        }
        hand.isDoubled = doublerCheck.isChecked();
        hand.lastHand = lastHand.isChecked();

        try {
            switch (selectGame.getSelectedItemPosition()) {
                case 1:
                    hand.gameType = Hand.GameType.MauerCheck;
                    hand.scoreHand(Hand.Option.Some, Hand.TrickPointsRange.ZeroToThirty);
                    returnCode = RESULT_SCORED;
                    break;
                case 2:
                    hand.gameType = Hand.GameType.Leaster;
                    hand.scoreHand(Hand.Option.Some, Hand.TrickPointsRange.ZeroToThirty);
                    returnCode = RESULT_SCORED;
                    break;
                default:
                    hand.gameType = Hand.GameType.Regular;
                    switch (pointsTaken.getSelectedItemPosition()) {
                        case 0:
                            if (noTrick.isChecked()) {
                                hand.scoreHand(Hand.Option.None, Hand.TrickPointsRange.ZeroToThirty);
                            } else {
                                hand.scoreHand(Hand.Option.Some, Hand.TrickPointsRange.ZeroToThirty);
                            }
                            returnCode = RESULT_SCORED;
                            break;
                        case 1:
                            hand.scoreHand(Hand.Option.Some, Hand.TrickPointsRange.ThirtyOneToFiftyNine);
                            returnCode = RESULT_SCORED;
                            break;
                        case 2:
                            hand.scoreHand(Hand.Option.Some, Hand.TrickPointsRange.Sixty);
                            returnCode = RESULT_SCORED;
                            break;
                        case 3:
                            hand.scoreHand(Hand.Option.Some, Hand.TrickPointsRange.SixtyOneToNinety);
                            returnCode = RESULT_SCORED;
                            break;
                        case 4:
                            if (allTricks.isChecked()) {
                                hand.scoreHand(Hand.Option.All, Hand.TrickPointsRange.NinetyOneToOneHundredTwenty);
                            } else {
                                hand.scoreHand(Hand.Option.Some, Hand.TrickPointsRange.NinetyOneToOneHundredTwenty);
                            }
                            returnCode = RESULT_SCORED;
                            break;
                    }
                    break;
            }
        } catch (InvalidHandException ex) {
            // skip the scoring and log the error.
            Log.e(TAG, "Error scoring hand: " + ex.getMessage());
            returnCode = RESULT_CANCELED;
        }

        return returnCode;
    }


}
