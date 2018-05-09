package com.baidu.duer.dcs.oauth.api;

import android.os.Bundle;

import com.baidu.dcs.okhttp3.OkHttpClient;
import com.baidu.dcs.okhttp3.Request;
import com.baidu.dcs.okhttp3.Response;
import com.baidu.duer.dcs.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by uidq0955 on 2018/5/9.
 */

public class ClientCredentialsUtil {

    private static final String LOG_TAG = "ClientCredentialsUtil";

    public static void authorize(String clientId, String clientSecret, AccessTokenManager accessTokenManager, AuthorizeListener listener) throws IOException {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request mRequest = new Request.Builder()
                .url("https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret)
                .build();
        Response mResponse = mOkHttpClient.newCall(mRequest).execute();
        if(mResponse.isSuccessful() && mResponse.code() == 200){
            String message = mResponse.body().string();
            LogUtil.d(LOG_TAG,"response.code()=="+mResponse.code());
            LogUtil.d(LOG_TAG,"response.message()=="+mResponse.message());
            LogUtil.d(LOG_TAG,"res=="+message);
            if(listener != null){
                Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                AuthorizeResult mAuthorizeResult = mGson.fromJson(message, AuthorizeResult.class);
                LogUtil.d(LOG_TAG,"mAuthorizeResult=="+mAuthorizeResult);

                Bundle mBundle = new Bundle();
                mBundle.putString("access_token",mAuthorizeResult.access_token);
                mBundle.putString("session_key",mAuthorizeResult.session_key);
                mBundle.putString("scope",mAuthorizeResult.scope);
                mBundle.putString("refresh_token",mAuthorizeResult.refresh_token);
                mBundle.putString("session_secret",mAuthorizeResult.session_secret);
                mBundle.putString("expires_in",""+mAuthorizeResult.expires_in);
                accessTokenManager.storeToken(mBundle);

                listener.onComplete(mBundle);
            }
        }else{
            if(listener != null){
                listener.onError();
            }
        }
    }

    class AuthorizeResult{
        public String access_token = "";
        public String session_key = "";
        public String scope = "";
        public String refresh_token = "";
        public String session_secret = "";
        public long expires_in;

        @Override
        public String toString() {
            return "AuthorizeResult{" +
                    "access_token='" + access_token + '\'' +
                    ", session_key='" + session_key + '\'' +
                    ", scope='" + scope + '\'' +
                    ", refresh_token='" + refresh_token + '\'' +
                    ", session_secret='" + session_secret + '\'' +
                    ", expires_in=" + expires_in +
                    '}';
        }
    }

    public interface AuthorizeListener {
        void onComplete(Bundle values);
        void onError();
    }
}
