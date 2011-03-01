package com.wadsack.android.sheepshead.scorer;

/**
 * Author: jeremywadsack
 */

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HandScoringFeature {

    private Hand hand;
    private Hand.Option tricksTaken = Hand.Option.Some;
    private Hand.TrickPointsRange pickersPoints;

    @BeforeMethod
    public void context() {
        hand = new Hand(5);
        hand.setPickerIndex(0);
        tricksTaken = Hand.Option.Some;
        pickersPoints = null;
    }

    @DataProvider(name = "pickerPartnerTestData")
    public Object[][] pickerPartnerTestData() {
        return new Object[][] {
                {Hand.TrickPointsRange.ZeroToThirty, -8, -4, 4},
                {Hand.TrickPointsRange.ThirtyOneToFiftyNine, -4, -2, 2},
                {Hand.TrickPointsRange.Sixty, -4, -2, 2},
                {Hand.TrickPointsRange.SixtyOneToNinety, 2, 1, -1},
                {Hand.TrickPointsRange.NinetyOneToOneHundredTwenty, 4, 2, -2},
        };
    }

    @Test(dataProvider = "pickerPartnerTestData")
    public void Picker_has_a_Partner_Should_Score_Correctly( Hand.TrickPointsRange range, int picker, int partner, int opponents) throws InvalidHandException {
        Given_that_the_Picker_has_a_Partner();
        Given_that_the_Pickers_team_has_trickPoints_points(range);
        Given_that_the_Pickers_team_took_one_or_more_tricks();
        When_the_hand_is_scored();
        Then_the_pickers_score_should_change_by_arg_points(picker);
        Then_the_partners_score_should_change_by_arg_points(partner);
        Then_the_opponents_scores_should_change_by_arg_points(opponents);
    }

    @Test
    public void Picker_and_Partner_Take_No_Tricks() throws InvalidHandException {
        Given_that_the_Picker_has_a_Partner();
        Given_that_the_Pickers_team_has_trickPoints_points(Hand.TrickPointsRange.ZeroToThirty);
        Given_that_the_Pickers_team_took_no_tricks();
        When_the_hand_is_scored();
        Then_the_pickers_score_should_change_by_arg_points(-16);
        Then_the_partners_score_should_change_by_arg_points(-8);
        Then_the_opponents_scores_should_change_by_arg_points(8);
    }

    @Test
    public void Picker_and_Partner_Take_All_Tricks() throws InvalidHandException {
        Given_that_the_Picker_has_a_Partner();
        Given_that_the_Pickers_team_has_trickPoints_points(Hand.TrickPointsRange.NinetyOneToOneHundredTwenty);
        Given_that_the_Pickers_team_took_all_tricks();
        When_the_hand_is_scored();
        Then_the_pickers_score_should_change_by_arg_points(8);
        Then_the_partners_score_should_change_by_arg_points(4);
        Then_the_opponents_scores_should_change_by_arg_points(-4);
    }

    @DataProvider(name = "pickerAloneTestData")
    public Object[][] pickerAloneTestData() {
        return new Object[][] {
                {Hand.TrickPointsRange.ZeroToThirty, -16, 4},
                {Hand.TrickPointsRange.ThirtyOneToFiftyNine, -8, 2},
                {Hand.TrickPointsRange.Sixty, -8, 2},
                {Hand.TrickPointsRange.SixtyOneToNinety, 4, -1},
                {Hand.TrickPointsRange.NinetyOneToOneHundredTwenty, 8, -2},
        };
    }

    @Test(dataProvider = "pickerAloneTestData")
    public void Picker_goes_alone_Partner_Should_Score_Correctly( Hand.TrickPointsRange range, int picker, int opponents) throws InvalidHandException {
        Given_that_the_Pickers_team_has_trickPoints_points(range);
        Given_that_the_Pickers_team_took_one_or_more_tricks();
        When_the_hand_is_scored();
        Then_the_pickers_score_should_change_by_arg_points(picker);
        Then_the_opponents_scores_should_change_by_arg_points(opponents);
    }

    @Test
    public void Picker_Takes_No_Tricks_Alone() throws InvalidHandException {
        Given_that_the_Pickers_team_has_trickPoints_points(Hand.TrickPointsRange.ZeroToThirty);
        Given_that_the_Pickers_team_took_no_tricks();
        When_the_hand_is_scored();
        Then_the_pickers_score_should_change_by_arg_points(-32);
        Then_the_opponents_scores_should_change_by_arg_points(8);
    }

    @Test
    public void Picker_Takes_All_Tricks_Alone() throws InvalidHandException {
        Given_that_the_Pickers_team_has_trickPoints_points(Hand.TrickPointsRange.NinetyOneToOneHundredTwenty);
        Given_that_the_Pickers_team_took_all_tricks();
        When_the_hand_is_scored();
        Then_the_pickers_score_should_change_by_arg_points(16);
        Then_the_opponents_scores_should_change_by_arg_points(-4);
    }


    // Assertions (nulls and such)

    // Next hand doubler on 60-60

    // Mauer scoring & next hand doubler

    // Liester scoring & next hand doubler




    public void Given_that_the_Picker_has_a_Partner() {
        hand.setPartnerIndex(1);
    }

    public void Given_that_the_Pickers_team_has_trickPoints_points(Hand.TrickPointsRange trickPoints) {
        pickersPoints = trickPoints;
    }


    public void Given_that_the_Pickers_team_took_one_or_more_tricks() {
        tricksTaken = Hand.Option.Some;
    }

    public void Given_that_the_Pickers_team_took_no_tricks() {
        tricksTaken = Hand.Option.None;
    }

    public void Given_that_the_Pickers_team_took_all_tricks() {
        tricksTaken = Hand.Option.All;
    }

    public void When_the_hand_is_scored() throws InvalidHandException {
        hand.scoreHand(tricksTaken, pickersPoints);
    }

    public void Then_the_pickers_score_should_change_by_arg_points( int arg ) {
        Assert.assertEquals(
            hand.getScore(0),
            arg
        );
    }

    public void Then_the_partners_score_should_change_by_arg_points( int arg ) {
        Assert.assertEquals(
            hand.getScore(1),
            arg
        );
    }

    public void Then_the_opponents_scores_should_change_by_arg_points( int arg ) {
        Assert.assertEquals(
            hand.getScore(2),
            arg
        );
        Assert.assertEquals(
            hand.getScore(3),
            arg
        );
        Assert.assertEquals(
            hand.getScore(4),
            arg
        );
    }


}
