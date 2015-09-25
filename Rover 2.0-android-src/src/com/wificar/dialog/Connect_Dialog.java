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

public class Connect_Dialog {
	public Connect_Dialog() {
		
	}
	public static AlertDialog.Builder createconnectDialog(Context context){
		final AlertDialog.Builder connectDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.layout.share_dialog));
	
		connectDialog.setTitle("Connection Status ");
		
		connectDialog.setMessage("CAR 2.0 is not connected, press exit to check your Wi-Fi connection or " +
				"press Share to share CAR 2.0 photos and videos");
		connectDialog .setCancelable(false);

		connectDialog .setPositiveButton("Exit",new DialogInterface.OnClickListener() {
					//@Override
					public void onClick(DialogInterface dialog, int id) {
						
						WificarActivity.getInstance().exit();	
						dialog.cancel();
						//WificarActivity.getInstance().exitProgrames();
					}
				}).setNegativeButton("Share",new DialogInterface.OnClickListener() {
			//@Override
			public void onClick(DialogInterface dialog, int id) {
				// Action for 'NO' Button 
				
				WificarActivity.getInstance().share();
			}
		});
		return connectDialog;
	}

	
}