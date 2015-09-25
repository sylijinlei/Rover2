package com.wificar.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;

public class GetThumb {
	  /** 
     * 通过文件名 获取视频的缩略图 
     *  
     * @param context 
     * @param cr         cr = getContentResolver(); 
     * @param testVideopath  全路径 "/mnt/sdcard/sidamingbu.mp4"; 
     * @return 
     */
    public static Bitmap getVideoThumbnail(Context context, ContentResolver cr, String testVideopath) { 
            // final String testVideopath = "/mnt/sdcard/sidamingbu.mp4"; 
            ContentResolver testcr = context.getContentResolver(); 
            String[] projection = { MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID, }; 
            String whereClause = MediaStore.Video.Media.DATA + " = '" + testVideopath + "'"; 
            Cursor cursor = testcr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, whereClause, 
                            null, null); 
            int _id = 0; 
            String videoPath = ""; 
            if (cursor == null || cursor.getCount() == 0) { 
                    return null; 
            } 
            if (cursor.moveToFirst()) { 

                    int _idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID); 
                    int _dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA); 

                    do { 
                            _id = cursor.getInt(_idColumn); 
                            videoPath = cursor.getString(_dataColumn); 
                            System.out.println(_id + " " + videoPath); 
                    } while (cursor.moveToNext()); 
            } 
            BitmapFactory.Options options = new BitmapFactory.Options(); 
            options.inDither = false; 
            options.inPreferredConfig = Bitmap.Config.ARGB_8888; 
            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, _id, Images.Thumbnails.MINI_KIND, 
                            options); 
            return bitmap; 
    }
    /** 
     * 通过文件名 获相片的缩略图 
     *  
     * @param context 
     * @param cr         cr = getContentResolver(); 
     * @param testVideopath  全路径 "/mnt/sdcard/sidamingbu.jpg"; 
     * @return 
     */
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
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, _id, Images.Thumbnails.MINI_KIND, 
                            options); 
            return bitmap; 
    }
}
