package com.wificar.surface;

import com.wificar.component.WifiCar;

public interface ControllerInterface {
	public void initial() ;
	public void setWifiCar(WifiCar wifiCar);
	public void disableControl();
	public void enableControl() ;
	public void destroyDrawingCache();
	public void setZOrderOnTop(boolean bool);
}
