//********************************************************************************************
/**
 * @file		DrawView.java
 * @brief		ディスプレイへ描画
 */
//********************************************************************************************

package com.example.hello.saito;

import android.content.Context;
import android.graphics.Canvas;
//import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder holder;
    private Paint paint;
    
    public DrawView(Context context)   {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        paint = new Paint();
        
//        // FPS等の情報描画文字色
//        paint.setColor(Color.BLUE);
//        paint.setTextSize(50);

    }
    public void surfaceDestroyed(SurfaceHolder holder)   {
        holder.removeCallback(this);
        holder=null;
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
    public void surfaceCreated(SurfaceHolder holder) {}

    //********************************************************************************************
    /**
     * @brief		ビットマップを描画する
     *
	 * @param[in]	int[] img			描画Bitmap
	 * @param[in]	float scaleX		イメージの横比率
	 * @param[in]	float scaleY		イメージの縦比率
	 * @param[in]	int width			イメージの幅
	 * @param[in]	int height			イメージの高さ
	 * @param[in]	String strInfo		画面表示する情報
	 *
	 * @return		なし
     */
    //********************************************************************************************
	public void drawBitmap(int[] img, float scaleX, float scaleY, int width, int height, String strInfo) {
        // Canvasをロック
        Canvas canvas = holder.lockCanvas();
        // 描画比率を設定
        canvas.scale(scaleX, scaleY);
        // Bitmapを描画
        canvas.drawBitmap(img, 0, width, 0, 0, width, height, false, paint);
//        paint.setColor(Color.BLUE);
//        canvas.drawRect(50, 100, 150, 200, paint);
        // 情報を表示
        if (strInfo != "") {
        	canvas.drawText(strInfo, 20, 60, paint);
        }
        
        // Canvasをロック解除
        holder.unlockCanvasAndPost(canvas);
   }
}
