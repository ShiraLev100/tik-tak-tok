package com.example.tiktaktok;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static com.example.tiktaktok.LogginSignupActivity.myRef;
import static com.example.tiktaktok.LogginSignupActivity.now_playing_in_this_phone;

public class scoresHistoryActivity extends AppCompatActivity {
    ArrayList <Player> playersArr ;
    ListView lv_players;
    PlayerAdapter playerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores_history);
        playersArr = new ArrayList<>();
        lv_players = (ListView)findViewById(R.id.lv_players);

        Query q = myRef.child("Users Table").orderByKey();

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dst : snapshot.getChildren()) {
                    Player player = dst.getValue(Player.class);
                    playersArr.add(player);
                }
                bubbleSort(playersArr);

                //adapter
                if(playersArr.size()!=0) {
                    playerAdapter = new PlayerAdapter(scoresHistoryActivity.this, 0, 0, playersArr);
                    lv_players.setAdapter(playerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });




    }

    @Override
    protected void onDestroy() {
        if(now_playing_in_this_phone!= null) {
            now_playing_in_this_phone.isActive = false;
            myRef.child("Users Table").child("PlayerId" + now_playing_in_this_phone.getPlayerId()).setValue(now_playing_in_this_phone);
        }
        super.onDestroy();
    }

    public void bubbleSort(ArrayList<Player> array) {
        boolean sorted = false;
        Player temp;
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < array.size() - 1; i++) {
                if (array.get(i).getScore() < array.get(i+1).getScore()) {
                    temp = array.get(i);
                    array.set(i,array.get(i+1));
                    array.set(i+1,temp);
                    sorted = false;
                }
            }
        }
    }
}