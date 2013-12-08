package com.example.hello.saito;

import java.io.File;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationContext;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
//import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
//import android.widget.Toast;
import android.widget.Toast;

import com.fuetrek.fsr.FSRServiceEventListener;
import com.fuetrek.fsr.FSRServiceOpen;
import com.fuetrek.fsr.FSRServiceEnum.BackendType;
import com.fuetrek.fsr.FSRServiceEnum.EventType;
import com.fuetrek.fsr.FSRServiceEnum.Ret;
import com.fuetrek.fsr.entity.AbortInfoEntity;
import com.fuetrek.fsr.entity.ConstructorEntity;
import com.fuetrek.fsr.entity.RecognizeEntity;
import com.fuetrek.fsr.entity.ResultInfoEntity;
import com.fuetrek.fsr.entity.StartRecognitionEntity;

public class MainActivity extends Activity {

	// 定数
	// ファイルの保存場所
	private static final String rootDirectory = Environment.getExternalStorageDirectory().getPath() + "/mimimiru/";
	private static final String ImageFilePath = rootDirectory + "mimimirupic.jpg";
	
	// 変数
	private ViewGroup.LayoutParams mLayoutParams;
	private CameraView mCamView;
	private OverlayView mOverlayView;
	private Boolean mLongPressed = false; // 長押し判定フラグ

// >>> 音声認識
    private Handler handler_;
    private Button buttonStart_;
    private ProgressBar progressLevel_;
    private TextView textResult_;
    private fsrController controller_ = new fsrController();


    // BackendTypeはBackendType.D固定
    private static final BackendType backendType_ = BackendType.D;


    // Context
    private Activity activity_ = null;

	private Twitter twitter;
//	private TwitterStream twitterStream;
	public static OAuthAuthorization _oauth;
	public static RequestToken _req;
//	private boolean isSpeechInitialized = false;
//	private TextToSpeech textToSpeech;
	private SharedPreferences sp;
	
    
    public class TEST {
    	
    }
    
	private void initializeTwitter() {
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		if (sp.getString("TwitterAccessToken", null) == null) {
			executeOauth();
		} else {
			Log.d("TwitterAccessToken", sp.getString("TwitterAccessToken", null));
			Log.d("TwitterAccessTokenSecret", sp.getString("TwitterAccessTokenSecret", null));

			// ここでMediaProviderをTWITTERにする

			twitter = TwitterFactory.getSingleton();
			Log.d("ore", "hoge4");
			twitter.setOAuthConsumer("zTt8enzdc9yC5A5emjjH3w", "MYbjim5apUN3YK9hIMqs4erl0KkyobL7JnQYn2NAko");
			twitter.setOAuthAccessToken(new AccessToken(sp.getString("TwitterAccessToken", null), sp.getString("TwitterAccessTokenSecret", null)));
			Log.d("ore", "hoge5");
		}
	}
	
