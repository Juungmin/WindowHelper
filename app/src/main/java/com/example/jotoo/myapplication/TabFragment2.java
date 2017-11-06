package com.example.jotoo.myapplication;

/**
 * Created by kwangwoon on 2017. 10. 14..
 */

import android.content.Context;
import android.graphics.Typeface;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TabFragment2 extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef_gas;
    TextView tv_gas;
    ImageView iv_smoke;
    LinearLayout tab_layout;
    private static final String TAG = "CardListActivity";
    private SmokeArrayAdapter smokeArrayAdapter;
    private ListView listView;
    Context context;
    View view;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);

    String today = sdf.format(new Date());

    String today_2 = sdf2.format(new Date());

    String zone_location = "zone_"+today;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_2, container, false);
        context = getActivity();
        tv_gas = (TextView) view.findViewById(R.id.smoke_value);
        iv_smoke = (ImageView) view.findViewById(R.id.iv_smoke);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_gas);
        TextView tvFeed = (TextView) view.findViewById(R.id.feed_Title);
        tab_layout = (LinearLayout) view.findViewById(R.id.tab_layout);
        database = FirebaseDatabase.getInstance();
        myRef_gas = database.getReference("home test");

        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(),"BMJUA_ttf.ttf");
        tv_gas.setTypeface(typeface1);

        Typeface typeface2 = Typeface.createFromAsset(context.getAssets(),"BMDOHYEON_ttf.ttf");
        tvTitle.setTypeface(typeface2);
        tvFeed.setTypeface(typeface2);

        //Read from the DB
        //update if there is a change on DB
        myRef_gas.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //집 가스 출력
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String value = childSnapshot.child("gas").child("gas_val").getValue(String.class);
                    tv_gas.setText(value + " ppm");

                    // 담배사진 변경
                    if(value!=null) {
                        if (Integer.parseInt(value) > 300)
                            iv_smoke.setImageResource(R.drawable.smoking);
                        else
                            iv_smoke.setImageResource(R.drawable.no_smoking);
                    }
                }


                //피드 출력
                listView = view.findViewById(R.id.card_listView);
                smokeArrayAdapter = new SmokeArrayAdapter(context.getApplicationContext(), R.layout.list_item_smoke);

                for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
                    String date = zoneSnapshot.child("gas").child("date").getValue(String.class);
                    String time = zoneSnapshot.child("gas").child("time").getValue(String.class);
                    String gas_val = zoneSnapshot.child("gas").child("gas_val").getValue(String.class);

                    if(today_2 != date) {
                        Smoke smoke = new Smoke("[ " + date + " ]\n" + time + " 에 측정된 값은 '" + gas_val + "' 입니다");
                        smokeArrayAdapter.add(smoke);
                    }
                    else {
                        Smoke smoke = new Smoke("[ " + date + " ]\n" + time + " 에 측정된 값은 '" + gas_val + "' 입니다");
                    }

                }

                listView.setAdapter(smokeArrayAdapter);
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