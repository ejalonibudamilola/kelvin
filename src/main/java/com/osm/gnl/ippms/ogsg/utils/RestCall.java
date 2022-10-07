package com.osm.gnl.ippms.ogsg.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service("restCall")
public class RestCall {

    @Value("${API_IP}")
    private String API_IP;

    public String executeRequest(String endPoint, JSONObject js, String ip) throws JSONException, IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(API_IP  + endPoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String dataToSend = js.toString();
            //System.out.println("dataToSend: "+dataToSend + "url: "+endPoint);
           // JSONObject requestObj = new JSONObject();
            try (OutputStream wr = connection.getOutputStream()){
                byte[] in = dataToSend.getBytes(StandardCharsets.UTF_8);
                wr.write(in, 0, in.length);
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder myResponse = new StringBuilder();
            String my_response;
            while ((my_response = rd.readLine()) != null) {
                myResponse.append(my_response);
            }
           // System.out.println("dataReceived: "+myResponse.toString());
            return myResponse.toString();
        } catch (IOException e) {
            System.out.println(e.toString());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String executeGet(String endPoint) throws JSONException {
        HttpURLConnection connection = null;
        try {
            JSONParser parser = new JSONParser();
            URL url = new URL(API_IP + endPoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder myResponse = new StringBuilder();
            String my_response;
            while ((my_response = rd.readLine()) != null) {
                JSONArray a = (JSONArray) parser.parse(my_response);
                for (Object o : a) {
                    myResponse.append(o);
                }
            }
            //System.out.println("dataReceived: "+myResponse.toString());
            return myResponse.toString();
        } catch (IOException | ParseException e) {
            System.out.println(e.toString());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