	// http://ameblo.jp/yolluca/entry-10817608482.html
	private void executeOauth(){
		//Twitetr4jの設定を読み込む
		Configuration conf = ConfigurationContext.getInstance();

		//Oauth認証オブジェクト作成
		// OAuthAuthorization oauth = new OAuthAuthorization(conf);
		_oauth = new OAuthAuthorization(conf);
		_oauth.setOAuthAccessToken(null);
		//Oauth認証オブジェクトにconsumerKeyとconsumerSecretを設定
		_oauth.setOAuthConsumer("zTt8enzdc9yC5A5emjjH3w", "MYbjim5apUN3YK9hIMqs4erl0KkyobL7JnQYn2NAko");
		//アプリの認証オブジェクト作成
		//RequestToken _req = null;
		_req = null;
		try {
			Log.d("getOauth", "pre");
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			Log.d("getOauth", "pre2");
			_req = _oauth.getOAuthRequestToken("mimimiru://twoauth");
			Log.d("getOauth", "post");
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		String _uri;
		_uri = _req.getAuthorizationURL();
		startActivityForResult(new Intent(Intent.ACTION_VIEW , Uri.parse(_uri)), 0);
	}
	
    
    // FSRServiceの待ち処理でブロッキングする実装としている為、
    // UI更新を妨げないよう別スレッドとしている。
    public class fsrController extends Thread implements FSRServiceEventListener {
        FSRServiceOpen fsr_;
        SyncObj event_CompleteConnect_ = new SyncObj();
        SyncObj event_CompleteDisconnect_ = new SyncObj();
        SyncObj event_EndRecognition_ = new SyncObj();
        Ret ret_;
        String result_;

        // 認識完了時の処理
        // (UIスレッドで動作させる為にRunnable()を使用している)
        final Runnable notifyFinished = new Runnable() {
            public void run() {
                try {
                    // 念のためスレッドの完了を待つ
                    controller_.join();
                } catch (InterruptedException e) {
                }
                textResult_.append("***Result***" + System.getProperty("line.separator"));
                textResult_.append(controller_.result_);
                // >>> saito
                Log.d("debug","controller_.result_=" + controller_.result_);
 //               mOverlayView.setMsg(controller_.result_);
                
                String speakmessage = controller_.result_;
                if ( speakmessage.indexOf("ありがとう") == -1 ) {
                	// ありがとう　なし
                	Log.d("debug", "ありがとう　なし");
                	mOverlayView.setDebugmsg("ありがとう　なし");
            		Tweet tweet = new Tweet(twitter, speakmessage, sp, getApplicationContext());
                } else {
                	// ありがとう　あり
                	Log.d("debug", "ありがとう　あり");
                	mOverlayView.setDebugmsg("ありがとう　あり");
                	Tweet tweet = new Tweet(twitter, speakmessage, sp, getApplicationContext());
                }
                
                // <<< 
                buttonStart_.setEnabled(true);

            }
        };


        // 認識処理
        @Override
        public void run() {
            result_ = "";
            try {
                result_=execute();
            } catch (Exception e) {
                result_ = "(error)";
                e.printStackTrace();
            }
            handler_.post(notifyFinished);
        }

        /**
         * 認識処理
         *
         * 現状は毎回インスタンス生成～destroy()を実施しているが、
         * 繰り返し認識させる場合は、以下のように制御した方がオーバーヘッドが少なくなる
         * アプリ起動時：インスタンス生成～connectSession()
         * 認識要求時　：startRecognition()～getSessionResult()
         * アプリ終了時：destroy()
         *
         * @throws Exception
         */
        public String execute() throws Exception {

            try{
                final ConstructorEntity construct = new ConstructorEntity();
                construct.setContext(activity_);

                // 別途発行されるAPIキーを設定してください(以下の値はダミーです)
                construct.setApiKey(DocomoAPI.getApi());

                construct.setSpeechTime(10000);
                construct.setRecordSize(240);
                construct.setRecognizeTime(10000);

                // インスタンス生成
                // (thisは FSRServiceEventListenerをimplementsしている。)
                if( null == fsr_ ){
                    fsr_ = new FSRServiceOpen(this, this, construct);
                }

                // connect
                fsr_.connectSession(backendType_);
                event_CompleteConnect_.wait_();
                if( ret_ != Ret.RetOk ){
                    Exception e = new Exception("filed connectSession.");
                    throw e;
                }

                // 認識開始

                final StartRecognitionEntity startRecognitionEntity = new StartRecognitionEntity();
                startRecognitionEntity.setAutoStart(true);
                startRecognitionEntity.setAutoStop(true);				// falseにする場合はUIからstopRecognition()実行する仕組みが必要
                startRecognitionEntity.setVadOffTime((short) 500);
                startRecognitionEntity.setListenTime(0);
                startRecognitionEntity.setLevelSensibility(10);

                // 認識開始
                fsr_.startRecognition(backendType_, startRecognitionEntity);

                // 認識完了待ち
                // (setAutoStop(true)なので発話終了を検知して自動停止する)
                event_EndRecognition_.wait_();

                // 認識結果の取得
                RecognizeEntity recog = fsr_.getSessionResultStatus(backendType_);
                String result="(no result)";
                if( recog.getCount()>0 ){
                    ResultInfoEntity info=fsr_.getSessionResult(backendType_, 1);
                    result = info.getText();
                }

                // 切断
                fsr_.disconnectSession(backendType_);
                event_CompleteDisconnect_.wait_();

                return result;
            } catch (Exception e) {
//                showErrorDialog(e);
                throw e;
            }finally{
                if( fsr_!=null ){
                    fsr_.destroy();
                    fsr_=null;
                }
            }
        }

        @Override
        public void notifyAbort(Object arg0, AbortInfoEntity arg1) {
            Exception e = new Exception("Abort!!");
            showErrorDialog(e);
        }

        @Override
        public void notifyEvent(final Object appHandle, final EventType eventType, final BackendType backendType, Object eventData) {

            switch(eventType){

            case CompleteConnect:
                // 接続完了
                ret_ = (Ret)eventData;
                event_CompleteConnect_.notify_();
                break;

            case CompleteDisconnect:
                // 切断完了
                event_CompleteDisconnect_.notify_();
                break;

            case NotifyEndRecognition:
                // 認識完了
                event_EndRecognition_.notify_();
                Log.d("debug", "認識終了");
                break;

            case NotifyLevel:
                // レベルメータ更新
                int level = (Integer)eventData;
                progressLevel_.setProgress(level);
                break;
            }
        }

    }
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//
//        handler_ = new Handler();
//        buttonStart_ = (Button) findViewById(R.id.button_start);
//        progressLevel_ = (ProgressBar) findViewById(R.id.progress_level);
//        textResult_ = (TextView) findViewById(R.id.text_result);
//        activity_ = this;
//
//        // コントロール初期化
//        progressLevel_.setMax(100);
//        textResult_.setTextSize(28.0f);
//    }

    /**
     * 開始ボタン押下
     *
     * @param view ビュー
     */
    public void onClickStart(final View view) {
        textResult_.setText("");
        buttonStart_.setEnabled(false);
        controller_ = new fsrController();
        controller_.start();
    }


    /**
     * エラーダイアログを表示する
     */
    public final void showErrorDialog(Exception e) {
        final Activity activity = this;
        final String text=(e.getCause()!=null)?e.getCause().toString():e.toString();
        final AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle("Error");
        ad.setMessage(text);
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
            }
        });
        ad.create();
        ad.show();
    }

    /**
     * トーストを表示する。
     */
    public final void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }    
