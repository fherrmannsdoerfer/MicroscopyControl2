package editorModulesDefinitions;

import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;
import microscopeControl.MainFrame;

public class MeasurementTagGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	JTextField measurementTag = new JTextField("");
	JTextField pathField = new JTextField("");
	transient MainFrame mf;
	private static String name = "Output Control";
	transient MainFrameEditor mfe;
	
	public MeasurementTagGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.mf = mfe.getMainFrameReference();
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
		this.pathField.setText(mf.getPath());
	}
	
	public MeasurementTagGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(4, 1,60,15));
		retPanel.add(new JLabel("Path:"));
		retPanel.add(pathField);
		
		retPanel.add(new JLabel("Measurement tag:"));
		retPanel.add(measurementTag);
		
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new MeasurementTagGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[2];
		tempString[0] = pathField.getText();
		tempString[1] = measurementTag.getText();
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		pathField.setText(tempString[0]);
		measurementTag.setText(tempString[1]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof MeasurementTagGUI){
			MeasurementTagGUI returnObject = new MeasurementTagGUI(mfe);
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
		try {
			//mf.setMeasurementTag(measurementTag.getText());
			System.out.println(Utility.parseParameter(measurementTag.getText(), mfe));
			mf.setPathForMeasurment(Utility.parseParameter(pathField.getText(), mfe));
			mf.setMeasurementTag(Utility.parseParameter(measurementTag.getText(), mfe));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setProgressbarValue(100);
	}

	@Override
	public boolean checkForValidity() {
		if (measurementTag.getText().isEmpty() || pathField.getText().isEmpty()) {
			return false;
		}
		return true;
	}

}
