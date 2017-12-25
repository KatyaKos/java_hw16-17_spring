package tictactoe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player extends Thread {
    private char mark;
    private Player opponent;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public Player getOpponent() {
        return opponent;
    }

    public Player(Socket socket, char mark) {
        this.socket = socket;
        this.mark = mark;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("WELCOME " + mark);
            writer.println("MESSAGE Waiting for opponent to connect");
        } catch (IOException e) {
            System.out.println("ERROR : " + e);
        }
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public void otherPlayerMoved(int position) {
        writer.println("OPPONENT MOVED " + position);
        if (Game.hasWinner()) {
            writer.println("DEFEAT");
        } else if (Game.boardFilledUp()) {
            writer.println("DRAW");
        } else {
            writer.println("");
        }
    }

    public void run() {
        try {
            writer.println("MESSAGE All players connected");
            if (mark == 'X') {
                writer.println("MESSAGE Your move");
            }

            while (true) {
                String command = reader.readLine();
                if (command.startsWith("MOVE")) {
                    int position = Integer.parseInt(command.substring(5));
                    int check = Game.legalMove(position, this);
                    if (check == 0) {
                        writer.println("VALID MOVE");
                        if (Game.hasWinner()) {
                            writer.println("VICTORY");
                        } else if (Game.boardFilledUp()) {
                            writer.println("DRAW");
                        } else {
                            writer.println("");
                        }
                    } else if (check == 1) {
                        writer.println("MESSAGE it is not your turn");
                    } else {
                        writer.println("MESSAGE this cell is not empty");
                    }
                } else if (command.startsWith("QUIT")) {
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Player died: " + e);
        } finally {
            try {socket.close();} catch (IOException e) {}
        }
    }
}
