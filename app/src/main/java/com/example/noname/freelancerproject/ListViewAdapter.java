package com.example.noname.freelancerproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Armen on 30.09.2016.
 */
public class ListViewAdapter extends BaseAdapter {
    private  LayoutInflater layoutInflater;
    private ArrayList<String> stringList ;
    private Context context;
    private int temp;

    public ListViewAdapter(Context context, ArrayList<String> stringList) {
        layoutInflater = LayoutInflater.from(context);
        this.stringList = stringList;
        this.context = context;


    }

    @Override
    public int getCount() {
        return stringList.size();
    }

    @Override
    public Object getItem(int position) {
        return stringList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       if(convertView == null){
           convertView = layoutInflater.inflate(com.example.noname.freelancerproject.R.layout.activity_row,parent,false);
       }
        TextView textView = (TextView) convertView.findViewById(com.example.noname.freelancerproject.R.id.rowText);
        textView.setText(""+stringList.get(position));


        // set the checkbox stuff
        CheckBox chkEnabled = (CheckBox) convertView.findViewById(com.example.noname.freelancerproject.R.id.chkEnabled);

        if (true){
            chkEnabled.setChecked(true);
        }

        chkEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                Toast.makeText(context, "Checked/Unchecked update", Toast.LENGTH_SHORT).show();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Main list item clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
