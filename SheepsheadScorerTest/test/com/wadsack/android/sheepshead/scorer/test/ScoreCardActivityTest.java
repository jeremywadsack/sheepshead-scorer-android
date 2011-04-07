package com.wadsack.android.sheepshead.scorer.test;

import com.wadsack.android.sheepshead.scorer.Hand;
import com.wadsack.android.sheepshead.scorer.InvalidHandException;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RobolectricTestRunner.class)
public class ScoreCardActivityTest {

    @Test
    public void shouldScoreSixPlayersCorrectly() throws InvalidHandException {
        Hand hand = new Hand(6);
        hand.setPickerIndex(0);
        hand.setPartnerIndex(1);
        hand.dealerIndex = 3;
        hand.scoreHand(Hand.Option.Some, Hand.TrickPointsRange.SixtyOneToNinety);
        Assert.assertEquals(2, hand.getScore(0));
        Assert.assertEquals(1, hand.getScore(1));
        Assert.assertEquals(-1, hand.getScore(2));
        Assert.assertEquals(0, hand.getScore(3));
        Assert.assertEquals(-1, hand.getScore(4));
    }
}
