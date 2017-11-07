package com.example.jotoo.myapplication;

/**
 * Created by kwangwoon on 2017. 10. 14..
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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
import java.util.Timer;
import java.util.TimerTask;

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

    double temp_mq2 = 0;
    double temp_mq7 = 0;

    int count = 1;

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

                String value_mq2 = dataSnapshot.child(zone_location).child("gas").child("gas_mq2").getValue(String.class);
                String value_mq7 = dataSnapshot.child(zone_location).child("gas").child("gas_mq7").getValue(String.class);

                String val_first_mq2 = dataSnapshot.child(zone_location).child("gas").child("first_mq2").getValue(String.class);
                String val_first_mq7 = dataSnapshot.child(zone_location).child("gas").child("first_mq7").getValue(String.class);

                double value_avg = (Double.parseDouble(value_mq2)+ Double.parseDouble(value_mq7))/2.0;
                String value = Double.toString(value_avg);
                String value3 = getStatusString(Double.parseDouble(val_first_mq2), Double.parseDouble(val_first_mq7), Double.parseDouble(value_mq2), Double.parseDouble(value_mq7));
                String value2 = value3 + " 입니다.";

                tv_gas.setText(value2);
                SpannableStringBuilder ssb = new SpannableStringBuilder(value2);
                if(!value3.equals("보통")){
                    ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#DC143C")),0,2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else{
                    ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#32CD32")),0,2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                tv_gas.setText(ssb);

                for(int i=0;i<3;i++){
                    if(!value3.equals("보통")) {
                        new Timer().schedule(new TimerTask() {
                            public void run() {
                                iv_smoke.setImageResource(R.drawable.smoking);
                            }
                        }, 500);

                        new Timer().schedule(new TimerTask() {
                            public void run() {
                                iv_smoke.setImageResource(R.drawable.no_smoking);
                            }
                        }, 500);
                    }
                }


                //피드 출력
                listView = view.findViewById(R.id.card_listView);
                smokeArrayAdapter = new SmokeArrayAdapter(context.getApplicationContext(), R.layout.list_item_smoke);

                for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
                    String date = zoneSnapshot.child("gas").child("date").getValue(String.class);
                    String time = zoneSnapshot.child("gas").child("time").getValue(String.class);
                    String gas_mq2 = zoneSnapshot.child("gas").child("gas_mq2").getValue(String.class);
                    String gas_mq7 = zoneSnapshot.child("gas").child("gas_mq7").getValue(String.class);

                    String first_mq2 = zoneSnapshot.child("gas").child("first_mq2").getValue(String.class);
                    String first_mq7 = zoneSnapshot.child("gas").child("first_mq7").getValue(String.class);

                    String sensor_val_mq2 = zoneSnapshot.child("gas").child("sensor_val_mq2").getValue(String.class);
                    String sensor_val_mq7 = zoneSnapshot.child("gas").child("sensor_val_mq7").getValue(String.class);

                    String status = "";


                    status = getStatus(Double.parseDouble(first_mq2), Double.parseDouble(first_mq7), Double.parseDouble(gas_mq2), Double.parseDouble(gas_mq7), Double.parseDouble(sensor_val_mq2), Double.parseDouble(sensor_val_mq7));

                    if(today_2 != date) {
                        Smoke smoke = new Smoke("[ " + date + " ]\n" + time + " 에 측정된 농도는 " + status);
                        smokeArrayAdapter.add(smoke);
                    }
                    else {
                        Smoke smoke = new Smoke("[ " + date + " ]\n" + time + " 에 측정된 농도는 " + status);
                    }
                    count++;
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

    public String getStatusString(double mq2_init, double mq7_init, double mq2_ratio, double mq7_ratio) {
        String status = "";

        if((mq2_init - mq2_ratio) >= 2.5 || (mq7_init - mq7_ratio) >= 5.2) {
            status = "매우 높음";
        }
        else if((mq2_init - mq2_ratio) >= 1.7 || (mq7_init - mq7_ratio) >= 3.3) {
            status = "높음";
        }
        else if(mq2_init - mq2_ratio >= 0.7 || (mq7_init - mq7_ratio) >= 1.2) {
            status = "약간 높음";
        } else {
            status = "보통";
        }

        return status;
    }

    public String getStatus(double mq2_init, double mq7_init, double mq2_ratio, double mq7_ratio, double sensor_val_mq2, double sensor_val_mq7) {
        String status = "";

        double mq2_fix = 0;
        double mq7_fix = 0;

        double sensor_val = (sensor_val_mq2 + sensor_val_mq7)/2;

        iv_smoke.setImageResource(R.drawable.no_smoking);
        if((mq2_init - mq2_ratio) >= 2.5 || (mq7_init - mq7_ratio) >= 5.2) {
            iv_smoke.setImageResource(R.drawable.smoking);
            status = Double.toString(sensor_val) + " 입니다.\n => 담배연기가 30cm 이내에서 탐지 되었습니다.";
        }
        else if((mq2_init - mq2_ratio) >= 1.7 || (mq7_init - mq7_ratio) >= 3.3) {
            iv_smoke.setImageResource(R.drawable.smoking);
            status = Double.toString(sensor_val) + " 입니다.\n => 담배연기가 50cm 이내에서 탐지 되었습니다.";
        }
        else if(mq2_init - mq2_ratio >= 0.7 || (mq7_init - mq7_ratio) >= 1.2) {
            iv_smoke.setImageResource(R.drawable.smoking);
            status = Double.toString(sensor_val) + " 입니다.\n => 담배연기가 1m 이내에서 탐지 되었습니다.";
        } else {
            iv_smoke.setImageResource(R.drawable.no_smoking);
            status = Double.toString(sensor_val) + " 입니다.\n => 담배연기가 탐지 되지 않았습니다";
        }

        return status;
    }
}