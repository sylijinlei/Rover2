package com.wificar.surface;

import java.io.IOException;

import com.CAR2.R;

import com.wificar.WificarActivity;
import com.wificar.component.WifiCar;
import com.wificar.surface.CamerSettingSurfaceView;
import com.wificar.util.ImageUtility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class CamerSettingSurfaceView extends SurfaceView implements SurfaceHolder. Callback 
{     
	static CamerSettingSurfaceView instance;
	
	SurfaceHolder holder;
	private Handler handler = new Handler();
	private Handler handler_camer = new Handler();
	
	//摇杆的X,Y坐标以及摇杆的半径
//	public float RSmallRockerCircleX = 0;
//	public float RSmallRockerCircleY = 40;
//	public float RSmallRockerCircleY_1 = 65;
//	public float RSmallRockerCircleY_2 = 32;
	
	public float RSmallRockerCircleX = 0;
	public float RSmallRockerCircleY = WificarActivity.dip2px(getContext(), 60);
	public float RSmallRockerCircleY_1 = WificarActivity.dip2px(getContext(), 70);
	public float RSmallRockerCircleY_2 = WificarActivity.dip2px(getContext(), 40);
	private WifiCar wifiCar = null;	
	
	
	Bitmap background = null;
	Bitmap stickBall = null;;
	private final int tStep = 1;
	private boolean controlEnable = false;
	private boolean enableRun = false;
	private boolean cameraPressed = false;
	private int cameramove =0 ;
	
	private int f = 0;
	private int b = 0;
	
	//private MySurfaceViewThread mySurfaceViewThread;    
	//private boolean hasSurface;  
	
	
	public static CamerSettingSurfaceView getInstance(){
		return instance;
	}
	
	CamerSettingSurfaceView(Context context) {      
		super(context);      
		init();    
		} 
	public CamerSettingSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		instance = this;
		//Log.d("wild0", "new ControllerSurfaceView:1");
		init();
	}
	private void init() {     
		//创建一个新的SurfaceHolder， 并分配这个类作为它的回调(callback)     
		holder = getHolder();  
		
		background = ImageUtility.createBitmap(getResources(),
				R.drawable.back);
		stickBall = ImageUtility.createBitmap(getResources(),
				R.drawable.stick_back);
		holder.addCallback(this);          
		} 
	//@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		Log.e("zhangzhangzhang", "surfavechange");
		redraw();
	}

	//@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		holder.setFormat(PixelFormat.TRANSPARENT);
		redraw();
		handler.postDelayed(rightMovingTask, tStep);
		//handler_camer.postDelayed(camer, tStep);
	}

	//@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	//@Override
	public void setWifiCar(WifiCar wifiCar) {
		this.wifiCar = wifiCar;
	}
	private void clear(Canvas canvas) {

		if (canvas != null) {

			canvas.drawColor(Color.TRANSPARENT);
		}
	}
