package com.wificar.util;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapterPhoto extends BaseAdapter {
	/* 类变量声明 */
	private Context mContext;
	private ArrayList<Bitmap> photos = new ArrayList<Bitmap>();

	/**
	 * @param context
	 * 上下文构造函数
	 */
	public ImageAdapterPhoto(Context context) {
		mContext = context;
	}

	// 把图片添加到数组
	public void addPhoto(Bitmap photo) {
		photos.add(photo);
	}

	// 得到图片数量
	public int getCount() {
		return photos.size();
	}

	// 获取对应位置的对象
	public Object getItem(int position) {
		return photos.get(position);
	}

	// 获取ID
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		/* 局部变量声明 */
		ImageView imageView;

		/* 查看缓存是否有我们需要的内容 */
		if (convertView == null) {
			imageView = new ImageView(mContext);

		} else {
			imageView = (ImageView) convertView;
		}

		/* 图片属性设置 */
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		// 获取图片&装入adapter
		imageView.setImageBitmap(photos.get(position));

		return imageView;
	}
}
