package com.wificar;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.CAR2.R;
import com.wificar.dialog.DeleteDialog;
import com.wificar.dialog.wifi_not_connect;
import com.wificar.util.GetThumb;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ImageGalleryActivity extends Activity {
	private static ImageGalleryActivity mContext;
	protected static final int SHOW_PROGRESS = 0;
	private PopupWindow mPopupWindow;
	private MyGallery myGallery;
	private String photoPath;
	private int position;
	private List<String> photo_path;
	private List<String> photo_path1;
	
	private TextView photos_count;	
	private Button deleButton;
	private Button shareButton;
	
	private boolean isShowing = false;
	private boolean connectWifi = false;
	
	private File file;
	private Dialog dlg;
	
	public static ImageGalleryActivity getInstance() {
		// TODO Auto-generated method stub
		return mContext;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.my_gallery);
		
		connectWifi = note_Intent(mContext);
		myGallery = (MyGallery)findViewById(R.id.myGallery);
		
		photo_path1 = getInSDPhotoVideo();
		
		Intent intent = getIntent();
		photoPath = intent.getStringExtra("ImagePath");
		position = intent.getIntExtra("position", 0);
		
		myGallery.setAdapter(new ImageAdapter(getApplicationContext() ,photo_path1));
		//myGallery.setAdapter(new ImageAdapter(getApplicationContext() , ShareActivity.getInstance().photo_path));
		myGallery.setSelection(position);
		myGallery.setOnItemSelectedListener(listener);
		
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
public static List<String> getInSDPhotoVideo() {
		
		/**
		 *  设定图片所在路径
		 */
		List<String> it_p = new ArrayList<String>();
		String path = Environment.getExternalStorageDirectory().toString() + "/CAR 2.0/Pictures"; 
		File f = new File(path);
		File[] files = f.listFiles();

		/**
		 *  遍历文件，将所有文件存入ArrayList中,这个地方存的还是文件路径
		 */
		for(File file : files){
			if (file.isDirectory()) {
				
			}else {  
				String fileName = file.getName(); 
				
					if (fileName.endsWith(".jpg")) { 						
						it_p.add(file.getPath());
					}
				}
				  
			}
		return it_p;
	}

	private void reLoadPhoto(int p){
		position = p;
		photoPath = photo_path.get(p).toString();
		photo_path1 = getInSDPhotoVideo();
		myGallery.setAdapter(new ImageAdapter(getApplicationContext() , photo_path1));
		//myGallery.setAdapter(new ImageAdapter(getApplicationContext() , ShareActivity.getInstance().photo_path));
		myGallery.setSelection(position);
		photos_count.setText(position+1 + " of " + ShareActivity.getInstance().photo_path.size());
	}
	//判断是否连接互联网
	public boolean note_Intent(Context context) {
		ConnectivityManager con = (ConnectivityManager) context
		.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = con.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
		// 当前网络不可用
			//Toast.makeText(context.getApplicationContext(), "请先连接Internet！",
			//Toast.LENGTH_SHORT).show();
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
                Gravity.TOP , 0, 5);
        mPopupWindow.update();

        photos_count = (TextView) foot_popunwindwow.findViewById(R.id.photo_counts);
        deleButton = (Button) foot_popunwindwow.findViewById(R.id.delete_button);
        deleButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg = new DeleteDialog(mContext,R.style.DeleteDialog,1);
				 
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
					shareIntent.setAction(Intent.ACTION_SEND);
					shareIntent.setType("image/jpeg"); 
	                //设置主题
					shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share");
	                	//设置内容
					
					 file = new File(photoPath); 
					 
					 ContentValues content = new ContentValues(5);
					 content.put(MediaStore.Images.ImageColumns.TITLE, "Share");
					 content.put(MediaStore.Images.ImageColumns.SIZE, file.length());
					 content.put(MediaStore.Images.ImageColumns.DATE_ADDED,System.currentTimeMillis() / 1000); 
					 content.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
					 content.put(MediaStore.Images.Media.DATA, photoPath);
					 ContentResolver resolver = mContext.getContentResolver();
					 Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, content);
					 
					 if(uri == null){
						 shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file)); 
					 }else{
						 shareIntent.putExtra(Intent.EXTRA_STREAM, uri); 
					 }
					 
					 Log.i("zhang", "the pictrue path : " + photoPath);
					startActivity(Intent.createChooser(shareIntent, "Share"));
				
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
	
	public OnItemSelectedListener listener = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Log.i("ImageGalleryActivity", "the position is :" + arg2);
			position = arg2;
			photoPath = photo_path.get(position).toString();
			Log.i("ImageGalleryActivity", "the photoPath is :" + photoPath);
			if(!isShowing){
				showPopWindow();
			}
			
			photos_count.setText(position+1 + " of " + photo_path.size());
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	public void Delete_photo(){
		Log.i("ImageGalleryActivity", "delete the photoPath is :" + photoPath);
		file = new File(photoPath); 
		if(file.exists()){
			 file.delete();
		 }
		if(position == photo_path.size() - 1){
			 position =0;
			 ShareActivity.getInstance().loadPhoto();
			 reLoadPhoto(position);
		}else{
			ShareActivity.getInstance().loadPhoto();
			reLoadPhoto(position);
		}
		dlg.dismiss();
	}
	class ImageAdapter extends BaseAdapter{
		private Context mContext; 
		private Bitmap bitmap;
		public ImageAdapter(Context applicationContext, List<String> path) {
			// TODO Auto-generated constructor stub
			mContext = applicationContext;
			photo_path = path;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return photo_path.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ImageView imageview = new ImageView(mContext);
			//imageview.setBackgroundColor(0xFF000000);
			imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageview.setLayoutParams(new MyGallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			
			//bitmap = BitmapFactory.decodeFile(photo_path.get(arg0).toString());
			bitmap = GetThumb.getImageThumbnail(mContext, getContentResolver(), photo_path.get(arg0).toString());
			imageview.setImageBitmap(bitmap);
			
			return imageview;

		}
		
	}
	
}
