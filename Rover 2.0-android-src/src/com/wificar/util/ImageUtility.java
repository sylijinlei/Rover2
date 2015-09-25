package com.wificar.util;

import com.CAR2.R;
import com.wificar.WificarActivity;


import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;

public class ImageUtility {
	public static Bitmap createBitmap(Resources res, int srcId) {

		Bitmap bitmap = BitmapFactory.decodeResource(res, srcId);
		return bitmap;

	}
	public static Bitmap createBitmap(Resources res, int srcId, Options opt) {

		Bitmap bitmap = BitmapFactory.decodeResource(res, srcId, opt);
		return bitmap;

	}

	public static void createJPEGFile(byte[] buf, ContentResolver cr) {

        try {
            Bitmap snap = BitmapFactory.decodeByteArray(buf, 0, buf.length);
            MediaStore.Images.Media.insertImage(cr, snap, System.currentTimeMillis() + ".jpg", System.currentTimeMillis() + ".jpg");
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
	public static int getWidth(Context context){
		int x = context.getResources().getDisplayMetrics().widthPixels;
		return x;
	}
	public static int getHeight(Context context){
		int y = context.getResources().getDisplayMetrics().heightPixels;
		return y;
	}
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		
		int yPixel = context.getResources().getDisplayMetrics().heightPixels;
		float ydpi = context.getResources().getDisplayMetrics().ydpi;
		
		//Log.d("wificar", "scale:"+context.getResources().getDisplayMetrics());
		//Log.d("wificar", "y scale:"+yPixel/ydpi);
		return (int) (dpValue * (scale/1.0f));
	}
	public static float getDensity(Context context){
		final float scale = context.getResources().getDisplayMetrics().density;
		return scale;
	}
	public static int px2dip(Context context, float pxValue) {
		
		final float scale = context.getResources().getDisplayMetrics().density;
		//Log.d("wificar", "scale:"+context.getResources().getDisplayMetrics());
		
		return (int) ((pxValue / (1.5f/scale)));
	}
	public static int getBatterySection(int value){
		Resources res = WificarActivity.getInstance().getResources();
		if(value<2){
			return 0;
		}
		else if(value<3){
			return 1;
		}
		else if(value<4){
			return 2;
		}
		else if(value<6){
			return 3;
		}
		else {
			return 4;
		}
	}
}
