package editorModulesDefinitions;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataTypes.CameraParameters;
import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;


//for detailed comments look at performMeasurementGUI
public class FocusLockStateGUI extends EditorModules{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComboBox focuslockState;
	JTextField focuslockStateSelectionNumber = new JTextField("1");
	
	transient MainFrameEditor mfe;
	private static String name = "Focus Lock State";
	
	public FocusLockStateGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
	}
	
	public FocusLockStateGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(2, 2,60,15));
		focuslockState = new JComboBox();
		focuslockState.addItem("off");
		focuslockState.addItem("on");
		//laserSelection = new JComboBox(dummyLaserNames);
		
		retPanel.add(new JLabel("Focus Lock State:"));
		retPanel.add(focuslockState);
		retPanel.add(new JLabel("Focus Lock Selection Number:"));
		retPanel.add(focuslockStateSelectionNumber);

		focuslockState.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				focuslockStateSelectionNumber.setText((focuslockState.getSelectedIndex()+1)+"");
			}
		});
		
		focuslockStateSelectionNumber.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					focuslockState.setSelectedIndex(Integer.parseInt(focuslockStateSelectionNumber.getText())-1);
				}
				catch (Exception e){
					
				}
			}	
		});
		
		return retPanel;
	}
	
		
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new FocusLockStateGUI(mfe);
	}

	public String[] getSettings(){
		String[] tempString = new String[2];
		tempString[0] = String.valueOf(focuslockState.getSelectedIndex());
		tempString[1] = focuslockStateSelectionNumber.getText();
		return tempString;
	}
	public void setSettings(String[] tempString){
		focuslockState.setSelectedIndex(Integer.parseInt(tempString[0]));
		focuslockStateSelectionNumber.setText(tempString[1]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof FocusLockStateGUI){
			FocusLockStateGUI returnObject = new FocusLockStateGUI(mfe);
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
			if (Integer.parseInt(Utility.parseParameter(focuslockStateSelectionNumber.getText(), mfe))-1 == 0){
				mfe.getMainFrameReference().setFocusLockState(false);
			}
			else{
				mfe.getMainFrameReference().setFocusLockState(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		if(focuslockStateSelectionNumber.getText().isEmpty()){
			return false;
		}
		return true;
	}
}
