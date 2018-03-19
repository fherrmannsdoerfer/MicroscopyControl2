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
public class AddWashingSolutionToVialGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	transient MainFrameEditor mfe;
	private static String name = "AddWashingSolutionToVial";
	private JTextField washingStationIndex = new JTextField("1");
	private JTextField volume = new JTextField("300");
	private JTextField targetVial = new JTextField("-1");
	private JCheckBox useLS2 = new JCheckBox("Use LS2");
	
	public AddWashingSolutionToVialGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public AddWashingSolutionToVialGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(4, 2,60,15));
		retPanel.add(new JLabel("Index Washing Station (1: PBS, 2: H2O):"));
		retPanel.add(washingStationIndex);
		retPanel.add(new JLabel("Volume [Microliter]:"));
		retPanel.add(volume);
		retPanel.add(new JLabel("Vial Number From Rack 3:"));
		retPanel.add(targetVial);
		retPanel.add(new JLabel(""));
		retPanel.add(useLS2);
		useLS2.setSelected(true);
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new AddWashingSolutionToVialGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[4];
		tempString[0] = washingStationIndex.getText();
		tempString[1] = volume.getText();
		tempString[2] = targetVial.getText();
		if (useLS2.isSelected()){
			tempString[3] = "selected";
		}
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		washingStationIndex.setText(tempString[0]);
		volume.setText(tempString[1]);
		targetVial.setText(tempString[2]);
		if (tempString[3].equals("selected")){
			useLS2.setSelected(true);
		}
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof AddWashingSolutionToVialGUI){
			AddWashingSolutionToVialGUI returnObject = new AddWashingSolutionToVialGUI(mfe);
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
		// TODO Auto-generated method stub
		Utility.createSampleListForSolutionAddingFromWashingStationToVial(getIndex(washingStationIndex), getVolume(volume,useLS2.isSelected()), getVialNumber(targetVial),mfe.getMainFrameReference().getPathToExchangeFolder());
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		if (getIndex(washingStationIndex)==-1|| getVolume(volume,useLS2.isSelected())==-1|| getVialNumber(targetVial)==-1) {
			return false;
		}
		return true;
	}

}
