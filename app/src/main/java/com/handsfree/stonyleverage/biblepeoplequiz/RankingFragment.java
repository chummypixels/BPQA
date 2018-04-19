package com.handsfree.stonyleverage.biblepeoplequiz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.handsfree.stonyleverage.biblepeoplequiz.Common.Common;
import com.handsfree.stonyleverage.biblepeoplequiz.Interface.ItemClickListener;
import com.handsfree.stonyleverage.biblepeoplequiz.Interface.RankingCallback;
import com.handsfree.stonyleverage.biblepeoplequiz.Model.QuestionScore;
import com.handsfree.stonyleverage.biblepeoplequiz.Model.Ranking;
import com.handsfree.stonyleverage.biblepeoplequiz.ViewHolder.RankingViewHolder;


public class RankingFragment extends Fragment {
    View myFragment;

    RecyclerView rankingList;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<Ranking, RankingViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference questionScore, RankingTbl;

    int sum = 0;
    public static RankingFragment newInstance(){
        RankingFragment rankingFragment = new RankingFragment();
        return rankingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        questionScore = database.getReference("Question_Score");
        RankingTbl = database.getReference("Ranking");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_ranking,container,false);

        //Init View
        rankingList = (RecyclerView)myFragment.findViewById(R.id.rankingList);
        layoutManager = new LinearLayoutManager(getActivity());
        rankingList.setHasFixedSize(true);

        //Because orderByChild method of firebase would sort list with ascending
        //so we need to reverse our recycler data by layout manager

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rankingList.setLayoutManager(layoutManager);



        //Now we need to implement callback
        updateScore(Common.currentUser.getName(), new RankingCallback<Ranking>() {
            @Override
            public void callBack(Ranking ranking) {
                //update to Ranking Table
                RankingTbl.child(ranking.getUsername()).setValue(ranking);
             //   showRanking(); // After ranking, we would sort ranking table and show result

            }
        });

        //set Adapter
        adapter = new FirebaseRecyclerAdapter<Ranking, RankingViewHolder>(
                Ranking.class,
                R.layout.layout_ranking,
                RankingViewHolder.class,
                RankingTbl.orderByChild("score")
        ) {
            @Override
            protected void populateViewHolder(RankingViewHolder viewHolder, final Ranking model, int position) {
                viewHolder.txt_name.setText(model.getUsername());
                viewHolder.txt_score.setText(String.valueOf(model.getScore()));

                //Fixed crash when click to item

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent scoreDetail = new Intent(getActivity(),ScoreDetail.class);
                        scoreDetail.putExtra("viewUser",model.getUsername());
                        startActivity(scoreDetail);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rankingList.setAdapter(adapter);
        return myFragment;
    }

    private void showRanking() {
        //Print log to show
        RankingTbl.orderByChild("score").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren())
                {
                    Ranking local = data.getValue(Ranking.class);
                    Log.d("DEBUG", local.getUsername());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void updateScore(final String name, final RankingCallback<Ranking>callback) {

        questionScore.orderByChild("user").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren())
                {
                    QuestionScore ques = data.getValue(QuestionScore.class);
                    sum+=Integer.parseInt(ques.getScore());
                }
                //After summing all scores, we need to process all variables here
                //Because firebase is async db, so if processed outside, our 'sum',
                //value would be reset to 0

                Ranking ranking = new Ranking(name, sum);
                callback.callBack(ranking);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
