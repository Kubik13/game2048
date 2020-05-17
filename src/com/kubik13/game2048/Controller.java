package com.kubik13.game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter {

    private static final int WINNING_TILE =  2048;
    private Model model;
    private View view;

    public Tile[][] getGameTiles(){
        return model.getGameTiles();
    }

    public Controller(Model model) {
        this.model = model;
        view = new View(this);
        JOptionPane.showMessageDialog(view, "Welcome to the 2048 game!\n" +
                "arrows - move tiles\n" +
                "ESC - reset game\n" +
                "Z - rollback move\n" +
                "R - random move\n" +
                "A - auto move(trying to make best move\n" +
                "Q - full auto game");
    }

    public int getScore(){
        return model.score;
    }

    public int getMoves(){
        return model.moves;
    }

    public View getView() {
        return view;
    }

    public void resetGame(){
        model.score = 0;
        model.moves = 0;
        view.isGameLost = false;
        view.isGameWon = false;
        model.resetGameTiles();
    }

    public void keyPressed(KeyEvent e){
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) this.resetGame();
        if (!model.canMove()) view.isGameLost = true;
        if (!view.isGameLost && !view.isGameWon) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) model.left();
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) model.right();
            if (e.getKeyCode() == KeyEvent.VK_UP) model.up();
            if (e.getKeyCode() == KeyEvent.VK_DOWN) model.down();
            if (e.getKeyCode() == KeyEvent.VK_Z) model.rollback();
            if (e.getKeyCode() == KeyEvent.VK_R) model.randomMove();
            if (e.getKeyCode() == KeyEvent.VK_A) model.autoMove();
            if (e.getKeyCode() == KeyEvent.VK_Q) autoGame();
            if (model.maxTile == WINNING_TILE) view.isGameWon = true;
            view.repaint();
        }
    }

    private void autoGame() {
        while (model.canMove()){
            model.autoMove();
            if (model.maxTile == WINNING_TILE) view.isGameWon = true;
        }
        view.repaint();
    }
}
