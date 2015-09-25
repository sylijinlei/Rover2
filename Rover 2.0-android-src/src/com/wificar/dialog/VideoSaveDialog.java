package com.wificar.dialog;



import com.CAR2.R;
import com.wificar.WificarActivity;
import com.wificar.component.WifiCar;
import com.wificar.util.VideoUtility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.widget.EditText;

public class VideoSaveDialog{

	/*public VideoSaveDialog() {
		
	}
	public static AlertDialog.Builder createInputFilenameDialog(Context context){
		final AlertDialog.Builder inputDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomDialog));
		
		inputDialog.setTitle(R.string.video_dialog_input_filename_label);
		
		final EditText inputName = new EditText(context);
		inputDialog.setView(inputName);
		//inputDialog.setMessage("請輸入要儲存的檔案名稱");
		inputDialog.setCancelable(false);

		inputDialog.setPositiveButton(R.string.video_dialog_save_label,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {
							// instance.exit();
							String fileName = inputName.getEditableText()
									.toString();
							if (!fileName.equals("")) {
								String newFileName = VideoUtility
										.getVideoFile(fileName);
								WificarActivity.getInstance().openVideoStream(newFileName);
								// openStream(WifiCar.getVideoFolder(context)+"/"+fileName);
								// videoRecordEnable = true;
								//WificarActivity.getInstance()
								//		.getCameraSurfaceView()
								//		.startRecording();
								// cameraSurfaceView.startRecording();
								dialog.cancel();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}).setNegativeButton(R.string.video_dialog_cancel_label,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Action for 'NO' Button
				dialog.cancel();
			}
		});
		return inputDialog;
	}*/

}
