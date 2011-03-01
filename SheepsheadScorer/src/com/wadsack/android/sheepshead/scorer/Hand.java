package com.wadsack.android.sheepshead.scorer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Arrays;

/**
 * Author: Jeremy Wadsack
 */
public class Hand implements Parcelable{
    private static final String TAG = Hand.class.getSimpleName();

    public enum Option {
        None,
        Some,
        All,
    }

    public enum TrickPointsRange {
        ZeroToThirty,
        ThirtyOneToFiftyNine,
        Sixty,
        SixtyOneToNinety,
        NinetyOneToOneHundredTwenty,
    }

    public enum GameType {
        Regular,
        MauerCheck,
        Leaster
    }


    private final int[] scores;

    public int pickerIndex = -1;
    public int partnerIndex = -1;
    public int dealerIndex = -1;

    public int crackerIndex = -1;
    public int recrackerIndex = -1;
    public boolean isDoubled = false;
    public boolean lastHand = false;

    public Option pickerTricksTaken = Option.Some;
    public TrickPointsRange pickerPoints;
    public GameType gameType = GameType.Regular;

    private boolean isScored = false;


    public Hand(int numberOfPlayers) {
        scores = new int[numberOfPlayers];
    }

    public Hand(Parcel parcel) {
        scores = new int[parcel.readInt()];
        parcel.readIntArray(scores);
        pickerIndex = parcel.readInt();
        partnerIndex = parcel.readInt();
        dealerIndex = parcel.readInt();
        crackerIndex = parcel.readInt();
        recrackerIndex = parcel.readInt();
        isDoubled = parcel.readInt() == 1;
        lastHand = parcel.readInt() == 1;
        isScored = parcel.readInt() == 1;
        switch (parcel.readInt()) {
            case 0:
                pickerPoints = TrickPointsRange.ZeroToThirty;
                break;
            case 1:
                pickerPoints = TrickPointsRange.ThirtyOneToFiftyNine;
                break;
            case 2:
                pickerPoints = TrickPointsRange.Sixty;
                break;
            case 3:
                pickerPoints = TrickPointsRange.SixtyOneToNinety;
                break;
            case 4:
                pickerPoints = TrickPointsRange.NinetyOneToOneHundredTwenty;
                break;
        }
        switch (parcel.readInt()) {
            case 0:
                pickerTricksTaken = Option.None;
                break;
            case 2:
                pickerTricksTaken = Option.All;
                break;
            default:
                pickerTricksTaken = Option.Some;
                break;
        }
        switch (parcel.readInt()) {
            case 1:
                gameType = GameType.Leaster;
                break;
            case 2:
                gameType = GameType.MauerCheck;
                break;
            default:
                gameType = GameType.Regular;
                break;
        }
    }

    /**
     *
     * @param picker    Who was the picker for this hand
     */
    public void setPickerIndex(int picker){
        if (picker < 0 || picker >= scores.length) {
            Log.e(TAG, "Invalid index for picker, " + picker);
            return;
        }
        pickerIndex = picker;
    }

    /**
     *
     * @param partner   Who was the partner; may be null
     */
    public void setPartnerIndex(int partner) {
        if (partner < 0 || partner >= scores.length) {
            Log.e(TAG, "Invalid index for partner, " + partner);
            return;
        }
        partnerIndex = partner;
    }

    /**
     * Tells whether the hand has been scored yet.
     * @return true is the hand has been scored, otherwise false
     */
    public boolean isScored() {
        return isScored;
    }


    // Enums can't be cast as int?
    public int getPickerPointsIndex() {
        if (pickerPoints == null) {
            return -1;
        }
        switch (pickerPoints) {
            case ZeroToThirty:
                return 0;
            case ThirtyOneToFiftyNine:
                return 1;
            case Sixty:
                return 2;
            case SixtyOneToNinety:
                return 3;
            case NinetyOneToOneHundredTwenty:
                return 4;
        }
        throw new IllegalArgumentException("Don't know the value of " + pickerPoints.toString());
    }




