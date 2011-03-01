package com.wadsack.android.sheepshead.scorer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Author: Jeremy Wadsack
 */
public class NewPlayerActivity extends GuiceActivity {
    private static final String TAG = NewPlayerActivity.class.getSimpleName();

    @InjectView(R.id.name)
    private EditText nameView;

    @InjectView(R.id.initial)
    private EditText initialView;

    @InjectView(R.id.done)
    private Button doneButton;


    public static final String EXTRA_PLAYER = "An optional player; provided when editing. Returned when done.";
    @InjectExtra(value = EXTRA_PLAYER, optional = true)
    private Player player;

    public static final String EXTRA_PLAYER_INDEX = "An optional index for the player provided when editing. Returned when done.";
    @InjectExtra(value = EXTRA_PLAYER_INDEX, optional = true)
    private int playerIndex = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_player);

        if (savedInstanceState != null) {
            player = savedInstanceState.getParcelable(EXTRA_PLAYER);
        }

        if (player != null) {
            nameView.setText(player.FullName);
            initialView.setText(player.Initial);
            initialHasBeenEdited = true;
        }

        doneButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                done();
            }
        });

        nameView.setOnKeyListener(keyListener);
        initialView.setOnKeyListener(keyListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        storePlayer();
        outState.putParcelable(EXTRA_PLAYER, player);
    }

    private void done() {
        storePlayer();
        finish();
    }

    private void storePlayer() {
        String name = nameView.getText().toString().trim();
        if (!name.equals("")) {
            String initial = initialView.getText().toString().trim();
            if (initial.equals("")) {
                initial = name.substring(0,1);
            }
            Intent i = new Intent();
            Player player = new Player(initial, name);
            Log.d(TAG, "Created player (" + player.Initial + ") " + player.FullName);
            i.putExtra( EXTRA_PLAYER, player);
            if (playerIndex > -1) {
                i.putExtra( EXTRA_PLAYER_INDEX, playerIndex);
            }
            setResult(0, i);
        }
    }


    /*
     * Handle key strokes from the edit fields
     */
    private boolean initialHasBeenEdited = false;
    private final View.OnKeyListener keyListener = new View.OnKeyListener() {
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (view.getId() == nameView.getId()) {
                // auto-fill initial from first letter in name when typing
                CharSequence name = ((EditText)view).getText();
                CharSequence initial = initialView.getText();
                if (name != null && name.length() > 0) {
                    if (initial == null || initial.length() == 0) {
                          initialView.setText(name.subSequence(0, 1));
                          // todo: validation warning if this is a duplicate initial?
                    }
                } else if (!initialHasBeenEdited) {
                    initialView.setText("");
                }
            } else {
                initialHasBeenEdited = true;
            }
            // complete on ENTER key
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                done();
                return true;
            }
            return false;
        }
    };


}
