package com.example.noname.freelancerproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity  {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public JSONArray jArray = null;
    public JSONObject jsonObj = null;
    ListView mainList;
    public static Handler mUiHandler = null;

    private Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.noname.freelancerproject.R.layout.activity_main);

        ctx = this;

        // Create service incoming handler
        mUiHandler = new Handler() // Receive messages from service class
        {


            public void handleMessage(Message msg)
            {
                try {
                switch(msg.what)
                {
                    case 0:
                        // add the status which came from service and show on GUI
                        Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ctx, DetailActivity.class);
                        JSONObject jsonObjDetail = jArray.getJSONObject(0);
                        intent.putExtra("clickedItem", jsonObjDetail.toString());
                        ctx.startActivity(intent);
                        break;

                    default:
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            }

        };



        // Load state
        pref= getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor=pref.edit();
        mainList = (ListView) findViewById(com.example.noname.freelancerproject.R.id.checkableList);

        try {
        //Get Data From internal file, if doesn't exist copy resource to internal and then use it .

            if(!pref.contains("json"))
            {
                jsonObj=openJsonFile("items.json");
            }
            else
            {
                jsonObj=new JSONObject(pref.getString("json",null));
            }
            JSONObject jObjectResult = jsonObj.getJSONObject("Items");
            this.jArray = jObjectResult.getJSONArray("Item");
            // Bind Data and pass the json object read from a file to the adapter
            MainViewAdapter customListViewAdapter = new MainViewAdapter(this, jsonObj);
            mainList.setAdapter(customListViewAdapter);


            // Service communication

            //startService(new Intent(this, MyService.class));
            onClickStartService();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //stopService(new Intent(this, MyService.class));

        Toast.makeText(this, "App Stopped", Toast.LENGTH_LONG).show();
    }

    private JSONObject openJsonFile(String filename) throws IOException, JSONException
    {
        InputStream inputStream = getResources().openRawResource(com.example.noname.freelancerproject.R.raw.items);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        ctr = inputStream.read();
        while (ctr != -1) {
            byteArrayOutputStream.write(ctr);
            ctr = inputStream.read();
        }
        inputStream.close();
        // Parse the data into json object to get original data in form of json.
        jsonObj = new JSONObject(byteArrayOutputStream.toString());
        return jsonObj;
    }

    //start the service
    public void onClickStartService()
    {
        //start the service from here //MyService is your service class name
        startService(new Intent(this, MyService.class));
    }
    //Stop the started service
    public void onClickStopService()
    {
        //Stop the running service from here//MyService is your service class name
        //Service will only stop if it is already running.
        stopService(new Intent(this, MyService.class));
    }
    //send message to service
    public void onClickSendMessage()
    {
        //only we need a handler to send message to any component.
        //here we will get the handler from the service first, then
        //we will send a message to the service.

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

}