public synchronized void redraw() {
		
		Canvas canvas = holder.lockCanvas();
		if (canvas == null)
			return;
		clear(canvas);
		Paint paint = new Paint();
		paint.setAlpha(255);
		
		if (controlEnable) {
			
			if(WificarActivity.getInstance().dimension > 5.8){
				if(WificarActivity.getInstance().with > 850){
					if(RSmallRockerCircleY !=40){
						RSmallRockerCircleY=RSmallRockerCircleY -18;
					}
					canvas.drawBitmap(background, 0, 0, paint);
					canvas.drawBitmap(stickBall, WificarActivity.dip2px(getContext(), 50), RSmallRockerCircleY, paint);
					Log.e("1212121", "Y   :" + RSmallRockerCircleY);
				}else{
					if(RSmallRockerCircleY_2 !=32){
					RSmallRockerCircleY_2=RSmallRockerCircleY_2 -18;
				}
					canvas.drawBitmap(background, 0, 0, paint);
					canvas.drawBitmap(stickBall, 1, RSmallRockerCircleY_2, paint);
					Log.e("1212121", "Y   :" + RSmallRockerCircleY_2);
				}
				
			}else {
				if(RSmallRockerCircleY_1 !=65){
				RSmallRockerCircleY_1=RSmallRockerCircleY_1 -18;
				}	
				canvas.drawBitmap(background, 0, 0, paint);
				canvas.drawBitmap(stickBall, 1, RSmallRockerCircleY_1, paint);
				Log.e("1212121", "Y111   :" + RSmallRockerCircleY_1);
			}
			
			
		}
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!controlEnable){
			return true;
		}
		int action = (event.getAction() & MotionEvent.ACTION_MASK);
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				//cameraPressed = true;
				//enableRun = true;
				Log.e("zhangdddd", " the f is : " + f);
				Log.e("zhangddd11", " the b is : " + b);
				
				float x = event.getX();
				float y = event.getY();
				
				RSmallRockerCircleX = 0;
				RSmallRockerCircleY = y ;
				
				RSmallRockerCircleY_1 = y;
				RSmallRockerCircleY_2 = y;
				
				if(y>90){
					 RSmallRockerCircleY = WificarActivity.dip2px(getContext(), 60);
				 }
				 if(y<20){
					 RSmallRockerCircleY = WificarActivity.dip2px(getContext(), 20);
				 }
				 
				 if(y>125){
					 RSmallRockerCircleY_1 = 125;
				 }
				 if(y<20){
					 RSmallRockerCircleY_1 = 20;
				 }
				 
				 if(y>70){
					 RSmallRockerCircleY_2 = 70;
				 }
				 if(y<17){
					 RSmallRockerCircleY_2 = 17;
				 }
				
				if (this.isLocateAtCameraForwardBoundary(x, y)) {
					Log.e("zhangzhangzhn", "up_up");
					WificarActivity.getInstance().getWifiCar().enableMoveFlag();
					f ++;
					Log.e("zhangdddd__11111", " the f is : " + f);
					if( f == 1){
					cameramove = 1;
						
					}
					
				} else if (this.isLocateAtCameraBackwardBoundary(x, y)) {
					Log.e("zhangzhangzhn", "down_down");
					WificarActivity.getInstance().getWifiCar().enableMoveFlag();
					b ++;
					Log.e("zhangdddd__11111", " the b is : " + b);
					if(b == 1){
					cameramove = 2;
						
					}
				}
				else if(this.isLocateAtCamera(x, y)){
					b = 0;
					f = 0;
					cameramove = 0;
					
					Log.e("zhang33333333 ", " the f is : " + f);
					Log.e("zhang33333333 ", " the b is : " + b);
					
				}
				
		break;
			case MotionEvent.ACTION_UP:
				
				f = 0;
				b = 0;
				cameramove = 0;
				
				Log.e("zhangupup", " the f is : " + f);
				Log.e("zhangupup11", " the b is : " + b);
				float ux = event.getX();
				float uy = event.getY();
				
				RSmallRockerCircleX = 0;
				RSmallRockerCircleY = 40 ;
				
				RSmallRockerCircleY_1 = 65;
				RSmallRockerCircleY_2 = 32;
				WificarActivity.getInstance().getWifiCar().disableMoveFlag();
				
				
				
				break;
			case MotionEvent.ACTION_MOVE:

				
				Log.e("zhangmv", " the f is : " + f);
				Log.e("zhangmv11", " the b is : " + b);
				float mx = event.getX();
				float my = event.getY();
				
				RSmallRockerCircleX = 0;
				RSmallRockerCircleY = my ;
				
				RSmallRockerCircleY_1 = my ;
				RSmallRockerCircleY_2 = my ;
				
				if(my>90){
					 RSmallRockerCircleY = 90;
				 }
				 if(my<20){
					 RSmallRockerCircleY = 20;
				 }
				 
				 if(my>125){
					 RSmallRockerCircleY_1 = 125;
				 }
				 if(my<20){
					 RSmallRockerCircleY_1 = 20;
				 }
				 
				 
				 if(my>70){
					 RSmallRockerCircleY_2 = 70;
				 }
				 if(my<17){
					 RSmallRockerCircleY_2 = 17;
				 }
				if (this.isLocateAtCameraForwardBoundary(mx, my)) {
					f++;
					Log.e("zhangdddd__22222", " the f is : " + f);
					Log.e("zhangzhangzhn", "moveup_up");
					WificarActivity.getInstance().getWifiCar().enableMoveFlag();
					if( f == 1 ){	
					cameramove = 1;
						
						
					}
					
				} else if (this.isLocateAtCameraBackwardBoundary(mx, my)) {
					Log.e("zhangzhangzhn", "movedown_down");
					b++;
					Log.e("zhangdddd__2222222", " the b is : " + b);
					WificarActivity.getInstance().getWifiCar().enableMoveFlag();
					if(b == 1){
						
					cameramove = 2;
						
					}
					
				
				}
				else if(this.isLocateAtCamera(mx, my)){
					
					b = 0;
					f = 0;
					//cameramoveup = 0;
					//cameramovedown = 0;
					Log.e("zhang2323 ", " the f is : " + f);
					Log.e("zhang23232 ", " the b is : " + b);
				}
				break;
			case MotionEvent.ACTION_POINTER_1_UP:
				
				
				f = 0;
				b = 0;
				cameramove = 0;
				
				Log.e("zhangup", " the f is : " + f);
				Log.e("zhangup11", " the b is : " + b);
				
				float x2 = event.getX();
				float y2 = event.getY();
				
				RSmallRockerCircleX = 0;
				RSmallRockerCircleY = 40;
				
				RSmallRockerCircleY_1 = 65;
				RSmallRockerCircleY_2 = 32;
				
				WificarActivity.getInstance().getWifiCar().disableMoveFlag();
				
				
				try {
					wifiCar.camerastop();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
	}
		this.redraw();
		return true;
	}
	private boolean isLocateAtCameraForwardBoundary(float x, float y) {
		// TODO Auto-generated method stub
		
		Log.e("aaaa222222222222F", "x  , y " + x +" ," + y);
		if(x>-10 && x<80 && y < 45 && y > -80) {
			
			return true;
		} else {
			return false;
		}
		
	}

	private boolean isLocateAtCameraBackwardBoundary(float x, float y) {
		// TODO Auto-generated method stub
		Log.e("aaaa222222222222222B", "x  , y " + x +" ," + y);
		if(x > -50 && x<80 && y>70 && y < 240 ){
			
			return true;
		}
		else{
			return false;
		}
	}
	private boolean isLocateAtCamera(float x, float y) {
		// TODO Auto-generated method stub
		Log.e("aaaa33333333333", "x  , y " + x +" ," + y);
		if(x > -50 && x<80 && y>45 && y < 70 ){
			
			return true;
		}
		else{
			return false;
		}
	}

	
	public static Bitmap createBitmap(Resources res, int srcId) {

		Bitmap bitmap = BitmapFactory.decodeResource(res, srcId);
		return bitmap;

	}
	//@Override
	public void disableControl() {
		// TODO Auto-generated method stub
		controlEnable = false;
		this.setVisibility(View.INVISIBLE);
		Log.e("zhang11", "setting controldisable");
		this.redraw();
	}
	//@Override
	public void enableControl() {
		// TODO Auto-generated method stub
		controlEnable = true;
		this.setVisibility(View.VISIBLE);
		Log.e("zhang", "setting controlenable");
		this.redraw();
	}
	private Runnable rightMovingTask = new Runnable() {
		//@Override
		public void run() {
			
			if (controlEnable) {
				//if (iLastSpeedL != 0 && iCarSpeedL == 0)
				//	wifiCar.moveCommand(WifiCar.GO_DIRECTION.Left, iCarSpeedL);
				//iLastSpeedL = iCarSpeedL;
				if (cameramove== 0){
					Log.e("aaaa_sp", "camera stop:" + cameramove);
					//wifiCar.canCommand(1);
					try {
						wifiCar.disableMoveFlag();
						wifiCar.camerastop();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (cameramove == 1 && f == 1){
					Log.e("aaaa_up", "camera up:" + cameramove);
					
					try {
						wifiCar.enableMoveFlag();
						wifiCar.cameraup();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				if (cameramove == 2 && b == 1){
					Log.e("aaaa_down", "camera down:" + cameramove);
					
					try {
						wifiCar.enableMoveFlag();
						wifiCar.cameradown();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				
			}
			
			handler.postDelayed(this, tStep);
		}
	};

	/*private Runnable camer = new Runnable(){

		public void run() {
			// TODO Auto-generated method stub
			if(enableRun){
				if(cameraPressed){
					wifiCar.enableMoveFlag();
				}else if(!cameraPressed){
					wifiCar.disableMoveFlag();
				}
			}
			handler_camer.postDelayed(camer, 10);
		}
		
	};*/

}
