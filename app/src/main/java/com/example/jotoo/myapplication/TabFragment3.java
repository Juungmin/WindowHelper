package com.example.jotoo.myapplication;

/**
 * Created by kwangwoon on 2017. 10. 14..
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TabFragment3 extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef_dust;
    DatabaseReference myRef_gas;
    Context context;
    View view;
    TextView title;
    TextView subtitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_3, container, false);
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
                Toast.makeText(context.getApplicationContext(), "미세먼지 푸시 현재 상태 : "+b, Toast.LENGTH_SHORT).show();
                switch_check[0] = b;
            }
        });
        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(context.getApplicationContext(), "미세먼지 푸시 현재 상태 : "+b, Toast.LENGTH_SHORT).show();
                switch_check[1] = b;
            }
        });
        sw3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(context.getApplicationContext(), "담배 푸시 현재 상태 : "+b, Toast.LENGTH_SHORT).show();
                switch_check[2] = b;
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        String today = sdf.format(new Date());
        String zone_location = "zone_"+today;

//        myRef_dust = database.getReference("home test").child("dust");
        myRef_dust = database.getReference("home test").child(zone_location).child("dust");
        myRef_gas = database.getReference("home test").child(zone_location).child("gas");
        //Read from the DB
        //update if there is a change on DB
        myRef_dust.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value_dust = dataSnapshot.child("dust_val").getValue(String.class);

                if(value_dust!=null) {
                    if (Integer.parseInt(value_dust) >= 150 && switch_check[0] == true) {
                        Notification_dust();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        myRef_gas.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value_gas = dataSnapshot.child("gas_val").getValue(String.class);

                if(value_gas!=null) {
                    if (Integer.parseInt(value_gas) >= 150 && switch_check[2] == true) {
                        Notification_gas();
                        //Log.d(Boolean.toString(switch_check[1]),"aaaa");
                    }
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

    public void Notification_dust() {
        Resources res = getResources();

        Intent notificationIntent = new Intent(context.getApplicationContext(), Notifications.class);
        notificationIntent.putExtra("notificationId", 9999); //전달할 값
        PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());

        builder.setContentTitle("WindowHelper")
                .setContentText("우리 집 미세먼지 농도가 150 이상으로 감지되었습니다.")
                .setTicker("상태바 한줄 메시지")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1234, builder.build());
    }
    public void Notification_gas() {
        Resources res = getResources();

        Intent notificationIntent = new Intent(context.getApplicationContext(), Notifications.class);
        notificationIntent.putExtra("notificationId", 9999); //전달할 값
        PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());

        builder.setContentTitle("WindowHelper")
                .setContentText("우리 집 가스 농도가 150 이상으로 감지되었습니다.")
                .setTicker("상태바 한줄 메시지")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1234, builder.build());
    }
}
