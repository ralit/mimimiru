//********************************************************************************************
/**
 * @file		DisplayInfo.java
 * 
 * @brief		ディスプレイの情報関連
 * 
　*/
//********************************************************************************************

package com.example.hello.saito;

import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;

public class DisplayInfo {
	
	// 自分自身のオブジェクト実体
	private static DisplayInfo DisplayInfoObject_ = null;

    // ディスプレイサイズ
    private int mDisplayW = 0;
    private int mDisplayH = 0;

	// 画像とdisplayの比率
	private float mRatioImage2Display = 0;

	// displayと画像の比率
	private float mRatioDisplay2Image = 0;
	
	public static DisplayInfo getDisplayInfo (Activity activity) {
		if (DisplayInfoObject_ == null) {
			DisplayInfoObject_ = new DisplayInfo(activity);
		}
		return DisplayInfoObject_;
	}
	
    // コンストラクタ
    public DisplayInfo(Activity activity)   {
   	
		// ディスプレイのサイズを取得
    	Display display = activity.getWindowManager().getDefaultDisplay();
    	Point size = new Point();
    	display.getSize(size);
        mDisplayW = size.x;
        mDisplayH = size.y;        
        
		Log.d("debug", "mDisplayW=" + mDisplayW + ",mDisplayH=" + mDisplayH);
        
    }

	// メンバ変数のgetter/setter
	public void setImageSize(int imageW, int imageH) {
		
		float scaleWf;
		float scaleHf;

		// 画像とディスプレイのサイズ比率を算出
		scaleWf = (float)mDisplayW / (float)imageW;
		scaleHf = (float)mDisplayH / (float)imageH;
		mRatioImage2Display = Math.min(scaleWf, scaleHf);

		// ディスプレイと画像のサイズ比率を算出
		scaleWf = (float)imageW / (float)mDisplayW;
		scaleHf = (float)imageH / (float)mDisplayH;
		mRatioDisplay2Image = Math.max(scaleWf, scaleHf);
//        Log.d("debug2", "mRatioDisplay2Image=" + mRatioDisplay2Image );
	
	}
	public int getDisplayW() {
		return this.mDisplayW;
	}
	public int getDisplayH() {
		return this.mDisplayH;
	}
	public float getRatioImage2Display() {
		return this.mRatioImage2Display;
	}
	public float getRatioDisplay2Image() {
		return this.mRatioDisplay2Image;
	}

}