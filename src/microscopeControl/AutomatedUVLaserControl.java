package microscopeControl;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import mmcorej.CMMCore;

//Creates the panel to turn the automated UV laser power increment on
//this class only provides the interface to couple the power of the UV laser
//to the number of currently blinking fluorophores per frame. The order to do so and
//the methods to decide whether or not to increase the laser power are located
//in the LaserControl class
public class AutomatedUVLaserControl extends JPanel {
	JTextField textTargetBlinkingEventsPerFrame;
	JLabel lblBlinkingEventsPerFrame;
	CMMCore core;
	JCheckBox chkboxEnableUVControl;
	
	int counter = 0;
	ArrayList<Integer> lastBlinkingEventsPerFrame = new ArrayList<Integer>(Arrays.asList(0,0,0,0,0,0,0,0,0,0));
	
	public AutomatedUVLaserControl(CMMCore core){
		this.core = core;
		
		//use FlowLayout to align the two columns horizontally
		this.setLayout(new FlowLayout(FlowLayout.LEADING,20,0));
		
		//Create two columns
		JPanel leftColumn = new JPanel();
		JPanel rightColumn = new JPanel();
		
		//each with a 3 by 1 Grid to have a constant width
		leftColumn.setLayout(new GridLayout(3,1));
		rightColumn.setLayout(new GridLayout(3,1));
		
		chkboxEnableUVControl = new JCheckBox("Enable UV Laser Control");
		leftColumn.add(chkboxEnableUVControl);
		
		//add empty label to create a void in the upper right corner
		rightColumn.add(new JLabel(""));
		leftColumn.add(new JLabel("Minimal # of blinking events per frame:"));
		
		textTargetBlinkingEventsPerFrame = new JTextField();
		Utility.setFormatTextFields(textTargetBlinkingEventsPerFrame, 20, 16, 3);
		rightColumn.add(textTargetBlinkingEventsPerFrame);
		
		leftColumn.add(new JLabel("Current blinking events per frame:"));
		rightColumn.add(lblBlinkingEventsPerFrame = new JLabel("New label"));
		
		this.add(leftColumn);
		this.add(rightColumn);
		
	}
	
	public boolean isUVControlEnabled(){
		return chkboxEnableUVControl.isSelected();
	}
	
	public int getRequestedNumberOfBlinkingEvents() throws NumberFormatException{
		return Integer.parseInt(textTargetBlinkingEventsPerFrame.getText());	
	}
	
	public void setCurrentNumberOfBlinkingEvents(int nbrBlinkingEvents){
		lblBlinkingEventsPerFrame.setText(""+nbrBlinkingEvents);
	}
	
	public void setUVControlEnabled(boolean state) {
		chkboxEnableUVControl.setSelected(state);
	}
}
