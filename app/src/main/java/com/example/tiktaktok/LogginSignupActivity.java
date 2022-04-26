package com.example.tiktaktok;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LogginSignupActivity extends AppCompatActivity {
    public static FirebaseDatabase database;
    public static DatabaseReference myRef;


    MyReceiverWiFi receiverWiFi = new MyReceiverWiFi();
    MyReceiverBatteryLow receiverBatteryLow = new MyReceiverBatteryLow();

    private Player newPlayer;
    private Button playsong;
    private Button btn_login;
    private Button btn_signup;
    private EditText txt_username;
    private EditText txt_password;
    public static Player now_playing_in_this_phone;
    Intent musicIntent;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loggin_sginup);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        playsong = (Button) findViewById(R.id.button);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        txt_username = (EditText) findViewById(R.id.txt_username);
        txt_password = (EditText) findViewById(R.id.txt_password);


        playsong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicIntent = new Intent(LogginSignupActivity.this, BackgroundMousicService.class);
                startService(musicIntent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //after making sure user is not in system, send name and password to table
                //make sure username and password is written
                if (txt_username.getText().toString().trim().length()== 0 ||
                        txt_password.getText().toString().trim().length()== 0)
                {
                    Toast.makeText(LogginSignupActivity.this, "Pleas enter both password and username", Toast.LENGTH_SHORT).show();
                }

                //if username or password are taken
                Query query = myRef.child("Users Table").orderByValue();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                        boolean isUserAlreadyExist = false;

                        newPlayer = new Player(txt_username.getText().toString(),
                                txt_password.getText().toString(),
                                R.drawable.cat); //here I need to change picture

                        for(DataSnapshot dst: snapshot.getChildren())
                        {
                            Player player = dst.getValue(Player.class);
                            //check that user is not active and pass and nme mach
                            if(player.getName().equals(newPlayer.getName()) && player.getPassword().equals(newPlayer.getPassword())
                            && player.isActive == false)
                            {
                                isUserAlreadyExist = true;
                                now_playing_in_this_phone = player;
                                player.isActive = true;
                                myRef.child("Users Table").child("PlayerId" + player.getPlayerId()).setValue(player);
                            }
                        }
                        if (isUserAlreadyExist == true)
                        {
                            Toast.makeText(LogginSignupActivity.this, "login seccesful", Toast.LENGTH_SHORT).show();
                            openActivity2();
                        }
                        else
                        {
                            Toast.makeText(LogginSignupActivity.this, "login failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

                    }

                });

            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make sure username and password is written
                if (txt_username.getText().toString().trim().length()== 0 ||
                        txt_password.getText().toString().trim().length()== 0)
                {
                    Toast.makeText(LogginSignupActivity.this, "Pleas enter both password and username", Toast.LENGTH_SHORT).show();
                }

                //if username or password are taken
                Query query = myRef.child("Users Table").orderByValue();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                        boolean isUserNameTaken = false;
                        for (DataSnapshot dst : snapshot.getChildren()) {
                            Player player = dst.getValue(Player.class);
                            if (player.getName().equals(txt_username.getText().toString())) //name is not taken
                            {
                                isUserNameTaken = true;
                            }
                        }
                        if (isUserNameTaken == false) {
                            int howMany = (int) snapshot.getChildrenCount();
                            newPlayer = new Player(txt_username.getText().toString(), txt_password.getText().toString(), R.drawable.cat); //here I need to change picture
                            newPlayer.setPlayerId(howMany);
                            newPlayer.isActive = true;
                            now_playing_in_this_phone = newPlayer;
                            myRef.child("Users Table").child("PlayerId" + howMany).setValue(newPlayer);
                            Toast.makeText(LogginSignupActivity.this, "Signup Seccesfuly!", Toast.LENGTH_SHORT).show();
                            openActivity2();
                        } else {
                            if (isUserNameTaken) {
                                Toast.makeText(LogginSignupActivity.this, "this name is already in use...choose another name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

                    }

                });



            }
        });

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && now_playing_in_this_phone!= null) {
            now_playing_in_this_phone.isActive = false;
            myRef.child("Users Table").child("PlayerId" + now_playing_in_this_phone.getPlayerId()).setValue(now_playing_in_this_phone);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if(now_playing_in_this_phone!= null) {
            now_playing_in_this_phone.isActive = false;
            myRef.child("Users Table").child("PlayerId" + now_playing_in_this_phone.getPlayerId()).setValue(now_playing_in_this_phone);
        }
        stopService(musicIntent);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiverBatteryLow);
        unregisterReceiver(receiverWiFi);

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiverWiFi, filter);
        registerReceiver(receiverBatteryLow, filter);
    }

    public void openActivity2() {
        Intent intent = new Intent(this, RoomManagerActivity.class);
        startActivity(intent);
    }
}