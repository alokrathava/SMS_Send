package com.devmonkey.sms_send;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.devmonkey.sms_send.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Context context = this;
    private ActivityMainBinding binding;
    private String Message;
    private String PhoneNumber;
    private int PermissionCode = 10001;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        binding = ActivityMainBinding.inflate ( getLayoutInflater ( ) );
        setContentView ( binding.getRoot ( ) );

        requestPermission ( );
        init ( );
    }

    /*---------------------------------------------Permissions---------------------------------------*/
    private void requestPermission () {
        if (ContextCompat.checkSelfPermission ( context , Manifest.permission.SEND_SMS ) != PackageManager.PERMISSION_GRANTED) {
            PermissionReq ( );
        } else {
            init ( );
        }
    }

    private void PermissionReq () {
        ActivityCompat.requestPermissions ( MainActivity.this , new String[]{Manifest.permission.SEND_SMS} , PermissionCode );
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode , @NonNull String[] permissions , @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult ( requestCode , permissions , grantResults );

        if (requestCode == PermissionCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText ( context , "PERMISSION GRANTED" , Toast.LENGTH_SHORT ).show ( );
                init ( );
            } else {
                Toast.makeText ( context , "PERMISSION DENIED" , Toast.LENGTH_SHORT ).show ( );
            }
        }
    }

    /*---------------------------------------------Send SMS------------------------------------------*/
    private void init () {
        binding.enliLogin.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick ( View v ) {
                PhoneNumber = binding.phone.getText ( ).toString ( ).trim ( );
                Message     = binding.message.getText ( ).toString ( ).trim ( );

                sendSMS ( PhoneNumber , Message );
            }
        } );
    }

    private void sendSMS ( String phoneNumber , String message ) {

        Log.e ( TAG , "sendSMS: " + phoneNumber );
        Log.e ( TAG , "sendSMS: " + message );

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast ( context , 0 ,
                new Intent ( SENT ) , 0 );

        PendingIntent deliveredPI = PendingIntent.getBroadcast ( context , 0 ,
                new Intent ( DELIVERED ) , 0 );


        //---when the SMS has been sent---
        registerReceiver ( new BroadcastReceiver ( ) {
            @Override
            public void onReceive ( Context arg0 , Intent arg1 ) {
                switch (getResultCode ( )) {
                    case Activity.RESULT_OK:
                        Toast.makeText ( getBaseContext ( ) , "SMS sent" ,
                                Toast.LENGTH_SHORT ).show ( );
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText ( getBaseContext ( ) , "Generic failure" ,
                                Toast.LENGTH_SHORT ).show ( );
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText ( getBaseContext ( ) , "No service" ,
                                Toast.LENGTH_SHORT ).show ( );
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText ( getBaseContext ( ) , "Null PDU" ,
                                Toast.LENGTH_SHORT ).show ( );
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText ( getBaseContext ( ) , "Radio off" ,
                                Toast.LENGTH_SHORT ).show ( );
                        break;
                }
            }
        } , new IntentFilter ( SENT ) );

        //---when the SMS has been delivered---
        registerReceiver ( new BroadcastReceiver ( ) {
            @Override
            public void onReceive ( Context arg0 , Intent arg1 ) {
                switch (getResultCode ( )) {
                    case Activity.RESULT_OK:
                        Toast.makeText ( getBaseContext ( ) , "SMS delivered" ,
                                Toast.LENGTH_SHORT ).show ( );
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText ( getBaseContext ( ) , "SMS not delivered" ,
                                Toast.LENGTH_SHORT ).show ( );
                        break;
                }
            }
        } , new IntentFilter ( DELIVERED ) );

        SmsManager sms = SmsManager.getDefault ( );
        sms.sendTextMessage ( phoneNumber , null , message , sentPI , deliveredPI );

    }
}