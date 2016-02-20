package com.app.unibe.teleinfoapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetCellLocation extends AsyncTask<String, Void, String> {

//    private final String API_KEY = "AIzaSyCqewDykDetko5ZfgNSbsQ46p6uXc2bOGk";
    private final String API_KEY = "AIzaSyDipTYsh9TO8sIH55tt62zKM40iYkZtkSY";

    ProgressDialog pd;

    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(MainActivity.context);
        pd.setTitle("Localizando torre");
        pd.setMessage("...");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
    }

    @Override
    protected String doInBackground(String... params) {

        String result = null;

        HttpClient httpclient =  new DefaultHttpClient();
        HttpPost httpost = new HttpPost("https://www.googleapis.com/geolocation/v1/geolocate?key=" + API_KEY);

        StringEntity se;

        try {
            JSONObject cellTower = new JSONObject();
            cellTower.put("cellId", params[0]);
            cellTower.put("locationAreaCode", params[1]);
            cellTower.put("mobileCountryCode", params[2]);
            cellTower.put("mobileNetworkCode", params[3]);

            JSONArray cellTowers = new JSONArray();
            cellTowers.put(cellTower);

            JSONObject rootObject = new JSONObject();
            rootObject.put("cellTowers", cellTowers);

            se = new StringEntity(rootObject.toString());
            se.setContentType("application/json");

            httpost.setEntity(se);
            httpost.setHeader("Accept", "application/json");
            httpost.setHeader("Content-type", "application/json");

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String response = httpclient.execute(httpost, responseHandler);

            result = response;
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (pd != null) {
            try {
                pd.dismiss();
            }
            catch (Exception e) {}
        }

        if (result != null) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                JSONObject location = jsonResult.getJSONObject("location");
                String lat, lng;
                lat = location.getString("lat");
                lng = location.getString("lng");

                if ((lat != null) &&
                        (!lat.isEmpty()) &&
                        (lng != null) &&
                        (!lng.isEmpty())) {
                    MainActivity.context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + lat + "," + lng + "&iwloc=A")));
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.context, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

}