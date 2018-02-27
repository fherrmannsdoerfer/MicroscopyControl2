package editorModulesDefinitions;

import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;

public class WashSyringeGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	transient MainFrameEditor mfe;
	private static String name = "Wash Syringe";
	private JTextField washingStationIndex = new JTextField("1");
	private JTextField repetitions = new JTextField("3");
	private JCheckBox useLS2 = new JCheckBox("Use LS2");
	
	public WashSyringeGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public WashSyringeGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(3, 2,60,15));
		retPanel.add(new JLabel("Index Washing Station (1: PBS, 2: H2O):"));
		retPanel.add(washingStationIndex);
		retPanel.add(new JLabel("Repetitions:"));
		retPanel.add(repetitions);
		retPanel.add(new JLabel(""));
		retPanel.add(useLS2);
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new WashSyringeGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[3];
		tempString[0] = washingStationIndex.getText();
		tempString[1] = repetitions.getText();
		if (useLS2.isSelected()){
			tempString[2] = "selected";
		}
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		washingStationIndex.setText(tempString[0]);
		repetitions.setText(tempString[1]);
		if (tempString[2].equals("selected")){
			useLS2.setSelected(true);
		}
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof WashSyringeGUI){
			WashSyringeGUI returnObject = new WashSyringeGUI(mfe);
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
	
	private int getRepetitions() {
		int reps = Integer.parseInt(repetitions.getText());
		if (reps<1 || reps>100) {
			System.err.println("Repetitions are not within limits of 1 to 100!");
			return -1;
		} else {
			return reps;
		}
		
	}
	
	

	@Override
	public void perform() {
		// TODO Auto-generated method stub
		Utility.createSampleListForWashingSyringe(getIndex(), getRepetitions(), useLS2.isSelected(),mfe.getMainFrameReference().getPathToExchangeFolder());
		setProgressbarValue(100);
	}

}
