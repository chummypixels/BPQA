package com.handsfree.stonyleverage.biblepeoplequiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.handsfree.stonyleverage.biblepeoplequiz.Common.Common;
import com.handsfree.stonyleverage.biblepeoplequiz.Model.User;


public class SignIn extends AppCompatActivity {

    EditText edtPhone,edtPassword;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPhone = (EditText)findViewById(R.id.edtPhone);

        btnSignIn = (Button)findViewById(R.id.btnSignIn);

        //Init firebase

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog mProgressDialog = new ProgressDialog(SignIn.this);
                mProgressDialog.setMessage("Please wait...");
                mProgressDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child(edtPhone.getText().toString()).exists())
                        {


                        mProgressDialog.dismiss();
                        User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                        if(user.getPassword().equals(edtPassword.getText().toString()))
                        {
                            Intent homeActivity = new Intent(SignIn.this, Home.class);
                            Common.currentUser = user;
                            startActivity(homeActivity);
                            finish();

                        }else
                            {
                                Toast.makeText(SignIn.this, "Wrong Password!",Toast.LENGTH_SHORT).show();
                            }
                    }else
                        {
                            mProgressDialog.dismiss();
                            Toast.makeText(SignIn.this,"User does not exist",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
