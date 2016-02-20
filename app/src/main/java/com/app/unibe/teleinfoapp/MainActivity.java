package com.app.unibe.teleinfoapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSION_NETWORK = 1;
    private static final int REQUEST_READ_PHONE_STATE_PERMISSION = 225;

    public static Context context;
    private TelephonyManager tm;
    private TextView lba2,lbb2,lbc2,lbd2,lbe2,lbf2,lbg2,lbh2,lbi2,errormsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);


        //getting Components
        lba2 = (TextView) findViewById(R.id.lba2);
        lbb2 = (TextView) findViewById(R.id.lbb2);
        lbc2 = (TextView) findViewById(R.id.lbc2);
        lbd2 = (TextView) findViewById(R.id.lbd2);
        lbe2 = (TextView) findViewById(R.id.lbe2);
        lbf2 = (TextView) findViewById(R.id.lbf2);
        lbg2 = (TextView) findViewById(R.id.lbg2);
        lbh2 = (TextView) findViewById(R.id.lbh2);
        lbi2 = (TextView) findViewById(R.id.lbi2);
        errormsg = (TextView) findViewById(R.id.errormsg);

        //requestPermissions
        boolean permissionReadPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        boolean permissionCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;

        if (permissionReadPhone && permissionCoarseLocation) {
            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            this.setInfo();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_READ_PHONE_STATE_PERMISSION);

        }

        //go to ChartActivity action
        findViewById(R.id.btnChart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChartActivity.class));
            }
        });

        //GetCellLocation action
        findViewById(R.id.btnMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GsmCellLocation cellGsmLocation = (GsmCellLocation) tm.getCellLocation();

                String mCID = String.valueOf(cellGsmLocation.getCid());
                String mLAC = String.valueOf(cellGsmLocation.getLac());

                String mMNC = tm.getSimOperator().substring(3, tm.getSimOperator().length());
                String mMCC = tm.getSimOperator().substring(0, 3);

                new GetCellLocation().execute(mCID,mLAC,mMCC,mMNC);
            }
        });

    }

    //validate requestPermissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    this.setInfo();
                } else {
                    errormsg.setText("Permiso READ_PHONE_STATE denegado.");
                }
                return;
            }
            case REQUEST_PERMISSION_NETWORK: {
                if (grantResults.length > 0 && grantResults[0]!= PackageManager.PERMISSION_GRANTED) {
                    errormsg.setText("Permiso ACCESS_COARSE_LOCATION denegado.");
                }
                return;
            }
        }
    }

    //GetNetworkType
    public String GetNetworkType(TelephonyManager tm){
        switch (tm.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT: return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA: return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EDGE: return "EDGE";
            case TelephonyManager.NETWORK_TYPE_EHRPD: return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_EVDO_0: return "EVDO rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A: return "EVDO rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B: return "EVDO rev. B";
            case TelephonyManager.NETWORK_TYPE_GPRS: return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA: return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA: return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPAP: return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_HSUPA: return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_IDEN: return "iDen";
            case TelephonyManager.NETWORK_TYPE_LTE: return "LTE";
            case TelephonyManager.NETWORK_TYPE_UMTS: return "UMTS";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN: return "Unknown";
        }
        throw new RuntimeException("New type of network");
    }

    //getPhoneType
    public String getPhoneType(TelephonyManager phonyManager){
        int phoneType = phonyManager.getPhoneType();
        switch(phoneType){
            case TelephonyManager.PHONE_TYPE_NONE:
                return "NONE";

            case TelephonyManager.PHONE_TYPE_GSM:
                return "GSM";

            case TelephonyManager.PHONE_TYPE_CDMA:
                return "CDMA";

            case TelephonyManager.PHONE_TYPE_SIP:
                 return "SIP";

            default:
                return "UNKNOWN";
        }

    }

    //get all information from TelephonyManager
    public void setInfo(){
        String SIMSerialNumber = tm.getSimSerialNumber();
        String simOperatorCode = tm.getSimOperator() + " - " + tm.getSimOperatorName();
        String Imsi = tm.getSubscriberId();
        String SIMCountryISO = tm.getSimOperator().substring(0,3) + " - "+ tm.getSimCountryIso();
        String networkCountryISO = tm.getSimOperator().substring(3,tm.getSimOperator().length()) + " - " + tm.getNetworkCountryIso();
        String NetworkOperator = tm.getNetworkOperator() + " - " + tm.getNetworkOperatorName();
        String NetworkType = Integer.toString(tm.getNetworkType()) + " - " + this.GetNetworkType(tm);
        String PhoneNumber = tm.getLine1Number();
        if(PhoneNumber.isEmpty()){
            PhoneNumber = "Número no disponible";
        }
        String PhoneType = getPhoneType(tm);

        lba2.setText(SIMSerialNumber);
        lbb2.setText(simOperatorCode);
        lbc2.setText(Imsi);
        lbd2.setText(SIMCountryISO);
        lbe2.setText(networkCountryISO);
        lbf2.setText(NetworkOperator);
        lbg2.setText(NetworkType);
        lbh2.setText(PhoneNumber);
        lbi2.setText(PhoneType);
    }
}

