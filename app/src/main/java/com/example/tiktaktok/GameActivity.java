package com.example.tiktaktok;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import androidx.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import static com.example.tiktaktok.LogginSignupActivity.myRef;

public class GameActivity<extras> extends AppCompatActivity implements View.OnClickListener {


    public int gameRoomNumber;
    public GameRoom gameRoom;
    private Button[][] buttons;
    private TextView textViewPlayer1;
    private TextView textViewPlayer2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(this, "Item 1 selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.scores_history:
                Toast.makeText(this, "Item 2 selected", Toast.LENGTH_SHORT).show();
                openScoresHistoryActivity();
                return true;
            case R.id.Rools:
                Toast.makeText(this, "Rools tab selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.instractions:
                Toast.makeText(this, "instractions selected", Toast.LENGTH_SHORT).show();
                openInstracitonAcivity();
                return true;
            case R.id.cheet:
                Toast.makeText(this, "Cheet sheet 2 selected", Toast.LENGTH_SHORT).show();
                openCheetSheetActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            gameRoomNumber = extras.getInt("roomNum");
        }
        Button buttonReset = findViewById(R.id.button_reset);
        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                resetGame();
            }
        });


        // Preparing 3x3 buttons board
        buttons = new Button[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName()); //??
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(GameActivity.this);
            }
        }
        //listen to room changes
        Query q = myRef.child("all app game rooms").child("game room number" + gameRoomNumber).orderByKey();
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                gameRoom = snapshot.getValue(GameRoom.class);
                if(gameRoom == null) {
                    goBackToRoomMenagerActivity();
                }
                else if(gameRoom.bothFriendsAreConnected) {
                    updateNamesText();
                    Toast.makeText(GameActivity.this, "now", Toast.LENGTH_SHORT).show();
                    int relevant_player_id = -1;
                    relevant_player_id = gameRoom.getPlayer1().getPlayerId();
                    myRef.child("Users Table").child("PlayerId"+relevant_player_id).setValue(gameRoom.getPlayer1());
                    relevant_player_id = gameRoom.getPlayer2().getPlayerId();
                    myRef.child("Users Table").child("PlayerId"+relevant_player_id).setValue(gameRoom.getPlayer2());
                    for(int m=0;m<3;m++) {
                        for (int n = 0; n < 3; n++)
                            buttons[m][n].setText(gameRoom.getGm_buttons().get(m*3+n));
                    }

                }
                else{
                    Toast.makeText(GameActivity.this, "game not started. waiting for a freind!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });



    }

    @Override
    public void onClick(View v) {
        int index=0;
        for(int row=0;row<3;row++)
            for(int col=0;col<3;col++)
                if(v==buttons[row][col]) index = row*3+col;

        if(!gameRoom.bothFriendsAreConnected)
            Toast.makeText(GameActivity.this, "game not started. waiting for a freind!", Toast.LENGTH_LONG).show();
        else {
            if (((LogginSignupActivity.now_playing_in_this_phone.getPlayerId() == gameRoom.getPlayer1().getPlayerId())  &&  gameRoom.getPlayer1().isMyTurn()  ) ||
                ((LogginSignupActivity.now_playing_in_this_phone.getPlayerId() == gameRoom.getPlayer2().getPlayerId())  &&  gameRoom.getPlayer2().isMyTurn()  ) )  {

                //Here I check if the button is clear, and then mark it according to player's turn
                if (!((Button) v).getText().toString().equals("")) return;
                else {
                    ((Button) v).setText(LogginSignupActivity.now_playing_in_this_phone.X_or_O);
                    gameRoom.replaceTurns();
                    gameRoom.setRoundCount(gameRoom.getRoundCount() + 1);
                    gameRoom.getGm_buttons().set(index,LogginSignupActivity.now_playing_in_this_phone.X_or_O);
                    myRef.child("all app game rooms").child("game room number" + gameRoom.gameRoomNumber).setValue(gameRoom);

                    if (checkForWin()) {
                        if (gameRoom.getPlayer1().isMyTurn())
                            player1Win();
                        else
                            player2Win();
                    }
                    else{
                        if (gameRoom.getRoundCount() == 9)
                            draw();
                    }
                }
            }
            else {
                Toast.makeText(this, "wait for your turn", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkForWin(){
        String[][] filed = new String[3][3];

        for(int i=0; i<3; i++)
            for (int j =0; j < 3; j++)
                filed[i][j] = buttons[i][j].getText().toString();

        for(int i=0; i<3; i++){
            //check for lines
            if(filed[i][0].equals(filed[i][1])
                    && filed[i][0].equals(filed[i][2])
                    && !filed[i][0].equals("")){
                return true;
            }

            if(filed[0][i].equals(filed[1][i])
                    && filed[0][i].equals(filed[2][i])
                    && !filed[0][i].equals("")){
                return true;
            }
        }
        if(filed[0][0].equals(filed[1][1])
                && filed[0][0].equals(filed[2][2])
                && !filed[0][0].equals("")){
            return true;
        }
        if(filed[2][0].equals(filed[1][1])
                && filed[2][0].equals(filed[0][2])
                && !filed[2][0].equals("")){
            return true;
        }
        return false;
    }

    private void player1Win(){
        gameRoom.setPlayerOnePoints(gameRoom.getPlayerOnePoints() + 1);
        Toast.makeText(this, "Player 1 wins!", Toast.LENGTH_SHORT).show();
        myRef.child("Users Table").child("PlayerId" + gameRoom.getPlayer1().getPlayerId()).setValue(gameRoom.getPlayer1());

        gameRoom.resetBoard();
    }

    private void player2Win(){
        gameRoom.setPlayerTowPoints(gameRoom.getPlayerTowPoints() + 1);
        Toast.makeText(this, "Player 2 wins!", Toast.LENGTH_SHORT).show();
        myRef.child("Users Table").child("PlayerId" + gameRoom.getPlayer2().getPlayerId()).setValue(gameRoom.getPlayer2());
        gameRoom.resetBoard();
    }

    private void draw(){

        Toast.makeText(this, "Game is over (9 clicks)", Toast.LENGTH_SHORT).show();
      //  gameRoom.resetBoard();
    }

    private void updateNamesText(){
        textViewPlayer1.setText("X Player is :" + gameRoom.getPlayer1().getName() );
        textViewPlayer2.setText("O Player is :" + gameRoom.getPlayer2().getName() );
    }

    private void resetGame(){
        //connect room to data base
        gameRoom.resetBoard();
        myRef.child("all app game rooms").child("game room number" + gameRoom.gameRoomNumber).setValue(gameRoom);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(gameRoom == null) goBackToRoomMenagerActivity();
        else {
            outState.putInt("roundCount", gameRoom.getRoundCount());
            outState.putInt("playerOnePoints", gameRoom.getPlayerOnePoints());
            outState.putInt("playerTowPoints", gameRoom.getPlayerTowPoints());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        gameRoom.setRoundCount(savedInstanceState.getInt("roundCount"));
        gameRoom.setPlayerOnePoints(savedInstanceState.getInt("playerOnePoints"));
        gameRoom.setPlayerTowPoints(savedInstanceState.getInt("playerTowPoints"));

    }

    public void openScoresHistoryActivity() {
        Intent intent = new Intent(this, scoresHistoryActivity.class);
        startActivity(intent);
    }

    public void openInstracitonAcivity() {
        Intent intent = new Intent(this, InstracitonAcivity.class);
        startActivity(intent);
    }

    public void openCheetSheetActivity() {
        Intent intent = new Intent(this, CheetSheetActivity.class);
        startActivity(intent);
    }

    public void goBackToRoomMenagerActivity() {
        Toast.makeText(this, "Game over", Toast.LENGTH_SHORT).show();
        myRef.child("all app game rooms").child("game room number" + gameRoomNumber).setValue(null);
        Intent intent = new Intent(this, RoomManagerActivity.class);
        startActivity(intent);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            myRef.child("all app game rooms").child("game room number" + gameRoomNumber).setValue(null);
            Toast.makeText(this, "Game over", Toast.LENGTH_SHORT).show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if ((LogginSignupActivity.now_playing_in_this_phone.getPlayerId() == gameRoom.getPlayer1().getPlayerId()) && gameRoom.getPlayer1().isMyTurn())
        {
            gameRoom.getPlayer1().isActive = false;
            myRef.child("Users Table").child("PlayerId" + gameRoom.getPlayer1().getPlayerId()).setValue(gameRoom.getPlayer1());
        }
        else if((LogginSignupActivity.now_playing_in_this_phone.getPlayerId() == gameRoom.getPlayer2().getPlayerId())  &&  gameRoom.getPlayer2().isMyTurn()  ) {
            gameRoom.getPlayer2().isActive = false;
            myRef.child("Users Table").child("PlayerId"+gameRoom.getPlayer2().getPlayerId()).setValue(gameRoom.getPlayer2());
        }
        myRef.child("all app game rooms").child("game room number" + gameRoomNumber).setValue(null);

        super.onDestroy();

    }
}