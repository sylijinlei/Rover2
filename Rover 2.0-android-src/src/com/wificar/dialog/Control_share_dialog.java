package com.wificar.dialog;

import com.CAR2.R;

import com.wificar.WificarActivity;
import com.wificar.util.VideoUtility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Control_share_dialog {
	
	public Control_share_dialog() {
		
	}
	
	public static AlertDialog.Builder createcontrolsharedialog(Context context){
		
		final AlertDialog.Builder shareDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.layout.share_dialog));
		
		//shareDialog.setTitle("Info");
		
		shareDialog.setMessage("The Facebook, Twitter, Tumblr, and YouTube apps must already be installed on your device to share CAR 2.0 photos and videos.\n" +
				"Exit the app, go to Settings and access a Wi-Fi network other than CAR 2.0. Open the CAR 2.0 app and select Share.");
		shareDialog .setCancelable(false);

		shareDialog .setPositiveButton("OK",new DialogInterface.OnClickListener() {
					//@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						//WificarActivity.getInstance().exit();	
					}
				});
		return shareDialog;
	}

}