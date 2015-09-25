package com.wificar.util;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapterPhoto extends BaseAdapter {
	/* ��������� */
	private Context mContext;
	private ArrayList<Bitmap> photos = new ArrayList<Bitmap>();

	/**
	 * @param context
	 * �����Ĺ��캯��
	 */
	public ImageAdapterPhoto(Context context) {
		mContext = context;
	}

	// ��ͼƬ��ӵ�����
	public void addPhoto(Bitmap photo) {
		photos.add(photo);
	}

	// �õ�ͼƬ����
	public int getCount() {
		return photos.size();
	}

	// ��ȡ��Ӧλ�õĶ���
	public Object getItem(int position) {
		return photos.get(position);
	}

	// ��ȡID
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		/* �ֲ��������� */
		ImageView imageView;

		/* �鿴�����Ƿ���������Ҫ������ */
		if (convertView == null) {
			imageView = new ImageView(mContext);

		} else {
			imageView = (ImageView) convertView;
		}

		/* ͼƬ�������� */
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		// ��ȡͼƬ&װ��adapter
		imageView.setImageBitmap(photos.get(position));

		return imageView;
	}
}
