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
//for detailed comments look at performMeasurementGUI
public class VortexVialGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	transient MainFrameEditor mfe;
	private static String name = "Agitate Solution in Vial";
	private JTextField vialIndex = new JTextField("-1");
	private JTextField repetitions = new JTextField("3");
	private JTextField vortexVolume = new JTextField("200");
	private JCheckBox useLS2 = new JCheckBox("Use LS2");
	
	public VortexVialGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public VortexVialGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(4, 2,60,15));
		retPanel.add(new JLabel("Vial Number From Rack 3:"));
		retPanel.add(vialIndex);
		retPanel.add(new JLabel("Repetitions:"));
		retPanel.add(repetitions);
		retPanel.add(new JLabel("Vortex Volume:"));
		retPanel.add(vortexVolume);
		retPanel.add(new JLabel(""));
		retPanel.add(useLS2);
		useLS2.setSelected(true);
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new VortexVialGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[4];
		tempString[0] = vialIndex.getText();
		tempString[1] = repetitions.getText();
		if (useLS2.isSelected()){
			tempString[2] = "selected";
		}
		tempString[3] = vortexVolume.getText();
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		vialIndex.setText(tempString[0]);
		repetitions.setText(tempString[1]);
		if (tempString[2].equals("selected")){
			useLS2.setSelected(true);
		}
		vortexVolume.setText(tempString[3]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof VortexVialGUI){
			VortexVialGUI returnObject = new VortexVialGUI(mfe);
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
		Utility.createSampleListForVortexingVial(getVialNumber(vialIndex), getNbrCycles(repetitions), getVolume(vortexVolume,useLS2.isSelected()), useLS2.isSelected(),mfe.getMainFrameReference().getPathToExchangeFolder());
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		if(getVialNumber(vialIndex)==-1||getNbrCycles(repetitions)==-1||getVolume(vortexVolume,useLS2.isSelected())==-1) {
			return false;
		}
		return true;
	}

}
