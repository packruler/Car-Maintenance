package com.packruler.carmaintenance.json;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Packruler on 5/11/15.
 */
public class VINDecoder {
    private static final String TAG = "VIN Decoder";

    private static final String API_KEY = "j53yt737y5ghad78ppe4k5k6";

    public static final String YEAR = "year";
    public static final String MAKE = "make";
    public static final String MODEL = "model";
    public static final String TRIM_LEVEL = "trim_level";
    public static final String TRIM_DETAILS = "trim_details";
    public static final String ENGINE = "engine";
    public static final String STYLE = "style";


    public static JSONObject decode(String VIN) {

        InputStream inputStream = null;
        JSONObject result = null;
        VIN = "WMWMM33508TP71418";

//        String url_select = "https://vindecoder.p.mashape.com/decode_vin?vin=" + VIN;
        String url_select = "https://api.edmunds.com/api/vehicle/v2/vins/" + VIN + "?fmt=json&api_key=" + API_KEY;

        ArrayList<NameValuePair> param = new ArrayList<>();
//        param.add(new BasicNameValuePair("X-Mashape-Key", "oK20yXYE8XmshQL1pGYx3EoJQfcLp1iyR4vjsnjCb84c24rtO5"));
//        param.add(new BasicNameValuePair("Accept", "application/json"));

        try {
            // Set up HTTP post

            // HttpClient is more then less deprecated. Need to change to URLConnection
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url_select);
            httpPost.setEntity(new UrlEncodedFormEntity(param));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            // Read content & Log
            inputStream = httpEntity.getContent();
        } catch (UnsupportedEncodingException e1) {
            Log.e("UnsupportedEncoding", e1.toString());
            e1.printStackTrace();
        } catch (ClientProtocolException e2) {
            Log.e("ClientProtocolException", e2.toString());
            e2.printStackTrace();
        } catch (IllegalStateException e3) {
            Log.e("IllegalStateException", e3.toString());
            e3.printStackTrace();
        } catch (IOException e4) {
            Log.e("IOException", e4.toString());
            e4.printStackTrace();
        }
        // Convert response to string using String Builder
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder sBuilder = new StringBuilder();

            String line = null;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line + "\n");
            }

            inputStream.close();
            String output = sBuilder.toString();
            result = new JSONObject(output);
            Log.i(TAG, result.toString());
        } catch (Exception e) {
            Log.e("StringBuilding", "Error converting result " + e.toString());
        }

        return result;
    }

    private static String getYear(JSONObject jsonObject) throws JSONException {
        String output = jsonObject.getJSONArray("years").getJSONObject(0).getString("year");
        Log.i(TAG, "Year: " + output);
        return output;
    }

    private static String getMake(JSONObject jsonObject) throws JSONException {
        String output = jsonObject.getJSONObject("make").getString("niceName");
        Log.i(TAG, "Make: " + output);
        return output;
    }

    private static String getModel(JSONObject jsonObject) throws JSONException {
        String output = jsonObject.getJSONObject("model").getString("niceName");
        Log.i(TAG, "Model: " + output);
        return output;
    }

    private static String getSubModel(JSONObject jsonObject) throws JSONException {
        String output = jsonObject.getJSONArray("years").getJSONObject(0).getJSONArray("styles").getJSONObject(0).getJSONObject("submodel").getString("niceName");
        Log.i(TAG, "SubModel: " + output);
        return output;
    }

    private static String getTrimLevel(JSONObject jsonObject) throws JSONException {
        String output = jsonObject.getJSONArray("years").getJSONObject(0).getJSONArray("styles").getJSONObject(0).getString("trim");
        Log.i(TAG, "trim: " + output);
        return output;
    }

//    private static  String getEngine(JSONObject jsonObject) throws JSONException {
//        String output = jsonObject.getJSONObject("years").getString("year");
//        Log.i(TAG, "Year: " + output);
//        return output;
//    }

//    private  static String getTransmission(JSONObject jsonObject) throws JSONException {
//        String output = jsonObject.getJSONObject("years").getString("year");
//        Log.i(TAG, "Year: " + output);
//        return output;
//    }

    private static String getColor(JSONObject jsonObject) throws JSONException {
        Log.i(TAG, "Category: " + jsonObject.getJSONArray("colors").getJSONObject(1).getString("category"));
        String output = jsonObject.getJSONArray("colors").getJSONObject(1).getJSONArray("options").getJSONObject(0).getString("name");
        Log.i(TAG, "colors: " + output);
        return output;
    }

}