// <<<  音声認識
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);

//        // 画面の向きを横で固定
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 全画面表示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // スリープ無効
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // タイトルの非表示
        requestWindowFeature(Window.FEATURE_NO_TITLE);

// >>> 音声認識
        // >>> dummy
        TEST test_dummy = new TEST();
        // <<< dummy
        
        setContentView(R.layout.activity_main);

        handler_ = new Handler();
        buttonStart_ = (Button) findViewById(R.id.button_start);
        progressLevel_ = (ProgressBar) findViewById(R.id.progress_level);
        textResult_ = (TextView) findViewById(R.id.text_result);
        activity_ = this;

        // コントロール初期化
        progressLevel_.setMax(100);
        textResult_.setTextSize(28.0f);        
// <<< 音声認識
        
        // レイアウト用
        mLayoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
       // viewの初期化と追加
       DrawView dView = new DrawView(this);
       mOverlayView = new OverlayView(this,this);
       mCamView = new CameraView(this, dView, mOverlayView, this);
       setContentView(dView, mLayoutParams);
       addContentView(mCamView, mLayoutParams);
       addContentView(mOverlayView, mLayoutParams);
       
       // ファイル
       
       // Twitter
       initializeTwitter();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){
			this.mLongPressed = true;
			return super.onKeyUp(keyCode, event);
		}
		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("debug", "onKeyDown keyCode=" + keyCode);

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d("debug", "onKeyUp keyCode=" + keyCode);

