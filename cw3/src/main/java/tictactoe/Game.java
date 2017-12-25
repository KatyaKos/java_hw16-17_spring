package tictactoe;

public class Game {
    private static Player[] board = {null, null, null, null, null, null, null, null, null};
    private static Player currentPlayer;

    public void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    /**
     * Tells if one of the players won.
     * @return true if someone won
     */
    public static boolean hasWinner() {
        for (int i = 0; i < 3; i++) {
            if (board[3 * i] != null && board[3 * i] == board[3 * i + 1] && board[3 * i] == board[3 * i + 2]) {
                return true;
            }
            if (board[i] != null && board[i] == board[i + 3] && board[i] == board[i + 6]) {
                return true;
            }
        }
        return (board[0] != null && board[0] == board[4] && board[0] == board[8])
                ||(board[2] != null && board[2] == board[4] && board[2] == board[6]);
    }

    /**
     * Tells if the field is filled.
     * @return true if there is no more space
     */
    public static boolean boardFilledUp() {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tells if the move of the player is legal.
     * @param position where player wants to put cross/zero
     * @param player which player want to move
     * @return 0 if it is this players turn and the cell is empty, 1 is it is not your turn, 2 if cell is not empty
     */
    public static synchronized int legalMove(int position, Player player) {
        if (player == currentPlayer && board[position] == null) {
            board[position] = currentPlayer;
            currentPlayer = currentPlayer.getOpponent();
            currentPlayer.otherPlayerMoved(position);
            return 0;
        }
        if (player != currentPlayer) {
            return 1;
        }
        return 2;
    }
}