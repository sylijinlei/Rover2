package com.wificar.mediaplayer;

import java.io.File;
import java.nio.ByteBuffer;
import com.CAR2.R;
import com.wificar.VideoGalleryActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MediaPlayerActivity extends Activity {	
	private final int UPDATEUI_FOR_STOP = 0x100;
	private final int UPDATEUI_CURRENT_TIME = 0x101;
	private SurfaceHolder mSurfaceHolder;
	private String mVideoFileName = null;
	private int mVideoFilePosition = 0;
	private LinearLayout mOverlay;
	private SurfaceView mWindow = null;
	private SurfaceHolder mWindowHolder = null;
	private int mWindowWidth = 0;
	private int mWindowHeight = 0;
	
	private ImageButton mPreviousSongImgBtn = null;
	private ImageButton mPlayImgBtn = null;
	private ImageButton mNextSongImgBtn = null;
	
	private View mSpacer;
	
	private SeekBar mSeekBar = null;
	private TextView mDurationText = null;
	private TextView mCurrentTimeText = null;
	private int mDuration = 0;
	private int mCurrentTime = 0;
	
	private boolean mIsPlaying = false;
	private boolean mIsVideoRender = false;
	private boolean mIsAudioRender = false;
	private boolean mIsVideoEnd = false;
	private boolean mIsAudioEnd = false;
	private boolean mIsFileEnd = false;
	private boolean mIsVideoSwitch = false;
	private boolean mIsStop = true;
	private boolean calledbyother=false;
	private boolean mShowing=false;
	
	private int mVideoWidth = 0;
	private int mVideoHeight = 0;
	private LinearLayout mDecor;
	private Handler mHandler = null;
	float density;
	PowerManager.WakeLock mWakeLock = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /* Full screen display without the task and status bar */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.player);
        
        density = this.getResources().getDisplayMetrics().density;
        
        mDecor = (LinearLayout)findViewById(R.id.player_overlay_decor);
       
        LayoutInflater inflater = (LayoutInflater) getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
        mOverlay = (LinearLayout)inflater.inflate(R.layout.player_overlay, null);
        mWindow = (SurfaceView)findViewById(R.id.window);
        mSurfaceHolder = mWindow.getHolder();	
    	mSurfaceHolder.setKeepScreenOn(true);
    	mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
    	
        mPreviousSongImgBtn = (ImageButton)mOverlay.findViewById(R.id.previous);
        mPlayImgBtn = (ImageButton)mOverlay.findViewById(R.id.play);
        mNextSongImgBtn = (ImageButton)mOverlay.findViewById(R.id.next); 
        
        mSeekBar = (SeekBar)mOverlay.findViewById(R.id.seek_bar);
        mDurationText = (TextView)mOverlay.findViewById(R.id.duration_text);
        mCurrentTimeText = (TextView)mOverlay.findViewById(R.id.current_time_text);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
       
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				
			}

	
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}

	
			public void onStopTrackingTouch(SeekBar seekBar) {
				JNIWificarVideoPlay.playerSeek(seekBar.getProgress() * 1000);
			}
        	
        });
        
        mDecor.addView(mOverlay);
        mShowing=true;
        
        mSpacer = (View)findViewById(R.id.player_overlay_spacer);
		mSpacer.setOnTouchListener(mTouchListener);
        
		if(!(getIntent().getAction() != null&& getIntent().getAction().equals(Intent.ACTION_VIEW ))){
			 mVideoFileName = getIntent().getExtras().getString("file_name");
			 mVideoFilePosition = getIntent().getExtras().getInt("file_position");
		}
       
        implSurfaceHolderCallback();
        playControl();
        
        mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case UPDATEUI_FOR_STOP:	
					Log.e("MediaPlayer", "UPDATEUI_FOR_STOP");
					mIsAudioRender = false;
					mIsVideoRender = false;	
					mIsPlaying = false;
					mIsStop = true;					
					
					mPlayImgBtn.setBackgroundResource(R.drawable.ic_play);
					
					mCurrentTime = 0;
					mCurrentTimeText.setText(timeFormat(mCurrentTime));
					mSeekBar.setProgress(mCurrentTime);	
					
					JNIWificarVideoPlay.playerStop();
					
					/*if(calledbyother)*/ finish();
		    		/*else{
		    			mIsVideoRender = false;
				    	mIsAudioRender = false;
						mIsPlaying = false;
						
						mIsVideoEnd = true;
						mIsAudioEnd = true;					
						mIsVideoSwitch = true;
						
						mPlayImgBtn.setBackgroundResource(R.drawable.ic_play);					
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						if(mVideoFilePosition < FileListActivity.mFilesNameArray.length - 1){
							mVideoFilePosition = mVideoFilePosition + 1;
							mVideoFileName = FileListActivity.mFileDirectory + 
									         FileListActivity.mFilesNameArray[mVideoFilePosition];
							if(mVideoFileName == null){
								return;
							}
							if(initPlayerStart() < 0){
								return;
							}
						}
						
						
						mPlayImgBtn.setBackgroundResource(R.drawable.ic_pause);

		    		}*/
					
					
					break;
					
				case UPDATEUI_CURRENT_TIME:
					if(mCurrentTime < 0){
						mCurrentTime = 0;
					}
					mCurrentTimeText.setText(timeFormat(mCurrentTime));
					mSeekBar.setProgress(mCurrentTime);		
					//Log.e("tieme","ctime"+mCurrentTime);
					break;
				}
				super.handleMessage(msg);
			}
        };
        
       
   
    } 
    @Override
	protected void onResume() {
    	/*
    	 * if the video is playing, keep the screen light always.
    	 */
    	if(mWakeLock == null){
    		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE); 
	        mWakeLock = pm.newWakeLock(pm.SCREEN_DIM_WAKE_LOCK, "media player wakelook"); 
	        mWakeLock.acquire();
    	}
    	
		super.onResume();
	}
    
	@Override
	protected void onPause() {	
		mIsAudioRender = false;
		mIsVideoRender = false;  
		
		while(!mIsVideoEnd && !mIsAudioEnd)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		JNIWificarVideoPlay.playerStop();
		
		mIsStop = true;  	
		mIsPlaying = false;
				
		/*
		 * if the activity is background, you should release 
		 * the wake lock, make the screen can enter wake mode,
		 * it's benefit for power.
		 */
		if(mWakeLock != null && mWakeLock.isHeld()){
    		mWakeLock.release();
    		mWakeLock = null;
        }
        finish();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mIsAudioRender = false;
    	mIsVideoRender = false;
		mIsPlaying = false;
		mIsStop = true;
		mIsVideoSwitch = false;

		mIsFileEnd = true;
		Log.e("MediaPlayer", "onDestroy");
		 finish();
		super.onDestroy();
	}

	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		mIsAudioRender = false;
