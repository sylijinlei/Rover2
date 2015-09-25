package com.wificar;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import com.CAR2.R;
import com.wificar.dialog.DeleteDialog;
import com.wificar.dialog.wifi_not_connect;
import com.wificar.mediaplayer.JNIWificarVideoPlay;
import com.wificar.mediaplayer.MediaPlayerActivity;

import android.content.ContentResolver;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.provider.MediaStore.Video.Media;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class VideoGalleryActivity extends Activity {
	private static final int VIDEO = 2;
	private static VideoGalleryActivity mContext = null;
	//public static LibVLC mLibVLC;
	public static VideoAdapter imageAdapterV;
	private PopupWindow mPopupWindow;
	private MyGallery myGallery;
	
	private String videoPath;
	
	private int positionV;
	private int currenPosition;
	public List<String> video_path1;
	private List<String> video_path;
	
	private TextView photos_count;	
	private Button deleButton;
	private Button shareButton;
	
	private boolean isShowing = false;
	private boolean connectWifi = false;
	
	private File file;
	private Dialog dlg;
	
	public static VideoGalleryActivity getInstance() {
		// TODO Auto-generated method stub
		return mContext;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		 if (mPopupWindow != null) {
//        mPopupWindow.dismiss();
//        mPopupWindow = null;
//        Log.d("PopWin", "dismiss ok");
//    }
		Log.i("VideoGalleryActivity", "onPause");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i("VideoGalleryActivity", "onStop");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("VideoGalleryActivity", "onDrestroy");
	}
	
	
	
//	@Override
//	public void onBackPressed() {
//		// TODO Auto-generated method stub
//		 if (mPopupWindow != null) {
//	            mPopupWindow.dismiss();
//	            mPopupWindow = null;
//	            Log.d("PopWin", "dismiss ok");
//	        }
//		 finish();
//		super.onBackPressed();
//		
//	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.i("VideoGalleryActivity", "onRestart");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.my_gallery);
		/* try {
				mLibVLC = LibVLC.getInstance();
			} catch (LibVlcException e) {
				e.printStackTrace();
			}*/
		 
		connectWifi = note_Intent(mContext);
		
		myGallery = (MyGallery)findViewById(R.id.myGallery);
		
		video_path = getInSDPhotoVideo();
		
		imageAdapterV = new VideoAdapter(mContext , video_path);
		//imageAdapterV = new VideoAdapter(mContext , ShareActivity.getInstance().video_path);
		
		Intent intent = getIntent();
		videoPath = intent.getStringExtra("videoPath");
		currenPosition = intent.getIntExtra("position", 0);
		Log.i("VideoGalleryActivity", "the currenPosition :" + currenPosition);
		
		myGallery.setAdapter(imageAdapterV);
    	myGallery.setSelection(currenPosition);
		myGallery.setOnItemSelectedListener(listenerVideo);
		
	}
public static List<String> getInSDPhotoVideo() {
		
		List<String> it_p = new ArrayList<String>();
		String path = Environment.getExternalStorageDirectory().toString() + "/CAR 2.0/Videos"; 
		File f = new File(path);
		File[] files = f.listFiles();

		/**
		 *  遍历文件，将所有文件存入ArrayList中,这个地方存的还是文件路径
		 */
		for(File file : files){
			if (file.isDirectory()) {
				
			}else {  
				String fileName = file.getName(); 
				
					if (fileName.endsWith(".avi")) { 						
						it_p.add(file.getPath());
					}
				}
			}
		return it_p;
	}
	private void loadVideoGallery(int pv){
		positionV = pv;
		videoPath = video_path1.get(pv).toString();
		video_path = getInSDPhotoVideo();
    	//imageAdapterV = new VideoAdapter(mContext , ShareActivity.getInstance().video_path);
    	imageAdapterV = new VideoAdapter(mContext , video_path);
    	myGallery.setAdapter(imageAdapterV);
    	
    	myGallery.setSelection(pv);
		photos_count.setText(positionV+1 + " of " + video_path1.size());
	}
	
	//判断是否连接互联网
	public boolean note_Intent(Context context) {
		ConnectivityManager con = (ConnectivityManager) context
		.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = con.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
		// 当前网络不可用
			return false;
			}
			else{
				return true;
			}
	}
	 public void dismiss() {
	        Log.d("PopWin", "dismiss");
	        if (mPopupWindow != null) {
	            mPopupWindow.dismiss();
	            mPopupWindow = null;
	            Log.d("PopWin", "dismiss ok");
	        }
	    }
	 
	private void showPopWindow(){
		
	    dismiss();
	    isShowing = true;
        View foot_popunwindwow = null;

        LayoutInflater LayoutInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        foot_popunwindwow = LayoutInflater
                .inflate(R.layout.photo_count, null);

        mPopupWindow = new PopupWindow(foot_popunwindwow,
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
       
        mPopupWindow.showAtLocation(findViewById(R.id.layout),
                Gravity.TOP , 10, 5);
        mPopupWindow.update();

        photos_count = (TextView) foot_popunwindwow.findViewById(R.id.photo_counts);
        deleButton = (Button) foot_popunwindwow.findViewById(R.id.delete_button);
        deleButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg = new DeleteDialog(mContext,R.style.DeleteDialog,2);
				 
				 WindowManager m = getWindowManager(); 
			 	 Display d = m.getDefaultDisplay(); //为获取屏幕宽、高 
			 	// dlg = new DeleteDialog(instance);
			 	// dlg.setTitle(" " );
			 	 Window w=dlg.getWindow(); 
				 WindowManager.LayoutParams lp =w.getAttributes(); 
				 
				 w.setGravity(Gravity.RIGHT | Gravity.TOP);
				 lp.x=10; 
				 lp.y=70;
				 lp.height = (int) (d.getHeight() * 0.3); //高度设置为屏幕的0.6 ;
				 //lp.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.95
				 w.setAttributes(lp);
				 dlg.show();
			}
		});
        shareButton = (Button) foot_popunwindwow.findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(connectWifi){
					
					Intent shareIntent =new Intent();
					shareIntent.setAction("android.intent.action.SEND");
					shareIntent.setType("video/*"); 
					file = new File(videoPath); 
					
					ContentValues content = new ContentValues(5);
					content.put(Video.VideoColumns.TITLE, "Share");
					content.put(MediaStore.Video.VideoColumns.SIZE, file.length());
					content.put(Video.VideoColumns.DATE_ADDED,System.currentTimeMillis() / 1000); 
					content.put(Video.Media.MIME_TYPE, "video/avi");
					content.put(MediaStore.Video.Media.DATA, videoPath);
					ContentResolver contentResolver = getContentResolver();
					Uri base = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
					Uri newUri = contentResolver.insert(base, content);
			       
			         Log.i("ShareActivity", " values:" + content);
			         Log.i("ShareActivity", " storeLocation:" + newUri);
					
					if(newUri == null){
						shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file)); 
					}else{
						shareIntent.putExtra(Intent.EXTRA_STREAM, newUri); 
					}
					 
					shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					startActivity(Intent.createChooser(shareIntent, "Share"));
					Log.i("startShare", "start start");
					
				    /*Dialog dlg = new share_photo_dialog(instance,R.style.ShareDialog ,imagePath);
					 
					 WindowManager m = getWindowManager(); 
				 	 Display d = m.getDefaultDisplay(); //为获取屏幕宽、高 
				 	// dlg = new DeleteDialog(instance);
				 	// dlg.setTitle(" " );
				 	 Window w=dlg.getWindow(); 
					 WindowManager.LayoutParams lp =w.getAttributes(); 
					 
					 w.setGravity(Gravity.CENTER);
					//lp.x=10; 
					 //lp.y=70;
					// lp.height = (int) (d.getHeight() * 0.5); //高度设置为屏幕的0.6 ;
					 //lp.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.95
					 w.setAttributes(lp);
					 dlg.show();*/
			}else{
        		wifi_not_connect.createwificonnectDialog(mContext).show();
        	}
			}
		});
	}
	
	public OnItemSelectedListener listenerVideo = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Log.i("VideoGalleryActivity", "the positionV is :" + arg2);
			positionV = arg2;
			videoPath = video_path1.get(positionV).toString();
			if(!isShowing){
				showPopWindow();
			}
			
			photos_count.setText(positionV+1 + " of " + video_path1.size());
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			Log.i("VideoGalleryActivity", "onNothingSelected :" + arg0);
		}
	};
	public void Delete_video() {
		// TODO Auto-generated method stub
		file = new File(videoPath); 
		if(file.exists()){
			 file.delete();
		 }
		if(positionV == video_path1.size() - 1){
			 positionV =0;
			 ShareActivity.getInstance().loadVideo();
			 loadVideoGallery(positionV);
			 
		}else{
			 ShareActivity.getInstance().loadVideo();
			 loadVideoGallery(positionV);
		}
		if (dlg != null && dlg.isShowing())
		{
			dlg.dismiss();
		}
//		dlg.dismiss();
	}
	class VideoAdapter extends BaseAdapter{
		/* 类变量声明 */
		private Context mContext;
		//private ArrayList<Bitmap> videos = new ArrayList<Bitmap>();
		LayoutInflater inflater1;
		/**
		 * @param context
		 * 上下文构造函数
		 */
		public VideoAdapter(Context context) {
			mContext = context;
		}

		public VideoAdapter(VideoGalleryActivity mContext2,
				List<String> path) {
			// TODO Auto-generated constructor stub
			mContext = mContext2;
			video_path1 = path;
			inflater1 = LayoutInflater.from(mContext);
		}

		// 得到图片数量
		public int getCount() {
			return video_path1.size();
		}

		// 获取对应位置的对象
		public Object getItem(int position) {
			return video_path1.get(position);
		}

		// 获取ID
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Bitmap bitmap = null;
			
			ImageView imageview = new ImageView(mContext);
			View v1 = inflater1.inflate(R.layout.video_play_item, null);
			ImageView imgv = (ImageView) v1.findViewById(R.id.imageView_video_play);
			ImageView playBtn = (ImageView) v1.findViewById(R.id.video_play_button);
			playBtn.setOnClickListener(videoPlayListent);
			
			imgv.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageview.setLayoutParams(new MyGallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			
//			byte[] rgb565Array = mLibVLC.getThumbnail(video_path.get(position).toString(), 320, 240);
			byte[] rgb565Array = JNIWificarVideoPlay.getVideoSnapshot(video_path1.get(position).toString());
			if((rgb565Array == null) || (rgb565Array.length == 0)){
				bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.video_snapshot1);
			}else{
				 // Get the thumbnail.
				bitmap = rgb565ToBitmap(rgb565Array);
				//bitmap = Bitmap.createBitmap(320,240, Config.ARGB_8888);
	           // bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(rgb565Array));
			}
			
			imgv.setImageBitmap(bitmap);
			
			return v1;
		}
		
	}
	public OnClickListener videoPlayListent = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.i("zhang", "the video path : " + videoPath);
		    Intent intent = new Intent(VideoGalleryActivity.this, MediaPlayerActivity.class);
		    intent.putExtra("file_name", videoPath);
			intent.putExtra("file_position", positionV);
			VideoGalleryActivity.this.startActivity(intent);
		}
	};
	
	private Bitmap rgb565ToBitmap(byte[] data){	
		//Bitmap bitmap = Bitmap.createBitmap(320,240, Config.ARGB_8888);
		Bitmap bitmap = Bitmap.createBitmap(80, 60, Bitmap.Config.RGB_565);		
		ByteBuffer buffer = ByteBuffer.wrap(data);
		bitmap.copyPixelsFromBuffer(buffer); 
		
		return bitmap;
	}
}
