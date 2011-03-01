package com.wadsack.android.sheepshead.scorer;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;

/**
 * Author: Jeremy Wadsack
 */
public class TestActivity extends GuiceActivity {

    @InjectView(R.id.header)
    private TableLayout header;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        TableRow headerRow = new TableRow(this);
        for (int i = 0; i < 5; i++) {
            TextView playerInitial = new TextView(this);
            playerInitial.setText("  S");
            playerInitial.setTypeface(playerInitial.getTypeface(), Typeface.BOLD);
            headerRow.addView(playerInitial, new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        }
        TextView handDetails = new TextView(this);
        handDetails.setText(R.string.hand_details);
        handDetails.setTypeface(handDetails.getTypeface(), Typeface.BOLD);
        headerRow.addView(handDetails);
        header.addView(headerRow);

    }
}