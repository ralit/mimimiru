package com.example.hello.saito;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class CallBackActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callback);
        AccessToken token = null;
        
        //Twitterの認証画面から発行されるIntentからUriを取得
        Uri uri = getIntent().getData();
 
//        if(uri != null && uri.toString().startsWith("Callback://CallBackActivity")){
        if(uri != null && uri.toString().startsWith("mimimiru://twoauth")){       
            //oauth_verifierを取得する
            String verifier = uri.getQueryParameter("oauth_verifier");
            try {
                //AccessTokenオブジェクトを取得
                token = MainActivity._oauth.getOAuthAccessToken(MainActivity._req, verifier);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
 
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putString("TwitterAccessToken", token.getToken()).commit();
        sp.edit().putString("TwitterAccessTokenSecret", token.getTokenSecret()).commit();
        
        CharSequence cs = "token：" + token.getToken() + "\r\n" + "token secret：" + token.getTokenSecret();
        Log.d("CallBacked", cs.toString());
        finish();
    }
    
    
}
