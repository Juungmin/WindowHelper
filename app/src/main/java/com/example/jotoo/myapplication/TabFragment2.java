package com.example.jotoo.myapplication;

/**
 * Created by kwangwoon on 2017. 10. 14..
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TabFragment2 extends Fragment {

    private static final String TAG = "Tab2";

    FirebaseDatabase database;
    DatabaseReference myRef_gas;

    TextView tv_gas;
    ImageView iv_smoke;

    /*Scroll view property*/
    ScrollView scroll_view;
    LinearLayout tab_layout;
    TextView scroll_view_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.tab_fragment_2, container, false);
        //TextView tv_dust;

        tv_gas = (TextView) view.findViewById(R.id.tv_gas);
        iv_smoke = (ImageView) view.findViewById(R.id.iv_smoke);
        scroll_view = (ScrollView) view.findViewById(R.id.scroll_view);
        tab_layout = (LinearLayout) view.findViewById(R.id.tab_layout);
        //scroll_view_text = (TextView) view.findViewById(R.id.scroll_view_text);

        database = FirebaseDatabase.getInstance();

        myRef_gas = database.getReference("home test");
        myRef_gas = myRef_gas.child("gas");
        myRef_gas = myRef_gas.child("gas_val");


        //Read from the DB
        //update if there is a change on DB
        myRef_gas.addValueEventListener(new ValueEventListener(){
            double gas_val = 0;

            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //String value = dataSnapshot.getValue(String.class);
                String value = dataSnapshot.getValue(String.class);
                //데이터를 화면에 출력해 준다.
                tv_gas.setText("우리 집 가스 농도: "+value + " ppm");
                //Log.d(TAG, "Value is: " + value);

                try {
                    gas_val = Double.parseDouble(value);
                    Log.d(TAG, "Value is: " + (int) gas_val);

                    if((int) gas_val >= 200) {
                        iv_smoke.setImageResource(R.drawable.smoking); // change image to smoking pic

                        //for (int i =0 ; i < 1; i++) {
                        TextView textView = new TextView(TabFragment2.this.getActivity());
                        //}

                        textView.setText("담배연기 " + value + "ppm 이 감지되었습니다.");
                        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                        tab_layout.addView(textView, p);
                    }
                    else {
                        iv_smoke.setImageResource(R.drawable.no_smoking);
                    }
                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
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