//********************************************************************************************
/**
 * @file		OverlayView.java
 * @brief		情報表示用オーバレイビュー
 */
//********************************************************************************************
package com.example.hello.saito;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;

public class OverlayView extends View {
	

    private Paint mPaint;
    
	private String mFPS;
	private String mDebugMsg;
	private int mRectNum = 0;
	private Rect[] mRectInfo = null;
	private String mMsg;
	private String mRecognizedText = "";

    // アクティビティ
    private Activity mActivity;

    // ディスプレイ
    private DisplayInfo mDisplayInfo;

	// 画像とdisplayの比率
	private float mRatioImage2display = 0;
    
    // ディスプレイサイズ
    private int mDisplayW;
    private int mDisplayH;
    
//    // 描画用矩形
//    private Rect mDrawRect = new Rect();	// ※onDrawのみで使用するが、onDraw内で宣言するとワーニングが出るため、ここで宣言する。

//    // ジャンプ用アイコン
//    private Bitmap mImgIconSearch;
//
//	// ジャンプ用アイコンの座標
//    private Rect mIconPos = new Rect(0,0,0,0);

    
	public OverlayView(Context context, Activity activity) {
	    super(context);

    	mActivity = activity;
	    setFocusable(true);

	    // 変数初期化
//	    setFPS("");
//	    setMsg("");
	    setDebugmsg("");
	    
	    // 描画用paint生成
        mPaint = new Paint();

        // ディスプレイ情報取得
        mDisplayInfo = DisplayInfo.getDisplayInfo(mActivity);
        mDisplayW = mDisplayInfo.getDisplayW();
        mDisplayH = mDisplayInfo.getDisplayH();
        
	}

//    public boolean onTouchEvent(MotionEvent event) {
//    x = event.getX();
//    y = event.getY();
//    invalidate();
//    return true;
//    }
// >>>

	// メンバ変数のsetter
//	public void setRectNum(int RectNum, Rect[] RectInfo, float RatioImage2display) {
//		this.mRectNum = RectNum;
//		this.mRectInfo = RectInfo;
//		this.mRatioImage2display = RatioImage2display;
//	}
//
//	public void setFPS(String FPS) {
//		this.mFPS = FPS;
//	}
//
//	public void setMsg(String Msg) {
//		this.mMsg = Msg;
//		drawResult();
//	}

	public void setDebugmsg(String Debugmsg) {
		this.mDebugMsg = Debugmsg;
		drawResult();
	}

//	public void setRecognizedText(String RecognizedText) {
//		this.mRecognizedText = RecognizedText;
//	}

//	// タッチした座標がアイコンかを判定
//	public boolean checkIconTouch(Point touchpoint) {
//
//		boolean ret = false;
//
//		Log.d("debug2", "checkIconTouch touchpoint.x=" + touchpoint.x + ",touchpoint.y=" + touchpoint.y);
//		Log.d("debug2", "checkIconTouch mIconPos.left=" + mIconPos.left + ",mIconPos.right=" + mIconPos.right + ",mIconPos.top=" + mIconPos.top + ",mIconPos.bottom=" + mIconPos.bottom);
//		
//		
//		if (mIconPos.left <= touchpoint.x && mIconPos.right >= touchpoint.x &&
//				mIconPos.top <= touchpoint.y &&mIconPos.bottom >= touchpoint.y ) {
//			ret = true;
//		}
//		return ret;
//	}

	
	// 描画メソッド
	public void drawResult( ) {
		invalidate();
    }
    
	// drawイベント
    protected void onDraw(Canvas canvas) {

//     	int i;

		// 描画用にpaintを設定
        mPaint.setColor(Color.MAGENTA);
		mPaint.setStyle(Paint.Style.STROKE);		// 図形を枠のみ描画(塗りつぶさない)
		mPaint.setStrokeWidth(5);				//　線の太さを設定
			
//		// 矩形描画
//		for (i=0; i<mRectNum ; i++) {
//			mDrawRect.set((int)(mRectInfo[i].left * mRatioImage2display),
//						  (int)(mRectInfo[i].top * mRatioImage2display),
//						  (int)(mRectInfo[i].right * mRatioImage2display),
//						  (int)(mRectInfo[i].bottom * mRatioImage2display));
//
//			canvas.drawRect(mDrawRect, mPaint);
//		}
//		
//		// FPS
//		if (mFPS != "") {
//	        mPaint.setColor(Color.BLUE);
//	        mPaint.setTextSize(30);
//			mPaint.setStyle(Paint.Style.FILL);		// 図形(文字)を塗りつぶして描画
//			mPaint.setStrokeWidth(1);				//　線の太さを設定
//			canvas.drawText(mFPS, mDisplayW - 260, mDisplayH - 150, mPaint);
//		}
//		
		// msg
//		if (mMsg != "") {
//	        mPaint.setColor(Color.BLUE);
//	        mPaint.setTextSize(12);
//			mPaint.setStyle(Paint.Style.FILL);		// 図形(文字)を塗りつぶして描画
//			mPaint.setStrokeWidth(1);				//　線の太さを設定
//			canvas.drawText( mMsg, 10, 10 , mPaint);
//		}
		
		// DEBUG
//		if (mDebugMsg != "") {
	        mPaint.setColor(Color.MAGENTA);
	        mPaint.setTextSize(12);
			mPaint.setStyle(Paint.Style.FILL);		// 図形(文字)を塗りつぶして描画
			mPaint.setStrokeWidth(1);				//　線の太さを設定
//			canvas.drawText("Debug:" + mDebugMsg, mDisplayW - 260, mDisplayH - 50 , mPaint);
			canvas.drawText("Debug:" + mDebugMsg, 10, 10 , mPaint);
//		}
//		
//		// 認識した文字列
//		if (mRecognizedText != "") {
//	        mPaint.setColor(Color.MAGENTA);
//	        mPaint.setTextSize(12);
//			mPaint.setStyle(Paint.Style.FILL);		// 図形(文字)を塗りつぶして描画
//			mPaint.setStrokeWidth(1);				//　線の太さを設定
//			canvas.drawText("認識文字列", mDisplayW - 260, mIconPos.bottom , mPaint);
//
//			mPaint.setColor(Color.WHITE);
//	        mPaint.setTextSize(18);
//			mPaint.setStyle(Paint.Style.FILL);		// 図形(文字)を塗りつぶして描画
//			mPaint.setStrokeWidth(1);				//　線の太さを設定
//			canvas.drawText(mRecognizedText, mDisplayW - 260, mIconPos.bottom+20 , mPaint);
//			
//			// 検索アイコン表示
//			canvas.drawBitmap(mImgIconSearch, mIconPos.left, mIconPos.top, mPaint);
//		}
		
    }

}