/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2013 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.docdoku.android.plm.network;

import android.os.AsyncTask;
import android.util.Log;
import com.docdoku.android.plm.network.listeners.HttpPutListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;

/**
 *
 * @author: Martin Devillers
 */
public class HttpPutTask extends HttpTask<String, Void, Boolean> {


    private static String baseUrl;
    private static byte[] id;
    private HttpPutListener httpPutListener;

    public HttpPutTask(HttpPutListener httpPutListener) {
        super();
        this.httpPutListener = httpPutListener;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        Boolean result = false;

        try {
            URL url = createURL(strings[0]);
            Log.i("com.docdoku.android.plm.client", "Sending HttpPut request to url: " + url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Basic " + new String(id, "US-ASCII"));
            conn.setRequestProperty("Content-Type","application/json");
            byte[] messageBytes = null;
            try{
                String message = strings[1];
                try {
                    JSONObject object = new JSONObject(message);
                } catch (JSONException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                messageBytes = message.getBytes();
                Log.i("com.docdoku.android.plm", "Message found attached to put request: " + message);
                //conn.setRequestProperty("Content-Length", Integer.toString(message.getBytes().length));
                conn.setFixedLengthStreamingMode(messageBytes.length);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setAllowUserInteraction(true);
                conn.setRequestProperty("Connection", "keep-alive");
            }catch (ArrayIndexOutOfBoundsException e){
                Log.i("com.docdoku.android.plm", "No message attached to HttpPut request");
            }
            conn.setRequestMethod("PUT");
            conn.connect();

            writeBytesToConnection(conn, messageBytes);

            int responseCode = conn.getResponseCode();
            Log.i("com.docdoku.android.plm","Response message: " + conn.getResponseMessage());

            conn.disconnect();

            Log.i("com.docdoku.android.plm.client","Response code: " + responseCode);
            if (responseCode == 200){
                result = true;
            }

        } catch (MalformedURLException e) {
            Log.e("com.docdoku.android.plm.client","ERROR: MalformedURLException");
            e.printStackTrace();
        } catch (ProtocolException e) {
            Log.e("com.docdoku.android.plm.client","ERROR: ProtocolException");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.e("com.docdoku.android.plm.client", "ERROR: UnsupportedEncodingException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("com.docdoku.android.plm.client", "ERROR: IOException");
            e.printStackTrace();
            Log.e("com.docdoku.android.plm.client", "Exception message: " + e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean result){
        super.onPostExecute(result);
        if (httpPutListener != null){
            httpPutListener.onHttpPutResult(result);
        }
    }
}
