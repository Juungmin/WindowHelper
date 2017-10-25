package com.example.jotoo.myapplication;

/**
 * Created by kwangwoon on 2017. 10. 14..
 */

import android.content.Context;
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

import java.util.ArrayList;
import java.util.Map;

public class TabFragment2 extends Fragment {


    FirebaseDatabase database;
    DatabaseReference myRef_gas;

    TextView tv_gas;
    ImageView iv_smoke;

    /*Scroll view property*/
    ScrollView scroll_view;
    LinearLayout tab_layout;
    TextView scroll_view_text;

    private static final String TAG = "CardListActivity";
    private SmokeArrayAdapter smokeArrayAdapter;
    private ListView listView;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.tab_fragment_2, container, false);

        context = getActivity();

        tv_gas = (TextView) view.findViewById(R.id.tv_gas);
        iv_smoke = (ImageView) view.findViewById(R.id.iv_smoke);
        tab_layout = (LinearLayout) view.findViewById(R.id.tab_layout);

        database = FirebaseDatabase.getInstance();
        myRef_gas = database.getReference("home test").child("gas");

        //Read from the DB
        //update if there is a change on DB
        myRef_gas.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //gas record수집이 필요하면 사용.
               // collectGasRecords((Map<String, Object>) dataSnapshot.getValue());

               for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    //gasDataList.add(String.valueOf(childSnapshot.getValue()));
                   String value = childSnapshot.child("gas_val").getValue(String.class);

                    tv_gas.setText("우리 집 가스 농도: "+value + " ppm");
                }
                //Log.d(TAG, "Value is: " + value);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }

        });


        listView = view.findViewById(R.id.card_listView);


        smokeArrayAdapter = new SmokeArrayAdapter(context.getApplicationContext(), R.layout.list_item_smoke);

        for (int i = 0; i < 10; i++) {
            Smoke smoke = new Smoke("Card " + (i+1) + " Line 1", "Card " + (i+1) + " Line 2");
            smokeArrayAdapter.add(smoke);
        }
        listView.setAdapter(smokeArrayAdapter);

        return view;
    }

    private void collectGasRecords(Map<String, Object> gas) {
        ArrayList<String> gasDataList = new ArrayList<>();
        int totalSize = 0;

        for(Map.Entry<String, Object> entry : gas.entrySet()) {
            Map singleGasRecord = (Map) entry.getValue();
            gasDataList.add((String) singleGasRecord.get("gas_val"));
        }

        totalSize = gasDataList.size();

        for(int i = 0; i < totalSize; i++) {
            tv_gas.setText("우리 집 가스 농도: "+gasDataList.get(i) + " ppm");
        }

    }
}