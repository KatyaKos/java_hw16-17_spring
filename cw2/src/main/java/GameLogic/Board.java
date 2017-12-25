package GameLogic;

import Gui.BoardGui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class represents logical part of a board. It checks cards and decides what to show.
 */
public class Board {
    private BoardGui boardGui;
    private Card cardOne;
    private Card cardTwo;
    private int cardsGuessedNumber = 0;
    private final int CARDS_IN_ROW;
    private final int CARDS_NUMBER;

    public Board(BoardGui boardGui) {
        this.boardGui = boardGui;
        CARDS_IN_ROW = boardGui.getCardsInRowNumber();
        CARDS_NUMBER = CARDS_IN_ROW * CARDS_IN_ROW;

        ArrayList<Integer> cardsValues = new ArrayList<>();
        int valuesNumber = CARDS_NUMBER / 2;
        for (int i = 0; i < valuesNumber; i++) {
            int n = ThreadLocalRandom.current().nextInt(0, valuesNumber);
            cardsValues.add(n);
            cardsValues.add(n);
        }
        Collections.shuffle(cardsValues);

        ArrayList<Card> cardsList = new ArrayList<>();
        for (int value : cardsValues) {
            Card card = new Card(value);
            card.getButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    makeMove(card);
                }
            });
            cardsList.add(card);
        }

        for (Card card : cardsList){
            boardGui.addCard(card);
        }
    }

    /**
     * Checks if two cards are equal.
     */
    public void checkCards() {
        boardGui.setCardEnable(cardOne, false);
        boardGui.setCardEnable(cardTwo, false);
        if (cardOne.getId() == cardTwo.getId()) {
            cardsGuessedNumber += 2;
            if (isFinished()) {
                boardGui.finishWithMessage("You won!");
            }
        } else {
            boardGui.setCardText(cardOne, "");
            boardGui.setCardText(cardTwo, "");
            boardGui.setCardEnable(cardOne, true);
            boardGui.setCardEnable(cardTwo, true);
        }
        cardOne = null;
        cardTwo = null;
    }

    /**
     * Decides if to show card or not.
     * @param selectedCard card that user selected
     */
    public void makeMove(Card selectedCard) {
        String cardNumber = String.valueOf(selectedCard.getId());
        if (cardOne == null && cardTwo == null) {
            cardOne = selectedCard;
            boardGui.setCardText(cardOne, cardNumber);
        } else if (cardOne != null && cardOne != selectedCard && cardTwo == null) {
            cardTwo = selectedCard;
            boardGui.setCardText(cardTwo, cardNumber);
            boardGui.startTimer();
        }
    }

    public int getCardsInRowNumber() {
        return CARDS_IN_ROW;
    }

    private boolean isFinished() {
        return CARDS_NUMBER == cardsGuessedNumber;
    }

    public void finishWithMessage(String message) {
        boardGui.finishWithMessage(message);
    }

    public BoardGui getBoardGui() {
        return boardGui;
    }
}