    /**
     * Scores the hand based on the number of points the picking team had.
     * @param pickerTricksTaken  Whether the picker's team took All, Some or None of the tricks
     * @param pickerPoints  Number of trick points taken by the picker's team
     * @throws InvalidHandException If the combination of points and tricks taken is impossible.
     */
    public void scoreHand(Option pickerTricksTaken, TrickPointsRange pickerPoints) throws InvalidHandException {
        if (pickerIndex == -1) {
            Log.e(TAG, "Picker must be chosen before hand can be scored.");
            isScored = false;
            return;
        }

        if (scores.length < 5) {
            Log.e(TAG, "Scoring is not supported for less than 5 players.");
            isScored = false;
            return;
        }

        // Lookup table, int[3]: Picker, Partner, Opponents
        int[] scoreTable = ScoringTableFactory.getScoringTable(gameType, partnerIndex != -1 && partnerIndex != pickerIndex).get(pickerPoints).clone();

        // Adjust scores for no-trickers, crackers, doublers and last hand
        int factor = 1;
        if ((pickerTricksTaken == Option.None && pickerPoints == TrickPointsRange.ZeroToThirty) ||
            (pickerTricksTaken == Option.All && pickerPoints == TrickPointsRange.NinetyOneToOneHundredTwenty)) {
            factor *= 2;
        } else if (pickerTricksTaken != Option.Some) {
            throw new InvalidHandException("Picker can not take all or no tricks if points are " + pickerPoints.toString());
        }
        if (crackerIndex > -1) {
            factor *= 2;
            if (recrackerIndex > -1) {
                factor *= 2;
            }
        }
        if (isDoubled) {
            factor *= 2;
        }
        if (lastHand) {
            factor *= 2;
        }
        if( factor > 1) {
            for (int i = 0; i < scoreTable.length; i++ ) {
                scoreTable[i] = factor * scoreTable[i];
            }
        }


        // Store the values in the data object
        this.pickerTricksTaken = pickerTricksTaken;
        this.pickerPoints = pickerPoints;

        // Assign scores to the players
        int totalPlayers = scores.length;
        for (int i = 0; i < totalPlayers; i++) {
            if (dealerIndex < 0 && totalPlayers > 5) {
                Log.e(TAG, String.format("Cannot score %d players when dealer is not chosen.", totalPlayers));
                isScored = false;
                return;
            }

            if (!isPlayerPlaying(i, dealerIndex, totalPlayers)) {
                // no change in score for the dealer and players to the right of the dealer when more than five are seated
                Log.d(TAG, String.format("No score for player %d", i));
                scores[i] = 0;
            } else if (i == pickerIndex) {
                scores[i] = scoreTable[0];
            } else if(i == partnerIndex) {
                // if no partner, then this won't get hit
                Log.d(TAG, String.format("Scoring partner %d as %d", i, scoreTable[1]));
                scores[i] = scoreTable[1];
            } else {
                scores[i] = scoreTable[2];
            }
        }
        isScored = true;

        // assert that sum of scores in 0
        int sum = 0;
        for (int score : scores) {
            sum += score;
        }
        if (sum != 0) {
            String msg = "";
            for (int s : scores) msg += s + ",";
            msg = msg.substring(0,msg.length() - 1);
            Log.e(TAG, String.format("Scores do not total 0. [%s]", msg));
            Arrays.fill(scores, 0);
            isScored = false;
        }
    }

    public int getScore(int playerIndex) {
        if (!isScored) {
            Log.e(TAG, "Hand has not been scored yet.");
            return 0;
        }
        return scores[playerIndex];
    }


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(scores.length);
        parcel.writeIntArray(scores);
        parcel.writeInt(pickerIndex);
        parcel.writeInt(partnerIndex);
        parcel.writeInt(dealerIndex);
        parcel.writeInt(crackerIndex);
        parcel.writeInt(recrackerIndex);
        parcel.writeInt(isDoubled ? 1 : 0);
        parcel.writeInt(lastHand ? 1 : 0);
        parcel.writeInt(isScored ? 1 : 0);
        parcel.writeInt(getPickerPointsIndex());
        switch (pickerTricksTaken) {
            case None:
                parcel.writeInt(0);
                break;
            case Some:
                parcel.writeInt(1);
                break;
            case All:
                parcel.writeInt(2);
                break;
            default:
                parcel.writeInt(-1);
        }
        switch (gameType) {
            case Leaster:
                parcel.writeInt(1);
                break;
            case MauerCheck:
                parcel.writeInt(2);
                break;
            default:
                parcel.writeInt(0);
        }
    }

    /**
     * Helper function to determine if the player at the provided index is
     * part of the current hand.
     *
     * @param playerIndex The index of the player to test.
     * @param dealerIndex The index of the player who is dealing this hand.
     * @param totalPlayers The number of players in the game.
     *
     * @return true if the player is playing this hand; false if sitting out.
     */
    public static boolean isPlayerPlaying(int playerIndex, int dealerIndex, int totalPlayers) {
        int sittingOutCount = totalPlayers - 5;

        boolean isPlaying = (totalPlayers <= 5) ||
                (!((playerIndex <= dealerIndex && playerIndex > dealerIndex - sittingOutCount)
                 || playerIndex > totalPlayers + dealerIndex - sittingOutCount));

        Log.d(TAG, String.format("For player %d with dealer %d: %s", playerIndex, dealerIndex, isPlaying));
        return isPlaying;
    }


    @SuppressWarnings({"UnusedDeclaration"})
    public static final Creator<Hand> CREATOR
            = new Creator<Hand>() {
        public Hand createFromParcel(Parcel parcel) {
            return new Hand(parcel);
        }

        public Hand[] newArray(int size) {
            return new Hand[size];
        }
    };

}
