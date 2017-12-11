package microscopeControl;

import javax.swing.JOptionPane;

import dataTypes.XYStagePosition;
import mmcorej.CMMCore;

public class XYStageWorker {
	MainFrame mf;
	CMMCore core;
	String xyStageName;
	public XYStageWorker(MainFrame mf){
		this.mf = mf;
		core = mf.getCoreObject();
		xyStageName = mf.getXYStageName();
	}

	public XYStagePosition getXYStagePosition(){
		try {
			double xPos = core.getXPosition(xyStageName);
			double yPos = core.getYPosition(xyStageName);
			return new XYStagePosition(xPos,yPos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void moveTo(double xPos, double yPos) {
		try {
			if (Math.abs(xPos) > 1000 || Math.abs(yPos) > 1000){
				JOptionPane.showMessageDialog(null, "Parameters would move the stage too far.");
			}
			else{
				mf.setAction("Moving Stage");
				core.setXYPosition(xPos, yPos);
				while (true){ //delay Program until moving has finished
					Thread.sleep(100);
					if (Math.abs(core.getXPosition(xyStageName) - xPos) < 2 &&Math.abs(core.getYPosition(xyStageName) - yPos) < 2){
						break;
					}
				}
				mf.setAction("");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
