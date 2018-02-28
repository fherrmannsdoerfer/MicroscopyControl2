package editorModulesDefinitions;

import java.awt.GridLayout;

import javax.swing.Box;
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
		retPanel.setLayout(new GridLayout(2, 2,60,15));
		retPanel.add(new JLabel("Index Washing Station (1: PBS, 2: H2O):"));
		retPanel.add(washingStationIndex);
		retPanel.add(new JLabel("Volume [Microliter]:"));
		retPanel.add(volume);
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new AddWashingSolutionToSampleGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[2];
		tempString[0] = washingStationIndex.getText();
		tempString[1] = volume.getText();
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		washingStationIndex.setText(tempString[0]);
		volume.setText(tempString[1]);
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
	
	private int getIndex() {
		int index = Integer.parseInt(washingStationIndex.getText());
		if (index<1 || index>2) {
			System.err.println("Index of Washing Station must be 1 or 2!");
			return -1;
		} else {
			return index;
		}
		
	}
	
	private int getVolume() {
		int intVolume = Integer.parseInt(volume.getText());
		if (intVolume<1 || intVolume>1000) {
			System.err.println("Volume is not within limits of 1 to 1000!");
			return -1;
		} else {
			return intVolume;
		}
		
	}
	
	

	@Override
	public void perform() {
		// TODO Auto-generated method stub
		Utility.createSampleListForSolutionAddingFromWashingStation(getIndex(), getVolume(),mfe.getMainFrameReference().getXYStagePosition(),mfe.getMainFrameReference().getPathToExchangeFolder());
		setProgressbarValue(100);
	}

}