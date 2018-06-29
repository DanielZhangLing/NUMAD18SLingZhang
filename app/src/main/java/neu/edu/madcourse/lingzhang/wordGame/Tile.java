package neu.edu.madcourse.lingzhang.wordGame;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;

import java.util.Stack;

import neu.edu.madcourse.lingzhang.R;

public class Tile {
    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getmWord() {
        return mWord;
    }

    public void setmWord(String mWord) {
        this.mWord = mWord;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Stack<Integer> getStack() {
        return stack;
    }

    public void setStack() {
        stack = new Stack<>();
    }

    private final GameFragment mGame;
    private char letter = '{';
    private View mView;
    private Tile mSubTiles[];
    private boolean isSelected = false;
    private String mWord = "";
    private boolean isAvailable = true;
    private Stack<Integer> stack;

    public Tile(GameFragment game) {
        this.mGame = game;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        this.mView = view;
        updateDrawableState();
    }

    public Tile[] getSubTiles() {
        return mSubTiles;
    }

    public void setSubTiles(Tile[] subTiles) {
        this.mSubTiles = subTiles;
    }

    public void updateDrawableState() {
        if (mView == null) return;
        int level = getLevel();
        if (mView.getBackground() != null) {
            mView.getBackground().setLevel(level);
        }
        if (mView instanceof ImageButton) {
            Drawable drawable = ((ImageButton) mView).getDrawable();
            drawable.setLevel(level);
        }
    }

    private int getLevel() {
        if (isSelected)
            return letter + 4;
        return letter - 96;
    }

    public void animate() {
        Animator anim = AnimatorInflater.loadAnimator(mGame.getActivity(),
                R.animator.scroggle);
        if (getView() != null) {
            anim.setTarget(getView());
            anim.start();
        }
    }

    public void revAnimate() {
        Animator anim = AnimatorInflater.loadAnimator(mGame.getActivity(),
                R.animator.revscroggle);
        if (getView() != null) {
            anim.setTarget(getView());
            anim.start();
        }
    }
}

