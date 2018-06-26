package edu.neu.madcourse.fengwan;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

public class GameFragment extends Fragment {
    // Data structures go here...
    static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3,
            R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8,
            R.id.large9,};
    static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3,
            R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8,
            R.id.small9,};

    private Tile mEntireBoard = new Tile('a', 0);
    private Tile mLargeTiles[] = new Tile[9];
    private Tile mSmallTiles[][] = new Tile[9][9];
    private int phase;
    private int timerValue;

    private Set<Tile> mAvailable = new HashSet<Tile>();
    private Runnable runnable;
    private Handler handler = new Handler();
    private TextView timer;
    private Set<String> dictionary;

    public void setDictionary(Set<String> dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        initGame();

//        dictionary = ((GameActivity)getActivity()).getDictionary();
//        Log.d("XXX XXX YYY YYY ZZZ ZZZ", "onCreate: " + dictionary.size());
        runnable = new Runnable() {
            @Override
            public void run() {
                startTimer();
            }
        };
    }

    private void startTimer() {
        timer.setText(String.valueOf(timerValue));
        if (timerValue == 0) {
            handler.removeCallbacks(runnable);
        } else {
            if (timerValue == 20) {
                clearBadWords();
                phase = 2;
                updateAvailable();
                updateAllTiles();
            }
            timerValue--;
            handler.postDelayed(runnable, 1000);
        }
    }

    private void clearBadWords() {
        for (int large = 0; large < 9; large++) {
            Tile largeTile = mLargeTiles[large];
            String word = getWordOfLargeTile(large, 1);
            if (!word.isEmpty() && !dictionary.contains(word)) {
                largeTile.getSelectedTiles().clear();
                for (int small = 0; small < 9; small++) {
                    mSmallTiles[large][small].setStatus(0);
                }
            }
        }
    }

    private String getWordOfLargeTile(int large, int phase) {
        Tile board = phase == 1 ? mLargeTiles[large] : mEntireBoard;
        String word = "";
        for (Tile tile : board.getSelectedTiles()) {
            word += tile.getLetter();
        }
        return word;
    }

    private void clearAvailable() {
        mAvailable.clear();
    }

    private void addAvailable(Tile tile) {
        mAvailable.add(tile);
    }

    public boolean isAvailable(Tile tile) {
        return mAvailable.contains(tile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_game, container, false);
        initViews(rootView);
        updateAllTiles();

        timer = rootView.findViewById(R.id.timer);
        timer.setText(String.valueOf(timerValue));
        return rootView;
    }

    public void startTimerNow() {
        runnable.run();
    }

    private void initViews(View rootView) {
        mEntireBoard.setView(rootView);
        final Deque<Tile> selectedAgain = mEntireBoard.getSelectedTiles();
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIds[large]);
            Tile largeTile = mLargeTiles[large];
            largeTile.setView(outer);
            final Deque<Tile> selected = largeTile.getSelectedTiles();

            for (int small = 0; small < 9; small++) {
                ImageButton inner = (ImageButton) outer.findViewById
                        (mSmallIds[small]);
                final int fLarge = large;
                final int fSmall = small;
                final Tile smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (phase == 1) {
                            if (isAvailable(smallTile)) {
                                makeMove(fLarge, fSmall);
                            } else if (smallTile == selected.peekLast()) {
                                cancelMove(fLarge);
                            }
                        } else if (phase == 2) {
                            if (isAvailable(smallTile)) {
                                makeMove(fLarge, fSmall);
                            } else if (smallTile == selectedAgain.peekLast()) {
                                cancelMove(fLarge);
                            }
                        }
                        String word = getWordOfLargeTile(fLarge, phase);
                        if (dictionary.contains(word)) {
                            ((GameActivity)getActivity()).playSound();
                        }
                    }
                });
            }
        }
    }

    private void makeMove(int large, int small) {
        Tile smallTile = mSmallTiles[large][small];
        Tile largeTile = mLargeTiles[large];
        if (phase == 1) {
            Deque<Tile> selected = largeTile.getSelectedTiles();
            smallTile.setStatus(1);
            selected.addLast(smallTile);
        } else if (phase == 2) {
            Deque<Tile> selectedAgain = mEntireBoard.getSelectedTiles();
            smallTile.setStatus(2);
            selectedAgain.addLast(smallTile);
        }
        updateAvailable();
        updateAllTiles();
    }

    public void cancelMove(int large) {
        Tile largeTile = mLargeTiles[large];
        if (phase == 1) {
            Deque<Tile> selected = largeTile.getSelectedTiles();
            Tile last = selected.pollLast();
            last.setStatus(0);
        } else if (phase == 2) {
            Deque<Tile> selectedAgain = mEntireBoard.getSelectedTiles();
            Tile last = selectedAgain.pollLast();
            last.setStatus(1);
        }
        updateAvailable();
        updateAllTiles();
    }

    public void restartGame() {
        initGame();
        initViews(getView());
        updateAllTiles();

        handler.removeCallbacks(runnable);
        startTimerNow();
    }

    public void initGame() {
        char[] char_arr = {
                'a', 'c', 'c', 'p', 'm', 'o', 'a', 'n', 'y',    // accompany
                'a', 'c', 'c', 'd', 'r', 'o', 'i', 'n', 'g',    // according
                'a', 't', 't', 'b', 'i', 'r', 'u', 't', 'e',    // attribute
                'p', 'm', 'i', 'o', 'r', 't', 't', 'n', 'a',    // important
                'l', 'o', 'p', 'l', 'u', 't', 'n', 'o', 'i',    // pollution
                'u', 'd', 'e', 'c', 'a', 't', 'n', 'o', 'i',    // education
                'b', 'n', 'i', 'e', 'n', 'n', 'g', 'i', 'g',    // beginning
                'd', 'i', 'o', 'e', 'c', 'u', 'l', 'i', 's',    // delicious
                'l', 'n', 'i', 'i', 't', 'n', 'g', 'h', 'g',    // lightning
        };

        Random random = new Random();
        mEntireBoard = new Tile('a', 0);
        // Create all the tiles
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large] = new Tile('a', large);
            for (int small = 0; small < 9; small++) {
                int index = large * 9 + small;
                mSmallTiles[large][small] = new Tile(char_arr[index], index);
            }
            mLargeTiles[large].setSelectedTiles(new ArrayDeque<Tile>());
        }
        mEntireBoard.setSelectedTiles(new ArrayDeque<Tile>());

        phase = 1;
        timerValue = 40;
        updateAvailable();
    }

    private boolean areAdjacentTiles(int a, int b) {
        int ra = a / 3;
        int ca  = a % 3;
        int rb = b / 3;
        int cb = b % 3;
        return Math.abs(ra - rb) <= 1 && Math.abs(ca - cb) <= 1;
    }

    private boolean isAlreadySelected(int large, int small) {
        Tile largeTile = mLargeTiles[large];
        Tile tile = mSmallTiles[large][small];
        Deque<Tile> selected = largeTile.getSelectedTiles();
        return selected.contains(tile);
    }

    private boolean isAlreadySelected(int large) {
        Tile largeTile = mLargeTiles[large];
        Deque<Tile> localSelected = largeTile.getSelectedTiles();
        Deque<Tile> globalSelected = mEntireBoard.getSelectedTiles();
        boolean already = false;
        for (Tile t :
                localSelected) {
            if (globalSelected.contains(t)) {
                already = true;
                break;
            }
        }
        return already;
    }

    private void updateAvailable() {
        clearAvailable();
        Deque<Tile> selectedAgain = mEntireBoard.getSelectedTiles();
        Tile lastAgain = selectedAgain.peekLast();

        for (int l = 0; l < 9; l++) {
            Tile largeTile = mLargeTiles[l];
            Deque<Tile> selected = largeTile.getSelectedTiles();
            if (phase == 1) {
                for (int s = 0; s < 9; s++) {
                    Tile tile = mSmallTiles[l][s];
                    if (selected.isEmpty()) {
                        addAvailable(tile);
                    } else {
                        Tile last = selected.peekLast();
                        if (areAdjacentTiles(s, last.getIndex() % 9) && !isAlreadySelected(l, s)) {
                            addAvailable(tile);
                        }
                    }
                }
            } else if (phase == 2) {
                if (isAlreadySelected(l) || selected.isEmpty()) {
                    continue;
                } else {
                    if (lastAgain == null || areAdjacentTiles(lastAgain.getIndex() / 9, l)) {
                        for (Tile tile: selected) {
                            addAvailable(tile);
                        }
                    }
                }
            }
        }
    }

    private void updateAllTiles() {
        mEntireBoard.updateDrawableState();
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large].updateDrawableState();
            for (int small = 0; small < 9; small++) {
                if (phase == 2) mSmallTiles[large][small].setPhase(2);
                mSmallTiles[large][small].updateDrawableState();
            }
        }
    }

    /** Create a string containing the state of the game. */
    public String getState() {
        handler.removeCallbacks(runnable);
        StringBuilder builder = new StringBuilder();
        builder.append(phase);
        builder.append(',');
        builder.append(timerValue);
        builder.append(',');
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                Tile smallTile = mSmallTiles[large][small];
                builder.append(smallTile.getPhase());
                builder.append(',');
                builder.append(smallTile.getIndex());
                builder.append(',');
                builder.append(smallTile.getLetter());
                builder.append(',');
                builder.append(smallTile.getStatus());
                builder.append(',');
            }
        }
        for (int large = 0; large < 9; large++) {
            Tile largeTile = mLargeTiles[large];
            builder.append(largeTile.getSelectedTiles().size());
            builder.append(',');
            for (Tile t : largeTile.getSelectedTiles()) {
                builder.append(t.getIndex());
                builder.append(',');
            }
        }
        builder.append(mEntireBoard.getSelectedTiles().size());
        builder.append(',');
        for (Tile t : mEntireBoard.getSelectedTiles()) {
            builder.append(t.getIndex());
            builder.append(',');
        }
        return builder.toString();
    }

    /** Restore the state of the game from the given string. */
    public void putState(String gameData) {
        String[] fields = gameData.split(",");
        int index = 0;
        phase = Integer.parseInt(fields[index++]);
        timerValue = Integer.parseInt(fields[index++]);
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                Tile smallTile = mSmallTiles[large][small];
                smallTile.setPhase(Integer.parseInt(fields[index++]));
                smallTile.setIndex(Integer.parseInt(fields[index++]));
                smallTile.setLetter(fields[index++].charAt(0));
                smallTile.setStatus(Integer.parseInt(fields[index++]));
            }
        }
        for (int large = 0; large < 9; large++) {
            int size = Integer.parseInt(fields[index++]);
            Tile largeTile = mLargeTiles[large];
            for (int i = 0; i < size; i++) {
                int tileIndex = Integer.parseInt(fields[index++]);
                Tile smallTile = mSmallTiles[tileIndex / 9][tileIndex % 9];
                largeTile.getSelectedTiles().addLast(smallTile);
            }
        }
        int size = Integer.parseInt(fields[index++]);
        for (int i = 0; i < size; i++) {
            int tileIndex = Integer.parseInt(fields[index++]);
            Tile smallTile = mSmallTiles[tileIndex / 9][tileIndex % 9];
            mEntireBoard.getSelectedTiles().addLast(smallTile);
        }
        updateAvailable();
        updateAllTiles();
    }
}