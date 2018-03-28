package microscopeControl;

import javax.swing.JOptionPane;

import org.apache.commons.math3.util.Precision;

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
				//the smaract stage does not perform for numbers with more than 1 decimal...
				xPos = Precision.round(xPos,1);
				yPos = Precision.round(yPos,1);
				Thread.sleep(100);
				mf.setAction("Moving Stage");
				core.setXYPosition(xPos, yPos);
				//check constantly if there is a offset between the desired and the current stage position
				//this blocks execution of all other microscope function until the stage is close to 
				//its target position
				int counter = 0;
				while (true){ //delay Program until moving has finished
					Thread.sleep(100);
					System.out.println(core.getXPosition(xyStageName) + " "+core.getYPosition(xyStageName));
					if (Math.abs(core.getXPosition(xyStageName) - xPos) < 2 &&Math.abs(core.getYPosition(xyStageName) - yPos) < 2){
						break;
					}
					counter = counter + 1;
					if (counter>30) {
						System.err.println("Stage position was not reached in time");
						break;
					}
				}
				Thread.sleep(50);
				mf.setAction("");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setXYStageSpeed(double speed) {
		try {
			core.setProperty(xyStageName, "Speed", speed);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
