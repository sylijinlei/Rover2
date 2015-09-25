package com.wificar.surface;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import com.CAR2.R;
import com.wificar.WificarActivity;
import com.wificar.component.VideoData;
import com.wificar.util.ImageUtility;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
//import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.AbsoluteLayout;
import android.widget.ToggleButton;

public class CameraSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	private Paint p = new Paint();
	private Bitmap bMap;
	private float actualheight=ImageUtility.getHeight(this.getContext())/ImageUtility.getDensity(this.getContext());
	private BitmapFactory.Options opt = new BitmapFactory.Options(); 
	
	private Bitmap currentBitmap = null;
	private float[] ZOOM = new float[] { 100, 125, 150, 175, 200 };

	private int[] WIDTH_PORTAIT = new int[] { 320, 400, 480, 560, 640 };
	private int[] HEIGHT_PORTAIT = new int[] { 240, 300, 360, 420, 480 };
	
	private int[] WIDTH_LAND;
	private int[] HEIGHT_LAND;
	//private int[] WIDTH_LAND = new int[] { 480, 600, 720, 840, 960 };
	//private int[] HEIGHT_LAND = new int[] { 360, 450, 540, 630, 720 };

	//private int[] WIDTH = new int[] { 480, 600, 720, 840, 960 };
	//private int[] HEIGHT = new int[] { 360, 450, 540, 630, 720 };

	private int timeOut = 20;
	private int recordingTimeOut = 100;
	long currentTime = System.currentTimeMillis();
	long intervalTime = 0;
	private int targetZoom = 0;
	// private int width = 0;
	// private int height = 0;
	private float cx = 0; // center of x
	private float cy = 0; // center of y
	//private int picture = 0;

	// private int sx = 0; // start of x
	// private int sy = 0; // start of y

	private final Semaphore available = new Semaphore(1, true);
	private boolean bMoveImage = false;
	//private byte[] cameraBytes = null;
	VideoData cameraData = null;
	private byte[] copyBytes = null;
	
	private AbsoluteLayout.LayoutParams params = null;
	private int currentWidth, currentHeight, originalWidth, originalHeight;
	private int sx, sy;
	// 20111125: Use handler to replace timer
	//Timer t = new Timer(true);
	private HandlerThread handlerThread = new HandlerThread("camera surface");
	private Handler handler = null;
	
	private boolean isConnect = false;
	public final static  int MESSAGE_SHIFT_VIEWPORT = 11;
	
	private Handler messageHandler = new Handler() {
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case MESSAGE_SHIFT_VIEWPORT :
				Log.d("shift","shift viewport");
				redraw(cameraData.getData());
				break;
			}
		}};
	public boolean isRecordAvailable(){
		try{
		if(currentBitmap.getWidth()==320 && currentBitmap.getHeight()==240){
			return true;
		}
		return false;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	//public void setCameraBytes(byte[] bytes) {
	public void setCameraBytes(VideoData vdata) {
		try {
			
			if (available.tryAcquire(timeOut, TimeUnit.MILLISECONDS)) {
				cameraData = vdata;
				/*
				try {
					WificarActivity.getInstance().writeVideoBytes(cameraBytes, timestamp);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
				available.release();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private int getScaledWidth(int zoom){
		if(WificarActivity.getInstance().isPortait()){
			return WIDTH_PORTAIT[zoom];
		}
		else{
			return WIDTH_LAND[zoom];
		}
		
	}
	private int getScaledHeight(int zoom){
		if(WificarActivity.getInstance().isPortait()){
			return HEIGHT_PORTAIT[zoom];
		}
		else{
			return HEIGHT_LAND[zoom];
		}
		
	}
	public int zoomIn() throws InterruptedException {
		
		if (targetZoom >= 0 && targetZoom < 4) {
			targetZoom++;
			//float scale = ZOOM[targetZoom] / 100.0f;
			
			int previousWidth = ImageUtility.dip2px(this.getContext(), getScaledWidth(targetZoom-1));
			int previousHeight = ImageUtility.dip2px(this.getContext(),getScaledHeight(targetZoom-1));
			
			int currentWidth = ImageUtility.dip2px(this.getContext(), getScaledWidth(targetZoom));
			int currentHeight = ImageUtility.dip2px(this.getContext(),getScaledHeight(targetZoom));
			
			
			//int sx = this.getLeft();//shift value
			//int sy = this.getTop();
			
			int previousCenterX = previousWidth/2;
			int previousCenterY = previousHeight/2;
			
			int currentCenterX = currentWidth/2;
			int currentCenterY = currentHeight/2;
			
			//int shiftX = previousCenterX - currentCenterX;
			//int shiftY = previousCenterY - currentCenterY;
			//加上缩放前宽、高的一半大小 ,再减去该元件缩放后宽、高的一半大小
			sx = sx+ previousCenterX - currentCenterX;       
			sy = sy+ previousCenterY - currentCenterY;
			
			this.setVisibility(SurfaceView.GONE);
			this.setVisibility(SurfaceView.VISIBLE);
		}
		
		return targetZoom;
	}

	public int zoomOut() throws InterruptedException {
		
		if (targetZoom > 0 && targetZoom <= 4) {
			targetZoom--;
			int originalWidth = ImageUtility.dip2px(this.getContext(), getScaledWidth(0));
			int originalHeight = ImageUtility.dip2px(this.getContext(),getScaledHeight(0));		
			
			int previousWidth = ImageUtility.dip2px(this.getContext(), getScaledWidth(targetZoom+1));
			int previousHeight = ImageUtility.dip2px(this.getContext(),getScaledHeight(targetZoom+1));		
			
			int currentWidth = ImageUtility.dip2px(this.getContext(),getScaledWidth(targetZoom));
			int currentHeight = ImageUtility.dip2px(this.getContext(),getScaledHeight(targetZoom));
			
			//int sx = this.getLeft();//shift value
			//int sy = this.getTop();
			
			int previousCenterX = previousWidth/2;
			int previousCenterY = previousHeight/2;
			
			int currentCenterX = currentWidth/2;
			int currentCenterY = currentHeight/2;
			
			int shiftX = previousCenterX - currentCenterX;
			int shiftY = previousCenterY - currentCenterY;
			
			int newX = 0;
			int newY = 0;
			
			//Log.d("wificar","zoom out x1:"+(shiftX+sx));
			if((shiftX+sx)>0){
				newX = 0;
			}
			else{
				newX = shiftX+sx;
			}
			if((shiftY+sy)>0){
				newY = 0;
			}
			else{
				newY = shiftY+sy;
			}
			
			
			if((shiftX+sx+currentWidth)<originalWidth){
				newX = originalWidth - currentWidth;
				//Log.d("wificar","zoom out x:"+newX);
			}
			else{
				//newX = shiftX+sx;
			}
			//Log.d("wificar","zoom out y:"+(shiftY+sy+currentHeight));
			if((shiftY+sy+currentHeight)<originalHeight){
				newY = originalHeight - currentHeight;
				//Log.d("wificar","zoom out y:"+newY);
			}
			else{
				//newY = shiftY+sy;
			}
			sx = newX;
			sy = newY;
			
			this.setVisibility(SurfaceView.GONE);
			
			this.setVisibility(SurfaceView.VISIBLE);
		}
		return targetZoom;
	}

	public float getTargetZoomValue() {
		return ZOOM[targetZoom];
	}

	SurfaceHolder holder;

	public void initial() {
		handlerThread.start();
		handler = new Handler(handlerThread.getLooper());
		
		
		
		holder = this.getHolder();

		holder.addCallback(this);
	}

	public CameraSurfaceView(Context context) {
		super(context);
		initial();
	}

	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initial();

	}

	public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initial();
	}

	private float bx = 0; // touch x;
	private float by = 0; // touch y;
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//float scale = ZOOM[targetZoom] / 100.0f;
		//float scaledWidth =ImageUtility.dip2px(this.getContext(), getScaledWidth(targetZoom));
		//float scaledHeight = ImageUtility.dip2px(this.getContext(),getScaledHeight(targetZoom));

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(WificarActivity.getInstance().succeedConnect){
				bMoveImage = true;
				bx = event.getX();//base x
				by = event.getY();//base y
				//sx = this.getLeft();//shift value
				//sy = this.getTop();
				
				currentWidth = ImageUtility.dip2px(this.getContext(),getScaledWidth(targetZoom));
				currentHeight = ImageUtility.dip2px(this.getContext(),getScaledHeight(targetZoom));
				originalWidth = ImageUtility.dip2px(this.getContext(),getScaledWidth(0));
				originalHeight = ImageUtility.dip2px(this.getContext(),getScaledHeight(0));
				/*
				try{
					available.tryAcquire(timeOut, TimeUnit.MILLISECONDS);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				*/
			}
			
			break;
		case MotionEvent.ACTION_MOVE:
			if(WificarActivity.getInstance().succeedConnect){
				float nx = (event.getX() - bx);
				float ny = (event.getY() - by);

				//Log.d("touch", "sx:" + sx);
				//Log.d("touch", "nx:" + nx);
				//Log.d("touch", "currentWidth:" + currentWidth);
				//Log.d("touch", "originalWidth:" + originalWidth);
				if((sx + nx)<0 && (sx + nx+currentWidth)>originalWidth && (sy + ny)<0 && (sy + ny+ currentHeight)>originalHeight)
				{
					sx += nx;
					sy += ny;

					//this.shiftViewport((int) (sx), (int) (sy));
				}
				
				
					Message msg = new Message();
					msg.what = this.MESSAGE_SHIFT_VIEWPORT;
					messageHandler.sendMessage(msg);
				
				
				//if((sy + by + currentHeight)>originalHeight){
				//	this.shiftViewport((int) (sx), (int) (sy + ny));
				//}

				
				bx = event.getX();
				by = event.getY();
			}
			
			

			break;
		case MotionEvent.ACTION_UP:
			bMoveImage = false;
			//available.release();
			break;
		default:
			break;
		}

		return true;

	}
	
	//@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		this.cx = ((float) this.getWidth()) / 2.0f;
		this.cy = ((float) this.getHeight()) / 2.0f;
		//Log.d("wificar", "width:" + this.getWidth() + ";height:" + this.getHeight());
		//Log.d("wificar", "CameraSurface:change");

	}

	//@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//Log.d("wificar", "CameraSurface:create");
		// this.width = this.getWidth();
		// this.height = this.getHeight();
		this.cx = ((float) this.getWidth()) / 2.0f;
		this.cy = ((float) this.getHeight()) / 2.0f;
		//Log.d("wificar", "width:" + this.getWidth() + ";height:" + this.getHeight());
		//Log.d("wificar", "CameraSurface:create");
		//params = ((AbsoluteLayout.LayoutParams) getLayoutParams());
		start();
	}

	//@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//Log.d("wificar", "CameraSurface:destroy");
		//t.cancel();
		handler.removeCallbacks(DrawingTask);
	}

	//public void takePicture() {
	//	picture++;
	//}

	public void start() {

		//t.schedule(new DrawingTask(), 0, timeOut);
		
		handler.postDelayed(DrawingTask, timeOut);
		//isConnect = WificarActivity.getInstance().succeedConnect; //判断是否成功连接
		
		if(WificarActivity.getInstance().dimension > 5.8 ){
			
			 WIDTH_LAND = new int[] { 720,840, 960 ,1080, 1200 };
			 HEIGHT_LAND = new int[] {540,630 , 720, 810, 900 };
			 
		}else {  //if(WificarActivity.getInstance().dimension < 6.5)
			
			WIDTH_LAND = new int[] { 480, 600, 720, 840, 960 };
			HEIGHT_LAND = new int[] { 360, 450, 540, 630, 720 };
		//	WIDTH_LAND = new int[] {440, 560, 680, 800 ,920};
		//	HEIGHT_LAND = new int[] {330, 420, 510, 600 ,690};
			
		}
		//Timer t = new Timer();
		//t.schedule(task, delay, period)
	}

	private Runnable DrawingTask = new Runnable() {
		public void run() {
			//Log.d("Camera", "Run");
			if(!bMoveImage)
			{
				try {
					long preTime = System.currentTimeMillis();
					if (available.tryAcquire(timeOut, TimeUnit.MILLISECONDS)) {
						//copyBytes = cameraData.getData();
						
						if(cameraData!=null){
							redraw(cameraData.getData());
							long postTime = System.currentTimeMillis();
							Log.d("redraw","redraw time("+cameraData.getTimestamp()+"):"+(postTime-preTime));
						}
						available.release();
					}
					//long postTime = System.currentTimeMillis();
					//if(cameraData!=null){
					
					//}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			handler.postDelayed(this, timeOut);
		}
	};

public synchronized void redraw(byte[] bArrayImage) {
		
		Canvas canvas = holder.lockCanvas();
		if (canvas == null)
			return;
	    float scalesize=0;	
		p.setARGB(255, 0, 0, 0);
		Rect rect = new Rect(0, 0, this.getWidth(), this.getHeight());
		 //opt.inSampleSize =2;//get sample in every inSamplesize
		 opt.inPurgeable = true;    
		 opt.inInputShareable = true;    
		 opt.inDither = true;  
       	 opt.inPreferredConfig = Bitmap.Config.RGB_565;

		if (bArrayImage == null) {
	//		Log.v(TAG, "----bMap -----");
			canvas.drawRect(rect, p);
		} else
		{
			// Log.e("decode","start");
			 bMap = BitmapFactory.decodeByteArray(bArrayImage,0,bArrayImage.length,opt);
			// Log.e("decode","end");
			 	 if(bMap==null) 
			{
				holder.unlockCanvasAndPost(canvas);
				return ;
			}
			//int originalWidth = ImageUtility.dip2px(this.getContext(),getScaledWidth(0));
			//int originalHeight = ImageUtility.dip2px(this.getContext(),getScaledHeight(0));
			
			// Resize Bitmap to match the size of surface
			int currentWidth = ImageUtility.dip2px(this.getContext(),getScaledWidth(targetZoom));
			int currentHeight = ImageUtility.dip2px(this.getContext(),getScaledHeight(targetZoom));
			
			if ((currentWidth != bMap.getWidth())|| (currentHeight != bMap.getHeight())) {
				float scaleWidth = ((float) currentWidth) / bMap.getWidth();
				float scaleHeight = ((float)currentHeight)/ bMap.getHeight();			
				Matrix mRescale = new Matrix();
				mRescale.reset();
				mRescale.postScale(scaleWidth, scaleHeight); 
			  //mRescale.postScale(1, 1);
		      //srcrect = new Rect(0, 0, bMap.getWidth(),bMap.getHeight());
		      //newrect = new Rect(0, 0, currentWidth,currentHeight);
			  //bMap=Bitmap.createScaledBitmap(bMap, currentWidth, currentHeight, false);
				scalesize=(targetZoom+1)*actualheight;
				//Log.e("scalesize","size="+scalesize);
				if(scalesize>500){				
					Matrix matrix = new Matrix();
					matrix.reset();
					matrix.postScale(1, 1);
					matrix.postTranslate( (sx),  (sy));
					
				bMap = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), mRescale, true);
				canvas.drawBitmap(bMap,matrix, p);
				}
				else {
					mRescale.postTranslate( (sx),  (sy));
					canvas.drawBitmap(bMap, mRescale, p);	
				}
				//Log.e("scale","end");			
			}
			else{				
				canvas.drawBitmap(bMap,0,0, p);
			}
		    //canvas.drawBitmap(bMap, srcrect, newrect, p);	            
		}
		
		holder.unlockCanvasAndPost(canvas);
		if(bMap!=null)bMap.recycle();
		if(p!=null)p.reset();		
		
	}
	public synchronized void redraw1(byte[] bArrayImage) {
		
		Canvas canvas = holder.lockCanvas();
		if (canvas == null)
			return;
		//Log.d("wild0","byte length:"+bArrayImage.length);
		
		// int width = this.getWidth();
		// int height = this.getHeight();
		Paint p = new Paint();
		p.setARGB(255, 0, 0, 0);
		Rect rect = new Rect(0, 0, this.getWidth(), this.getHeight());

		if (bArrayImage == null) {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inDither = true;
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			
		} else {
			
			
				//byte[] data = new byte[230400];
				//for(int i=0;i<data.length;i++){
				//	data[i] = 0;
				//}
			BitmapFactory.Options opt = new BitmapFactory.Options();
			
			opt.inDither = true;
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			currentBitmap = BitmapFactory.decodeByteArray(bArrayImage, 0,
					bArrayImage.length, opt);
			//Log.d("wild0","bitmap width:"+bMap.getWidth());
			//Log.d("wild0","bitmap height:"+bMap.getHeight());
			if(currentBitmap==null) return ;
			
			//int originalWidth = ImageUtility.dip2px(this.getContext(),getScaledWidth(0));
			//int originalHeight = ImageUtility.dip2px(this.getContext(),getScaledHeight(0));
			
			// Resize Bitmap to match the size of surface
			//targetZoom
			int currentWidth = ImageUtility.dip2px(this.getContext(),getScaledWidth(0));
			int currentHeight = ImageUtility.dip2px(this.getContext(),getScaledHeight(0));
			Bitmap adjustBitmap = Bitmap.createBitmap(currentBitmap);
			
			Log.d("shift","shift viewport:currentWidth:("+currentWidth+")");

			
			if ((currentWidth != currentBitmap.getWidth())|| (currentHeight != currentBitmap.getHeight())) {
				float scaleWidth = ((float) currentWidth) / currentBitmap.getWidth();
				float scaleHeight = ((float)currentHeight)
						/ currentBitmap.getHeight();

				Matrix mRescale = new Matrix();
				mRescale.reset();
				Log.d("shift","shift viewport:scaleWidth:("+scaleWidth+")");
				Log.d("shift","shift viewport:scaleHeight:("+scaleHeight+")");
				mRescale.postScale(scaleWidth, scaleHeight);
				//mRescale.postTranslate((int) (sx), (int) (sy));
				

				adjustBitmap = Bitmap.createBitmap(adjustBitmap, 0, 0, adjustBitmap.getWidth(),adjustBitmap
						.getHeight(), mRescale, true);
			}
			
		
			
			float scale = (ZOOM[targetZoom] / 100.0f);
			
			Matrix matrix = new Matrix();
			
			matrix.postScale(scale, scale);
			matrix.postTranslate( (sx),  (sy));
			//matrix.postTranslate( 100,  100);
			Log.d("shift","shift viewport:scale:("+scale+"),t("+sx+","+sy+")");
		
			if (this.getWidth() <= adjustBitmap.getWidth()
					&& this.getHeight() <= adjustBitmap.getHeight()) {
				
				canvas.drawBitmap(adjustBitmap,matrix, p);
				
			}else{				
				canvas.drawBitmap(adjustBitmap,0,0, p);
			}
			// canvas.drawBitmap(bMap, 0, 0, p);
		}
		
		holder.unlockCanvasAndPost(canvas);
	}

}
