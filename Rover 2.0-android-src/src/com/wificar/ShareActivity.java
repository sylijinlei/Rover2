package com.wificar;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import com.CAR2.R;
import com.wificar.mediaplayer.JNIWificarVideoPlay;
import com.wificar.util.ImageAdapterPhoto;
import com.wificar.util.ImageAdapterVideo;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class ShareActivity extends Activity implements AdapterView.OnItemSelectedListener {
	private static final int VIDEOPHOTO = 0;
	private static final int PHOTO = 1;
	private static final int VIDEO = 2;
	private static String path ;
	private static ShareActivity instance;
	public static ImageAdapterPhoto imageAdapterP;
	public static ImageAdapterVideo imageAdapterV;
	private GridView photo_gridview;
	private GridView video_gridview;
	
	public List<String> video_path;		 //save the videos path
	public List<String> photo_path;
	
	public String [] photo;
	public String [] video;
	private String filePath;
	private String filePathV;
	
	private TextView titlText;
	private Button photo_button;
	private Button video_button;
	
	private long lastPressTime = 0;
	private static final int DOUBLE_PRESS_INTERVAL = 2000;
	private boolean comtoShare = false;
	
	public static ShareActivity getInstance(){
		return instance;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		Log.i("shareActiviry", "on pause");
		if(!comtoShare){
			WificarActivity.getInstance().isNotExit = false;
			WificarActivity.getInstance().onPause();
			WificarActivity.getInstance().finish();
			WificarActivity.getInstance().exitProgrames();
			SplashActivity.getInstance().exit();
			
			//android.os.Process.killProcess(android.os.Process.myPid());
			finish();
			System.exit(0);
		}
		comtoShare = false;
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		
		Log.i("shareActiviry33", "on stop:" +comtoShare);
		comtoShare = false;
		super.onStop();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_gallery);
        if(!SdCardIsExsit()){
        	//没有SDcard
        	Toast.makeText(instance, R.string.wificar_no_sdcard, Toast.LENGTH_LONG).show();
        }else{
        	path = ReadSDPath() + "/CAR 2.0";  // "/DCIM/Camera/"
        	deleIndexVideo();
        	photo_gridview = (GridView) findViewById(R.id.photoGallery);
        	video_gridview = (GridView) findViewById(R.id.videoGallery);
        	
        	loadPhoto();
        	loadVideo();
        	
        	titlText = (TextView) findViewById(R.id.titl);
        	photo_button = (Button) findViewById(R.id.photo);
            photo_button.setOnClickListener(bPListener);
            video_button = (Button) findViewById(R.id.video);
            video_button.setOnClickListener(bVListener);
             
             photo_gridview.setOnItemClickListener(new OnItemClickListener() {

     			public void onItemClick(AdapterView<?> parent, View view,
     					int position, long id) {
     				// TODO Auto-generated method stub
     				comtoShare = true;
     				Intent i = new Intent();
     				i.setClass(ShareActivity.this, ImageGalleryActivity.class);
     				i.putExtra("ImagePath", photo_path.get(position).toString());
     				i.putExtra("position", position);
     				startActivity(i);
     			}
     		});
             video_gridview.setOnItemClickListener(new OnItemClickListener() {

     			public void onItemClick(AdapterView<?> parent, View view,
     					int position, long id) {
     				// TODO Auto-generated method stub
     				comtoShare = true;
     				Intent v = new Intent();
     				v.setClass(ShareActivity.this, VideoGalleryActivity.class);
     				v.putExtra("videoPath", video_path.get(position).toString());
     				v.putExtra("position", position);
     				startActivity(v);
     			}
     		});
        }
    }
    
	public OnClickListener bPListener = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			photo_gridview.setVisibility(0);
			video_gridview.setVisibility(8);
			titlText.setText("Photos");
			titlText.setTextSize(20);
		}
	};
	public OnClickListener bVListener = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			photo_gridview.setVisibility(8);
			video_gridview.setVisibility(0);
			titlText.setText("Videos");
			titlText.setTextSize(20);
		}
	};
	
    //判断是否有Sdcard
    private boolean SdCardIsExsit(){  
	    return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);  
	  }
  //获取SD卡的跟路径
  	private String ReadSDPath(){
  		boolean SDExit=Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
  		if(SDExit){
  			return Environment.getExternalStorageDirectory().toString();
  		}else{
  			return null;
  		}
  	}
    //加载图片
    public void loadPhoto(){
    	photo_gridview.setClipToPadding(true);
    	imageAdapterP = new ImageAdapterPhoto(getApplicationContext());
    	photo_gridview.setAdapter(imageAdapterP);
    	
    	photo_path = getInSDPhotoVideo(PHOTO);
    	photo = photo_path.toArray(new String[photo_path.size()]);
    	
    	getAsyncTaskPhoto();
    	photo_gridview.setOnItemSelectedListener(this); 
    }
    //加载视频
    public void loadVideo(){
    	video_gridview.setClipToPadding(true);
    	imageAdapterV = new ImageAdapterVideo(getApplicationContext());
    	video_gridview.setAdapter(imageAdapterV);
    	
    	video_path = getInSDPhotoVideo(VIDEO);
    	video = video_path.toArray(new String[video_path.size()]);
    	
    	getAsyncTaskVideo();
    	video_gridview.setOnItemSelectedListener(this); 
    	//Log.i("MainActivity", "videosThumb :" + videosThumb);
    }
    /**
	 * 获取SD卡中图片和视频文件的方法实现
	 * @return
	 */
	public static List<String> getInSDPhotoVideo(int i) {
		String current_pathString = "";
		/**
		 *  设定图片所在路径
		 */
		List<String> it_p = new ArrayList<String>();
		if (i == PHOTO)
		{
			current_pathString = path +"/Pictures";
		}else if(i == VIDEO){
			current_pathString = path +"/Videos";
		}
		File f = new File(current_pathString);
		File[] files = f.listFiles();

		/**
		 *  遍历文件，将所有文件存入ArrayList中,这个地方存的还是文件路径
		 */
		for(File file : files){
			if (file.isDirectory()) {
				
			}else {  
				String fileName = file.getName(); 
				if(i == PHOTO){
					if (fileName.endsWith(".jpg")) { 						
						it_p.add(file.getPath());
					}
				}
				if(i == VIDEO){
					if (fileName.endsWith(".avi")) { 						
						it_p.add(file.getPath());
					}
				}
				  
			}
		}
		return it_p;
	}
	
    private void getAsyncTaskPhoto() {
		// TODO Auto-generated method stub
		// 得到横屏时临时存储的数据
		final Object data = getLastNonConfigurationInstance();
		if (data == null) {// 如果没有数据则从新加载
			new AsyncTaskLoadPhoto(ShareActivity.this , photo_path).execute();
		} else {
			final Bitmap[] photos = (Bitmap[]) data;
			if (photos.length == 0) {
				new AsyncTaskLoadPhoto(ShareActivity.this ,photo_path).execute();
			}
			for (Bitmap photo : photos) {
				// addImage(photo);
				imageAdapterP.addPhoto(photo);
				imageAdapterP.notifyDataSetChanged();
			}
		}
	}
    public void getAsyncTaskVideo() {
		// TODO Auto-generated method stub
		// 得到横屏时临时存储的数据
		final Object data = getLastNonConfigurationInstance();
		if (data == null) {// 如果没有数据则从新加载
			new AsyncTaskLoadVideo(ShareActivity.this , video_path).execute();
		} else {
			final Bitmap[] videos = (Bitmap[]) data;
			if (videos.length == 0) {
				new AsyncTaskLoadVideo(ShareActivity.this ,video_path).execute();
			}
			for (Bitmap photo : videos) {
				// addImage(photo);
				imageAdapterV.addPhoto(photo);
				imageAdapterV.notifyDataSetChanged();
			}
		}
	}
    
    
    class AsyncTaskLoadPhoto extends AsyncTask<Object, Bitmap, Object> {
    	private Context context;
		private List<String> photo_lis;
		public AsyncTaskLoadPhoto(Context mContext,
				List<String> path) {
			// TODO Auto-generated constructor stub
			context = mContext;
			photo_lis = path;
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			Bitmap bitmap = null;
			Bitmap newbitmap = null;
			
			int i = 0;
			int sw = dip2px(context, 110);
			int sh = dip2px(context, 82);
			for(i = 0 ; i < photo_lis.size();i++){
				filePath = photo_lis.get(i).toString();
				if(filePath.endsWith(".jpg")){
					//bitmap = BitmapFactory.decodeFile(filePath);
					bitmap =  getImageThumbnail(context, getContentResolver(), filePath);
					if (bitmap != null) {
						// 将原来的位图转换成新的位图
						newbitmap = Bitmap.createScaledBitmap(bitmap, sw, sh, true);
						bitmap.recycle();// 释放内存
					}
				}
				if (newbitmap != null) {
					// 不停的调用onProgressUpdate方法
					publishProgress(newbitmap);
				}
				Log.i("zhang", "i :" + i );
			}
			return null;
		}
		@Override
		protected void onPostExecute(Object result) {
			Log.d("mybug", "异步处理 结束");
			// TODO Auto-generated method stub
			// super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Bitmap... values) {
			for (Bitmap bitmap : values) {
				imageAdapterP.addPhoto(bitmap);
				imageAdapterP.notifyDataSetChanged();
			}
		}

    	
    }
    class AsyncTaskLoadVideo extends AsyncTask<Object, Bitmap, Object> {
    	private Context context;
		private List<String> video_lis;
		public AsyncTaskLoadVideo(Context mContext,
				List<String> path) {
			// TODO Auto-generated constructor stub
			context = mContext;
			video_lis = path;
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			Bitmap bitmap = null;
			Bitmap newbitmap = null;
			
			int i = 0;
			int sw = dip2px(context, 110);
			int sh = dip2px(context, 82);
			for(i = 0 ; i < video_lis.size();i++){
				filePathV = video_lis.get(i).toString();
					Log.i("zhang", "video_lis.get(i).toString():" + video_lis.get(i).toString()+ " " + i);
					//byte[] rgb565Array = ShareActivity.this.mLibVLC.getThumbnail(video_lis.get(i).toString(), 110, 84);
					byte[] rgb565Array = JNIWificarVideoPlay.getVideoSnapshot(filePathV);
					if((rgb565Array == null) || (rgb565Array.length == 0)){
						newbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.video_snapshot1);
					}else{
						 // Get the thumbnail.
						//bitmap = Bitmap.createBitmap(110,84, Config.ARGB_8888);
			           if (rgb565Array == null) // We were not able to create a thumbnail for this item.
			                continue;
			           	bitmap = rgb565ToBitmap(rgb565Array);
			           // bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(rgb565Array));
			            if (bitmap != null) {
							// 将原来的位图转换成新的位图
			            	//Log.e("shareActivity", "bitmap" + addVideoBitMap(bitmapV));
			            	newbitmap = Bitmap.createScaledBitmap(bitmap, sw, sh, true);
							bitmap.recycle();// 释放内存
					}
				}
				
				//if(filePath.endsWith(".mp4") || filePath.endsWith(".3gp") || filePath.endsWith(".mov")){
				//	bitmap = GetThumb.getVideoThumbnail(context, getContentResolver(), filePath);
				//}
				
				if (newbitmap != null) {
					// 不停的调用onProgressUpdate方法
					publishProgress(newbitmap);
				}
				Log.i("zhang", "i :" + i );
			}
			return null;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object) 异步处理结束操作
		 */
		@Override
		protected void onPostExecute(Object result) {
			Log.d("mybug", "异步处理 结束");
			// TODO Auto-generated method stub
			// super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Bitmap... values) {
			/* 将数据放入adapter中，并且通知页面UI刷新 */
			for (Bitmap bitmap : values) {
				// 添加内容到adapter
				imageAdapterV.addPhoto(bitmap);
				// 通知页面UI刷新数据
				imageAdapterV.notifyDataSetChanged();
			}
		}

    	
    }
    public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		
		return (int) (dpValue * (scale/1.0f));
	}
    
    private Bitmap rgb565ToBitmap(byte[] data){	
		Bitmap bitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);		
		ByteBuffer buffer = ByteBuffer.wrap(data);
		bitmap.copyPixelsFromBuffer(buffer); 
		
		return bitmap;
	}
    public static Bitmap getImageThumbnail(Context context, ContentResolver cr, String testImagepath) { 
        // final String testVideopath = "/mnt/sdcard/sidamingbu.mp4"; 
        ContentResolver testcr = context.getContentResolver(); 
        String[] projection = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, }; 
        String whereClause = MediaStore.Images.Media.DATA + " = '" + testImagepath + "'"; 
        Log.e("getVideoThumb", "whereClause :" + whereClause);
        Cursor cursor = testcr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, whereClause, 
                        null, null); 
        int _id = 0; 
        String imagePath = ""; 
        Log.e("getVideoThumb", "cursor :" + cursor);
        if (cursor == null || cursor.getCount() == 0) { 
                return null; 
        } 
        if (cursor.moveToFirst()) { 

                int _idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID); 
                int _dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA); 

                do { 
                        _id = cursor.getInt(_idColumn); 
                        imagePath = cursor.getString(_dataColumn); 
                        System.out.println(_id + " " + imagePath); 
                } while (cursor.moveToNext()); 
        } 
        BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inDither = false; 
        options.inPreferredConfig = Bitmap.Config.RGB_565; 
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, _id, Images.Thumbnails.MICRO_KIND, 
                        options); 
        return bitmap; 
}
    
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.e("MainActivity", "the position :" + position);
	}
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	 @Override
		public void onBackPressed() {
			
				//exit();
	    	String statement = this.getResources().getString(
					R.string.click_again_to_exit_the_program);
			long pressTime = System.currentTimeMillis();

			if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL) {
				
				//exitshare();
				scanSdCard();
				//WificarActivity.getInstance().pause();
				//WificarActivity.getInstance().finish();
				finish();
				System.exit(0);
				
			} else {
				Toast.makeText(this, statement, Toast.LENGTH_SHORT).show();
			}
			lastPressTime = pressTime;
				
		}
	 private void deleIndexVideo(){
			String path1 = ReadSDPath() + "/CAR 2.0/Videos";
			File f = new File(path1);
			if(f.exists()){
				File[] files = f.listFiles();
				/**
				 *  遍历文件，将所有文件存入ArrayList中,这个地方存的还是文件路径
				 */
				for(File file : files){
					if (file.isDirectory()) {
						
					}else {  
						String fileName = file.getName(); 
						
						if (fileName.endsWith(".index")) { 
							File dfile = new File(file.getPath());
							dfile.delete();
							String filePath = file.getPath().substring(0,file.getPath().length()-6);
							Log.i("DeleFile", "filePath :" + filePath);
							File dfile1 = new File(filePath);
							dfile1.delete();
						}
					}
				}
			}
			
		}
	//扫描sdcard
	  	public void scanSdCard()
	  		{
	  			IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);   
	  		    intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);   
	  		    intentFilter.addDataScheme("file");   
	  		    registerReceiver(mReceiver, intentFilter);   
	  		  
//	  		     sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"  
//	  		                + Environment.getExternalStorageDirectory())));  
	  		     
	  		   MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + path}, null, null);
	  		  
	  		}
	  	private final  BroadcastReceiver mReceiver = new BroadcastReceiver() {   
		    // @Override  
		    public void onReceive(Context context, Intent intent) {   
		        if (intent.getAction().equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {   
		            }   
		         else if (intent.getAction().equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {   
		           }   
		       }   
		 };
}
