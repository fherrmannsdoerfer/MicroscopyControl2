package microscopeControl;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import mmcorej.CMMCore;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.border.TitledBorder;

public class LaserControl extends JPanel {
	CMMCore core;
	//LaserPanel lp1;
	//LaserPanel lp2;
	//LaserPanel lp3;
	//LaserPanel lp4;
	ArrayList<LaserPanel> laserPanels = new ArrayList<LaserPanel>();
	int indexOfUVLaser;
	AutomatedUVLaserControl aUVLC;
	//needed to keep track of the number of blinking events per frame
	//to decide whether or not to increase the laser power of the UV laser
	ArrayList<Integer> lastBlinkingEventsPerFrame = new ArrayList<Integer>();
	/**
	 * Create the panel.
	 */
	public LaserControl(MainFrame mf) {
		setPreferredSize(new Dimension(620, 420));
		setMaximumSize(new Dimension(5000, 5000));
		this.core = mf.getCoreObject();
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		//find index of UV laser
		indexOfUVLaser = mf.getIndexOfUVLaser();
		
		//outer box containing all elements
		Box verticalBox = Box.createVerticalBox();
		verticalBox.setBorder(new TitledBorder(null, "Laser Control", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
		
		//box to align all lasers from left to right
		Box horizontalBox = Box.createHorizontalBox();
		
		String[] laserNames = mf.getLaserNames();
		String[] laserWavelengths = mf.getLaserWavelengths();
		
		for (int i =0; i<laserNames.length;i++){
			laserPanels.add(new LaserPanel(mf,laserNames[i],laserWavelengths[i]));
		}
		
		Component horizontalGlue = Box.createHorizontalGlue();
		
		for (int i = 0; i<laserNames.length; i++){
			horizontalBox.add(laserPanels.get(i));
			horizontalBox.add(horizontalGlue);
		}
		
		Box horizontalBox2 = Box.createHorizontalBox();
		aUVLC = new AutomatedUVLaserControl(core);
		horizontalBox2.add(aUVLC);
		horizontalBox2.add(horizontalGlue);
		
		verticalBox.add(horizontalBox);
		verticalBox.add(horizontalBox2);
		add(verticalBox);
	
	}
	
	//whenever a new number of blinking events was estimated
	//it is added to the list. For the first 10 numbers the
	//list builds up. If already 10 values are in the list
	//the first (oldes) is deleted and the current value added
	//to the end of the list. 
	public void addBlinkingNumber(int number){
		if (lastBlinkingEventsPerFrame.size()<10){
			lastBlinkingEventsPerFrame.add(number);
		} 
		else {
			lastBlinkingEventsPerFrame.remove(0);
			lastBlinkingEventsPerFrame.add(number);
			if (aUVLC.isUVControlEnabled()){
				decideToIncreaseLaserPower();
			}
		}
		setBlinkingNumber(number);
	}
	//If the number of blinking events per frame stay below
	//the requested number the intensity of the UV laser is 
	//increased. The number of blinking events is averaged 
	//over 10 frames.
	void decideToIncreaseLaserPower(){
		int sum = 0;
		int count = 0;
		for (int i = 0; i<lastBlinkingEventsPerFrame.size(); i++){
			sum = sum + lastBlinkingEventsPerFrame.get(i);
			count = count + 1;
		}
		try{
			if (sum / count < aUVLC.getRequestedNumberOfBlinkingEvents()){
				laserPanels.get(indexOfUVLaser).increaseLaserPower(.1);
			}
		}
		catch(java.lang.NumberFormatException ne){
			System.err.println("probably no valid value set for the wanted number of blinking events");
		}
	}
	
	void setBlinkingNumber(int number){
		aUVLC.setCurrentNumberOfBlinkingEvents(number);
	}
}
