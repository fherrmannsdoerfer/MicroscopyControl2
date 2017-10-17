package microscopeControl;
import mmcorej.CMMCore;

import org.micromanager.api.MMPlugin;
import org.micromanager.api.MultiStagePosition;
import org.micromanager.api.PositionList;
import org.micromanager.api.ScriptInterface;
import org.micromanager.utils.MMScriptException;

import ch.qos.logback.core.net.SyslogOutputStream;


public class MicroscopeControlV2MainClass implements MMPlugin {
	
	//Name that appears in the plugin list
	public static final String menuName = "Microscope Control V2";
	
	//central object to control all hardware
	CMMCore core;
	
	@Override
	public String getCopyright() {
		return "Frank Herrmannsdörfer 2017";
	}

	@Override
	public String getDescription() {
		return "Program to control the complete custom built microscope, consiting of camera, xyz stage, focus lock, lasers and filterwheel";
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		return "Version 0.1";
	}

	@Override
	public void dispose() {
		// code that gets executed when the plugin closes

	}

	//code to start the plugin can be written here
	@Override
	public void setApp(ScriptInterface app) {
		core = app.getMMCore();
		try {
			PositionList list = app.getPositionList();
			MultiStagePosition[] msps = list.getPositions();
			System.out.println(msps.length);
//			msps[0].getX();
			System.out.println(list.toString());
			
		} catch (MMScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MainFrame mf = new MainFrame(core,app);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
	}

}
