package com.example.tiktaktok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import static com.example.tiktaktok.LogginSignupActivity.myRef;
import static com.example.tiktaktok.LogginSignupActivity.now_playing_in_this_phone;

public class RoomManagerActivity extends AppCompatActivity {
    Button btn_joinRoom;
    Button btn_createRoom;
    public int desiredRoomNum = 0;
    public int rememberFriendRoomNum;
    public static FirebaseDatabase database;
    public static DatabaseReference myRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_manager);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        btn_joinRoom = (Button)findViewById(R.id.btn_joinRoom);
        btn_createRoom = (Button)findViewById(R.id.btn_createRoom);

        btn_joinRoom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                EditText inputEditTextField = new EditText(RoomManagerActivity.this);
                inputEditTextField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                AlertDialog dialog = new AlertDialog.Builder(RoomManagerActivity.this)
                        .setTitle("Do you want to play with a friend?")
                        .setMessage("pleas type the room number:")
                        .setView(inputEditTextField)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String editTextInput = inputEditTextField.getText().toString();
                                Toast.makeText(RoomManagerActivity.this, "editext value is:" + editTextInput, Toast.LENGTH_LONG);
                                rememberFriendRoomNum = Integer.parseInt(editTextInput);

                                Query q = myRef.child("all app game rooms").orderByKey();
                                q.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        boolean thisRoomExists = false;

                                        for(DataSnapshot dst: snapshot.getChildren()) {
                                            GameRoom checkRoomNumber = dst.getValue(GameRoom.class);
                                            if (checkRoomNumber.gameRoomNumber == rememberFriendRoomNum){
                                                desiredRoomNum = rememberFriendRoomNum;
                                                thisRoomExists = true;
                                                checkRoomNumber.setPlayer2(LogginSignupActivity.now_playing_in_this_phone);
                                                LogginSignupActivity.now_playing_in_this_phone.X_or_O = "O";
                                                checkRoomNumber.bothFriendsAreConnected = true;
                                                myRef.child("all app game rooms").child("game room number" + checkRoomNumber.gameRoomNumber).setValue(checkRoomNumber);
                                                Toast.makeText(RoomManagerActivity.this, "Joined successfully",Toast.LENGTH_SHORT).show();
                                                openNewGame();
                                            }
                                        }
                                        if(!thisRoomExists) {
                                            Toast.makeText(RoomManagerActivity.this, "No such game... check your number", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });

        btn_createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query q = myRef.child("all app game rooms").orderByKey();
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        desiredRoomNum = (int) snapshot.getChildrenCount();
                        Toast.makeText(RoomManagerActivity.this, "Game number" + desiredRoomNum + " was created!",Toast.LENGTH_SHORT).show();
                        GameRoom g = new GameRoom();
                        g.gameRoomNumber = desiredRoomNum;
                        LogginSignupActivity.now_playing_in_this_phone.setMyTurn(true);
                        g.setPlayer1(LogginSignupActivity.now_playing_in_this_phone);
                        LogginSignupActivity.now_playing_in_this_phone.X_or_O = "X";
                        Player temp_player = new Player("not connected yet",0,0,"not connected yet");
                        g.setPlayer2(temp_player);
                        myRef.child("all app game rooms").child("game room number" + g.gameRoomNumber).setValue(g);


                        openNewGame();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }
        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            now_playing_in_this_phone.isActive = false;
            myRef.child("Users Table").child("PlayerId" + now_playing_in_this_phone.getPlayerId()).setValue(now_playing_in_this_phone);
        }
        return super.onKeyDown(keyCode, event);
    }
    public void openNewGame() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("roomNum", desiredRoomNum);
        startActivity(intent);
    }
}