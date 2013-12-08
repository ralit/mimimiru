//********************************************************************************************
/**
 * @file		CameraView.java
 * @brief		カメラを制御する
 */
//********************************************************************************************

package com.example.hello.saito;

//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Environment;
//import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

public class CameraView extends SurfaceView implements Camera.PreviewCallback, SurfaceHolder.Callback {

	// 定数
	// ファイルの保存場所
	private static final String rootDirectory = Environment.getExternalStorageDirectory().getPath() + "/mimimiru/";
	private static final String ImageFilePath = rootDirectory + "mimimirupic.jpg";
	
	
	// 定数
//  private static final int FUNC_FLASH = 0;
  private static final int FUNC_FOCUS = 1;
  private static final int FUNC_SCENE = 2;
  private static final int FUNC_RESOLUTION = 3;
  private static final int FUNC_PREVIEWSIZE = 4;
  private static final int FUNC_CHANGECAMERA = 5;
  private static final int FUNC_WHITEBALANCE = 6;

  
    // 変数
	private Camera camera;
    private DrawView view;
    private float scaleX, scaleY;
    private int[] bitmap;
    private SurfaceHolder mholder;
	private Object lockObject = new Object();					// スレッド排他用オブジェクト
	

   
    // 機能クラス
//    private ImageConverter mcvt;								// コンバータ
//    private FileAccessController mfac;							// ファイルアクセスコントローラ
//    private FpsMeter	mFps;									// FPS計測クラス

    // アクティビティ
    private Activity mActivity;

    // ディスプレイ
    private DisplayInfo mDisplayInfo;

    // オーバレイView
    private OverlayView mOverlayView;
    
    // カメラのパラメータ
	private Parameters mParams = null;
	private Parameters mParams_bk = null;
	// カメラの数
	private int mNumCameras = 0;
	// 現在使っているカメラのId
	private int mCameraId = 0;
	//後から追加
	private Context context;
	
    // 結果
//    private Point mTouchPoint = new Point(0,0);		// ディスプレイ上の選択点 

