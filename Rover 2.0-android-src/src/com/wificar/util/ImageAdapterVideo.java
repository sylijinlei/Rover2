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

	/* ��������� */
	private Context mContext;
	private ArrayList<Bitmap> videos = new ArrayList<Bitmap>();
	LayoutInflater inflater1;
	/**
	 * @param context
	 * �����Ĺ��캯��
	 */
	public ImageAdapterVideo(Context context) {
		mContext = context;
		inflater1 = LayoutInflater.from(mContext);
	}

	// ��ͼƬ��ӵ�����
	public void addPhoto(Bitmap photo) {
		videos.add(photo);
	}

	// �õ�ͼƬ����
	public int getCount() {
		return videos.size();
	}

	// ��ȡ��Ӧλ�õĶ���
	public Object getItem(int position) {
		return videos.get(position);
	}

	// ��ȡID
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v1 = inflater1.inflate(R.layout.video_view_item, null);
		ImageView imgv = (ImageView) v1.findViewById(R.id.imageView_video_view);
		/* �ֲ��������� */
		ImageView imageView;

		/* �鿴�����Ƿ���������Ҫ������ */
		/*if (convertView == null) {
			imageView = new ImageView(mContext);

		} else {
			imageView = (ImageView) convertView;
		}*/

		/* ͼƬ�������� */
		imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		//imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		// ��ȡͼƬ&װ��adapter
		//imageView.setImageBitmap(MainActivity.getInstance().videosThumb.get(position));
		imgv.setImageBitmap(videos.get(position));
		
		return v1;
	}
}