//    	mIsVideoRender = false;
//		mIsPlaying = false;
//		mIsStop = true;
//		mIsVideoSwitch = false;
//
//		mIsFileEnd = true;
//		try {
//			Thread.sleep(50);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		MediaPlayerActivity.this.finish();
		
//		mIsAudioRender = false;
//		mIsVideoRender = false;  
//		
//		while(!mIsVideoEnd && !mIsAudioEnd)
//		{
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		JNIWificarVideoPlay.playerStop();
//		
//		mIsStop = true;  	
//		mIsPlaying = false;
//				
//		/*
//		 * if the activity is background, you should release 
//		 * the wake lock, make the screen can enter wake mode,
//		 * it's benefit for power.
//		 */
//		if(mWakeLock != null && mWakeLock.isHeld()){
//    		mWakeLock.release();
//    		mWakeLock = null;
//        }
//		
//		mIsFileEnd = true;
//		
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		mIsAudioEnd = true;
//		mIsVideoEnd = true;
//		if(mIsAudioEnd && mIsVideoEnd){
//			Message message = new Message();
//			message.what = MediaPlayerActivity.this.UPDATEUI_FOR_STOP;
//			mHandler.sendMessage(message);
//		}
		new playStopListenThread().start();
////		JNIWificarVideoPlay.playerResume();
//		try {
//		Thread.sleep(1000);
//	} catch (InterruptedException e) {
//		e.printStackTrace();
//	}
//		JNIWificarVideoPlay.playerStop();
		mIsAudioRender = false;
    	mIsVideoRender = false;
		mIsPlaying = false;
		mIsStop = true;
		mIsVideoSwitch = false;

		mIsFileEnd = true;
		Log.e("MediaPlayer", "onBackPressed");
		super.onBackPressed();
	}



