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
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DoubleAxisLeftControllerSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback, ControllerInterface {
	private static final int MAX = 10;
	private static final String TAG = "DoubleAxisLeftControllerSurfaceView";
	static DoubleAxisLeftControllerSurfaceView instance;
	private int rightStartPointX = 20;
	private int rightStartPointY = 20;
	private int areaBoundWidth = 20;
	private int areaBoundHeight = 20;
	private int leftStartPointX = 20;
	private int leftStartPointY = 20;
	
	private int stickBallPointX = 10;
	private int stickBallPointY = 100;
	private int stickBallPointSmallY = 70;
	private int stickBallPointxLargeY =WificarActivity.dip2px(getContext(), 93);
	private int stickBallPointxxLargeY = WificarActivity.dip2px(getContext(), 70);
	private int stickBallPointLargeY = WificarActivity.dip2px(getContext(), 180);
	private int stickBallWidth = 60;
	private int stickBallHeight = 40;
	private int stickBallMotionAreaWidth = 100;
	private int stickBallMotionAreaHeight = 230;
	private int stickBallMotionAreaHeightSmall = 160;
	private int stickBallMotionAreaHeightLarge = 150;
	private int stickBallMotionAreaHeightxLarge = WificarActivity.dip2px(getContext(), 188);
	private int stickBallMotionAreaHeightxxLarge = WificarActivity.dip2px(getContext(), 148);
	private int stickBarPointX = 10;
	private int stickBarPointY = 0;
	private int stickBarPointSmallY = 0;
	private int stickBarPointxLargeY = 0;
	private int stickBarPointLargeY = 0;
	private int stickBarPointxxLargeY = 0;
	private int stickBarPointTopY =  WificarActivity.dip2px(getContext(), 13);
	private int stickBallPointBaseX = 10;
	private int stickBallPointBaseY = 100;
	private int stickBallPointBaseSmallY = 70;
	private int stickBallPointBasexLargeY = WificarActivity.dip2px(getContext(), 93);
	private int stickBallPointBasexxLargeY = WificarActivity.dip2px(getContext(), 70);
	private int stickBallPointBaseLargeY = 65;
	
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
	
	private int stickValue=0;
	
	Bitmap stickBar = null;
	Bitmap stickBall = null;
	
	Bitmap stickUp = null;
	Bitmap stickDown = null;
	Bitmap stickUpPress = null;
	Bitmap stickDownPress = null;
	public static DoubleAxisLeftControllerSurfaceView getInstance(){
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
			Log.e("Double", "180X40");
			stickBar = stickBar.createScaledBitmap(stickBar, ImageUtility.dip2px(getContext(), 40), ImageUtility.dip2px(getContext(), 180), true);
			stickBall = stickBall.createScaledBitmap(stickBall, ImageUtility.dip2px(getContext(), 40), ImageUtility.dip2px(getContext(), 40), true);
		}else{
		//if(this.size > 5.8){
			Log.e("Double", "225X48");
			stickBall = stickBall.createScaledBitmap(stickBall, ImageUtility.dip2px(getContext(), 48), ImageUtility.dip2px(getContext(), 48), true);
			stickBar = stickBar.createScaledBitmap(stickBar, ImageUtility.dip2px(getContext(), 48), ImageUtility.dip2px(getContext(), 225), true);
		}
		/*stickUp = ImageUtility
				.createBitmap(getResources(), R.drawable.stick_up);
		stickDown = ImageUtility.createBitmap(getResources(),
				R.drawable.stick_down);
		stickUpPress = ImageUtility
			.createBitmap(getResources(), R.drawable.stick_up_press);
		stickDownPress = ImageUtility.createBitmap(getResources(),
				R.drawable.stick_down_press);*/
		holder.addCallback(this);
		
	}

	/*
	 * private Runnable leftMovingTask = new Runnable() { public void run() { if
	 * (controlEnable) { //wifiCar.moveCommand(WifiCar.GO_DIRECTION.Left, 0); }
	 * handler.postDelayed(this, tStep); } };
	 */
	public DoubleAxisLeftControllerSurfaceView(Context context) {
		super(context);
		instance = this;
		initial();
		
		// TODO Auto-generated constructor stub
	}

	public DoubleAxisLeftControllerSurfaceView(Context context,AttributeSet attrs) {
		super(context, attrs);
		instance = this;
		// Log.e("wild0", "new ControllerSurfaceView:1");
		initial();
	}

	//@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		redraw();
	}

	public synchronized void redraw() {

		Canvas canvas = holder.lockCanvas();
		if (canvas == null)
			return;
		Paint paint = new Paint();
		paint.setAlpha(255);
		// canvas.drawColor(0, Mode.CLEAR);
		// clear(canvas);
		// Log.e("wificar", "controller");
		// clear(canvas);
		
		
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
					Log.e(TAG, "controlEnable -----  > 480  < 1100 ");
					}
				}
				
			}else{
				if(this.width <= 800 & this.height <= 480){  //适应分辨率低的Pad
					canvas.drawBitmap(stickBall, stickBallPointX, stickBallPointLargeY, paint);
				}else{
					canvas.drawBitmap(stickBall, stickBallPointX, stickBallPointxLargeY, paint);
				}
				
			}
			
			
			
		}
		holder.unlockCanvasAndPost(canvas);
	}
	//@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		holder.setFormat(PixelFormat.TRANSPARENT);
		redraw();
		handler.postDelayed(leftMovingTask, tStep);
	}

	//@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	public void setDirection(int left) {
		Log.e("DoubleLeft", "left :" + left);
		if(WificarActivity.getInstance().isPlayModeEnable){
			WificarActivity.getInstance().sendMessage(WificarActivity.MESSAGE_STOP_PLAY);
		}
		if(left > 0){  //0805当滑动的速度大于2时，给个全速10
			left = MAX;
		}
		if(left < 0 && left > -4){
			left = 0;
		}
		if(left < -4){
			left = -MAX;
		}
		Log.e("wild0", "direction left:" + left);
		/*if(this.size < 5.8){
			if(left > 0){  //0805当滑动的速度大于2时，给个全速10
				left = MAX;
			}
			if(left < -4){
				left = -MAX;
			}
		}else{
			if(this.width <= 800 & this.height <= 480){
				if(left > 0){  //0805当滑动的速度大于2时，给个全速10
					left = MAX;
				}
				if(left < -5){
					left = -MAX;
				}
			}else{
				
			}
		}*/
		
		iCarSpeedL = left;
		//this.stickBallPointY = -(left*9) + this.stickBallPointBaseY;
		//this.redraw();
	}

	public void setWifiCar(WifiCar wifiCar) {
		this.wifiCar = wifiCar;
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int pointerIndex = ((event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
		int pointerId = event.getPointerId(pointerIndex);
		
		int action = (event.getAction() & MotionEvent.ACTION_MASK);
		int pointerCount = event.getPointerCount();
		//Log.e("wild0", "touch left count(" +pointerId+":"+ action + ":" + event.getAction()
		//		+ "):" + pointerCount);
		Log.e("touchinfo", "touch left count(" + pointerId + ":" + action + ":" + event.getAction()
				+ "):" + pointerCount);
		for (int i = 0; i < pointerCount; i++) {
			int id = event.getPointerId(i);
			
			Log.e("moving","id:"+id+",action:"+action);
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				
				waitStopState = 0;
				Log.e("screen_left_stick", "("+i+")Action down");
				//if (pointerCount > 0)
				//{
				    Log.e("wild0L","id:"+id);
					float x = event.getX();
					float y = event.getY();
					int currentSpeed;
					if(this.size < 5.8){
						if(this.width <= 480 & this.height <= 320){
							currentSpeed = (int) (-(y-stickBallPointBaseSmallY)/7);
							if((y-stickBarPointSmallY)>stickBallMotionAreaHeightSmall){  //0805设置向下滑动的最大距离，不超出bar的长度
								y = stickBarPointSmallY+stickBallMotionAreaHeightSmall;
							}
							
						}else{
							if(this.width > 1100){
								Log.e(TAG, "y> 1100:"+y);
								currentSpeed = (int) (-(y-200)/12);
								if((y-stickBarPointxxLargeY)>stickBallMotionAreaHeightxxLarge){  //0805设置向下滑动的最大距离，不超出bar的长度
									y = stickBarPointxxLargeY+stickBallMotionAreaHeightxxLarge;
								}
							}else{
								Log.e(TAG, "480<y< 1100:"+y);
								currentSpeed = (int) (-(y-stickBallPointBaseY)/9);
								if((y-stickBarPointY)>stickBallMotionAreaHeight){  //0805设置向下滑动的最大距离，不超出bar的长度
									y = stickBarPointY+stickBallMotionAreaHeight;
								}
							}
						}
						
					}else{
						if(this.width <= 800 & this.height <= 480){
							currentSpeed = (int) (-(y-stickBallPointBaseY)/6);
							if((y-stickBarPointLargeY)>stickBallMotionAreaHeightLarge){  //0805设置向下滑动的最大距离，不超出bar的长度
								y = stickBarPointLargeY+stickBallMotionAreaHeightLarge;
							}
						}else{
							Log.e(TAG, "y:"+y);
							currentSpeed = (int) (-(y-180)/9);
							if((y-stickBarPointxLargeY)>stickBallMotionAreaHeightxLarge){  //0805设置向下滑动的最大距离，不超出bar的长度
								y = stickBarPointxLargeY+stickBallMotionAreaHeightxLarge;
							}
						}
					}
					if(y<stickBarPointTopY){  //0805设置向上滑动的最大距离，不超出bar的长度
						y = stickBarPointTopY;
					}
					//if(captureLeftBall){
					//	currentSpeed = (int) ((y-stickBallPointBaseY)/9);
					//}
					if(this.isLocateAtLeftStickBallBoundary(x, y)){
						captureLeftBall = true;
						this.stickBallPointY = (int) y-20;  //0805当按下按钮的时候就发送位置的y坐标，让圆形按钮移动到y点坐标处
						this.stickBallPointSmallY = (int) y-20;  //0805当按下按钮的时候就发送位置的y坐标，让圆形按钮移动到y点坐标处
						this.stickBallPointxLargeY = (int) y-20;  //0805当按下按钮的时候就发送位置的y坐标，让圆形按钮移动到y点坐标处
						this.stickBallPointxxLargeY = (int) y-20;  //0805当按下按钮的时候就发送位置的y坐标，让圆形按钮移动到y点坐标处
						this.stickBallPointLargeY = (int) y-20;  //0805当按下按钮的时候就发送位置的y坐标，让圆形按钮移动到y点坐标处
						this.setDirection(currentSpeed);
					}
					this.redraw();
					//Log.e("wild0", "touch left count(1):" + x + "," + y);
				break;
			case MotionEvent.ACTION_UP:
				Log.e("moving", "("+i+")Action up");
				stopLeftSignal = 1;
				stopRightSignal = 1;
				
				captureLeftBall = false;
				captureRightBall = false;
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
					DoubleAxisRightControllerSurfaceView.getInstance().setDirection(0);
					if(this.size < 5.8){
						if(this.width <= 480 ){
							DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointSmallY(stickBallPointBaseSmallY);
						}else{
							if(this.width > 1100){
								DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointxxLargeY(stickBallPointxxLargeY);
							}
							else
								DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointY(stickBallPointBaseY);
						}
						
					}else{
						if(this.width <= 800 & this.height <= 480){
							DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointLargeY(stickBallPointBaseLargeY);
						}else{
							DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointxLargeY(stickBallPointBasexLargeY);
						}
					}
					
					DoubleAxisRightControllerSurfaceView.getInstance().redraw();
					
				}
				//this.setDirection(0);
				//DoubleAxisRightControllerSurfaceView.getInstance().setDirection(0);
				break;
			case MotionEvent.ACTION_MOVE:
				waitStopState = 0;
				Log.e("moving", "("+i+")Action move");
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
				//float mx = event.getX(id);
				//float my = event.getY(id);
				
				if(this.size < 5.8){
					if(this.width <= 480 & this.height <= 320){
						if((my-stickBarPointSmallY)>stickBallMotionAreaHeightSmall){  //0805设置向下滑动的最大距离，不超出bar的长度
							my = stickBarPointSmallY+stickBallMotionAreaHeightSmall;
						}
					}else{
						if(this.width > 1100){
							if((my-stickBarPointxxLargeY)>stickBallMotionAreaHeightxxLarge){  //0805设置向下滑动的最大距离，不超出bar的长度
								my = stickBarPointxxLargeY+stickBallMotionAreaHeightxxLarge;
							}
						}else{
							if((my-stickBarPointY)>stickBallMotionAreaHeight){  //0805设置向下滑动的最大距离，不超出bar的长度
								my = stickBarPointY+stickBallMotionAreaHeight;
							}
						}
						
					}
					
				}else{
					if(this.width <= 800 & this.height <= 480){
						if((my-stickBarPointLargeY)>stickBallMotionAreaHeightLarge){  //0805设置向下滑动的最大距离，不超出bar的长度
							my = stickBarPointLargeY+stickBallMotionAreaHeightLarge;
						}
					}else{
						if((my-stickBarPointxLargeY)>stickBallMotionAreaHeightxLarge){  //0805设置向下滑动的最大距离，不超出bar的长度
							my = stickBarPointxLargeY+stickBallMotionAreaHeightxLarge;
						}
					}
				}
				if(my<stickBarPointTopY){  //0805设置向上滑动的最大距离，不超出bar的长度
					my = stickBarPointTopY;
				}
				//Log.e("leftbar", "("+id+")Action move");
				if(id==0){
					//this.stickBallPointY = (int) my;
					if(captureLeftBall){
						Log.e("DoubleLeft", "captureLeftBall:" + captureLeftBall+ "," + "my:" + my);
						int currentLeftSpeed;
						if(this.size < 5.8){
							if(this.width <= 480 & this.height <= 320){
								currentLeftSpeed = (int) (-(my-stickBallPointBaseSmallY)/7);
								this.stickBallPointSmallY = (int) my -20;  //0805减去20是为了在按下的时候，圆形按钮能滑动到按下的位置
								
							}else{
								if(this.width > 1100){
									currentLeftSpeed = (int) (-(my-stickBallPointBasexxLargeY)/12);
									this.stickBallPointxxLargeY = (int) my -WificarActivity.dip2px(getContext(), 10);  //0805减去20是为了在按下的时候，圆形按钮能滑动到按下的位置
								}else{
									currentLeftSpeed = (int) (-(my-stickBallPointBaseY)/9);
									this.stickBallPointY = (int) my -20;  //0805减去20是为了在按下的时候，圆形按钮能滑动到按下的位置
								}
								
							}
							
						}else{
							if(this.width <= 800 & this.height <= 480){
								currentLeftSpeed = (int) (-(my-stickBallPointBaseLargeY)/6);
								this.stickBallPointLargeY = (int) my -20;  //0805减去20是为了在按下的时候，圆形按钮能滑动到按下的位置
							}else{
								currentLeftSpeed = (int) (-(my-stickBallPointBasexLargeY)/9);
								this.stickBallPointxLargeY = (int) my -WificarActivity.dip2px(getContext(), 10);  //0805减去20是为了在按下的时候，圆形按钮能滑动到按下的位置
							
							}
						}
						
						Log.e("DoubleLeft", "currentLeftSpeed:" + currentLeftSpeed);
						//this.redraw();
						
						this.setDirection(currentLeftSpeed);
						
					}
				}
				else if(id==1){
					//Log.e("leftbar", "("+id+")Action move "+captureRightBall);
					if(captureRightBall){
						
						int currentRightSpeed = 0;
						
						if(this.size < 5.8){
							if(this.width <= 480 & this.height <= 320){
								currentRightSpeed = (int) (-(my-stickBallPointBaseSmallY)/8);
								DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointSmallY((int)my -20);
								
							}else{
								if(this.width > 1100){
									currentRightSpeed = (int) (-(my-stickBallPointBasexxLargeY)/12);
									DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointxxLargeY((int)my -20);
								}else{
									currentRightSpeed = (int) (-(my-stickBallPointBaseY)/9);
									DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointY((int)my -20);
								}
								
							}
							
						}else{
							if(this.width <= 800 & this.height <= 480){
								currentRightSpeed = (int) (-(my-stickBallPointBaseLargeY)/6);
								DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointLargeY((int)my -20);
							}else{
								currentRightSpeed = (int) (-(my-stickBallPointBasexLargeY)/9);
								DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointxLargeY((int)my -20);
							}
							
						}
						Log.e("DoubleLeft", "currentRightSpeed:" + currentRightSpeed);
						DoubleAxisRightControllerSurfaceView.getInstance().setDirection(currentRightSpeed);
						DoubleAxisRightControllerSurfaceView.getInstance().redraw();
					}
				}
				break;
			case MotionEvent.ACTION_POINTER_1_UP:
				//if(id==1){
				stopLeftSignal = 1;
				stopRightSignal = 1;
				waitStopState = 1;
					float x2 = event.getX(id);
					float y2 = event.getY(id);
					Log.e("wild0", "touch left point up count("+id+"):" + x2 + "," + y2);
					
					if(pointerId==id){
						
						//if(this.isLocateAtLeftStickBallBoundary(x2, y2)){
						if(id == 0){
							Log.e("DoubleLeft", "left is UP");
							setDirection(0);
							this.stickBallPointY = this.stickBallPointBaseY;
							this.stickBallPointSmallY = this.stickBallPointBaseSmallY;
							this.stickBallPointxLargeY = this.stickBallPointBasexLargeY;
							this.stickBallPointxxLargeY = this.stickBallPointBasexxLargeY;
							this.stickBallPointLargeY = this.stickBallPointBaseLargeY;
						}
							
						//}
						//if(this.isLocateAtRightStickBallBoundary(x2, y2)){
						if(id == 1){
							Log.e("DoubleLeft", "right is UP");
							if(this.size < 5.8){
								if(this.width <= 480 & this.height <= 320){
									DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointSmallY(stickBallPointBaseSmallY);
								}else{
									if(this.width > 1100){
										DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointxxLargeY(stickBallPointBasexxLargeY);
									}
									else
										DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointY(stickBallPointBaseY);
								}
								
							}else{
								if(this.width <= 800 & this.height <= 480){
									DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointLargeY(stickBallPointBaseLargeY);
								}else{
									DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointxLargeY(stickBallPointBasexLargeY);
								}
								
							}
							DoubleAxisRightControllerSurfaceView.getInstance().setDirection(0);
							DoubleAxisRightControllerSurfaceView.getInstance().redraw();
						}
							
						//}
					}
				break;
			case MotionEvent.ACTION_POINTER_1_DOWN:
				waitStopState = 0;
				
				//Log.e("touchinfo", "touch left point down count right(1)");
				if(id==1){
					float x1 = event.getX(id);
					float y1 = event.getY(id);
					int currentRightSpeed = 0;
					
					Log.e("touchinfo", "touch left point down count right(1):" + x1 + "," + y1);
					if(this.isLocateAtRightStickBallBoundary(x1, y1)){
						this.captureRightBall = true;
						
						if(this.size < 5.8){
							if(this.width <= 480 & this.height <= 320){
								if((y1-stickBarPointSmallY)>stickBallMotionAreaHeightSmall){  //0805设置向下滑动的最大距离，不超出bar的长度
									y1 = stickBarPointSmallY+stickBallMotionAreaHeightSmall;
								}
								if(y1<stickBarPointTopY){  //0805设置向上滑动的最大距离，不超出bar的长度
									y1 = stickBarPointTopY;
								}
								currentRightSpeed = (int) ((y1-stickBallPointBaseSmallY)/7);
								DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointSmallY((int)y1 -20);
							}else{
								if(this.width > 1100){
									if((y1-stickBarPointxxLargeY)>stickBallMotionAreaHeight){  //0805设置向下滑动的最大距离，不超出bar的长度
										y1 = stickBarPointxxLargeY+stickBallMotionAreaHeight;
									}
									if(y1<stickBarPointTopY){  //0805设置向上滑动的最大距离，不超出bar的长度
										y1 = stickBarPointTopY;
									}
									currentRightSpeed = (int) ((y1-stickBallPointBaseY)/12);
									DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointxxLargeY((int)y1 -20);
								}else{
									if((y1-stickBarPointSmallY)>stickBallMotionAreaHeight){  //0805设置向下滑动的最大距离，不超出bar的长度
										y1 = stickBarPointSmallY+stickBallMotionAreaHeight;
									}
									if(y1<stickBarPointTopY){  //0805设置向上滑动的最大距离，不超出bar的长度
										y1 = stickBarPointTopY;
									}
									currentRightSpeed = (int) ((y1-stickBallPointBaseY)/9);
									DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointY((int)y1 -20);
								}
								
							}
							
						}else{
							if(this.width <= 800 & this.height <= 480){
								if((y1-stickBarPointLargeY)>stickBallMotionAreaHeightLarge){  //0805设置向下滑动的最大距离，不超出bar的长度
									y1 = stickBarPointLargeY+stickBallMotionAreaHeightLarge;
								}
								if(y1<stickBarPointTopY){  //0805设置向上滑动的最大距离，不超出bar的长度
									y1 = stickBarPointTopY;
								}
								currentRightSpeed = (int) ((y1-stickBallPointBaseLargeY)/6);
								DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointLargeY((int)y1 -20);
							}else{
								if((y1-stickBarPointxLargeY)>stickBallMotionAreaHeightxLarge){  //0805设置向下滑动的最大距离，不超出bar的长度
									y1 = stickBarPointxLargeY+stickBallMotionAreaHeightxLarge;
								}
								if(y1<stickBarPointTopY){  //0805设置向上滑动的最大距离，不超出bar的长度
									y1 = stickBarPointTopY;
								}
								currentRightSpeed = (int) ((y1-stickBallPointBasexLargeY)/9);
								DoubleAxisRightControllerSurfaceView.getInstance().setStickBallPointxLargeY((int)y1 -20);
							}
							
						}
						Log.e("currentLeftSpeed", "currentLeftSpeed:" + currentRightSpeed);
						DoubleAxisRightControllerSurfaceView.getInstance().setDirection(currentRightSpeed);
						DoubleAxisRightControllerSurfaceView.getInstance().redraw();
					}
				}
				break;
			default:
				break;
			}
		}
		if(stopLeftSignal ==1 && waitStopState==0){
			//setDirection(0);
			stopLeftSignal = 0;
		}
		if(stopRightSignal ==1 && waitStopState==0){
			
			//DoubleAxisRightControllerSurfaceView.getInstance().setDirection(0);
			stopRightSignal =0 ;
		}
		this.redraw();
		//DoubleAxisRightControllerSurfaceView.getInstance().redraw();
		return true;
	}

	// public boolean isLocateAtRightBoundary(float x, float y){
	// return false;
	// }
	public void setStickBallPointY(int y){
		if(y > stickBallMotionAreaHeight){
			y = stickBallMotionAreaHeight;
		}
		if(y < 0){
			y = 0;
		}
		this.stickBallPointY = y;
	}
	public void setStickBallPointSmallY(int y){
		if(y > stickBallMotionAreaHeightSmall){
			y = stickBallMotionAreaHeightSmall;
		}
		if(y < 0){
			y = 0;
		}
		this.stickBallPointSmallY = y;
	}
	public void setStickBallPointLargeY(int y){
		if(y > stickBallMotionAreaHeightLarge){
			y = stickBallMotionAreaHeightLarge;
		}
		if(y < 0){
			y = 0;
		}
		this.stickBallPointLargeY = y;
	}
	public void setStickBallPointxLargeY(int y){
		if(y > stickBallMotionAreaHeightxLarge){
			y = stickBallMotionAreaHeightxLarge;
		}
		if(y < 0){
			y = 0;
		}
		this.stickBallPointxLargeY = y;
	}
	public void setStickBallPointxxLargeY(int y){
		if(y > stickBallMotionAreaHeightxxLarge){
			y = stickBallMotionAreaHeightxxLarge;
		}
		if(y < 0){
			y = 0;
		}
		this.stickBallPointxxLargeY = y;
	}
	public boolean isLocateAtLeftStickBallBoundary(float x, float y){
		
		int edge = stickBallMotionAreaWidth-stickBallWidth;
		Log.e("isLocateAtLeft", "x:" + x +" ," + "y:" + y  +"," + "edge:" + edge);
		Log.e("isLocateAtLeft", "stickBallMotionAreaWidth:" + stickBallMotionAreaWidth + "stickBallWidth:" + stickBallWidth);
		Log.e("isLocateAtLeftX", "(stickBallPointX-edge):" + (stickBallPointX-edge));
		Log.e("isLocateAtLeftX", "(stickBallPointX+stickBallWidth+edge):" + (stickBallPointX+stickBallWidth+edge));
		Log.e("isLocateAtLeftY", "(stickBallPointY-edge):" + (stickBallPointY-edge));
		Log.e("isLocateAtLeftY", "(stickBallPointY+stickBallHeight+edge):" + (stickBallPointY+stickBallHeight+edge));
		//if (x > (stickBallPointX-edge) && x < (stickBallPointX+stickBallWidth+edge)  && y >(stickBallPointY-edge-40) && y <(stickBallPointY+stickBallHeight*2+edge+10)) {
		if (x > (stickBallPointX-edge) && x < (stickBallPointX+stickBallWidth+edge)  && y > 20 && y < WificarActivity.dip2px(getContext(), 224)) {	
			return true;
		} else {
			return false;
		}
	}
	public boolean isLocateAtRightStickBallBoundary(float x, float y){
		int widthPixel = ImageUtility.getWidth(this.getContext());
		int leftXL = (widthPixel-stickBallWidth);
		int leftXR = (widthPixel);
		
		int edge = stickBallMotionAreaWidth-stickBallWidth;
		Log.e("barnotify", "width:"+widthPixel+", x:"+x+",("+leftXL+","+leftXR+")");
		Log.e("isLocateAtRightStick", "(x,y):"+ "("+x+","+y+")" + "edge:" + edge);
		Log.e("isLocateAtRightStick", "leftXL-edge*4:"+ (leftXL-edge*4));
		Log.e("isLocateAtRightStick", "(leftXR+edge):"+ (leftXR+edge));
		Log.e("isLocateAtRightStick", "stickBallPointY:"+ stickBallPointY + "," + "stickBallHeight:" + stickBallHeight);
		Log.e("isLocateAtRightStick", "(stickBallPointY-edge):"+ (stickBallPointY-edge));
		Log.e("isLocateAtRightStick", "(edge-stickBallPointY):"+ (edge-stickBallPointY));
		Log.e("isLocateAtRightStick", "(stickBallPointY+stickBallHeight+edge):"+ (stickBallPointY+stickBallHeight+edge)*3);
		
		//if (x > (leftXL-edge) && x < (leftXR+edge)  && y>(stickBallPointY-edge)  && y <(stickBallPointY+stickBallHeight+edge)) {  edge-stickBallPointY
		if (x > (leftXL-edge*4) && x < (leftXR+edge)  && y> -40  && y < 300) {
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

	private Runnable leftMovingTask = new Runnable() {
		public void run() {

			if (controlEnable) {
				// if (iLastSpeedL != 0 && iCarSpeedL == 0)
				// wifiCar.moveCommand(WifiCar.GO_DIRECTION.Left, iCarSpeedL);
				// iLastSpeedL = iCarSpeedL;
				if (iCarSpeedL != 0) {
					Log.e("move",
					 "Run left("+controlEnable+"):"+iCarSpeedL);
					try {
						wifiCar.move(WifiCar.LEFT_WHEEL, iCarSpeedL);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					iLastSpeedL = iCarSpeedL;
				}
				if (iCarSpeedL == 0 && iLastSpeedL != 0) {
					try {
						wifiCar.move(WifiCar.LEFT_WHEEL, iCarSpeedL);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					iLastSpeedL = 0;
				}

			}

			handler.postDelayed(this, tStep);
		}
	};
}
