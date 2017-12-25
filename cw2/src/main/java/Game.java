import Gui.BoardGui;

import javax.swing.*;
import java.awt.*;

/**
 * This class starts a game.
 */
public class Game{
    public static void main(String[] args){
        BoardGui board = new BoardGui();
        int n = board.getCardsInRowNumber();
        if (n % 2 == 1 || n <= 0) {
            board.finishWithMessage("Number of cards should be even!");
        }
        board.setPreferredSize(new Dimension(100 * n, 100 * n));
        board.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        board.pack();
        board.setVisible(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        board.setLocation(dim.width / 2 - board.getSize().width / 2, dim.height / 2 - board.getSize().height / 2);
    }
}
