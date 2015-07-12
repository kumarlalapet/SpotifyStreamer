package com.lalapetstudios.udacityprojects.spotifystreamer.util;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.lalapetstudios.udacityprojects.spotifystreamer.models.SpotifyAccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by g2ishan on 7/12/15.
 */
public class GenerateSpotifyAccessToken {

    private static final String TAG = GenerateSpotifyAccessToken.class.getName();

    private static SpotifyAccessToken token;

    public static String generateAccessToken() {
        String result;
        if(token != null && !token.isTokenExpired()) {
            Log.i(TAG, "Token present and NOT expired");
            result = token.getAccessToken();
        } else {
            Log.i(TAG, "Token NOT present OR expired");
            String tokenJSONStr = callSpotifyApiToGetToken();
            token = getTokenObjFromJSON(tokenJSONStr);
            result = token == null ? "" : token.getAccessToken();
        }
        Log.i(TAG, result);
        return result;
    }

    private static SpotifyAccessToken getTokenObjFromJSON(String tokenJSONStr) {
        try {
            JSONObject accessTokenJson = new JSONObject(tokenJSONStr);
            String accessToken = accessTokenJson.getString("access_token");
            int expiresIn = accessTokenJson.getInt("expires_in");
            return new SpotifyAccessToken(accessToken,expiresIn);
        } catch (JSONException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Log.e(TAG, e.getMessage());
            Log.e(TAG, sw.toString());
            return null;
        }
    }

    private static String callSpotifyApiToGetToken() {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("accounts.spotify.com")
                    .appendPath("api")
                    .appendPath("token");
            String myUrl = builder.build().toString();

            String response;

            try {

                URL url = new URL(myUrl);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("grant_type","client_credentials");

                // The client secret should not be handled here. It shoud be in the backend service
                // this is handled in the client just for the sample application
                String clientid = "9da7c02a59994e42b010992dfbdbf4e7";
                String clientsecret = "c610f03e20934213b880854a346592cc";
                String passthis = clientid+":"+clientsecret;

                String basicAuth = "Basic " + Base64.encodeToString(passthis.getBytes(),Base64.NO_WRAP);
                urlConnection.setRequestProperty("Authorization", basicAuth);

                StringBuilder result = new StringBuilder();
                result.append(URLEncoder.encode("grant_type", "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode("client_credentials", "UTF-8"));

                String requestBody = result.toString();

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(requestBody);
                writer.flush();
                writer.close();
                os.close();

                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    response = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    response = null;
                }
                response = buffer.toString();

            } catch (IOException e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);

                Log.e(TAG, e.getMessage());
                Log.e(TAG, sw.toString());

                response = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);

                        Log.e(TAG, e.getMessage());
                        Log.e(TAG, sw.toString());
                    }
                }
            }

            return response;

    }

}
