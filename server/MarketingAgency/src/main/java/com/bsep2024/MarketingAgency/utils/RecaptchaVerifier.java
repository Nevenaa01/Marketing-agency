package com.bsep2024.MarketingAgency.utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
public class RecaptchaVerifier {
    private static final String RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final String SECRET_KEY = "6Lf3nPYpAAAAAIXpeQh5ZV2NQLX5mThA_lwwap3U";

    public boolean verifyToken(String token) throws IOException {
        if(token==null)return false;
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(RECAPTCHA_URL);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("secret", SECRET_KEY));
        params.add(new BasicNameValuePair("response", token));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            String responseBody = EntityUtils.toString(entity);
            // Parse the JSON response
            // Example response: {"success": true, "score": 0.9, "action": "submit"}
            // You may need to adjust your logic based on your requirements
            return responseBody.contains("\"success\": true");
        } else {
            // Handle null entity
            return false;
        }
    }
}
