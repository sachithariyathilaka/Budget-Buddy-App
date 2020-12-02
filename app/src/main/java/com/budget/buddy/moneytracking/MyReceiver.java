package com.budget.buddy.moneytracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.budget.buddy.moneytracking.Activities.NewItemActivity;

public class MyReceiver extends BroadcastReceiver {


    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SmsBradcastReceiver";

    String msg, phoneNo = "";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG,"Intent Received: "+ intent.getAction());

        if (intent.getAction() == SMS_RECEIVED){
            Bundle bundle = intent.getExtras();
            if (bundle!=null){
                Object[] mypdu = (Object[])bundle.get("pdus");
                final SmsMessage[] message = new SmsMessage[mypdu.length];

                for (int i = 0; i<mypdu.length; i++){
                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        String format = bundle.getString("format");

                        message[i] = SmsMessage.createFromPdu((byte[])mypdu[i], format);
                    }else {
                        message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i]);
                    }
                    msg = message[i].getMessageBody();
                    phoneNo = message[i].getOriginatingAddress();
                }
                //Toast.makeText(context,phoneNo+" "+ msg,Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("message", msg);
                context.startActivity(i);
            }
        }

    }
}
