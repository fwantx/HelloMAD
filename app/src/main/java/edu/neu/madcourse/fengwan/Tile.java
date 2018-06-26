package edu.neu.madcourse.fengwan;


import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.Deque;
import java.util.Stack;

public class Tile {

    private View mView;

    private Deque<Tile> mSelectedTiles;

    private char letter;
    private int phase;
    private int status;
    private int index;

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public Tile(char letter, int index) {
        this.letter = letter;
        this.index = index;
        this.phase = 1;
        this.status = 0;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        this.mView = view;
    }

    public Deque<Tile> getSelectedTiles() {
        return mSelectedTiles;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setSelectedTiles(Deque<Tile> selectedTiles) {
        this.mSelectedTiles = selectedTiles;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void updateDrawableState() {
        if (mView == null) return;
        int level = getLevel();

        if (phase == 1) {
            if (mView.getVisibility() == View.INVISIBLE) {
                mView.setVisibility(View.VISIBLE);
            }
            if (mView.getBackground() != null) {
                if (this.getStatus() == 1) {
                    mView.getBackground().setLevel(1);
                } else if (this.getStatus() == 0) {
                    mView.getBackground().setLevel(0);
                }
            }
        } else if (phase == 2) {
            if (mView.getBackground() != null) {
                if (this.getStatus() == 2) {
                    mView.getBackground().setLevel(1);
                } else if (this.getStatus() == 1) {
                    mView.getBackground().setLevel(0);
                } else if (this.getStatus() == 0) {
                    mView.setVisibility(View.INVISIBLE);
                }
            }
        }

        if (mView instanceof ImageButton) {
            Drawable drawable = ((ImageButton) mView).getDrawable();
            drawable.setLevel(level);
        }
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    private int getLevel() {
        return this.letter - 'a';
    }
}