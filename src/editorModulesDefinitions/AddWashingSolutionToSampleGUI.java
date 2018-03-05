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

public class AddWashingSolutionToSampleGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	transient MainFrameEditor mfe;
	private static String name = "AddWashingSolutionToSample";
	private JTextField washingStationIndex = new JTextField("1");
	private JTextField volume = new JTextField("300");
	private JCheckBox useLS2 = new JCheckBox("Use LS2");
	
	public AddWashingSolutionToSampleGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public AddWashingSolutionToSampleGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(3, 2,60,15));
		retPanel.add(new JLabel("Index Washing Station (1: PBS, 2: H2O):"));
		retPanel.add(washingStationIndex);
		retPanel.add(new JLabel("Volume [Microliter]:"));
		retPanel.add(volume);
		retPanel.add(new JLabel(""));
		retPanel.add(useLS2);
		useLS2.setSelected(true);
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new AddWashingSolutionToSampleGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[3];
		tempString[0] = washingStationIndex.getText();
		tempString[1] = volume.getText();
		if (useLS2.isSelected()){
			tempString[2] = "selected";
		}
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		washingStationIndex.setText(tempString[0]);
		volume.setText(tempString[1]);
		if (tempString[2].equals("selected")){
			useLS2.setSelected(true);
		}
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof AddWashingSolutionToSampleGUI){
			AddWashingSolutionToSampleGUI returnObject = new AddWashingSolutionToSampleGUI(mfe);
			return returnObject;
		}
		return null;
	}

	@Override
	public String getFunctionName() {
		return name;
	}
	
	
	
		

	@Override
	public void perform() {
		Utility.createSampleListForSolutionAddingFromWashingStation(getIndex(washingStationIndex), getVolume(volume, useLS2.isSelected()), useLS2.isSelected(),mfe.getMainFrameReference().getXYStagePosition(),mfe.getMainFrameReference().getPathToExchangeFolder());
		setProgressbarValue(100);
	}

	@Override
	public boolean checkForValidity() {
		if (getVolume(volume,useLS2.isSelected())==-1 || getIndex(washingStationIndex)==-1) {
			return false;
		}
		return true;
	}

}
