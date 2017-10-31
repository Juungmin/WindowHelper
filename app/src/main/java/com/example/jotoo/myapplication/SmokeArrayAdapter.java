package com.example.jotoo.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwangwoon on 2017. 10. 18..
 */

public class SmokeArrayAdapter  extends ArrayAdapter<Smoke> {
private static final String TAG = "SmokeArrayAdapter";
private List<Smoke> smokeList = new ArrayList<Smoke>();

static class SmokeViewHolder {
    TextView line1;
}

    public SmokeArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(Smoke object) {
        smokeList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.smokeList.size();
    }

    @Override
    public Smoke getItem(int index) {
        return this.smokeList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SmokeViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item_smoke, parent, false);
            viewHolder = new SmokeViewHolder();
            viewHolder.line1 = (TextView) row.findViewById(R.id.line1);
            row.setTag(viewHolder);
        } else {
            viewHolder = (SmokeViewHolder)row.getTag();
        }
        Smoke smoke = getItem(position);
        viewHolder.line1.setText(smoke.getLine1());
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}