    // コンストラクタ
      public CameraView(Context context, DrawView view, OverlayView oView, Activity activity)   {

    	super(context);
    	this.context = context;
    	
    	mActivity = activity;
        this.view = view;
        mOverlayView = oView;  
        mDisplayInfo = DisplayInfo.getDisplayInfo(mActivity);

        // holderの設定
        mholder = getHolder();
        mholder.addCallback(this);
        mholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // viewのフォーカス設定
        this.setFocusable(true);
        this.requestFocus();
        
		// 搭載されているカメラの数を取得
		mNumCameras  = Camera.getNumberOfCameras();
		// 使用するカメラのindexを初期化
		mCameraId = 0;
        
//        // コンバータの生成
//        mcvt = new ImageConverter();
//        
//        // ファイルアクセスコントローラの生成
//        mfac = new FileAccessController();
//        
		
    }
    // viewに変化があった場合に実行
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)   {
    	
    	Log.d("debug", "CameraView.surfaceChanged");

    }

    // viewが作成されたら実行
    public void surfaceCreated(SurfaceHolder holder)   {
    	Log.d("debug", "CameraView.surfaceCreated");		

    	camera = Camera.open(mCameraId);
    	
    	Log.d("debug", "Camera.open mCameraId:" + mCameraId);
    	
    	configure();
    	
    	Log.d("debug", "configure");

    	try {
        	// カメラ画像を直接表示する(最下層に使用)
			camera.setPreviewDisplay(mholder);
	    	Log.d("debug", "camera.setPreviewDisplay");

    	} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
    	// プレビュー開始
		startPreview();
    }
    // viewが破棄されたら実行
    public void surfaceDestroyed(SurfaceHolder holder)   {
    	Log.d("debug", "CameraView.surfaceDestroyed");		

    	//camera.setPreviewCallback(null);
        camera.setPreviewCallbackWithBuffer(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }
   
    //プレビュー画像の取得
    public void onPreviewFrame(byte[] data, Camera camera)   {
    	
//    	Log.d("debug2", "onPreviewFrame start");
    	
//    	camera.addCallbackBuffer(data);

    	//Log.d("debug", "onPreviewFrame end");

    }
    
    public void setTouchPoint(Point TouchPoint) {
    	
//    	Log.d("debug", "setTouchPoint TouchPoint.x=" + TouchPoint.x + ",TouchPoint.y=" + TouchPoint.y );
//    	
//    	float posX = (float)TouchPoint.x * mDisplayInfo.getRatioDisplay2Image() ;
//    	float posY = (float)TouchPoint.y * mDisplayInfo.getRatioDisplay2Image() ;
//
//    	Log.d("debug", "setTouchPoint posX=" + posX + ",posY=" + posY );
//    	
//    	this.mTouchPoint.x = (int)posX;
//    	this.mTouchPoint.y = (int)posY;
    }
 
    //********************************************************************************************
    /**
     * @brief		カメラ制御の設定
     *
     */
    //********************************************************************************************
    private void configure() {

        // カメラのパラメータを設定
		configureCameraParameter();

        // カメラの向きを設定
        // ※不要
        //configureCameraOrientation();

	}
    
    // mParams.getPictureSize(解像度)に対応したアスペクト比の、プレビューサイズリストを返す
    // ※ここでは、ディスプレイサイズのアスペクト比は考慮しない。
    //   ディスプレイ内に必ず収まることを前提とする。
	private List<Size> getPreviewSizeList() {
        
    	float previewRatio;		// プレビューサイズのアスペクト比
    	float picRatio;			// 解像度のアスペクト比
    	List<Size> PreviewSizeList = new ArrayList<Size>();

        // 解像度を取得
        int picWidth = mParams.getPictureSize().width;
        int picHeight = mParams.getPictureSize().height;
        // アスペクト比を算出
        picRatio = ((float) picWidth) / picHeight;

    	//Log.d("debug", "Picture Layout Size - w: " + picWidth + ", h: " + picHeight);

    	List<Size> sizeList = mParams.getSupportedPreviewSizes();
        for (Size size : sizeList) {
        	previewRatio = ((float) size.width) / size.height;
           	//Log.d("debug", "getSupportedPreviewSizes(プレビューサイズ) - w: " + size.width + ", h: " + size.height + ", total:" + size.width*size.height + ",Ratio:" + previewRatio );
            if (previewRatio == picRatio) {
            	// 解像度のアスペクト比と一致するpreviewサイズを保持
            	PreviewSizeList.add(size);
            }
        }

        return PreviewSizeList;
    }
    
    //********************************************************************************************
    /**
     * @brief		カメラのパラメータを設定
     *
	 * @return		public void
     */
    //********************************************************************************************
	private void configureCameraParameter() {

		Log.d("debug", "configureCameraParameter");		
		
		// パラメータの引き継ぎは実施しない。
		// インカメラとアウトカメラの機能は大きく異なる場合があり、引き継ぐことに意味がないため。

		// 現在のパラメータを取得
		mParams = camera.getParameters();
	
//		if ( mParams_bk == null ) {
	
			// 解像度設定
			// 解像度：0  　　※仮
			List<Size> supportedSizeList = mParams.getSupportedPictureSizes();
			if(supportedSizeList != null){
				Size size = supportedSizeList.get(0);
				mParams.setPictureSize(size.width, size.height);
				Log.d("debug2","picturesize:w=" + size.width + ",h=" + size.height);
			}
		
			// プレビューサイズ連動設定
			resetPreviewSize();
			// 表示用領域の確保(プレビューサイズと同じ)
			bitmap = new int[mParams.getPreviewSize().width * mParams.getPreviewSize().height];
	    	// スケールを算出
			calcScale();
		
//		} else {
//			// 手動設定した値を再設定
//			//mParams.setFlashMode(mParams_bk.getFlashMode());
//			mParams.setFocusMode(mParams_bk.getFocusMode());
//			mParams.setSceneMode(mParams_bk.getSceneMode());
//			mParams.setWhiteBalance(mParams_bk.getWhiteBalance());
//			mParams.setPictureSize(mParams_bk.getPictureSize().width, mParams_bk.getPictureSize().height);
//			mParams.setPreviewSize(mParams_bk.getPreviewSize().width, mParams_bk.getPreviewSize().height);
//		}

        // パラメータを設定
        camera.setParameters(mParams);
        mParams = camera.getParameters();
         
        // レイアウトも併せて変更
        float picRatio = ((float)  mParams.getPictureSize().width) / mParams.getPictureSize().height;
		ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
		layoutParams.height = mDisplayInfo.getDisplayH();
		layoutParams.width = (int) (layoutParams.height * picRatio);
		this.setLayoutParams(layoutParams);
		view.setLayoutParams(layoutParams);

	}


	//********************************************************************************************
	/**
	 * @brief		端末でサポートしている値のリスト取得.
	 *
	 * @param[in]	int	function_num	対象機能.
	 *
	 * @return		private List<String>	端末でサポートしている対象機能の値のリスト
	 */
	//********************************************************************************************
	private List<String> getSupportedList( int function_num ) {

		List<String> supportedList = null;
		List<Size> supportedSizeList = null;

        switch(function_num) {

             case FUNC_PREVIEWSIZE:
              	//supportedSizeList = mParams.getSupportedPreviewSizes();
            	 supportedSizeList = getPreviewSizeList();
                 supportedList = new ArrayList<String>();
                 for(int i=0; i<supportedSizeList.size(); i++) {
                 	supportedList.add(supportedSizeList.get(i).width +
                 					  "x" +
                 					  supportedSizeList.get(i).height);
                 	Log.d("debug2", "PreviewSize:w=" + supportedSizeList.get(i).width +
       					  ",h=" +
       					  supportedSizeList.get(i).height);
                 }
                 break;


             default:
            	 return null;
        }
		return supportedList;
	}

	//********************************************************************************************
	/**
	 * @brief		解像度にあわせて、プレビューサイズをリセット
	 *
	 * @return		private void
	 */
	//********************************************************************************************
	private void resetPreviewSize() {

		int width;
		int height;
		float picRatio = 0;
		float previewRatio = 0;
		
		// 現在のプレビューサイズが、解像度に対応しているかチェック
		// 解像度のアスペクト比
		width = mParams.getPictureSize().width;
		height = mParams.getPictureSize().height;
        picRatio = ((float) width) / height;
		Log.d("debug", "解像度:width=" + width + ",height=" + height + ",picRatio" + picRatio);
        
        // プレビューサイズのアスペクト比
		width = mParams.getPreviewSize().width;
		height = mParams.getPreviewSize().height;
		if ( height != 0) {
        	previewRatio= ((float) width) / height;
		}
		Log.d("debug", "プレビューサイズ(変更前):width=" + width + ",height=" + height + ",picRatio" + previewRatio);
		
		// アスペクト比が異なる場合、index=0のプレビューサイズを適応する
        if (picRatio != previewRatio ) {
        	List<Size> supportedPreviewSizeList = getPreviewSizeList();
        	if ( supportedPreviewSizeList != null ) {
        		Size previewsize = supportedPreviewSizeList.get(0);
        		mParams.setPreviewSize(previewsize.width, previewsize.height);
        		mParams_bk = mParams;
        		Log.d("debug", "プレビューサイズ(変更後):width=" + previewsize.width + ",height=" + previewsize.height);
        		// レイアウトも併せて変更
        		ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
//        		layoutParams.height = mDisplay.getHeight();
        		layoutParams.height = mDisplayInfo.getDisplayH();
        		layoutParams.width = (int) (layoutParams.height * picRatio);
        		this.setLayoutParams(layoutParams);
        		view.setLayoutParams(layoutParams);
        	}
        }
	}
	
	
	//********************************************************************************************
	/**
	 * @brief		プレビューサイズを元に、表示Caｎvasのスケールを算出
	 *
	 * @return		public void
	 */
	//********************************************************************************************
	private void calcScale() {
	    // ディスプレイ表示用の比率を計算
	    // ※カメラは横向き固定とする。
	    // ※カメラの解像度に対し、ディスプレイの横幅比率が大きい場合を想定し、scaleXは固定でscaleYと同じ値とする
//	    scaleY = (float)mDisplay.getHeight() / (float)mParams.getPreviewSize().height;
	    scaleY = (float)mDisplayInfo.getDisplayH() / (float)mParams.getPreviewSize().height;
	    scaleX = scaleY;
	}
	
	//********************************************************************************************
	/**
	 * @brief		フレームサイズを取得
	 *
	 * @return		private int
	 */
	//********************************************************************************************
    private int getFrameSize() {

    	int imgformat = mParams.getPreviewFormat();
		int bitsperpixel = ImageFormat.getBitsPerPixel(imgformat);
		Camera.Size camerasize = mParams.getPreviewSize();
		int frame_size = ((camerasize.width * camerasize.height) * bitsperpixel) / 8;
		return frame_size;
    	
    }

	//********************************************************************************************
	/**
	 * @brief		プレビュー表示を一時停止
	 *
	 * @return		public void
	 */
	//********************************************************************************************
	public void stopPreview() {
		Log.d("debug", "stopPreview");
		camera.setPreviewCallbackWithBuffer(null);
		camera.stopPreview();
	}

	//********************************************************************************************
	/**
	 * @brief		プレビュー表示を再開
	 *
	 * @return		public void
	 */
	//********************************************************************************************
	public void startPreview() {

//		Log.d("debug", "startPreview mSettingFlag=" + mSettingFlag);
//		
//		if (mSettingFlag == false ) {

            // register the buffer.
        	int frame_size = getFrameSize();
            byte[] frame = new byte[frame_size];
            camera.addCallbackBuffer(frame);
            
            // 描画再開
            camera.setPreviewCallbackWithBuffer(this);
			camera.startPreview();
//		}
	
	}
    
    //********************************************************************************************
    /**
     * @brief		takePictureを実行
     *
     */
    //********************************************************************************************
    public void takePicture() {

		Log.d("debug", "takePicture start");

    	camera.autoFocus(new AutoFocusCallback() {
			public void onAutoFocus(boolean success, final Camera camera) {
				ShutterCallback shutter = new ShutterCallback() {
					public void onShutter() {
						Log.d("debug", "onShutter");
					}
				};
				PictureCallback raw = new PictureCallback() {
					public void onPictureTaken(byte[] data, Camera camera) {
						Log.d("debug", "onPictureTaken: raw");
					}
				};
				PictureCallback jpeg = new PictureCallback() {
					public void onPictureTaken(byte[] data, Camera camera) {
						Log.d("debug", "onPictureTaken: jpeg");

						try {
							// data配列をbitmapに変換
							Bitmap picturebitmap = BitmapFactory.decodeByteArray(data,0,data.length);

							// ファイルに保存
							saveBMP2JPG(picturebitmap);
							MojiRecognize moji1 = new MojiRecognize(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mimimiru/mimimirupic.jpg", context);
							moji1.start();

						} catch (Exception e) {
							e.printStackTrace();
						}
						
						camera.startPreview();
					}
				};
				
				Log.d("debug", "setPreviewCallback(null)");
				camera.setPreviewCallback(null);
				Log.d("debug", "takePicture");
				camera.takePicture(shutter, null, jpeg);
				
//				new Thread() {
//					@Override
//					public void run() {
//					
//						Log.d("debug", "setPreviewCallbackWithBuffer(this)");
//						setPreviewCallbackWithBuffer();
//						
//						Log.d("debug", "startPreview");
//						camera.startPreview();
//						
//					}
//				}.start();
			}
		});

		Log.d("debug", "takePicture end");
		
    }
    
    public void setPreviewCallbackWithBuffer() {
        // register the buffer.
    	int frame_size = getFrameSize();
        byte[] frame = new byte[frame_size];
        camera.addCallbackBuffer(frame);

    	//camera.setPreviewCallback(this);
        camera.setPreviewCallbackWithBuffer(this);
    	
    }

    //********************************************************************************************
    /**
     * @brief		ビットマップをjpgファイルへ保存
     *
	 * @param[in]	Bitmap bmp			ビットマップ情報
	 *
	 * @return		なし
     */
    //********************************************************************************************
//    public void saveBMP2JPG(Bitmap bmp) {
//
//		try {
//			//FileOutputStream output = openFileOutput(filename, Context.MODE_WORLD_READABLE);
//			FileOutputStream output = new FileOutputStream( ImageFilePath );
//			bmp.compress(Bitmap.CompressFormat.JPEG, 80, output);
//			output.close();
//			Log.d("debug","saveBMP2JPG success");
//		} catch (IOException e) {
//			e.printStackTrace();
//			Log.d("debug","saveBMP2JPG error");
//		}
//	}

    public void saveBMP2JPG(Bitmap bmp) {

    	Bitmap bitmap2;
    	int width=0;
    	int height=0;

    	width = bmp.getWidth();
    	height = bmp.getHeight();

    	Log.d("debug","bmp w=" + width + ",h=" + height);

    	if ( height > 1000 ) {
    		width/=2;
    		height/=2;
    		Log.d("debug","bmp 縮小 w=" + width + ",h=" + height);
    		bmp= Bitmap.createScaledBitmap(bmp, width, height, false);
    	}

    	try {
    		//FileOutputStream output = openFileOutput(filename, Context.MODE_WORLD_READABLE);
    		FileOutputStream output = new FileOutputStream( ImageFilePath );
    		bmp.compress(Bitmap.CompressFormat.JPEG, 80, output);
    		output.close();
    		Log.d("debug","saveBMP2JPG success");
    	} catch (IOException e) {
    		e.printStackTrace();
    		Log.d("debug","saveBMP2JPG error");
    	}
    }
}

