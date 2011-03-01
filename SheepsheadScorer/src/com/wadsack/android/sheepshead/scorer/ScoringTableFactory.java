package com.wadsack.android.sheepshead.scorer;

import java.util.Hashtable;

/**
 * A class to generate scoring table data for different games and different situations
 * Author: Jeremy Wadsack
 */
public class ScoringTableFactory {

    private static Hashtable<Hand.TrickPointsRange,int[]> pickerPartner;
    private static Hashtable<Hand.TrickPointsRange,int[]> pickerAlone;
    private static Hashtable<Hand.TrickPointsRange,int[]> leaster;
    private static Hashtable<Hand.TrickPointsRange,int[]> mauerCheck;


    /**
     * Gets a scoring table for the specified game.
     *
     * Scoring tables are a Dictionary of values indexed on HandTrickPointRange. For each
     * indexed entry, the table holds a three dimensional array of score change values.
     * The first value is for the picker (in a regular game) or the loser (in mauer check
     * or leaster), the second is for the partner (when a partner exists) and the
     * third is for all other players.
     *
     * @param gameType A regular game or Leaster or Mauer Check
     * @param hasPartner In a regular game, whether the picker had a partner
     * @return A scoring table as a Dictionary indexed on TrickPointsRange with values for picker, partner and others
     */
    public static Hashtable<Hand.TrickPointsRange, int[]> getScoringTable(Hand.GameType gameType, boolean hasPartner) {

        // todo: boolean doubleOnTheBump and other things from shared preferences

        if (gameType == Hand.GameType.Leaster) {
            if (leaster == null) {
                leaster = makeLeasterMauerFiveHand();
            }
            return leaster;
        }
        else if(gameType == Hand.GameType.MauerCheck) {
            if (mauerCheck == null) {
                mauerCheck = makeLeasterMauerFiveHand();
            }
            return mauerCheck;
        }

        if (hasPartner) {
            if (pickerPartner == null) {
                pickerPartner = makePickerPartnerDoubleOnTheBump248FiveHand();
            }
            return pickerPartner;
        }

        if (pickerAlone == null) {
            pickerAlone = makePickerAloneDoubleOnTheBump248FiveHand();
        }
        return pickerAlone;
    }

    /**
     * Clears any cached scoring tables so that they will be re-evaluated in the
     * factory based on the current settings.
     *
     * Usually called from the preferences when settings are changed.
     */
    public void clearCache() {
        pickerAlone = null;
        pickerPartner = null;
        leaster = null;
        mauerCheck = null;
    }

    private static Hashtable<Hand.TrickPointsRange, int[]> makeLeasterMauerFiveHand() {
        Hashtable<Hand.TrickPointsRange,int[]> table = new Hashtable<Hand.TrickPointsRange, int[]>();
        table.put(Hand.TrickPointsRange.ZeroToThirty,                   new int[] {-4,  0,  1});
        table.put(Hand.TrickPointsRange.ThirtyOneToFiftyNine,           new int[] {-4,  0,  1});
        table.put(Hand.TrickPointsRange.Sixty,                          new int[] {-4,  0,  1});
        table.put(Hand.TrickPointsRange.SixtyOneToNinety,               new int[] {-4,  0,  1});
        table.put(Hand.TrickPointsRange.NinetyOneToOneHundredTwenty,    new int[] {-4,  0,  1});
        return table;
    }


    private static Hashtable<Hand.TrickPointsRange, int[]> makePickerPartnerDoubleOnTheBump248FiveHand() {
        Hashtable<Hand.TrickPointsRange,int[]> table = new Hashtable<Hand.TrickPointsRange, int[]>();
        table.put(Hand.TrickPointsRange.ZeroToThirty,                   new int[] {-8, -4,  4});
        table.put(Hand.TrickPointsRange.ThirtyOneToFiftyNine,           new int[] {-4, -2,  2});
        table.put(Hand.TrickPointsRange.Sixty,                          new int[] {-4, -2,  2});
        table.put(Hand.TrickPointsRange.SixtyOneToNinety,               new int[] { 2,  1, -1});
        table.put(Hand.TrickPointsRange.NinetyOneToOneHundredTwenty,    new int[] { 4,  2, -2});
        return table;
    }

    private static Hashtable<Hand.TrickPointsRange, int[]> makePickerAloneDoubleOnTheBump248FiveHand() {
        Hashtable<Hand.TrickPointsRange,int[]> table = new Hashtable<Hand.TrickPointsRange, int[]>();
        table.put(Hand.TrickPointsRange.ZeroToThirty,                   new int[] {-16, 0,  4});
        table.put(Hand.TrickPointsRange.ThirtyOneToFiftyNine,           new int[] {-8, 0,  2});
        table.put(Hand.TrickPointsRange.Sixty,                          new int[] {-8, 0,  2});
        table.put(Hand.TrickPointsRange.SixtyOneToNinety,               new int[] { 4, 0, -1});
        table.put(Hand.TrickPointsRange.NinetyOneToOneHundredTwenty,    new int[] { 8, 0, -2});
        return table;
    }

}
