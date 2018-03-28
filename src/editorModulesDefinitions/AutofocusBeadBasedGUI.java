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
public class AutofocusBeadBasedGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	transient MainFrameEditor mfe;
	private static String name = "AutoFocusBead";
	private JTextField interval = new JTextField("100");
	private JTextField lowerPos = new JTextField("-300");
	private JTextField upperPos = new JTextField("300");

	
	public AutofocusBeadBasedGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorAutoFocus());
		this.setOptionPanel(createOptionPanel());
	}
	
	public AutofocusBeadBasedGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(3,2,60,15));
		retPanel.add(new JLabel("Interval [Micrometer]:"));
		retPanel.add(interval);
		retPanel.add(new JLabel("Relative Lower Mirror Position [Micrometer]:"));
		retPanel.add(lowerPos);
		retPanel.add(new JLabel("Relative Upper Mirror Position [Micrometer]:"));
		retPanel.add(upperPos);
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new AutofocusBeadBasedGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[3];
		tempString[0] = interval.getText();
		tempString[1] = lowerPos.getText();
		tempString[2] = upperPos.getText();
		return tempString;
	}
	

	@Override
	public void setSettings(String[] tempString) {
		interval.setText(tempString[0]);
		lowerPos.setText(tempString[1]);
		upperPos.setText(tempString[2]);
	}

	
	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof AutofocusBeadBasedGUI){
			AutofocusBeadBasedGUI returnObject = new AutofocusBeadBasedGUI(mfe);
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
		Utility.findFocusLockMirrorPosition((int)getPositiveValue(interval),(int)getNegativeValue(lowerPos),(int)getPositiveValue(upperPos), mfe.getMainFrameReference());
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		if (interval.getText().isEmpty()) {
			return false;
		}
		return true;
	}

}
