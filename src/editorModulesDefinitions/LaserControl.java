package editorModulesDefinitions;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;


public class LaserControl extends EditorModules{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComboBox laserSelection;
	JTextField laserIntensity = new JTextField("0.1");
	MainFrameEditor mfe;
	private static String name = "LaserControl";
	String[] dummyLaserNames = {"laser1","laser2","laser3","laser4"};
	
	public LaserControl(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
	}
	
	public LaserControl(){
		
	}
	
	private JPanel createOptionPanel(){
		laserIntensity = Utility.setFormatTextFields(laserIntensity,30,20,3);

		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(2, 2,60,15));
		//MainFrame mf = mfe.getMainFrameReference();
		//laserSelection = new JComboBox(mf.getLaserNames());
		laserSelection = new JComboBox(dummyLaserNames);
		retPanel.add(new JLabel("Laser Selection:"));
		retPanel.add(laserSelection);
		retPanel.add(new JLabel("Laser Intensity:"));
		retPanel.add(laserIntensity);
		
		return retPanel;
	}
	
		
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new LaserControl(mfe);
	}

	public String[] getSettings(){
		String[] tempString = new String[2];
		tempString[0] = String.valueOf(laserSelection.getSelectedIndex());
		tempString[1] = laserIntensity.getText();
		return tempString;
	}
	public void setSettings(String[] tempString){
		laserSelection.setSelectedIndex(Integer.parseInt(tempString[0]));
		laserIntensity.setText(tempString[1]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof LaserControl){
			LaserControl returnObject = new LaserControl(mfe);
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
		// TODO Auto-generated method stub
		
	}
}
