package com.wadsack.android.sheepshead.scorer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.wadsack.android.widget.DragListener;
import com.wadsack.android.widget.DragNDropListView;
import com.wadsack.android.widget.DropListener;
import roboguice.activity.GuiceListActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Jeremy Wadsack
 */
public class PickPlayersActivity extends GuiceListActivity {
    private static final String TAG = PickPlayersActivity.class.getSimpleName();


    // Request codes
    private static final int NEW_PLAYER = 1;

    public static final String EXTRA_PLAYERS = "Array of Players";
    @InjectExtra(value = EXTRA_PLAYERS, optional = true)
    private ArrayList<Player> players = new ArrayList<Player>();

    @InjectView(R.id.addPlayer)
    private Button addButton;

    @InjectView(R.id.done)
    private Button doneButton;


    @Override
    public void onCreate(Bundle savedInstanceData) {
        super.onCreate(savedInstanceData);
        setContentView(R.layout.player_list);

        if (savedInstanceData != null) {
            players = savedInstanceData.getParcelableArrayList(EXTRA_PLAYERS);
        }
        if (players != null && players.size() >= 5) {
            doneButton.setEnabled(true);
        }

        // todo: list known players and allow them to be re-ordered and/or removed from this game

        setListAdapter(new PlayerAdapter(this, R.layout.player_list_item, players));

        registerForContextMenu(getListView());

        addButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                // todo: make new player activity a dialog or make it the last element in the list?
                startActivityForResult(new Intent(PickPlayersActivity.this, NewPlayerActivity.class), NEW_PLAYER);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent i = new Intent();
                i.putParcelableArrayListExtra(EXTRA_PLAYERS, players);
                setResult(0, i);
                finish();
            }
        });

        ListView listView = getListView();
        if (listView instanceof DragNDropListView) {
        	((DragNDropListView) listView).setDropListener(dropListener);
        	((DragNDropListView) listView).setDragListener(dragListener);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_PLAYERS, players);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case NEW_PLAYER:
                if (intent != null) {
                    final Player p = intent.getParcelableExtra(NewPlayerActivity.EXTRA_PLAYER);
                    if (p != null) {
                        final int index = intent.getIntExtra(NewPlayerActivity.EXTRA_PLAYER_INDEX, -1);

                        if (index < 0 || index >= players.size()) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    players.add(p);
                                    ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    players.set(index, p);
                                    ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                                }
                            });
                        }
                    }
                    if (players.size() >= 5) {
                        doneButton.setEnabled(true);
                    }
                }
                break;
        }
    }

    @Override
    protected void onListItemClick(ListView list, View item, int position, long id) {
        Intent i = new Intent(PickPlayersActivity.this, NewPlayerActivity.class);
        i.putExtra(NewPlayerActivity.EXTRA_PLAYER, players.get(position));
        i.putExtra(NewPlayerActivity.EXTRA_PLAYER_INDEX, position);
        startActivityForResult(i, NEW_PLAYER);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.player_list_context, menu);

        // Setup the menu header
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(players.get(info.position).FullName);
        } catch (ClassCastException e) {
            Log.e(TAG, "MenuInfo was an unexpected type", e);
        }
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position = info.position;
        switch (item.getItemId()) {
            case R.id.edit:
                Intent i = new Intent(PickPlayersActivity.this, NewPlayerActivity.class);
                i.putExtra(NewPlayerActivity.EXTRA_PLAYER, players.get(position));
                i.putExtra(NewPlayerActivity.EXTRA_PLAYER_INDEX, position);
                startActivityForResult(i, NEW_PLAYER);
                return true;
            case R.id.delete:
                runOnUiThread(new Runnable() {
                    public void run() {
                        players.remove(position);
                        ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                        if (players.size() < 5) {
                            doneButton.setEnabled(false);
                        }

                    }
                });
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    private DropListener dropListener = new DropListener() {
        public void onDrop(final int from, final int to) {
            runOnUiThread(new Runnable() {
                public void run() {
                    players.add(to, players.remove(from));
                    ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                }
            });
        }
    };

    private DragListener dragListener = new DragListener() {
        int to = ListView.INVALID_POSITION;
        int from = ListView.INVALID_POSITION;

        public void onStartDrag(View itemView) {
            itemView.setVisibility(View.GONE);
        }

        public void onDrag(int x, int y, ListView listView) {
        }

        public void onStopDrag(View itemView) {
            itemView.setVisibility(View.VISIBLE);
        }
    };


    /**
     * A custom adapter class to map the player to the list item
     *
     * Man, a lambda expression would work great here.
     * new ArrayAdapter<Player>(this, players, R.layout.player_list_item, x => {x.Name => R.id.name, ...}
     */
    protected class PlayerAdapter extends ArrayAdapter<Player> {
        private final int resourceId;
        private final List<Player> players;
        private final PickPlayersActivity context;


        public PlayerAdapter(Context context, int resourceId, List<Player> players) {
            super(context, 0, players);
            this.resourceId = resourceId;
            this.players = players;
            this.context = (PickPlayersActivity)context;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if (position >= players.size()) {
                return null;
            }

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(resourceId, null);
            }

            TextView nameView = (TextView)convertView.findViewById(R.id.name);
            TextView initialView = (TextView)convertView.findViewById(R.id.initial);

            final Player player = players.get(position);
            if (position == 0) {
                nameView.setText(String.format(getString(R.string.dealer_annotation), player.FullName));
            } else {
                nameView.setText(player.FullName);
            }
            initialView.setText(player.Initial);

            return convertView;
        }
    }

}
