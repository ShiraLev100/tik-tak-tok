package com.example.tiktaktok;

import java.util.ArrayList;

import static com.example.tiktaktok.LogginSignupActivity.myRef;

public class GameRoom {
    public int gameRoomNumber = -1;
    private ArrayList<String> gm_buttons;
    private int roundCount = 0;
    private Player player1;
    private Player player2;
    public boolean bothFriendsAreConnected = false;

    public boolean isBothFriendsAreConnected() {
        return bothFriendsAreConnected;
    }

    public void setBothFriendsAreConnected(boolean bothFriendsAreConnected) {
        this.bothFriendsAreConnected = bothFriendsAreConnected;
    }

    public GameRoom() {
        gm_buttons = new ArrayList<>();

        for (int i=0; i<9; i++)
            gm_buttons.add("");

    }

    public void replaceTurns()
    {
        player1.setMyTurn(!player1.isMyTurn());
        player2.setMyTurn(!player2.isMyTurn());
    }

    public void defultTurns()
    {
        player1.setMyTurn(true);
        player2.setMyTurn(false);
    }
    public void resetBoard(){
        gm_buttons.clear();
        for (int i=0; i<9; i++)
            gm_buttons.add("");
        defultTurns();
        myRef.child("all app game rooms").child("game room number" + this.gameRoomNumber).setValue(this);

        roundCount = 0;
    }

    public ArrayList<String> getGm_buttons() {
        return gm_buttons;
    }

    public void setGm_buttons(ArrayList<String> gm_buttons) {
        this.gm_buttons = gm_buttons;
    }

    public void setGameRoomNumber(int gameRoomNumber) {
        this.gameRoomNumber = gameRoomNumber;
    }

    public void setRoundCount(int roundCount) {
        this.roundCount = roundCount;
    }

    public void setPlayerOnePoints(int playerOnePoints) {
        //player1.setScore(playerOnePoints);
    }

    public void setPlayerTowPoints(int playerTowPoints) {
        player2.setScore(playerTowPoints);
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) { this.player2 = player2; }

    public int getGameRoomNumber() {
        return gameRoomNumber;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public int getPlayerOnePoints() {
        return player1.getScore();
    }

    public int getPlayerTowPoints() {
        return player2.getScore();
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }
}
