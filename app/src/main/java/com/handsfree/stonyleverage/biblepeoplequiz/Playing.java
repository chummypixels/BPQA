package com.handsfree.stonyleverage.biblepeoplequiz;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.handsfree.stonyleverage.biblepeoplequiz.Common.Common;
import com.squareup.picasso.Picasso;

public class Playing extends AppCompatActivity implements View.OnClickListener{

    final static long INTERVAL = 1000; // 1 sec
    final static long TIMEOUT = 7000; // 7 secs
    int progress_value = 0;

    CountDownTimer mcountDownTimer;
    int index=0,score=0,thisQuestion=0,totalQuestion,correctAnswer;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference questions;

    ProgressBar progressBar;
    ImageView question_image;
    Button btnA, btnB, btnC, btnD;
    TextView txtScore, textQuestionNum,question_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);



        //Views
        txtScore = (TextView)findViewById(R.id.txtScores);
        textQuestionNum = (TextView)findViewById(R.id.txtTotalQuestions);
        question_txt = (TextView)findViewById(R.id.question_text);
        question_image = (ImageView)findViewById(R.id.question_image);

        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        btnA = (Button)findViewById(R.id.btnAnswerA);
        btnB = (Button)findViewById(R.id.btnAnswerB);
        btnC = (Button)findViewById(R.id.btnAnswerC);
        btnD = (Button)findViewById(R.id.btnAnswerD);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mcountDownTimer.cancel();

        if(index < totalQuestion)  //still has questions in list
        {
            Button clickedButton = (Button)view;
            if(clickedButton.getText().equals(Common.questionList.get(index).getCorrectAnswer()))
            {
                //Choose correct answer
                score+=10;
                correctAnswer++;
                showQuestion(++index);//next question
            }else
                {
                    //choose wrong answer
                    Intent intent = new Intent(this,Done.class);
                    Bundle dataSend = new Bundle();
                    dataSend.putInt("SCORE", score);
                    dataSend.putInt("TOTAL", totalQuestion);
                    dataSend.putInt("CORRECT", correctAnswer);
                    intent.putExtras(dataSend);
                    startActivity(intent);
                    finish();
                }

                txtScore.setText(String.format("%d", score));
        }
    }

    private void showQuestion(int index) {
        if(index < totalQuestion)
        {
            thisQuestion++;
            textQuestionNum.setText(String.format("%d / %d", thisQuestion,totalQuestion));
            progressBar.setProgress(0);
            progress_value = 0;

            if (Common.questionList.get(index).getIsImageQuestion().equals("true"))
            {
                //if image
                Picasso.with(getBaseContext())
                        .load(Common.questionList.get(index).getQuestion())
                        .into(question_image);
                question_image.setVisibility(View.VISIBLE);
                question_txt.setVisibility(View.INVISIBLE);
            }else
                {
                    question_txt.setText(Common.questionList.get(index).getQuestion());

                    //if question is text, we would set image to invisible
                    question_image.setVisibility(View.INVISIBLE);
                    question_txt.setVisibility(View.VISIBLE);
                }

                btnA.setText(Common.questionList.get(index).getAnswerA());
                btnB.setText(Common.questionList.get(index).getAnswerB());
                btnC.setText(Common.questionList.get(index).getAnswerC());
                btnD.setText(Common.questionList.get(index).getAnswerD());

                mcountDownTimer.start(); //start timer
        }else
            {
                //if it is final question
                Intent intent = new Intent(this,Done.class);
                Bundle dataSend = new Bundle();
                dataSend.putInt("SCORE", score);
                dataSend.putInt("TOTAL", totalQuestion);
                dataSend.putInt("CORRECT", correctAnswer);
                intent.putExtras(dataSend);
                startActivity(intent);
                finish();
            }
    }

    @Override
    protected void onResume() {
        super.onResume();

        totalQuestion = Common.questionList.size();

        mcountDownTimer = new CountDownTimer(TIMEOUT, INTERVAL) {
            @Override
            public void onTick(long minisec) {
                progressBar.setProgress(progress_value);
                progress_value++;
            }

            @Override
            public void onFinish() {
                mcountDownTimer.cancel();
                showQuestion(++index);
            }
        };
        showQuestion(index);
    }
}
