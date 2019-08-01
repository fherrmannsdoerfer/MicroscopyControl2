package editorModulesDefinitions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;
import microscopeControl.MainFrame;
//for detailed comments look at performMeasurementGUI
public class MeasurementTagGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	JTextField measurementTag = new JTextField("");
	JTextField pathField = new JTextField("");
	JTextArea comments = new JTextArea();
	//JTextField comments = new JTextField();
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
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Path:"));
		Utility.setFormatTextFields(pathField, 300, 20, 50);
		verticalBox.add(pathField);
		verticalBox.add(new JLabel("Measurement tag:"));
		Utility.setFormatTextFields(measurementTag, 300, 20, 50);
		verticalBox.add(measurementTag);
		verticalBox.add(new JLabel("Comments:"));
		comments.setMaximumSize(new Dimension(300, 600));
		comments.setBackground(new Color(150,150,150));
		verticalBox.add(comments);
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new MeasurementTagGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[3];
		tempString[0] = pathField.getText();
		tempString[1] = measurementTag.getText();
		tempString[2] = comments.getText();
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		pathField.setText(tempString[0]);
		measurementTag.setText(tempString[1]);
		comments.setText(tempString[2]);
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
		logTimeStart();
		try {
			//mf.setMeasurementTag(measurementTag.getText());
			System.out.println(Utility.parseParameter(measurementTag.getText(), mfe));
			mf.setPathForMeasurment(Utility.parseParameter(pathField.getText(), mfe));
			mf.setMeasurementTag(Utility.parseParameter(measurementTag.getText(), mfe));
			mf.setComments(Utility.parseParameter(comments.getText(), mfe));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		if (measurementTag.getText().isEmpty() || pathField.getText().isEmpty()) {
			return false;
		}
		return true;
	}

}
