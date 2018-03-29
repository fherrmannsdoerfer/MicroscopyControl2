package editorModulesDefinitions;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;

//for detailed comments look at performMeasurementGUI
public class LaserControl extends EditorModules{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComboBox laserSelection;
	JTextField laserSelectionNumber = new JTextField("");
	JTextField laserIntensity = new JTextField("0.1");
	transient MainFrameEditor mfe;
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
		retPanel.setLayout(new GridLayout(3, 2,60,15));
		laserSelection = new JComboBox(mfe.getMainFrameReference().getLaserNames());
		laserSelection.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				laserSelectionNumber.setText((laserSelection.getSelectedIndex()+1)+"");
			}
		});
		
		laserSelectionNumber.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					laserSelection.setSelectedIndex(Integer.parseInt(laserSelectionNumber.getText())-1);
				}
				catch (Exception e){
					
				}
			}	
		});
		
		//laserSelection = new JComboBox(dummyLaserNames);
		retPanel.add(new JLabel("Laser Selection:"));
		retPanel.add(laserSelection);
		retPanel.add(new JLabel("Laser Selection Number:"));
		retPanel.add(laserSelectionNumber);
		retPanel.add(new JLabel("Laser Intensity:"));
		retPanel.add(laserIntensity);
		
		return retPanel;
	}
	
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new LaserControl(mfe);
	}

	public String[] getSettings(){
		String[] tempString = new String[3];
		tempString[0] = String.valueOf(laserSelection.getSelectedIndex());
		tempString[1] = laserSelectionNumber.getText();
		tempString[2] = laserIntensity.getText();
		return tempString;
	}
	public void setSettings(String[] tempString){
		laserSelection.setSelectedIndex(Integer.parseInt(tempString[0]));
		laserSelectionNumber.setText(tempString[1]);
		laserIntensity.setText(tempString[2]);
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
		logTimeStart();
		//mfe.getMainFrameReference().setLaserIntensity(laserSelection.getSelectedIndex(), Double.parseDouble(laserIntensity.getText()));
		mfe.getMainFrameReference().setLaserIntensity(Integer.parseInt(Utility.parseParameter(laserSelectionNumber.getText(), mfe))-1, Double.parseDouble(Utility.parseParameter(laserIntensity.getText(),mfe)));
		System.out.println("index: "+(Integer.parseInt(Utility.parseParameter(laserSelectionNumber.getText(), mfe))-1));
		System.out.println("intensity: "+Double.parseDouble(Utility.parseParameter(laserIntensity.getText(),mfe)));
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		if (laserIntensity.getText().isEmpty()|| laserIntensity.getText().isEmpty()||laserIntensity.getText().contains(",")|| laserIntensity.getText().contains(",")){
			return false;
		} else {return true;}
	}
}
