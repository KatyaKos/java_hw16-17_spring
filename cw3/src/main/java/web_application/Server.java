package web_application;

import java.net.ServerSocket;
import tictactoe.*;

public class Server {

    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(1420);
        System.out.println("TicTacToe Server is Running");
        try {
            while (true) {
                Game game = new Game();
                Player playerX = new Player(listener.accept(), 'X');
                Player playerO = new Player(listener.accept(), 'O');
                playerX.setOpponent(playerO);
                playerO.setOpponent(playerX);
                game.setCurrentPlayer(playerX);
                playerX.start();
                playerO.start();
            }
        } finally {
            listener.close();
        }
    }
}