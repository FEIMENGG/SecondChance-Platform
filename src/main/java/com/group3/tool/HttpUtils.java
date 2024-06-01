package com.group3.tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author group3
 * @date 2024/04
 */

public class HttpUtils {
    /*
     * Function  :   Send a Post request to the server
     */
    static String submitPostData(String strUrlPath, Map<String, String> params, String encode) {

        byte[] data = getRequestData(params, encode).toString().getBytes();//get request data
        try {

            URL url = new URL(strUrlPath);

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(3000);     // Set the connection timeout period
            httpURLConnection.setDoInput(true);            //Open the input stream to get data from the server
            httpURLConnection.setDoOutput(true);           //Open the output stream to submit data to the server
            httpURLConnection.setRequestMethod("POST");    //Set data to be submitted in POST mode
            httpURLConnection.setUseCaches(false);         //The cache cannot be used in POST mode
            // Set the type of the request to text
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // Set the length of the request
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            // Get the output stream and write data to the server
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode();            //Get the server's response code
            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream);                     // Process the server's response
            }
        } catch (IOException e) {
            //e.printStackTrace();
            return "err: " + e.getMessage().toString();
        }
        return "-1";
    }

    /*
     * Function  :   Encapsulate the request information
     */
    private static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        // Store the encapsulated request body information
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);   // Delete the last "&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    /*
     * Function  :   Processing the server's response (turning the input into a string)
     */
    private static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      // Store the processing result
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }
}
