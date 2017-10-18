package com.example.jotoo.myapplication;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by kwangwoon on 2017. 10. 14..
 */

public class TabFragment1 extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef_dust;

    TextView tv_dust;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.tab_fragment_1, container, false);
        //TextView tv_dust;

        tv_dust = (TextView) view.findViewById(R.id.tv_dust);

        database = FirebaseDatabase.getInstance();

        myRef_dust = database.getReference("home test");
        myRef_dust = myRef_dust.child("dust");
        myRef_dust = myRef_dust.child("dust_val");
        //Read from the DB
        //update if there is a change on DB
        myRef_dust.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //String value = dataSnapshot.getValue(String.class);
                String value = dataSnapshot.getValue(String.class);
                //데이터를 화면에 출력해 준다.
                tv_dust.setText("우리 집 미세먼지 농도: "+value + " ㎍/㎥");
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }

        });
        return view;
    }

}
