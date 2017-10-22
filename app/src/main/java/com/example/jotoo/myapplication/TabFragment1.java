package com.example.jotoo.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by kwangwoon on 2017. 10. 14..
 */

public class TabFragment1 extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef_dust;
    Context context;

    TextView today;
    TextView gps_text;
    ImageView img1, img2, img3;
    Document doc = null;
    String loc;
    View rootView;

    GPSTracker gps = null;

    public Handler mHandler;

    public static int RENEW_GPS = 1;
    public static int SEND_PRINT = 2;

    TextView tv_dust;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        rootView =  inflater.inflate(R.layout.tab_fragment_1, container, false);

        String url, url2;
        String serviceKey;
        context = getActivity();

        gps_text = (TextView)rootView.findViewById(R.id.gps_text);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==RENEW_GPS){
                    makeNewGpsService();
                }
                if(msg.what==SEND_PRINT){
                    //logPrint((String)msg.obj);
                }
            }
        };

        if(gps == null) {
            gps = new GPSTracker(context.getApplicationContext(),mHandler);
        }else{
            gps.Update();
        }

        // check if GPS enabled
        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            String address = getAddress(latitude, longitude);

            gps_text.setText(address);
            // \n is for new line
            String[] Gu = address.split(" ");

            //editText.append(Gu[2]);
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        String currentDate = sdf.format(new Date());

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH", Locale.KOREA);
        String currentHour = sdf2.format(new Date());

        Log.d(currentHour, "원래 시간");
        if((Integer.parseInt(currentHour) < 18 && Integer.parseInt(currentHour) >= 6)){
            currentHour = "0600";
        }
        else if((Integer.parseInt(currentHour)>=18)){
            currentHour = "1800";
        }
        else if((Integer.parseInt(currentHour) < 6)){
            int Date = Integer.parseInt(currentDate)-1;
            currentDate = String.valueOf(Date);
        }
        //Log.d(currentHour, "현재 시간");
        //Log.d(currentDate, "현재 날짜");

        //showdate = (TextView) rootView.findViewById(R.id.date);
        //showdate.append(currentDate);
        loc = "11B00000";
        serviceKey = "T5fzCFA3Z5pBRBdAaL0%2Bge7wIl%2Bcuh4Xfa%2FpCg9G6%2BolcfOjtId7agCorNFCa6HGZg7yqvI6IDDJmq6baiT7gg%3D%3D";
        url = "http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleLandWeather?ServiceKey=T5fzCFA3Z5pBRBdAaL0%2Bge7wIl%2Bcuh4Xfa%2FpCg9G6%2BolcfOjtId7agCorNFCa6HGZg7yqvI6IDDJmq6baiT7gg%3D%3D"
                + "&regId=" + loc + "&tmFc=" + currentDate + currentHour;
        url2 = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastGrib" + "?ServiceKey=" + serviceKey + "&base_date=" + currentDate + "&base_time=" + currentHour + "&nx=60&ny=127";
        GetXMLTask task = new GetXMLTask();
        task.execute(url);

        GetXMLTask2 task2 = new GetXMLTask2();
        task2.execute(url2);

        //TextView tv_dust;

        tv_dust = (TextView) rootView.findViewById(R.id.tv_dust);

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
        return rootView;
    }

    public String getAddress(double lat, double lng) {
        String nowAddress = "현재 위치를 확인할 수 없습니다.";
        Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.KOREA);
        List<Address> address;
        try {
            if(geocoder != null) {
                address = geocoder.getFromLocation(lat, lng, 1);

                if(address != null && address.size() > 0) {
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    nowAddress = currentLocationAddress;
                }
            }
        } catch(IOException e) {
            //Toast.makeText("주소를 가져올 수 없습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return nowAddress;
    }
    public void makeNewGpsService(){
        if(gps == null) {
            gps = new GPSTracker(context.getApplicationContext(),mHandler);
        }else{
            gps.Update();
        }
    }

    private class GetXMLTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML문서 빌더 객체를 생성
                doc = db.parse(new InputSource(url.openStream())); //XML문서를 파싱한다.
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                //Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {

            TextView text2,text4;
            text2 = (TextView)rootView.findViewById(R.id.text2);
            text4 = (TextView)rootView.findViewById(R.id.text4);

            String wf3Pm = "";
            String wf4Pm = "";

            NodeList nodeList = doc.getElementsByTagName("item");

            Node node = nodeList.item(0);
            Element fstElmnt = (Element) node;


            NodeList nameList2 = fstElmnt.getElementsByTagName("wf3Pm");
            wf3Pm = ((Node) nameList2.item(0)).getChildNodes().item(0).getNodeValue();

            NodeList nameList4 = fstElmnt.getElementsByTagName("wf4Pm");
            wf4Pm = ((Node) nameList4.item(0)).getChildNodes().item(0).getNodeValue();

            text2.setText(wf3Pm);

            text4.setText(wf4Pm);

            //Toast.makeText(getApplicationContext(), wf3Pm + "\n"+wf4Pm, Toast.LENGTH_LONG).show();
            if(wf3Pm.equals("구름많음")){
                //setContentView(R.layout.activity_main);
                img2 = (ImageView)rootView.findViewById(R.id.img2);
                img2.setImageResource(R.mipmap.w3);
            }

            else if(wf3Pm.equals("구름조금")){
                //setContentView(R.layout.activity_main);
                img2 = (ImageView)rootView.findViewById(R.id.img2);
                img2.setImageResource(R.mipmap.w2);
            }
            else if(wf3Pm.equals("흐림")){
                //setContentView(R.layout.activity_main);
                img2 = (ImageView)rootView.findViewById(R.id.img2);
                img2.setImageResource(R.mipmap.ic_launcher);
            }
            else if(wf3Pm.equals("맑음")){
                //setContentView(R.layout.activity_main);
                img2 = (ImageView)rootView.findViewById(R.id.img2);
                img2.setImageResource(R.mipmap.w1);
            }
            if(wf4Pm.equals("구름많음")){
                //setContentView(R.layout.activity_main);
                img3 = (ImageView)rootView.findViewById(R.id.img3);
                img3.setImageResource(R.mipmap.w3);
            }

            else if(wf4Pm.equals("구름조금")){
                //setContentView(R.layout.activity_main);
                img3 = (ImageView)rootView.findViewById(R.id.img3);
                img3.setImageResource(R.mipmap.w2);
            }
            else if(wf4Pm.equals("흐림")){
                //setContentView(R.layout.activity_main);
                img3 = (ImageView)rootView.findViewById(R.id.img3);
                img3.setImageResource(R.mipmap.ic_launcher);
            }
            else if(wf4Pm.equals("맑음")){
                //setContentView(R.layout.activity_main);
                img3 = (ImageView)rootView.findViewById(R.id.img3);
                img3.setImageResource(R.mipmap.w1);
            }

            super.onPostExecute(doc);
        }
    }

    private class GetXMLTask2 extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                //Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {

            today = (TextView)rootView.findViewById(R.id.today);

            //today2 = (TextView) findViewById(R.id.today2);

            String pm10 = "";
            String pm25 = "";

            NodeList nodeList = doc.getElementsByTagName("item");

            Node node = nodeList.item(4);
            Element fstElmnt = (Element) node;

            NodeList nameList = fstElmnt.getElementsByTagName("obsrValue");
            Element nameElement = (Element) nameList.item(0);
            nameList = nameElement.getChildNodes();
            pm10 = ((Node) nameList.item(0)).getNodeValue();

            if(Integer.parseInt(pm10) == 1){
                today.setText("맑음");
            }
            else if(Integer.parseInt(pm10) == 2){
                today.setText("구름조금");
            }
            else if(Integer.parseInt(pm10) == 3){
                today.setText("구름많음");

            }
            else if(Integer.parseInt(pm10) == 4){
                today.setText("흐림");
            }

            /*NodeList nameList13 = fstElmnt.getElementsByTagName("fcstValue");
            pm25 = ((Node) nameList13.item(0)).getChildNodes().item(0).getNodeValue();*/


            //today2.setText(pm25);

            if(today.getText() == "구름많음"){
                //setContentView(R.layout.activity_main);
                img1 = (ImageView)rootView.findViewById(R.id.img1);
                img1.setImageResource(R.mipmap.w3);
            }

            else if(today.getText().toString() == "구름조금"){
                //setContentView(R.layout.activity_main);
                img1 = (ImageView)rootView.findViewById(R.id.img1);
                img1.setImageResource(R.mipmap.w2);
            }
            else if(today.getText().toString() == "흐림"){
                //setContentView(R.layout.activity_main);
                img1 = (ImageView)rootView.findViewById(R.id.img1);
                img1.setImageResource(R.mipmap.ic_launcher);
            }
            else if(today.getText().toString() == "맑음"){
                //setContentView(R.layout.activity_main);
                img1 = (ImageView)rootView.findViewById(R.id.img1);
                img1.setImageResource(R.mipmap.w1);
            }

            super.onPostExecute(doc);
        }
    }
}
