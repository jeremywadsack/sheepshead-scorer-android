package com.wadsack.android.sheepshead.scorer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Jeremy Wadsack
 */
public class ScoreCard implements Parcelable {

    /**
     * Provides a list of players in the current game.
     */
    public final ArrayList<Player> Players = new ArrayList<Player>();

    /**
     * Provides a series of scores for each hand in the game.
     */
    public final List<Hand> Hands = new ArrayList<Hand>();


    /**
     * Reconstructs a Scorecard from a parcel.
     * @param parcel    The parcel passed in an intent.
     */
    public ScoreCard(Parcel parcel) {
        Players.clear();
        for (Parcelable p : parcel.readParcelableArray(Player.class.getClassLoader())) {
            Players.add((Player)p);
        }
        Hands.clear();
        for (Parcelable h : parcel.readParcelableArray(Hand.class.getClassLoader())) {
            Hands.add((Hand)h);
        }
    }

    /**
     * Creates a new, blank score card
     */
    public ScoreCard() {
    }

    /**
     * Adds a hand to this score card
     * @param hand The hand to add; should be constructed with this scorecard
     */
    public void addHand(Hand hand) {
        Hands.add(hand);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelableArray(Players.toArray(new Player[Players.size()]), 0);
        parcel.writeParcelableArray(Hands.toArray(new Hand[Hands.size()]), 0);
    }

    public static final Creator<ScoreCard> CREATOR
            = new Creator<ScoreCard>() {
        public ScoreCard createFromParcel(Parcel parcel) {
            return new ScoreCard(parcel);
        }

        public ScoreCard[] newArray(int size) {
            return new ScoreCard[size];
        }
    };
}
