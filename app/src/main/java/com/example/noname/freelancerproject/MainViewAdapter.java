package com.example.noname.freelancerproject;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainViewAdapter extends BaseAdapter {
    private  LayoutInflater layoutInflater;
    private Context context;
    private int temp;
    private JSONArray listItems;
    private int positionPrivate;
    private JSONObject jsonObj;

    public MainViewAdapter(Context context, JSONObject jsonObj) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.jsonObj = jsonObj;

        JSONObject jObjectResult = null;

        try {
            jObjectResult = jsonObj.getJSONObject("Items");
            this.listItems = jObjectResult.getJSONArray("Item");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return listItems.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return listItems.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final SharedPreferences pref= context.getApplicationContext().getSharedPreferences("MyPref", context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        try {
            positionPrivate = position;

            if(convertView == null){
                convertView = layoutInflater.inflate(com.example.noname.freelancerproject.R.layout.activity_row,parent,false);
            }

            TextView textView = (TextView) convertView.findViewById(com.example.noname.freelancerproject.R.id.rowText);
            textView.setText(listItems.getJSONObject(position).getString("description"));

            // set the checkbox stuff
            CheckBox chkEnabled = (CheckBox) convertView.findViewById(com.example.noname.freelancerproject.R.id.chkEnabled);


            if(listItems.getJSONObject(position).getString("isChecked").equals("1")){
                chkEnabled.setChecked(true);
            }
            else
            {
                chkEnabled.setChecked(false);
            }

            chkEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    //Toast.makeText(context, "Checked/Unchecked update saving ...", Toast.LENGTH_SHORT).show();
                    try {
                        if (isChecked)
                        {
                            // update json object
                            jsonObj.getJSONObject("Items").getJSONArray("Item").getJSONObject(position).put("isChecked", "1");
                        }
                        else
                        {
                            // update json object
                            jsonObj.getJSONObject("Items").getJSONArray("Item").getJSONObject(position).put("isChecked", "0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    editor.putString("json",jsonObj.toString());
                    editor.commit();
                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        //Toast.makeText(context, "Main list item clicked", Toast.LENGTH_SHORT).show();
                        if (listItems != null){
                            JSONObject clickedItem = listItems.getJSONObject(positionPrivate); // position

                            Intent intent = new Intent(context, DetailActivity.class);

                            intent.putExtra("clickedItem", clickedItem.toString());
                            context.startActivity(intent);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }

                private void onClickSendMessage() {

                    if(null != MyService.mMyServiceHandler)
                    {
                        //first build the message and send.
                        //put a integer value here and get it from the service handler
                        //For Example: lets use 0 (msg.what = 0;) for getting service running status from the service
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj  = "Add your Extra Meaage Here"; // you can put extra message here
                        MyService.mMyServiceHandler.sendMessage(msg);
                    }
                }
            });

            return convertView;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
