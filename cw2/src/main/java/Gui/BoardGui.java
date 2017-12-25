package Gui;

import GameLogic.Board;
import GameLogic.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * This class represents Board user interface.
 */
public class BoardGui extends JFrame{
    private Board board;
    private final int CARDS_IN_ROW = 2;
    private Timer timer;
    private Container pane;

    public BoardGui() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.checkCards();
            }
        });
        timer.setRepeats(false);

        pane = getContentPane();
        pane.setLayout(new GridLayout(CARDS_IN_ROW, CARDS_IN_ROW));
        setTitle("Memory Game");

        board = new Board(this);
    }

    public void addCard(Card card) {
        pane.add(card.getButton());
    }

    public int getCardsInRowNumber() {
        return CARDS_IN_ROW;
    }

    public void setCardEnable(Card card, boolean flag) {
        card.getButton().setEnabled(flag);
    }

    public void setCardText(Card card, String text) {
        card.getButton().setText(text);
    }

    public void finishWithMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
        System.exit(0);
    }

    public void startTimer() {
        timer.start();
    }
}
