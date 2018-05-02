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
public class RunPumpsGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	transient MainFrameEditor mfe;
	private static String name = "RunPumps";
	private JTextField washingStationIndex = new JTextField("1");
	private JTextField time = new JTextField("10");
	private JCheckBox useLS2 = new JCheckBox("Use LS2");
	
	public RunPumpsGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public RunPumpsGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(3,2,60,15));
		retPanel.add(new JLabel("Index Washing Station (1: PBS, 2: H2O):"));
		retPanel.add(washingStationIndex);
		retPanel.add(new JLabel("Time [Seconds]:"));
		retPanel.add(time);
		retPanel.add(new JLabel(""));
		retPanel.add(useLS2);
		useLS2.setSelected(true);
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new RunPumpsGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[3];
		tempString[0] = washingStationIndex.getText();
		tempString[1] = time.getText();
		if (useLS2.isSelected()){
			tempString[2] = "selected";
		} else {
			tempString[2] = "ns";
		}
		return tempString;
	}
	

	@Override
	public void setSettings(String[] tempString) {
		washingStationIndex.setText(tempString[0]);
		time.setText(tempString[1]);
		if (tempString[2].equals("selected")){
			useLS2.setSelected(true);
		} else {
			useLS2.setSelected(false);
		}
	}

	
	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof RunPumpsGUI){
			RunPumpsGUI returnObject = new RunPumpsGUI(mfe);
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
		logTimeStart();
		//methods like getIndex(textfield) or getPumpTime(textfield) are located in the parent class (EditorModules from the editor package). If a new quantity is introduced for which there is no method yet, it has to be created, see comments in EditorModules for further instructions
		Utility.createSampleListForRunPumps(getIndex(washingStationIndex), getPumpTime(time), useLS2.isSelected(), mfe.getMainFrameReference().getPathToExchangeFolder());
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		if (getIndex(washingStationIndex)==-1|| getPumpTime(time)==-1) {
			return false;
		}
		return true;
	}

}
