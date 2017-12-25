package tictactoe;

import javax.swing.*;

public class Cell {
    private final int id;
    private final JButton button = new JButton();

    public Cell(int id) {
        this.id = id;
    }

    public JButton getButton() {
        return button;
    }

    public int getId(){
        return this.id;
    }
}