//	@Override
//	protected void onStop() {
//		// TODO Auto-generated method stub
//		Log.e("MediaPlayer", "onStop");
//		mIsAudioRender = false;
//    	mIsVideoRender = false;
//		mIsPlaying = false;
//		mIsStop = true;
//		mIsVideoSwitch = false;
//
//		mIsFileEnd = true;
//		super.onStop();
//	}



	private OnTouchListener mTouchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!mShowing) {
                	 mDecor.addView(mOverlay);
                	 mShowing=true;
                } else {
                	 mDecor.removeView(mOverlay);
                	 mShowing=false;
                }
            }
            return false;
        }
    };
    private void implSurfaceHolderCallback(){
    	mWindowHolder = mWindow.getHolder();
        mWindowHolder.addCallback(new Callback(){
        	
			public void surfaceCreated(SurfaceHolder holder){  
        		/*Get the playing window size , and adjust the height, make 
        		 * the video can fit the whole window, else because of the 
        		 * bitmap scale will keep the aspect of the origin video, 
        		 * so the playing window bottom has a black bar.Perhaps you
        		 * need to change it according the screen size.
        		 */
        		mWindowWidth = mWindow.getWidth();
        		mWindowHeight = mWindow.getHeight();
        		
        		if(initPlayerStart() < 0){
        			return;
        		}
			}
        	
			
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				
			}
			
			
			public void surfaceDestroyed(SurfaceHolder holder){
				mIsVideoSwitch = true;
			}
        	
        });
    }
    
    private void playControl(){
    	setPlayButtonControl();
    	setPreviousButtonControl();
    	setNextButtonControl();
    }
    
    private int initPlayerStart(){    
    	mIsVideoRender = false;
    	mIsAudioRender = false;
		mIsPlaying = false;
		mIsStop = true;
		
		mIsVideoEnd = false;
		mIsAudioEnd = false;
		mIsFileEnd = false;
    	
		if (getIntent().getAction() != null&& getIntent().getAction().equals(Intent.ACTION_VIEW )) {
            /* Started from external application */
        	//mVideoFileName = getIntent().getData().getPath();
			mVideoFileName = getRealPath(getIntent().getData());
			
			/*File f =new File(mVideoFileName);
            String fileName=f.getName();
            String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
            
            if(!prefix.equals("avi")){
            	  AlertDialog.Builder builder = new Builder(MediaPlayerActivity.this);
            	  builder.setMessage("Sorry!We don't support this video(."+prefix+").");
            	  builder.setTitle("Warning");
            	  builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            	   public void onClick(DialogInterface dialog, int which) {
            	       dialog.dismiss();
            	       finish();
            	   }
            	  });            	 

            	  builder.create().show();


            	return 0;
            }*/

            mPreviousSongImgBtn.setVisibility(View.INVISIBLE);
            mPreviousSongImgBtn.setClickable(false);
        	mNextSongImgBtn.setVisibility(View.INVISIBLE);
        	mNextSongImgBtn.setClickable(false);
        	calledbyother=true;
        	
        }
		
    	if(JNIWificarVideoPlay.playerInit(mVideoFileName) < 0){
			System.out.println("init player failed");
			return -1;
		}
		
		mVideoWidth = JNIWificarVideoPlay.getVideoWidth();
		mVideoHeight = JNIWificarVideoPlay.getVideoHeight();
		//Log.e("initPlayerstart","video size width is " +  mVideoWidth + " height is " + mVideoHeight);
		
		changeSurfaceSize();
		
		
		
		if(JNIWificarVideoPlay.playerStart() < 0){
			System.out.println("start player failed");
			return -1;
		}
		
		mDuration = JNIWificarVideoPlay.getDuration();
		mDurationText.setText(timeFormat(mDuration));
		mSeekBar.setMax(mDuration);

		mIsVideoRender = true;
		mIsAudioRender = true;
		mIsPlaying = true;
		mIsStop = false;
		mIsVideoSwitch = false;
		new videoRenderThread().start();
		new audioRenderThread().start();
		new playStopListenThread().start();
		new UpdateCurrentTimeThread().start();
    		
    	return 0;
    }
    
    private void setPlayButtonControl(){
    	mPlayImgBtn.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v) {
				mPlayImgBtn.setClickable(false);		
				switch(v.getId()){
				case R.id.play:	
					/**
					 * only the file is end, and the buffered video and audio is flush, and
					 * the player state is at pause, when press the play button we can restart
					 * the player.
					 */
					if(mIsFileEnd && mIsAudioEnd && mIsVideoEnd && !mIsPlaying){
						mVideoFileName = VideoGalleryActivity.getInstance().video_path1.get(mVideoFilePosition);
						if(mVideoFileName == null){
							break;
						}
						
						if(initPlayerStart() < 0){
							break;
						}
						
						mPlayImgBtn.setBackgroundResource(R.drawable.ic_pause);
						
						break;
					}
					
					/**
					 * normal playing state, easily pause player
					 */
					if(mIsPlaying){					
						JNIWificarVideoPlay.playerPause();
						mPlayImgBtn.setBackgroundResource(R.drawable.ic_play);
						mIsPlaying = false;
						break;
					}
					
					/**
					 * the player is at pause state is that the buffered
					 * audio or video is not end.
					 */
					if(!mIsPlaying && ((mIsAudioEnd != true) || (mIsVideoEnd != true))){
						JNIWificarVideoPlay.playerResume();
						mPlayImgBtn.setBackgroundResource(R.drawable.ic_pause);
						mIsPlaying = true;
						break;
					}
				}
				mPlayImgBtn.setClickable(true);
			}	
    	});
    }

    private void setPreviousButtonControl(){
    	mPreviousSongImgBtn.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v) {
				mPreviousSongImgBtn.setClickable(false);
				switch(v.getId()){
				case R.id.previous:
					mIsVideoRender = false;
			    	mIsAudioRender = false;
					mIsPlaying = false;	
					
					mIsVideoEnd = true;
					mIsAudioEnd = true;					
					mIsVideoSwitch = true;
					
					mPlayImgBtn.setBackgroundResource(R.drawable.ic_play);
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if(mVideoFilePosition > 0){
						mVideoFilePosition = mVideoFilePosition -1;
						mVideoFileName = VideoGalleryActivity.getInstance().video_path1.get(mVideoFilePosition);
						if(mVideoFileName == null){
							break;
						}
					}
					else{
						mVideoFilePosition = VideoGalleryActivity.getInstance().video_path1.size() - 1;
						mVideoFileName = VideoGalleryActivity.getInstance().video_path1.get(mVideoFilePosition);
						if(mVideoFileName == null){
							break;
						}
					}
					
					if(initPlayerStart() < 0){
						break;
					}
					
					mPlayImgBtn.setBackgroundResource(R.drawable.ic_pause);
					
					break;
				}
				mPreviousSongImgBtn.setClickable(true);
			}	
			
    	});
    }
    
    private void setNextButtonControl(){
    	mNextSongImgBtn.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v) {
				mNextSongImgBtn.setClickable(false);
				switch(v.getId()){
				case R.id.next:
					mIsVideoRender = false;
			    	mIsAudioRender = false;
					mIsPlaying = false;
					
					mIsVideoEnd = true;
					mIsAudioEnd = true;					
					mIsVideoSwitch = true;
					
					mPlayImgBtn.setBackgroundResource(R.drawable.ic_play);					
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if(mVideoFilePosition < VideoGalleryActivity.getInstance().video_path1.size() - 1){
						mVideoFilePosition = mVideoFilePosition + 1;
						mVideoFileName = VideoGalleryActivity.getInstance().video_path1.get(mVideoFilePosition);
						if(mVideoFileName == null){
							break;
						}
					}
					else{
						mVideoFilePosition = 0;
						mVideoFileName = VideoGalleryActivity.getInstance().video_path1.get(mVideoFilePosition);
						if(mVideoFileName == null){
							break;
						}
					}
					
					if(initPlayerStart() < 0){
						break;
					}

					mPlayImgBtn.setBackgroundResource(R.drawable.ic_pause);
					
					break;
				}
				mNextSongImgBtn.setClickable(true);
			}	
    	});
    }
    
    private AudioTrack createPlayer(int samplerate, int channels, int format){
    	AudioTrack audioPlayer = null;
    	
    	int chls = AudioFormat.CHANNEL_OUT_STEREO;
    	int fmt = AudioFormat.ENCODING_DEFAULT;

		switch(channels){
		case 1:
			chls = AudioFormat.CHANNEL_OUT_MONO;
			break;
		case 2:
			chls = AudioFormat.CHANNEL_OUT_STEREO;
			break;
		default:
			break;
		}
		
		switch(format){
		case 0:
			fmt = AudioFormat.ENCODING_PCM_8BIT;
			break;
		case 1:
			fmt = AudioFormat.ENCODING_PCM_16BIT;
			break;
		default:
			fmt = AudioFormat.ENCODING_INVALID;
			break;
		}
		
		if(fmt == AudioFormat.ENCODING_INVALID){
			audioPlayer = null;
			return audioPlayer;
		}
			
			int minBufSize = AudioTrack.getMinBufferSize(samplerate, chls, fmt);
			audioPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, samplerate, chls,
										 fmt, minBufSize, AudioTrack.MODE_STREAM);
		return audioPlayer;	
	}
    
    private Bitmap rgb565ToBitmap(byte[] data){	
		/**
		 * Convert rgb565 data to bitmap
		 */
		Bitmap bitmap = Bitmap.createBitmap(mVideoWidth, mVideoHeight, Bitmap.Config.RGB_565);		
		ByteBuffer buffer = ByteBuffer.wrap(data);
		bitmap.copyPixelsFromBuffer(buffer); 
		
		return bitmap;
	}
    
    public void showBitMap(Bitmap bm, Canvas canvas){
    	float scaleWidth = 0;
    	float scaleHeight = 0;
    	if(bm == null){
    		return;
    	}
    	
		// get bitmap's width and height 
		int width = bm.getWidth();  
		int height = bm.getHeight();
		
		Bitmap newbm = null;
		if((mWindowWidth != width) || (mWindowHeight != height)){
			// calculate the scaling factors  
			if(mWindowWidth>=1024 & density>=1.0){
				scaleWidth = ((float)mWindowWidth/(density*1.3f))/((float)width);  
				scaleHeight = ((float)mWindowHeight/(density*1.3f))/((float)height); 
			}
			else if(density<1.0){
				scaleWidth = ((float)mWindowWidth/(density*2.4f))/((float)width);  
				scaleHeight = ((float)mWindowHeight/(density*2.4f))/((float)height); 
			}
			else if(mWindowWidth<1024 & density>=1.0){
				scaleWidth = ((float)mWindowWidth/(density*1.3f))/((float)width);  
				scaleHeight = ((float)mWindowHeight/(density*1.3f))/((float)height); 
			}
			else{
				scaleWidth = ((float)mWindowWidth/(density*1.3f))/((float)width);  
				scaleHeight = ((float)mWindowHeight/(density*1.3f))/((float)height); 
			}
			//Log.e("showbitmap", "width=" + width + " " + "heigth= "+ height);
			//Log.e("showbitmap","mWindowWidth="+mWindowWidth+"mWindowHeight="+mWindowHeight+"density="+density+"surfwid"+mWindow.getWidth());
			// create transform matrix 
			Matrix matrix = new Matrix();  
			matrix.postScale(scaleWidth, scaleHeight);  
			//Log.e("showbitmap","scwid="+scaleWidth+"schei="+scaleHeight);
			// create the new bitmap based on the old one and transform matrix.  
			newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
			//Log.e("showbitmap","bmpWidth="+width+"bmpHeight="+height+newbm.getWidth()+newbm.getHeight());
			bm.recycle();
		}else{
			newbm = bm;
		}
		
		try{ 
			synchronized(mWindowHolder){
				canvas = mWindowHolder.lockCanvas();
			} 
		}finally{
			if(canvas != null){
				canvas.drawARGB(0, 0, 0, 0);
				canvas.drawBitmap(newbm, 0, 0, null);
				mWindowHolder.unlockCanvasAndPost(canvas);					
			}  
		}
		
		newbm.recycle();
	}

    class videoRenderThread extends Thread{
    	@Override
		public void run() {
    		Canvas drawCanvas = null;    
    		
    		while(mIsVideoRender){
    			if(mIsPlaying){    				
	    			byte[] frame = JNIWificarVideoPlay.videoRender();	    			
	    			if((frame == null) || (frame.length == 0)){	    				
	    				try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	    				
	    				if(mIsFileEnd){
							mIsVideoEnd = true;
						}
	    				
	    				continue;
	    			}
	    			
	    			Bitmap bm = rgb565ToBitmap(frame); 
	    			showBitMap(bm, drawCanvas);	    			
    			}else{
    				try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    				continue;
    			}
    		} 
    		
    		mIsVideoEnd = true;
    		
		}    	
    }
    
    class audioRenderThread extends Thread{
		@Override
		public void run() {
			byte[] sampleRateArray = new byte[4];
			int sampleRate = 0;
			byte[] channelsArray = new byte[4];
			int channles = 0;
			byte[] formatArray = new byte[4];
			int format = 0;
			
			AudioTrack audioPlayer = null;
			
			while(mIsAudioRender){
				if(mIsPlaying){
					byte[] buf = JNIWificarVideoPlay.audioRender(sampleRateArray, sampleRateArray.length,
											 channelsArray, channelsArray.length,
											 formatArray, formatArray.length);
					
					if((buf == null) || (buf.length == 0)){						
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						if(mIsFileEnd){
							mIsAudioEnd = true;
						}
						
						continue;
					}
					
					if(audioPlayer == null){
						sampleRate = byteArray2Int(sampleRateArray);
						channles = byteArray2Int(channelsArray);
						format = byteArray2Int(formatArray);
						
						audioPlayer = createPlayer(sampleRate, channles, format);
						if(audioPlayer != null){
							audioPlayer.play();
						}else{
							continue;
						}
					}
					
					audioPlayer.write(buf, 0, buf.length);
				}else{
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
			}
						
			audioPlayer.flush();
			audioPlayer.stop();
			audioPlayer.release();
			audioPlayer = null;
			
			mIsAudioEnd = true;
			
		}
    }
    
    class playStopListenThread extends Thread{
		@Override
		public void run() {
			while(true){
				if(JNIWificarVideoPlay.playerIsStop() == 0){	
					if(mIsStop || mIsVideoSwitch){
						break;
					}
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					continue;
				}else{
					mIsFileEnd = true;
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if(mIsAudioEnd && mIsVideoEnd){
						Message message = new Message();
						message.what = MediaPlayerActivity.this.UPDATEUI_FOR_STOP;
						mHandler.sendMessage(message);
						break;
					}else{
						continue;
					}
				}
			}
		}
    }
    
    class UpdateCurrentTimeThread extends Thread{
		@Override
		public void run() {
			while(!mIsStop){
				if(mIsVideoSwitch){
					break;
				}
				
				mCurrentTime = JNIWificarVideoPlay.getCurrentTime();
				
				Message message = new Message();
				message.what = MediaPlayerActivity.this.UPDATEUI_CURRENT_TIME;
				mHandler.sendMessage(message);
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
    }
    
    private int byteArray2Int(byte[] data){
    	int value = 0;
    	value = (data[0] & 0xff) | ((data[1] & 0xff) << 8) |
    			((data[2] & 0xff) << 16) | ((data[3] & 0xff) << 24);
    	
    	return value;
    }
    
    /**
     * 
     * @param seconds
     * @return : format the seconds as, hour:minute:seconds
     */
    private String timeFormat(int seconds){
    	String timeStr = null;
    	String fmtminute = null;
    	String fmtsecond = null;
    	
    	int hour = (int)(seconds / 3600);
    	int minute = (int)((seconds - hour * 3600) / 60);
    	int second = seconds - hour * 3600 - minute * 60;
    	
    	if(minute < 10) fmtminute = "0"+String.valueOf(minute);
    	else fmtminute = String.valueOf(minute);
    	
    	if(second < 10 ) fmtsecond = "0"+String.valueOf(second);
    	else fmtsecond = String.valueOf(second);
    	
    	timeStr = String.valueOf(hour) + ":" + fmtminute + ":" + fmtsecond;
    	
    	return timeStr;
    }
    
    private void changeSurfaceSize() {
		// get screen size
		int dw = getWindowManager().getDefaultDisplay().getWidth();
		int dh = getWindowManager().getDefaultDisplay().getHeight();
		//Log.e("changsurface init","dw="+dw+"dh="+dh+"mVideoWidth"+mVideoWidth+"mVideoHeight"+mVideoHeight);		
		// calculate aspect ratio
		double ar = (double)mVideoWidth / (double)mVideoHeight;
		// calculate display aspect ratio
		double dar = (double)dw / (double)dh;
		
		
		dw = (int) (dh * ar);
		
		//Log.e("changsurface","dw="+dw+"dh="+dh+"ar="+ar+"dar="+dar);
		mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
		LayoutParams lp = mWindow.getLayoutParams();
		lp.width = dw;
		lp.height = dh;
		mWindowHeight=dh;
		mWindowWidth=dw;
		//Log.e("changsurface","mWindowWidth="+mWindowWidth+"mWindowHeight="+mWindowHeight);
		mWindow.setLayoutParams(lp);
		mWindow.invalidate();
	}
  //获取真正的文件路径
		private String getRealPath(Uri fileUrl){
			String fileName = null;
			Uri filePathUri = fileUrl;
			if(fileUrl!= null){
			if (fileUrl.getScheme().toString().compareTo("content")==0) //content://开头的uri
			{ 
				Cursor cursor = getApplicationContext().getContentResolver().query(fileUrl, null, null, null, null);
			if (cursor != null && cursor.moveToFirst())
			{
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
				fileName = cursor.getString(column_index); //取出文件路径
				Log.e("VideoPlayerActivity", "the content to path :" + fileName);
				cursor.close();
			}
			}else if (fileUrl.getScheme().compareTo("file")==0) //file:///开头的uri
			{
				fileName = filePathUri.toString();
				fileName = filePathUri.toString().replace("file://", "");
				}
			}
			Log.e("videoPlayerActivty", "the realPath:" + fileName);
				return fileName;
			}
}