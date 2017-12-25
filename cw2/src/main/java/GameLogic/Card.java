package GameLogic;

import javax.swing.*;

/**
 * This class represents a card with a number.
 */
public class Card {
    private final int id;
    private final JButton button = new JButton();

    public Card(int id) {
        this.id = id;
    }

    public JButton getButton() {
        return button;
    }

    public int getId(){
        return this.id;
    }
}
