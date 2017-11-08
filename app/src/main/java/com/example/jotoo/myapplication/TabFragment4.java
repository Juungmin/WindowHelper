package com.example.jotoo.myapplication;

/**
 * Created by kwangwoon on 2017. 10. 14..
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TabFragment4 extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef_dust;
    DatabaseReference myRef_gas;
    Context context;
    View view;
    TextView title;
    TextView subtitle;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    SimpleDateFormat sdf2 = new SimpleDateFormat("HH", Locale.KOREA);

    String today = sdf.format(new Date());
    String currentHour = sdf2.format(new Date());

    double temp_mq2 = 0;
    double temp_mq7 = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_4, container, false);
        context=getActivity();

        final boolean[] switch_check = new boolean[3];
        switch_check[0] = true;
        switch_check[1] = true;
        switch_check[2] = true;

        title =  (TextView) view.findViewById(R.id.Title);
        Typeface typeface2 = Typeface.createFromAsset(context.getAssets(),"BMDOHYEON_ttf.ttf");
        title.setTypeface(typeface2);

        subtitle =  (TextView) view.findViewById(R.id.tv_SubTitle);
        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(),"BMJUA_ttf.ttf");
        subtitle.setTypeface(typeface1);

        TextView dustTitle = (TextView) view.findViewById(R.id.dust_Title);
        dustTitle.setTypeface(typeface2);
        TextView dustSubTitle = (TextView) view.findViewById(R.id.dust_SubTitle);
        dustSubTitle.setTypeface(typeface1);

        Switch toggle1 = (Switch)view.findViewById(R.id.toggle1);
        toggle1.setTypeface(typeface1);
        Switch toggle2 = (Switch)view.findViewById(R.id.toggle2);
        toggle2.setTypeface(typeface1);
        Switch toggle3 = (Switch)view.findViewById(R.id.toggle3);
        toggle3.setTypeface(typeface1);


        TextView smokeTitle = (TextView) view.findViewById(R.id.smoke_Title);
        smokeTitle.setTypeface(typeface2);
        TextView smokeSubTitle = (TextView) view.findViewById(R.id.smoke_SubTitle);
        smokeSubTitle.setTypeface(typeface1);



        database = FirebaseDatabase.getInstance();

        Switch sw = (Switch)view.findViewById(R.id.toggle1);
        Switch sw2 = (Switch)view.findViewById(R.id.toggle2);
        Switch sw3 = (Switch)view.findViewById(R.id.toggle3);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(context.getApplicationContext(), "아침 미세먼지 푸시 : "+b, Toast.LENGTH_SHORT).show();
                switch_check[0] = b;
            }
        });
        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(context.getApplicationContext(), "저녁 미세먼지 푸시 : "+b, Toast.LENGTH_SHORT).show();
                switch_check[1] = b;
            }
        });
        sw3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(context.getApplicationContext(), "담배연기 푸시 : "+b, Toast.LENGTH_SHORT).show();
                switch_check[2] = b;
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        String today = sdf.format(new Date());
        String zone_location = "zone_"+today;


        myRef_gas = database.getReference("home test").child(zone_location);
        myRef_dust = database.getReference("home test").child(zone_location);
        //Read from the DB
        //update if there is a change on DB
        //Morning notification from 5 to 12

        myRef_dust.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dust_outside = dataSnapshot.child("dust_outside").child("dust_out_val").getValue(String.class);
                String dust_inside = dataSnapshot.child("dust_inside").child("dust_val").getValue(String.class);
                String reed = dataSnapshot.child("reed").child("val").getValue(String.class);

                double value_outside = Double.parseDouble(dust_outside);
                double value_inside = Double.parseDouble(dust_inside);

                String status = "";

                if(dust_outside!=null && Integer.parseInt(reed) == 1) {
                    if (value_outside > 150 && switch_check[0] == true ) {
                        status = "'매우 나쁨'";
                        if(Integer.parseInt(currentHour) >= 5 && Integer.parseInt(currentHour) <= 12)
                            Notification_dust(status);
                    }
                    else if(value_outside > 80 && value_outside <= 150 && switch_check[0] == true) {
                        status = "'약간 나쁨'";
                        if(Integer.parseInt(currentHour) >= 5 && Integer.parseInt(currentHour) <= 12)
                            Notification_dust(status);
                    }
                }
                if(dust_inside!=null) {
                    if (value_inside > 150 && switch_check[0] == true) {
                        status = "'매우 나쁨'";
                        if(Integer.parseInt(currentHour) >= 5 && Integer.parseInt(currentHour) <= 12)
                            Notification_dust(status);
                    }
                    else if(value_inside > 80 && value_inside <= 150 && switch_check[0] == true) {
                        status = "'약간 나쁨'";
                        if(Integer.parseInt(currentHour) >= 5 && Integer.parseInt(currentHour) <= 12)
                            Notification_dust(status);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //Evening notification from 16 to 21
        myRef_dust.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dust_outside = dataSnapshot.child("dust_outside").child("dust_out_val").getValue(String.class);
                String dust_inside = dataSnapshot.child("dust_inside").child("dust_val").getValue(String.class);
                String reed = dataSnapshot.child("reed").child("val").getValue(String.class);

                double value_outside = Double.parseDouble(dust_outside);
                double value_inside = Double.parseDouble(dust_inside);

                String status = "";

                if(dust_outside!=null && Integer.parseInt(reed) == 1) {

                    if (value_outside > 150 && switch_check[1] == true) {
                        status = "'매우 나쁨'";
                        if(Integer.parseInt(currentHour) >= 16 && Integer.parseInt(currentHour) <= 21)
                            Notification_dust(status);
                    }
                    else if(value_outside > 80 && value_outside <= 150 && switch_check[1] == true) {
                        status = "'약간 나쁨'";
                        if(Integer.parseInt(currentHour) >= 16 && Integer.parseInt(currentHour) <= 21)
                            Notification_dust(status);
                    }
                }
                if(dust_inside!=null) {
                    if (value_inside > 150 && switch_check[1] == true) {
                        status = "'매우 나쁨'";
                        if(Integer.parseInt(currentHour) >= 16 && Integer.parseInt(currentHour) <= 21)
                            Notification_dust(status);
                    }
                    else if(value_inside > 80 && value_inside <= 150 && switch_check[1] == true) {
                        status = "'약간 나쁨'";
                        if(Integer.parseInt(currentHour) >= 16 && Integer.parseInt(currentHour) <= 21)
                            Notification_dust(status);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //Gas notification
        myRef_gas.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value_mq2 = dataSnapshot.child("gas").child("sensor_val_mq2").getValue(String.class);
                String value_mq7 = dataSnapshot.child("gas").child("sensor_val_mq7").getValue(String.class);
                String reed = dataSnapshot.child("reed").child("val").getValue(String.class);

                double mq2_ratio = Double.parseDouble(value_mq2);
                double mq7_ratio = Double.parseDouble(value_mq7);

                String status = "";

                if(temp_mq2 == 0) {
                    temp_mq2 = Double.parseDouble(value_mq2);
                }

                if(temp_mq7 == 0) {
                    temp_mq7 = Double.parseDouble(value_mq7);
                }

                if(value_mq2 != null && value_mq7 != null) {
                    if(Integer.parseInt(reed) == 1 && switch_check[2] == true)
                    {
                        if((temp_mq2 - mq2_ratio) >= 3 || (temp_mq7 - mq7_ratio) >= 6) {
                            status = "매우 높음";
                            Notification_gas(status);
                        }
                        else if((temp_mq2 - mq2_ratio) >= 2 || (temp_mq7 - mq7_ratio) >= 4) {
                            status = "높음";
                            Notification_gas(status);
                        }
                        else if(temp_mq2 - mq2_ratio >= 1 || (temp_mq7 - mq7_ratio) >= 2) {
                            status = "약간 높음";
                            Notification_gas(status);
                        } else {
                            status = "보통";
                            Notification_gas(status);
                        }
                    }
                }
                //}
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }

        });
        return view;
    }

    public void Notification_dust(String status) {

        Intent notificationIntent = new Intent(context.getApplicationContext(), TabFragment1.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());

        builder.setContentTitle("WindowHelper")
                .setContentText("우리 집 미세먼지 농도가 "+ status +" 감지되었습니다.")
                .setTicker("상태바 한줄 메시지")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

    }
    public void Notification_gas(String status) {
        Intent notificationIntent = new Intent(context.getApplicationContext(), TabFragment1.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());

        builder.setContentTitle("WindowHelper")
                .setContentText("우리 집 가스 농도가 " + status + " 으로 감지되었습니다.")
                .setTicker("상태바 한줄 메시지")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

    }
}