//		mOverlayView.setDebugmsg("onKeyUp keyCode=" + keyCode);

		if(this.mLongPressed){
			// 長押しされた時の処理
			// 戻るキーが押された時の処理
			mLongPressed = false;
			Log.d("debug", "onKeyUp long");
			finish(); // 戻るキーが長押しされたらアプリを終了
			return true;
		}else if(!this.mLongPressed){
			// 単押しされた時の処理
			if(keyCode == KeyEvent.KEYCODE_BACK){
				Log.d("debug", "onKeyUp KEYCODE_BACK");
				//mOverlayView.setDebugmsg("onKeyUp KEYCODE_BACK");
				
	    		// ファイル保存ディレクトリ存在確認
	    		File file = new File(rootDirectory);
				if (!file.exists()) {
					file.mkdir();
					Log.d("debug","mkdir");
				}    		
				Log.d("debug", rootDirectory);
	    	
				// 写真撮影
				mCamView.takePicture();
				
				// 音声認識 
		        textResult_.setText("");
		        buttonStart_.setEnabled(false);
		        controller_ = new fsrController();
		        controller_.start();
		        
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);		
		
//		if(keyCode == KeyEvent.KEYCODE_BACK){
//			Log.d("debug", "onKeyUp KEYCODE_BACK");
//			mOverlayView.setDebugmsg("onKeyUp KEYCODE_BACK");
//		}
		
		
//		Toast.makeText(MainActivity.this, 
//				"onKeyUp", Toast.LENGTH_SHORT).show();
		
//		if(this.mLongPressed){
//			// 長押しされた時の処理
//			// 戻るキーが押された時の処理
//			mLongPressed = false;
//			Log.d("fuga2", "onKeyUp long");
//			// finish(); // 戻るキーが長押しされたらアプリを終了
//			// return true;
//		}else if(!this.mLongPressed){
//			// 単押しされた時の処理
//			if(keyCode == KeyEvent.KEYCODE_BACK){
//				Log.d("fuga2", "onKeyUp short");
//				this.cameraMode();
//				return true;
//			}
//		}
//		return super.onKeyUp(keyCode, event);
	}
	
	//********************************************************************************************
	/**
	 * @brief		画面タッチ時のイベント処理
	 * ※タブレットでデバッグ用に、M100のキーイベントの代わりに実行
	 *
	 * @param[in]	MotionEvent event	イベント
	 *
	 * @return		public boolean	TRUE:イベント継続なし / FALSE:イベント継続あり
	 */
	//********************************************************************************************
	@Override
	public boolean onTouchEvent(MotionEvent event) {

    	if (event.getAction() == MotionEvent.ACTION_DOWN) {
    		Log.d("debug", "onTouchEvent");

    		// ファイル保存ディレクトリ存在確認
    		File file = new File(rootDirectory);
			if (!file.exists()) {
				file.mkdir();
				Log.d("debug","mkdir");
			}    		
			Log.d("debug", rootDirectory);
    	
			// 写真撮影
			mCamView.takePicture();

			// 音声認識 
	        textResult_.setText("");
	        buttonStart_.setEnabled(false);
	        controller_ = new fsrController();
	        controller_.start();
	        
    	}
    	
		return super.onTouchEvent(event);
 	}
	
	public String getImageFilePath() {
		return ImageFilePath;
	}
}
////////////
class SyncObj{
    boolean isDone=false;

    synchronized void wait_(){
        try {
            // wait_()より前にnotify_()が呼ばれた場合の対策としてisDoneフラグをチェックしている
            while(isDone==false){
                wait(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized void notify_(){
        isDone=true;
        notify();
    }
}

