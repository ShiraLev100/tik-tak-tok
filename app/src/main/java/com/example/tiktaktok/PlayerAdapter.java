package com.example.tiktaktok;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tiktaktok.Player;
import com.example.tiktaktok.R;

import java.util.List;

public class PlayerAdapter extends ArrayAdapter<Player> {
    Context context;
    List<Player> objects;


    public PlayerAdapter(@NonNull Context context, int resource, int textViewResourceId,
                         @NonNull List<Player> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.one_line,parent,false);
  //      View view = layoutInflater.inflate(R.layout.activity_scores_history, parent, false);

        TextView tv_player_name = (TextView)view.findViewById(R.id.tv_player_name);
        TextView tv_player_score = (TextView)view.findViewById(R.id.tv_player_score);
        ImageView iv_player_image = (ImageView)view.findViewById(R.id.iv_player_image) ;
        Player temp = objects.get(position);

        tv_player_name.setText(temp.getName());
        tv_player_score.setText(""+temp.getScore());
        iv_player_image.setImageResource(temp.getPicture());
        return view;
    }
}
