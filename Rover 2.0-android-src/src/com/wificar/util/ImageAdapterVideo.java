package com.wificar.util;

import java.util.ArrayList;

import com.CAR2.R;
import com.wificar.ShareActivity;
import com.wificar.VideoGalleryActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapterVideo extends BaseAdapter {

	/* 类变量声明 */
	private Context mContext;
	private ArrayList<Bitmap> videos = new ArrayList<Bitmap>();
	LayoutInflater inflater1;
	/**
	 * @param context
	 * 上下文构造函数
	 */
	public ImageAdapterVideo(Context context) {
		mContext = context;
		inflater1 = LayoutInflater.from(mContext);
	}

	// 把图片添加到数组
	public void addPhoto(Bitmap photo) {
		videos.add(photo);
	}

	// 得到图片数量
	public int getCount() {
		return videos.size();
	}

	// 获取对应位置的对象
	public Object getItem(int position) {
		return videos.get(position);
	}

	// 获取ID
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v1 = inflater1.inflate(R.layout.video_view_item, null);
		ImageView imgv = (ImageView) v1.findViewById(R.id.imageView_video_view);
		/* 局部变量声明 */
		ImageView imageView;

		/* 查看缓存是否有我们需要的内容 */
		/*if (convertView == null) {
			imageView = new ImageView(mContext);

		} else {
			imageView = (ImageView) convertView;
		}*/

		/* 图片属性设置 */
		imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		//imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		// 获取图片&装入adapter
		//imageView.setImageBitmap(MainActivity.getInstance().videosThumb.get(position));
		imgv.setImageBitmap(videos.get(position));
		
		return v1;
	}
}
