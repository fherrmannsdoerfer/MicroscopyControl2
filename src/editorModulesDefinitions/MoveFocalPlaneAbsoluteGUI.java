package editorModulesDefinitions;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;
//for detailed comments look at performMeasurementGUI
public class MoveFocalPlaneAbsoluteGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	JTextField mirrorPosition = new JTextField("");
	
	private static String name = "Shift Focal Plane Absolutely";
	transient MainFrameEditor mfe;
	
	public MoveFocalPlaneAbsoluteGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
	}
	
	public MoveFocalPlaneAbsoluteGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		Box verticalBox = Box.createVerticalBox();
		
		Box horizontalBox2 = Box.createHorizontalBox();
		horizontalBox2.add(new JLabel("Absolute Mirrorposition (Using Focuslock):"));
		horizontalBox2.add(Box.createHorizontalGlue());
		verticalBox.add(horizontalBox2);
		verticalBox.add(Box.createVerticalStrut(20));
		Box horizontalBox = Box.createHorizontalBox();
		//horizontalBox.add(Box.createHorizontalStrut(20));
		horizontalBox.add(Utility.setFormatTextFields(mirrorPosition,400,20,30));
		verticalBox.add(horizontalBox);
		retPanel.add(verticalBox);
		
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new MoveFocalPlaneAbsoluteGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[1];
		tempString[0] = mirrorPosition.getText();
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		mirrorPosition.setText(tempString[0]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof MoveFocalPlaneAbsoluteGUI){
			MoveFocalPlaneAbsoluteGUI returnObject = new MoveFocalPlaneAbsoluteGUI(mfe);
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
		mfe.getMainFrameReference().setMirrorPosition(Double.parseDouble((Utility.parseParameter(mirrorPosition.getText(), mfe)).replace(",", ".")));
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		if (mirrorPosition.getText().isEmpty()||mirrorPosition.getText().contains(",")) {
			return false;
		}
		else {
			return true;
		}
	}

}
