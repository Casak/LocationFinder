package com.example.noname.freelancerproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by admin on 10/10/2016.
 */
public class MyService extends Service {

    private static Timer timer = new Timer();
    private Context ctx;

    private static final String TAG = "MyService";

    //used for getting the handler from other class for sending messages
    public static Handler mMyServiceHandler 			= null;
    //used for keep track on Android running status
    public static Boolean 		mIsServiceRunning 			= false;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;

        timer.scheduleAtFixedRate(new mainTask(), 5000, 20000);


        Toast.makeText(this, "MyService Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            Message msgToActivity = new Message();
            msgToActivity.what = 1;
            msgToActivity.obj  = "TimerTask Trigger";

            MainActivity.mUiHandler.sendMessage(msgToActivity);
        }
    }

    // @Override
   // public void onStart(Intent intent, int startId) {
    //    Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
    //    Log.d(TAG, "onStart");
   // }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MyThread myThread = new MyThread();
        myThread.start();

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        mIsServiceRunning = true; // set service running status = true

        Toast.makeText(this, "Congrats! My Service Started", Toast.LENGTH_LONG).show();
        // We need to return if we want to handle this service explicitly.

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");

        mIsServiceRunning = false; // make it false, as the service is already destroyed.
    }

    //Your inner thread class is here to getting response from Activity and processing them
    class MyThread extends Thread
    {
        private static final String INNER_TAG = "MyThread";

        public void run()
        {
            this.setName(INNER_TAG);

            // Prepare the looper before creating the handler.
            Looper.prepare();
            mMyServiceHandler = new Handler()
            {
                //here we will receive messages from activity(using sendMessage() from activity)
                public void handleMessage(Message msg)
                {
                    Log.i("BackgroundThread","handleMessage(Message msg)" );
                    switch(msg.what)
                    {
                        case 0: // we sent message with what value =0 from the activity. here it is
                            //Reply to the activity from here using same process handle.sendMessage()
                            //So first get the Activity handler then send the message
                            if(null != MainActivity.mUiHandler)
                            {
                                //first build the message and send.
                                //put a integer value here and get it from the Activity handler
                                //For Example: lets use 0 (msg.what = 0;)
                                //for receiving service running status in the activity
                                Message msgToActivity = new Message();
                                msgToActivity.what = 0;
                                if(true ==mIsServiceRunning)
                                    msgToActivity.obj  = "Request Received. Service is Running"; // you can put extra message here
                                else
                                    msgToActivity.obj  = "Request Received. Service is not Running"; // you can put extra message here

                                MainActivity.mUiHandler.sendMessage(msgToActivity);
                            }

                            break;

                        default:
                            break;
                    }
                }
            };

            Looper.loop();
        }
    }
}

