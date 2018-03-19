package microscopeControl;

import javax.swing.JOptionPane;

import dataTypes.XYStagePosition;
import mmcorej.CMMCore;
//class to interact with the connected XY stage
public class XYStageWorker {
	MainFrame mf;
	CMMCore core;
	String xyStageName;
	public XYStageWorker(MainFrame mf){
		this.mf = mf;
		core = mf.getCoreObject();
		xyStageName = mf.getXYStageName();
	}
//returns the stage position as a XYStagePosition object. Definition for the XYStagePosition class can be found in the dataTypes package
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
	
	public XYStagePosition getXYStagePositionShifted() {
		XYStagePosition truePos = getXYStagePosition();
		return new XYStagePosition(truePos.getxPos()+mf.getXShift(), truePos.getyPos()+mf.getYShift());
	}
	
	public void moveToShiftedCoordinates(double xPosWrong, double yPosWrong) {
		moveTo(xPosWrong-mf.getXShift(), yPosWrong-mf.getYShift());
	}

	public void moveTo(double xPos, double yPos) {
		try {
			//check if the target coordinates lie within a square with 20 mm edge length centered at the stage origin
			if (Math.abs(xPos) > 10000 || Math.abs(yPos) > 10000){
				JOptionPane.showMessageDialog(null, "Parameters would move the stage too far.");
			}
			//move the stage
			else{
				mf.setAction("Moving Stage");
				core.setXYPosition(xPos, yPos);
				//check constantly if there is a offset between the desired and the current stage position
				//this blocks execution of all other microscope function until the stage is close to 
				//its target position
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
