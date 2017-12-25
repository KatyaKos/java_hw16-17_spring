package web_application;

import tictactoe.Cell;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.*;

public class Client {

    private JFrame frame = new JFrame("Tic Tac Toe");
    private JLabel messageLabel = new JLabel("");

    private Cell[] cellsList = new Cell[9];
    private Cell currentCell;
    private String mark;
    private String opponentMark;

    private static int PORT = 1420;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public Client(String serverAddress) throws Exception {
        socket = new Socket(serverAddress, PORT);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

        Container board = frame.getContentPane();
        board.setLayout(new BoxLayout(board, BoxLayout.Y_AXIS));
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));
        for (int i = 0; i < 9; i ++) {
            Cell cell = new Cell(i);
            panel.add(cell.getButton());
            cell.getButton().addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    currentCell = cell;
                    writer.println("MOVE " + cell.getId());}
            });
            cellsList[i] = cell;
        }
        board.add(panel);
        board.add(messageLabel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(300, 300));
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

    public void play() throws Exception {
        String response;
        try {
            response = reader.readLine();
            if (response.startsWith("WELCOME")) {
                mark = "" + response.charAt(8);
                if (mark.equals("X")) {
                    opponentMark = "O";
                } else {
                    opponentMark = "X";
                }
                frame.setTitle("TicTacToe Player " + mark);
            }

            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            int center_x = dim.width / 2 - frame.getSize().width / 2;
            int center_y = dim.height / 2 - frame.getSize().height / 2;
            if (mark.equals("X")) {
                frame.setLocation(center_x + 200, center_y);
            } else {
                frame.setLocation(center_x - 200, center_y);
            }

            while (true) {
                response = reader.readLine();
                if (response.startsWith("VALID MOVE")) {
                    messageLabel.setText("Nice move. Please, wait for your opponent.");
                    currentCell.getButton().setText(mark);
                } else if (response.startsWith("OPPONENT MOVED")) {
                    int position = Integer.parseInt(response.substring(15));
                    cellsList[position].getButton().setText(opponentMark);
                    messageLabel.setText("Opponent moved, your turn.");
                } else if (response.startsWith("VICTORY")) {
                    messageLabel.setText("Winner!");
                    break;
                } else if (response.startsWith("DEFEAT")) {
                    messageLabel.setText("Loser!");
                    break;
                } else if (response.startsWith("DRAW")) {
                    messageLabel.setText("You have draw.");
                    break;
                } else if (response.startsWith("MESSAGE")) {
                    messageLabel.setText(response.substring(8));
                }
            }
            writer.println("QUIT");
        }
        finally {
            socket.close();
        }
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            Client client = new Client("localhost");
            client.play();
            break;
        }
    }
}