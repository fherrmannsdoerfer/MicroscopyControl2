package editorModulesDefinitions;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;


public class CaptureWidefieldImageGUI extends EditorModules{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField exposureTime = new JTextField("100");
	transient MainFrameEditor mfe;
	private static String name = "CaptureWidefieldImageGUI";

	public CaptureWidefieldImageGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
	}
	
	public CaptureWidefieldImageGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(1, 2,60,15));
		//MainFrame mf = mfe.getMainFrameReference();
		//laserSelection = new JComboBox(mf.getLaserNames());
		retPanel.add(new JLabel("Exposure Time [ms]:"));
		retPanel.add(exposureTime);

		return retPanel;
	}
	
		
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new CaptureWidefieldImageGUI(mfe);
	}

	public String[] getSettings(){
		String[] tempString = new String[1];
		tempString[0] = exposureTime.getText();
		return tempString;
	}
	public void setSettings(String[] tempString){
		exposureTime.setText(tempString[0]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof CaptureWidefieldImageGUI){
			CaptureWidefieldImageGUI returnObject = new CaptureWidefieldImageGUI(mfe);
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
		mfe.getMainFrameReference().captureAndStoreWidefieldImage(Double.parseDouble(Utility.parseParameter(exposureTime.getText(), mfe)));
		setProgressbarValue(100);
	}

	@Override
	public boolean checkForValidity() {
		
		if (exposureTime.getText().isEmpty()) {
			return false;
		} else {return true;}
		
	}
}
