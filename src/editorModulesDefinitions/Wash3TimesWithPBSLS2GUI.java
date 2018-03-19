package editorModulesDefinitions;

import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;
//for detailed comments look at performMeasurementGUI
public class Wash3TimesWithPBSLS2GUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	transient MainFrameEditor mfe;
	private static String name = "Wash3TimesWithPBS";
	JTextField injectionVolume = new JTextField("300");
	JTextField volumePerSpot = new JTextField("200");
	JTextField waitTimeSeks = new JTextField("300");
	JCheckBox removeSolutionFirst = new JCheckBox("Remove Solution First");
	JCheckBox leaveSolutionLast = new JCheckBox("Leave PBS After Washing");
	
	public Wash3TimesWithPBSLS2GUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public Wash3TimesWithPBSLS2GUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(5, 2,60,15));
		retPanel.add(new JLabel("Volume Per Washing Step [Microliter]:"));
		retPanel.add(injectionVolume);
		retPanel.add(new JLabel("Aspiration Per Spot [Microliter]:"));
		retPanel.add(volumePerSpot);
		retPanel.add(new JLabel("Wait Time [Seconds]"));
		retPanel.add(waitTimeSeks);
		retPanel.add(new JLabel(""));
		retPanel.add(removeSolutionFirst);
		retPanel.add(new JLabel(""));
		retPanel.add(leaveSolutionLast);
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new Wash3TimesWithPBSLS2GUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[5];
		tempString[0] = injectionVolume.getText();
		tempString[1] = volumePerSpot.getText();
		tempString[2] = waitTimeSeks.getText();
		if (removeSolutionFirst.isSelected()){
			tempString[3] = "selected";
		}
		else {
			tempString[3] = "notSelected";
		}
		if (leaveSolutionLast.isSelected()){
			tempString[4] = "selected";
		}
		else {
			tempString[4] = "notSelected";
		}
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		injectionVolume.setText(tempString[0]);
		volumePerSpot.setText(tempString[1]);
		waitTimeSeks.setText(tempString[2]);
		if (tempString[3].equals("selected")){
			removeSolutionFirst.setSelected(true);
		}
		if (tempString[4].equals("selected")){
			leaveSolutionLast.setSelected(true);
		}
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof Wash3TimesWithPBSLS2GUI){
			Wash3TimesWithPBSLS2GUI returnObject = new Wash3TimesWithPBSLS2GUI(mfe);
			return returnObject;
		}
		return null;
	}

	@Override
	public String getFunctionName() {
		return name;
	}
	
	private int getWaitTime() {
		int wait = Integer.parseInt(waitTimeSeks.getText());
		if (wait<1 || wait>7200) {
			System.err.println("Wait time is not within limits of 1 to 7200!");
			return -1;
		} else {
			return wait;
		}
		
	}

	@Override
	public void perform() {
		logTimeStart();
		Utility.createSampleListForWashing3Times(getVolume(injectionVolume,true), getVolumePerSpot(volumePerSpot), getWaitTime(), removeSolutionFirst.isSelected(), leaveSolutionLast.isSelected(),mfe.getMainFrameReference().getXYStagePosition(),mfe.getMainFrameReference().getPathToExchangeFolder());
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		if (getVolume(injectionVolume,true)==-1||getVolumePerSpot(volumePerSpot)==-1||getWaitTime()==-1) {
			return false;
		}
		return true;
	}

}
