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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class TabFragment3 extends Fragment {

    Context context;
    View view;

    FirebaseDatabase database;
    DatabaseReference myRef_dust;
    DatabaseReference myRef_gas;
    TextView title;
    TextView subtitle;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    SimpleDateFormat sdf2 = new SimpleDateFormat("HH", Locale.KOREA);

    String today = sdf.format(new Date());
    String currentHour = sdf2.format(new Date());

    String zone_location = "zone_"+today;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_3, container, false);
        context = getActivity();

        TextView title = (TextView) view.findViewById(R.id.Title);
        Typeface typeface2 = Typeface.createFromAsset(context.getAssets(), "BMDOHYEON_ttf.ttf");
        title.setTypeface(typeface2);

        TextView subtitle = (TextView) view.findViewById(R.id.tv_SubTitle);
        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "BMJUA_ttf.ttf");
        subtitle.setTypeface(typeface1);

        return view;
    }
}
