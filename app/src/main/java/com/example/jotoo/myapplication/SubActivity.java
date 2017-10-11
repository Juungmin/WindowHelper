package com.example.jotoo.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SubActivity extends AppCompatActivity{

    TextView text1, text2, text3, text4, text5, text6, text7, text8, text9, text10, text11, text12, text13;
    Document doc = null;
    String loc;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub1);

        String url;
        Intent intent = getIntent();
        loc = "11B00000";

        url = "http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleLandWeather?ServiceKey=T5fzCFA3Z5pBRBdAaL0%2Bge7wIl%2Bcuh4Xfa%2FpCg9G6%2BolcfOjtId7agCorNFCa6HGZg7yqvI6IDDJmq6baiT7gg%3D%3D" + "&regId=" + loc + "&tmFc=201710060600";
        GetXMLTask task = new GetXMLTask();
        task.execute(url);
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
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {

            text1 = (TextView)findViewById(R.id.text1);
            text2 = (TextView)findViewById(R.id.text2);
            text3 = (TextView)findViewById(R.id.text3);
            text4 = (TextView)findViewById(R.id.text4);
            text5 = (TextView)findViewById(R.id.text5);
            text6 = (TextView)findViewById(R.id.text6);
            text7 = (TextView)findViewById(R.id.text7);
            text8 = (TextView)findViewById(R.id.text8);
            text9 = (TextView)findViewById(R.id.text9);
            text10 = (TextView)findViewById(R.id.text10);
            text11 = (TextView)findViewById(R.id.text11);
            text12 = (TextView)findViewById(R.id.text12);
            text13 = (TextView)findViewById(R.id.text13);

            String wf3Am = "";
            String wf3Pm = "";
            String wf4Am = "";
            String wf4Pm = "";
            String wf5Am = "";
            String wf5Pm = "";
            String wf6Am = "";
            String wf6Pm = "";
            String wf7Am = "";
            String wf7Pm = "";
            String wf8 = "";
            String wf9 = "";
            String wf10 = "";

            NodeList nodeList = doc.getElementsByTagName("item");


            Node node = nodeList.item(0);
            Element fstElmnt = (Element) node;

            NodeList nameList = fstElmnt.getElementsByTagName("wf3Am");
            Element nameElement = (Element) nameList.item(0);
            nameList = nameElement.getChildNodes();
            wf3Am = ((Node) nameList.item(0)).getNodeValue();

            NodeList nameList2 = fstElmnt.getElementsByTagName("wf3Pm");
            wf3Pm = ((Node) nameList2.item(0)).getChildNodes().item(0).getNodeValue();

            NodeList nameList3 = fstElmnt.getElementsByTagName("wf4Am");
            wf4Am = ((Node) nameList3.item(0)).getChildNodes().item(0).getNodeValue();

            NodeList nameList4 = fstElmnt.getElementsByTagName("wf4Pm");
            wf4Pm = ((Node) nameList4.item(0)).getChildNodes().item(0).getNodeValue();

            NodeList nameList5 = fstElmnt.getElementsByTagName("wf5Am");
            wf5Am = ((Node) nameList5.item(0)).getChildNodes().item(0).getNodeValue();

            NodeList nameList6 = fstElmnt.getElementsByTagName("wf5Pm");
            wf5Pm = ((Node) nameList6.item(0)).getChildNodes().item(0).getNodeValue();

            NodeList nameList7 = fstElmnt.getElementsByTagName("wf6Am");
            wf6Am = ((Node) nameList7.item(0)).getChildNodes().item(0).getNodeValue();

            NodeList nameList8 = fstElmnt.getElementsByTagName("wf6Pm");
            wf6Pm = ((Node) nameList8.item(0)).getChildNodes().item(0).getNodeValue();

            NodeList nameList9 = fstElmnt.getElementsByTagName("wf7Am");
            wf7Am = ((Node) nameList9.item(0)).getChildNodes().item(0).getNodeValue();

            NodeList nameList10 = fstElmnt.getElementsByTagName("wf7Pm");
            wf7Pm = ((Node) nameList10.item(0)).getChildNodes().item(0).getNodeValue();

            NodeList nameList11 = fstElmnt.getElementsByTagName("wf8");
            wf8 = ((Node) nameList11.item(0)).getChildNodes().item(0).getNodeValue();

            NodeList nameList12 = fstElmnt.getElementsByTagName("wf9");
            wf9 = ((Node) nameList12.item(0)).getChildNodes().item(0).getNodeValue();

            NodeList nameList13 = fstElmnt.getElementsByTagName("wf10");
            wf10 = ((Node) nameList13.item(0)).getChildNodes().item(0).getNodeValue();

            text1.setText(wf3Am);
            text2.setText(wf3Pm);
            text3.setText(wf4Am);
            text4.setText(wf4Pm);
            text5.setText(wf5Am);
            text6.setText(wf5Pm);
            text7.setText(wf6Am);
            text8.setText(wf6Pm);
            text9.setText(wf7Am);
            text10.setText(wf7Pm);
            text11.setText(wf8);
            text12.setText(wf9);
            text13.setText(wf10);

            super.onPostExecute(doc);
        }


    }
}
