package editorModulesDefinitions;

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
	MainFrame mf;
	private static String name = "Measurement Tag";
	MainFrameEditor mfe;
	
	public MeasurementTagGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.mf = mfe.getMainFrameReference();
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public MeasurementTagGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		Box verticalBox = Box.createVerticalBox();
		
		Box horizontalBox2 = Box.createHorizontalBox();
		horizontalBox2.add(new JLabel("Measurement tag:"));
		horizontalBox2.add(Box.createHorizontalGlue());
		verticalBox.add(horizontalBox2);
		verticalBox.add(Box.createVerticalStrut(20));
		Box horizontalBox = Box.createHorizontalBox();
		//horizontalBox.add(Box.createHorizontalStrut(20));
		horizontalBox.add(measurementTag);
		verticalBox.add(horizontalBox);
		retPanel.add(verticalBox);
		
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new MeasurementTagGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[1];
		tempString[0] = measurementTag.getText();
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		measurementTag.setText(tempString[0]);
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
			mf.setMeasurementTag(Utility.parseParameter(measurementTag.getText(), mfe));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
