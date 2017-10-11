package com.example.jotoo.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText edt1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt1 = (EditText)findViewById(R.id.editText);
    }

    public void btnStart(View v) {
        Intent intent = new Intent(this, SubActivity.class);
        intent.putExtra("input1",edt1.getText().toString());
        startActivity(intent);
    }
}
