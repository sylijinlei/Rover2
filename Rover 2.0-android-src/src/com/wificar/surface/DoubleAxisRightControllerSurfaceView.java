package com.wificar.surface;

import java.io.IOException;

import com.CAR2.R;
import com.wificar.WificarActivity;
import com.wificar.component.WifiCar;
import com.wificar.util.ImageUtility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class DoubleAxisRightControllerSurfaceView extends SurfaceView implements
SurfaceHolder.Callback,ControllerInterface{
	
	private static final int MAX = 10;
	private static final String TAG = "DoubleAxisRightControllerSurfaceView";
	static DoubleAxisRightControllerSurfaceView instance;
	private int rightStartPointX = 20;
	private int rightStartPointY = 20;
	private int areaBoundWidth = 20;
	private int areaBoundHeight = 20;
	private int leftStartPointX = 20;
	private int leftStartPointY = 20;
	
	private int stickBallPointX = 10;
	private int stickBallPointY =100;
	private int stickBallPointSmallY =70;
	private int stickBallPointLargeY =WificarActivity.dip2px(getContext(), 180);
	private int stickBallPointxLargeY =WificarActivity.dip2px(getContext(), 93);
	private int stickBallPointxxLargeY =WificarActivity.dip2px(getContext(), 70);
	private int stickBallWidth = 40;
	private int stickBallHeight = 40;
	private int stickBarPointX = 10;
	private int stickBarPointY = 0;
	private int stickBarPointSmallY = 0;
	private int stickBarPointLargeY = 0;
	private int stickBarPointxLargeY = 0;
	private int stickBarPointxxLargeY = 0;
	private int stickBarPointTopY = WificarActivity.dip2px(getContext(), 13);
	private int stickBarWidth = 60;
	//private int stickBallPointBaseX = 10;
	private int stickBallPointBaseY = 100;
	private int stickBallPointBaseSmallY = 70;
	private int stickBallPointBaseLargeY = 65;
	private int stickBallPointBasexLargeY = WificarActivity.dip2px(getContext(), 93);
	private int stickBallPointBasexxLargeY = WificarActivity.dip2px(getContext(), 70);
	
	private int stickBallMotionAreaWidth=80;
	private int stickBallMotionAreaHeight=230;
	private int stickBallMotionAreaHeightSmall=160;
	private int stickBallMotionAreaHeightLarge=150;
	private int stickBallMotionAreaHeightxLarge=WificarActivity.dip2px(getContext(), 188);
	private int stickBallMotionAreaHeightxxLarge=WificarActivity.dip2px(getContext(), 148);
	private boolean captureLeftBall = false;
	private boolean captureRightBall = false;
	
	private double size = 0.00;
	private int width;
	private int height;
	
	int maxPointIndex = 2;
	int leftIndexId = -1;
	int rightIndexId = -1;
	SurfaceHolder holder;
	private Handler handler = new Handler();

	private WifiCar wifiCar = null;
	private boolean controlEnable = true;
	
	private final int tStep = 100;
	private int iCarSpeedR = 0, iLastSpeedR = 0;
	private int iCarSpeedL = 0, iLastSpeedL = 0;
	
	private int stopLeftSignal = 0;
	private int stopRightSignal = 0;
	private int waitStopState = 0;
	
	Bitmap stickBar = null;
	Bitmap stickBall = null;
	
	Bitmap stickUp = null;
	Bitmap stickDown = null;
	Bitmap stickUpPress = null;
	Bitmap stickDownPress = null;
	
	
	
	
	public static DoubleAxisRightControllerSurfaceView getInstance(){
		return instance;
	}
	public void initial() {
		size = WificarActivity.getInstance().dimension;
		width = WificarActivity.getInstance().with;
		height = WificarActivity.getInstance().hight;
		
		holder = this.getHolder();
		
		stickBar = ImageUtility
				.createBitmap(getResources(), R.drawable.control_circle_left);
		stickBall = ImageUtility
				.createBitmap(getResources(), R.drawable.joy_stick);
		if(this.size < 5.8){
			Log.i("Double", "180X40");
			stickBar = stickBar.createScaledBitmap(stickBar, ImageUtility.dip2px(getContext(), 40), ImageUtility.dip2px(getContext(), 180), true);
			stickBall = stickBall.createScaledBitmap(stickBall, ImageUtility.dip2px(getContext(), 40), ImageUtility.dip2px(getContext(), 40), true);
		}
		if(this.size > 5.8){
			Log.i("Double", "225X48");
			//stickBar = stickBar.createScaledBitmap(stickBar,  48, 225, true);
			stickBall = stickBall.createScaledBitmap(stickBall, ImageUtility.dip2px(getContext(), 48), ImageUtility.dip2px(getContext(), 48), true);
			stickBar = stickBar.createScaledBitmap(stickBar, ImageUtility.dip2px(getContext(), 48), ImageUtility.dip2px(getContext(), 225), true);
			//stickBall = stickBall.createScaledBitmap(stickBall, ImageUtility.dip2px(getContext(), 48), ImageUtility.dip2px(getContext(), 48), true);
		}
		
		/*stickUp = ImageUtility.createBitmap(getResources(),
				R.drawable.stick_up);
		stickDown = ImageUtility.createBitmap(getResources(),
				R.drawable.stick_down);
		stickUpPress = ImageUtility
			.createBitmap(getResources(), R.drawable.stick_up_press);
		stickDownPress = ImageUtility.createBitmap(getResources(),
			R.drawable.stick_down_press);*/
		holder.addCallback(this);
		
	}
	public DoubleAxisRightControllerSurfaceView(Context context) {
		super(context);
		instance = this;
		// TODO Auto-generated constructor stub
		initial();
	}
	public DoubleAxisRightControllerSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		instance = this;
		//Log.e("wild0", "new ControllerSurfaceView:1");
		initial();
	}

	//@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		redraw();
	}

	//@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		holder.setFormat(PixelFormat.TRANSPARENT);
		redraw();
		handler.postDelayed(rightMovingTask, tStep);
	}

	//@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	public void setWifiCar(WifiCar wifiCar) {
		this.wifiCar = wifiCar;
	}
	
	public void setDirection(int right) {
		if(WificarActivity.getInstance().isPlayModeEnable){
			WificarActivity.getInstance().sendMessage(WificarActivity.MESSAGE_STOP_PLAY);
		}
		if(right > 0){  //0805当滑动的速度大于2时，给个全速10
			right = MAX;
		}
		if(right < 0 && right > -4){
			right = 0;
		}
		if(right < -4){
			right = -MAX;
		}
		Log.e("wild0", "direction right:" + right);
		/*if(this.size < 5.8){
			if(right > 0){  //0805当滑动的速度大于2时，给个全速10
				right = MAX;
			}
			if(right < -4){
				right = -MAX;
			}
		}else{
			if(this.width <= 800 & this.height <= 480){
				if(right > 0){  //0805当滑动的速度大于2时，给个全速10
					right = MAX;
				}
				if(right < -5){
					right = -MAX;
				}
			}else{
				
			}
		}*/
		
		iCarSpeedR = right;
	}
	public synchronized void redraw() {
		
		Canvas canvas = holder.lockCanvas();
		if (canvas == null)
			return;
		Paint paint = new Paint();
		paint.setAlpha(255);
		// canvas.drawColor(0, Mode.CLEAR);
		// clear(canvas);
		//Log.e("wificar", "controller");
		//clear(canvas);
		
		if (controlEnable) {
			canvas.drawBitmap(stickBar, stickBarPointX, stickBarPointY, paint);
			if(this.size < 5.8){
				if(this.width <= 480 & this.height <= 320){
					canvas.drawBitmap(stickBall, stickBallPointX, stickBallPointSmallY, paint);
				}else{
					if(this.width > 1100){
						Log.e(TAG, "controlEnable ----- >1100");
						canvas.drawBitmap(stickBall, stickBallPointX, stickBallPointxxLargeY, paint);
					}
					else {
						canvas.drawBitmap(stickBall, stickBallPointX, stickBallPointY, paint);
				
					}
				}
			}else{
				if(this.width <= 800 & this.height <= 480){  //适应分辨率低的Pad
					Log.e(TAG, "controlEnable ----- <800");
					canvas.drawBitmap(stickBall, stickBallPointX, stickBallPointLargeY, paint);
				}else{
					Log.e(TAG, "controlEnable ----- >800");
					canvas.drawBitmap(stickBall, stickBallPointX, stickBallPointxLargeY, paint);
				}
				
			}
			
		}
		holder.unlockCanvasAndPost(canvas);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!controlEnable){
			return true;
		}
		int pointerIndex = ((event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
		int pointerId = event.getPointerId(pointerIndex);
		int action = (event.getAction() & MotionEvent.ACTION_MASK);
		int pointerCount = event.getPointerCount();
		Log.e("touchinfo", "touch right count(" + pointerId + ":" + action + ":" + event.getAction()
				+ "):" + pointerCount);
		
		for (int i = 0; i < pointerCount; i++) {
			int id = event.getPointerId(i);
			Log.e("wild0","id:"+id+",action:"+action);
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				try{
				waitStopState = 0;
				
				//if (pointerCount > 0)
				//{
					Log.e("down","id:"+id);
					float x = event.getX(id);
					float y = event.getY(id);
					int currentSpeed;
					if(this.size < 5.8){
						if(this.width <= 480 & this.height <= 320){
							currentSpeed = (int) (-(y-stickBallPointBaseSmallY)/7);
							if((y-stickBarPointSmallY)>stickBallMotionAreaHeightSmall){
								y = stickBarPointSmallY+stickBallMotionAreaHeightSmall;
							}
						}else{
							if(this.width > 1100){
								currentSpeed = (int) (-(y-200)/12);
								if((y-stickBarPointxxLargeY)>stickBallMotionAreaHeightxxLarge){
									y = stickBarPointxxLargeY+stickBallMotionAreaHeightxxLarge;
								}
							}else{
								currentSpeed = (int) (-(y-stickBallPointBaseY)/9);
								if((y-stickBarPointY)>stickBallMotionAreaHeight){
									y = stickBarPointY+stickBallMotionAreaHeight;
								}
							}
							
						}
						
						}else{
							if(this.width <= 800 & this.height <= 480){
								currentSpeed = (int) (-(y-stickBallPointBaseY)/6);
								if((y-stickBarPointLargeY)>stickBallMotionAreaHeightLarge){  //0805适应Pad的
									y = stickBarPointLargeY+stickBallMotionAreaHeightLarge;
								}
							}else{
								currentSpeed = (int) (-(y-180)/9);
								if((y-stickBarPointxLargeY)>stickBallMotionAreaHeightxLarge){  //0805适应Pad的
									y = stickBarPointxLargeY+stickBallMotionAreaHeightxLarge;
								Log.e(TAG, "down ----------->800");
								}
							}
					}
					if(y<stickBarPointTopY){
						y = stickBarPointTopY;
					}
//TODO
					if(this.isLocateAtRightStickBallBoundary(x, y)){
						captureRightBall = true;
						this.stickBallPointY = (int) y - 20;  //0805当按下按钮的时候就发送位置的y坐标，让圆形按钮移动到y点坐标处
						this.stickBallPointSmallY = (int) y - 20;  //0805当按下按钮的时候就发送位置的y坐标，让圆形按钮移动到y点坐标处
						this.stickBallPointxLargeY = (int) y -20;  //0805当按下按钮的时候就发送位置的y坐标，让圆形按钮移动到y点坐标处
						this.stickBallPointxxLargeY = (int) y  -20;  //0805当按下按钮的时候就发送位置的y坐标，让圆形按钮移动到y点坐标处
						this.stickBallPointLargeY = (int) y - 20;  //0805当按下按钮的时候就发送位置的y坐标，让圆形按钮移动到y点坐标处
						this.setDirection(currentSpeed);
					}
					//Log.e("wild0", "("+i+")Action down["+x+","+y+"]");
					//Log.e("wild0", "touch right count(1):" + x + "," + y);
					
					this.redraw();
				}
				catch(Exception e){
					e.printStackTrace();
				}
				break;
			case MotionEvent.ACTION_UP:
				try{
				stopLeftSignal = 1;
				stopRightSignal = 1;
				waitStopState = 1;
				
				Log.e("barnotify", "ACTION_UP("+i+")Action up");
				if(pointerCount==2){
					waitStopState = 1;
				}
				else if(pointerCount==1){
					
					WificarActivity.getInstance().getWifiCar().disableMoveFlag();
					this.setDirection(0);
					this.stickBallPointY = this.stickBallPointBaseY;
					this.stickBallPointSmallY = this.stickBallPointBaseSmallY;
					this.stickBallPointxLargeY = this.stickBallPointBasexLargeY;
					this.stickBallPointxxLargeY = this.stickBallPointBasexxLargeY;
					this.stickBallPointLargeY = this.stickBallPointBaseLargeY;
					
					DoubleAxisLeftControllerSurfaceView.getInstance().setDirection(0);
					if(this.size < 5.8){
						if(this.width <= 480 & this.height <= 320){
							DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointSmallY(stickBallPointBaseSmallY);
						}else{
							if(this.width > 1100)
								DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointxxLargeY(stickBallPointBasexxLargeY);
							else{
								DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointY(stickBallPointBaseY);
							}
						}	
					}else{
						if(this.width <= 800 & this.height <= 480){
							DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointLargeY(stickBallPointBaseLargeY);
						}else{
							DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointxLargeY(stickBallPointBasexLargeY);
						}
						
					}
					
					DoubleAxisLeftControllerSurfaceView.getInstance().redraw();
					captureRightBall = false;
					captureLeftBall = false;
				}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				//this.setDirection(0);
				//DoubleAxisLeftControllerSurfaceView.getInstance().setDirection(0);
				break;
			case MotionEvent.ACTION_MOVE:
			
				try{
				waitStopState = 0;
				id = event.getPointerId(i);
				Log.e("rightbar", i+":("+id+")Action move=>"+event.getPointerCount());

				float mx = 0;
				float my = 0;
				if(event.getPointerCount()==1){
					mx = event.getX();
					my = event.getY();
				}
				else if(event.getPointerCount()==2){
					mx = event.getX(id);
					my = event.getY(id);
				}
				Log.e("rightbar", "("+id+")Action move");
				//Log.e("wild0", "("+i+")Action move");
				
				if(this.size < 5.8){
					if(this.width <= 480 & this.height <= 320){
						if((my-stickBarPointY)>stickBallMotionAreaHeightSmall){
							my = stickBarPointY+stickBallMotionAreaHeightSmall;
						}
					}else{
						if(this.width > 1100){
							if((my-stickBarPointxxLargeY)>stickBallMotionAreaHeightxxLarge){
								my = stickBarPointxxLargeY+stickBallMotionAreaHeightxxLarge;
							}
						}else{
							if((my-stickBarPointY)>stickBallMotionAreaHeight){
								my = stickBarPointY+stickBallMotionAreaHeight;
							}
						}
						
					}
					
				}else{
					if(this.width <= 800 & this.height <= 480){
						if((my-stickBarPointLargeY)>stickBallMotionAreaHeightLarge){  //0805适应Pad的
							my = stickBarPointLargeY+stickBallMotionAreaHeightLarge;
						}
					}else{
						if((my-stickBarPointxLargeY)>stickBallMotionAreaHeightxLarge){  //0805适应Pad的
							my = stickBarPointxLargeY+stickBallMotionAreaHeightxLarge;
						}
					}
				}
				if(my<stickBarPointTopY){
					my = stickBarPointTopY;
				}
				
				if(id==0){
					//this.stickBallPointY = (int) my;
					if(captureRightBall){
						int currentRightSpeed ;
						Log.i("DoubleRight", "captureRightBall:" + captureRightBall+ "," + "my:" + my);
						
						
						if(this.size < 5.8){
							if(this.width <= 480 & this.height <= 320){
								currentRightSpeed = (int) (-(my-stickBallPointBaseSmallY)/7);
								this.stickBallPointSmallY = (int) my - 20;  //0805减去20是为了在按下的时候，圆形按钮能滑动到按下的位置
							}else{
								if(this.width > 1100){
									currentRightSpeed = (int) (-(my-stickBallPointBasexxLargeY)/12);
									this.stickBallPointxxLargeY = (int) my - WificarActivity.dip2px(getContext(), 10);  //0805减去20是为了在按下的时候，圆形按钮能滑动到按下的位置
								}else{
									currentRightSpeed = (int) (-(my-stickBallPointBaseY)/9);
									this.stickBallPointY = (int) my - 20;  //0805减去20是为了在按下的时候，圆形按钮能滑动到按下的位置
								}
								
							}
							
						}else{
							if(this.width <= 800 & this.height <= 480){
								currentRightSpeed = (int) (-(my-stickBallPointBaseLargeY)/6);
								this.stickBallPointLargeY = (int) my - 20;  //0805减去20是为了在按下的时候，圆形按钮能滑动到按下的位置
							}else{
								currentRightSpeed = (int) (-(my-stickBallPointBasexLargeY)/9);
								this.stickBallPointxLargeY = (int) my - WificarActivity.dip2px(getContext(), 10);  //0805减去20是为了在按下的时候，圆形按钮能滑动到按下的位置
							}
						}
						//TODO
						Log.i("DoubleRight", "currentLeftSpeed:" + currentRightSpeed);
						this.setDirection(currentRightSpeed);
					
					}
					//this.redraw();
				}
				else if(id==1){
					if(captureLeftBall){
						int currentLeftSpeed = 0 ;
						
						if(this.size < 5.8){
							if(this.width <= 480 & this.height <= 320){
								currentLeftSpeed = (int) (-(my-stickBallPointBaseSmallY)/9);
								DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointSmallY((int)my -20);
							}else{
								if(this.width > 1100){
									currentLeftSpeed = (int) (-(my-stickBallPointBasexxLargeY)/12);
									DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointxxLargeY((int)my -20);
								}else{
									currentLeftSpeed = (int) (-(my-stickBallPointBaseY)/9);
									DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointY((int)my -20);
								}
								
							}
							
						}else{
							if(this.width <= 800 & this.height <= 480){
								currentLeftSpeed = (int) (-(my-stickBallPointBaseLargeY)/6);
								DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointLargeY((int)my -20);
							}else{
								currentLeftSpeed = (int) (-(my-stickBallPointBasexLargeY)/9);
								DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointxLargeY((int)my -20);
							}
						}
						Log.i("DoubleRight", "currentRightSpeed:" + currentLeftSpeed);
						DoubleAxisLeftControllerSurfaceView.getInstance().setDirection(currentLeftSpeed);
						DoubleAxisLeftControllerSurfaceView.getInstance().redraw();
					}
				}
				
				
				}
				catch(Exception e){
					e.printStackTrace();
					Log.e("rightbar", "excep ("+event.getX(0)+")Action move");
				}
				break;
			case MotionEvent.ACTION_POINTER_1_UP:
				//if(id==1){
				try{
					
				stopLeftSignal = 1;
				stopRightSignal = 1;
				waitStopState = 1;
					float x2 = event.getX(id);
					float y2 = event.getY(id);
					Log.e("barnotify", "ACTION_POINTER_1_UP("+pointerId+","+id+"):" + x2 + "," + y2);
					
					if(pointerId==id){
						//if(this.isLocateAtRightStickBallBoundary(x2)){
						if(id==0){	
							captureRightBall = false;
							setDirection(0);
							this.stickBallPointY = this.stickBallPointBaseY;
							this.stickBallPointSmallY = this.stickBallPointBaseSmallY;
							this.stickBallPointxLargeY = this.stickBallPointBasexLargeY;
							this.stickBallPointxxLargeY = this.stickBallPointBasexxLargeY;
							this.stickBallPointLargeY = this.stickBallPointBaseLargeY;
						}
						//}
						//if(this.isLocateAtLeftStickBallBoundary(x2)){
						if(id==1){
							captureLeftBall = false;
							if(this.size < 5.8){
								if(this.width <= 480 & this.height <= 320){
									DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointSmallY(stickBallPointBaseSmallY);
								}else{
									if(this.width > 1100)
										DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointxxLargeY(stickBallPointBasexxLargeY);
									else
										DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointY(stickBallPointBaseY);
								}
								
							}else{
								if(this.width <= 800 & this.height <= 480){
									DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointLargeY(stickBallPointBaseLargeY);
								}else{
									DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointxLargeY(stickBallPointBasexLargeY);
								}
								
							}
							
							DoubleAxisLeftControllerSurfaceView.getInstance().setDirection(0);
							DoubleAxisLeftControllerSurfaceView.getInstance().redraw();
						}
						//}
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				break;
			case MotionEvent.ACTION_POINTER_1_DOWN:
				try{
				waitStopState = 0;
				if(id==1){
					float x1 = event.getX(id);
					float y1 = event.getY(id);
					Log.e("wild0", "touch left point down count(1):" + x1 + "," + y1);
					if(this.isLocateAtLeftStickBallBoundary(x1, y1)){
						int currentLeftSpeed ;
						this.captureLeftBall = true;
						
						if(this.size < 5.8){
							if(this.width <= 480 & this.height <= 320){
								currentLeftSpeed = (int) (-(y1-stickBallPointBaseSmallY)/7);
								if((y1-stickBarPointSmallY)>stickBallMotionAreaHeightSmall){
									y1 = stickBarPointSmallY+stickBallMotionAreaHeightSmall;
								}
								if(y1<stickBarPointTopY){
									y1 = stickBarPointTopY;
								}
								DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointSmallY((int)y1 -20);
							}else{
								if(this.width > 1100){
									currentLeftSpeed = (int) (-(y1-stickBallPointBaseY)/9);
									if((y1-stickBarPointxxLargeY)>stickBallMotionAreaHeightxxLarge){
										y1 = stickBarPointxxLargeY+stickBallMotionAreaHeightxxLarge;
									}
									if(y1<stickBarPointTopY){
										y1 = stickBarPointTopY;
									}
									DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointxxLargeY((int)y1 -20);
								}else{
									currentLeftSpeed = (int) (-(y1-stickBallPointBaseY)/9);
									if((y1-stickBarPointY)>stickBallMotionAreaHeight){
										y1 = stickBarPointY+stickBallMotionAreaHeight;
									}
									if(y1<stickBarPointTopY){
										y1 = stickBarPointTopY;
									}
									DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointY((int)y1 -20);
								}
								
							}
							
							
						}else{
							if(this.width <= 800 & this.height <= 480){
								currentLeftSpeed = (int) (-(y1-stickBallPointBaseY)/6);
								if((y1-stickBarPointLargeY)>stickBallMotionAreaHeightLarge){  //0805适应Pad的
									y1 = stickBarPointLargeY+stickBallMotionAreaHeightLarge;
								}
								if(y1<stickBarPointTopY){
									y1 = stickBarPointTopY;
								}
								DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointLargeY((int)y1 -20);
							}else{
								currentLeftSpeed = (int) (-(y1-stickBallPointBaseY)/9);
								if((y1-stickBarPointxLargeY)>stickBallMotionAreaHeightxLarge){  //0805适应Pad的
									y1 = stickBarPointxLargeY+stickBallMotionAreaHeightxLarge;
								}
								if(y1<stickBarPointTopY){
									y1 = stickBarPointTopY;
								}
								DoubleAxisLeftControllerSurfaceView.getInstance().setStickBallPointxLargeY((int)y1 -WificarActivity.dip2px(getContext(), 10));
							}
							
						}
						DoubleAxisLeftControllerSurfaceView.getInstance().setDirection(currentLeftSpeed);
						DoubleAxisLeftControllerSurfaceView.getInstance().redraw();
					}
				}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
		if(stopRightSignal ==1 && waitStopState==0){
			//setDirection(0);
			stopRightSignal=0;
		}
		else if(stopLeftSignal ==1 && waitStopState==0){
			//DoubleAxisLeftControllerSurfaceView.getInstance().setDirection(0);
			stopLeftSignal=0;
		}
		this.redraw();
		DoubleAxisLeftControllerSurfaceView.getInstance().redraw();
		return true;
	
	}
	public void setStickBallPointY(int y){
		//0805在按下左边的时候，传过来的参数做下限制
			if(y>stickBallMotionAreaHeight){
				y = stickBallMotionAreaHeight;
			}
		
		if(y < 0){
			y = 0;
		}
		this.stickBallPointY = y;
	}
	public void setStickBallPointSmallY(int y){
		//0805在按下左边的时候，传过来的参数做下限制
			if(y>stickBallMotionAreaHeightSmall){
				y = stickBallMotionAreaHeightSmall;
			}
		
		if(y < 0){
			y = 0;
		}
		this.stickBallPointSmallY = y;
	}
	public void setStickBallPointLargeY(int y){
		//0805在按下左边的时候，传过来的参数做下限制
			if(y> stickBallMotionAreaHeightLarge){
				y = stickBallMotionAreaHeightLarge;
			}
		if(y < 0){
			y = 0;
		}
		this.stickBallPointLargeY = y;
	}
	public void setStickBallPointxLargeY(int y){
		//0805在按下左边的时候，传过来的参数做下限制
			if(y> stickBallMotionAreaHeightxLarge){
				y = stickBallMotionAreaHeightxLarge;
			}
		if(y < 0){
			y = 0;
		}
		this.stickBallPointxLargeY = y;
	}
	public void setStickBallPointxxLargeY(int y){
		//0805在按下左边的时候，传过来的参数做下限制
			if(y> stickBallMotionAreaHeightxxLarge){
				y = stickBallMotionAreaHeightxxLarge;
			}
		if(y < 0){
			y = 0;
		}
		this.stickBallPointxxLargeY = y;
	}
	public boolean isLocateAtLeftStickBallBoundary(float x, float y){
		int widthPixel = ImageUtility.getWidth(this.getContext());
		int leftXL = -(widthPixel-stickBarWidth);
		int leftXR = -(widthPixel-stickBarWidth)+stickBarWidth*2;
		int edge = stickBallMotionAreaWidth-stickBallWidth;
//		Log.i("barnotify", "width:"+widthPixel+", x:"+x+",("+leftXL+","+leftXR+")");
//		Log.i("isLocateAtRightStick", "(x,y):"+ "("+x+","+y+")" + "edge:" + edge);
//		Log.i("isLocateAtRightStick", "leftXL-edge*4:"+ (leftXL-edge*4));
//		Log.i("isLocateAtRightStick", "(leftXR+edge):"+ (leftXR+edge));
//		Log.i("isLocateAtRightStick", "stickBallPointY:"+ stickBallPointY + "," + "stickBallHeight:" + stickBallHeight);
//		Log.i("isLocateAtRightStick", "(stickBallPointY-edge):"+ (stickBallPointY-edge));
//		Log.i("isLocateAtRightStick", "(edge-stickBallPointY):"+ (edge-stickBallPointY));
//		Log.i("isLocateAtRightStick", "(stickBallPointY+stickBallHeight+edge):"+ (stickBallPointY+stickBallHeight+edge));
//		Log.e("barnotify", "width:"+widthPixel+", x:"+x+",("+leftXL+","+leftXR+")");
		if (x > (leftXL-edge*4) && x < (leftXR+edge)  && y>-40  && y < 300) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isLocateAtRightStickBallBoundary(float x, float y){
		int widthPixel = ImageUtility.getWidth(this.getContext());
		//int leftXL = (-widthPixel);
		//int leftXR = (-widthPixel+stickBallWidth);
		int edge = stickBallMotionAreaWidth-stickBallWidth;
		//Log.e("boundary", "x:"+x+",y:"+y);
		/*Log.i("isLocateAtRight", "x:" + x +" ," + "y:" + y  +"," + "edge:" + edge);
		Log.i("isLocateAtRight", "stickBallMotionAreaWidth:" + stickBallMotionAreaWidth + "stickBallWidth:" + stickBallWidth);
		Log.i("isLocateAtRight", "(stickBallPointX-edge):" + (stickBallPointX-edge));
		Log.i("isLocateAtRight", "(stickBallPointX+stickBallWidth+edge):" + (stickBallPointX+stickBallWidth+edge));
		Log.i("isLocateAtRight", "(stickBallPointY-edge):" + (stickBallPointY-edge));
		Log.i("isLocateAtRight", "(stickBallPointY+stickBallHeight+edge):" + (stickBallPointY+stickBallHeight*2+edge));*/
		//if (x > (stickBallPointX-edge) && x < (stickBallPointX+stickBallWidth+edge) && y>(stickBallPointY-edge-40) && y <(stickBallPointY+stickBallHeight*2+edge+10)) {
		if (x > (stickBallPointX-edge) && x < (stickBallPointX+stickBallWidth+edge) && y> 20 && y < WificarActivity.dip2px(getContext(), 224)) {
			Log.e("boundary", "r:"+true);
			return true;
		} else {
			return false;
		}
	}
	//@Override
	public void disableControl() {
		// TODO Auto-generated method stub
		controlEnable = false;
		this.setVisibility(INVISIBLE);
		this.redraw();
	}
	//@Override
	public void enableControl() {
		// TODO Auto-generated method stub
		controlEnable = true;
		this.setVisibility(VISIBLE);
		this.redraw();
	}
	private Runnable rightMovingTask = new Runnable() {
		public void run() {
			
			if (controlEnable) {
				//if (iLastSpeedL != 0 && iCarSpeedL == 0)
				//	wifiCar.moveCommand(WifiCar.GO_DIRECTION.Left, iCarSpeedL);
				//iLastSpeedL = iCarSpeedL;
				if (iCarSpeedR != 0){
					Log.e("wild0", "Run r("+controlEnable+"):"+iCarSpeedR);
					try {
						wifiCar.move(WifiCar.RIGHT_WHEEL, iCarSpeedR);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					iLastSpeedR=iCarSpeedR;
				}
				if (iCarSpeedR == 0 && iLastSpeedR!=0){
					try {
						wifiCar.move(WifiCar.RIGHT_WHEEL, iCarSpeedR);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					iLastSpeedR=0;
				}
				
			}
			
			handler.postDelayed(this, tStep);
		}
	};
}
