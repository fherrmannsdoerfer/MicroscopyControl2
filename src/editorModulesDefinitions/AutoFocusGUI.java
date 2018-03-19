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
public class AutoFocusGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	transient MainFrameEditor mfe;
	private static String name = "AutoFocus";
	private JTextField upperBound = new JTextField("38");
	private JTextField lowerBound = new JTextField("12");
	private JTextField stepSize = new JTextField("2");
	
	public AutoFocusGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public AutoFocusGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(3,2,60,15));
		retPanel.add(new JLabel("Upper Bound [Micrometers]:"));
		retPanel.add(upperBound);
		retPanel.add(new JLabel("Lower Bound [Micrometers]:"));
		retPanel.add(lowerBound);
		retPanel.add(new JLabel("Stepsize"));
		retPanel.add(stepSize);
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new AutoFocusGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[3];
		tempString[0] = upperBound.getText();
		tempString[1] = lowerBound.getText();
		tempString[2] = stepSize.getText();
		return tempString;
	}
	

	@Override
	public void setSettings(String[] tempString) {
		upperBound.setText(tempString[0]);
		lowerBound.setText(tempString[1]);
		stepSize.setText(tempString[2]);
	}

	
	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof AutoFocusGUI){
			AutoFocusGUI returnObject = new AutoFocusGUI(mfe);
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
		mfe.getMainFrameReference().findFocus(getPositiveValue(upperBound),getPositiveValue(lowerBound),getPositiveValue(stepSize));
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		if (getPositiveValue(upperBound)==-1|| getPositiveValue(lowerBound)==-1||getPositiveValue(stepSize)==-1) {
			return false;
		}
		return true;
	}

}
