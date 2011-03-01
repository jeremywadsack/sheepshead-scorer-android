package com.wadsack.android.sheepshead.scorer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: Jeremy Wadsack
 */
public class Player implements Parcelable {


    public String FullName;

    /**
     * The initial or initials of the player.
     */
    public final String Initial;


    public Player(Parcel parcel) {
        Initial = parcel.readString();
        FullName = parcel.readString();
    }

    public Player(CharSequence initial, CharSequence name) {
        Initial = initial.toString();
        FullName = name.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Initial);
        parcel.writeString(FullName);
    }

    @Override
    // Override to make it render the names when used to back lists
    public String toString() {
        return FullName;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static final Creator<Player> CREATOR
            = new Creator<Player>() {
        public Player createFromParcel(Parcel parcel) {
            return new Player(parcel);
        }

        public Player[] newArray(int size) {
            return new Player[size];
        }
    };


}
