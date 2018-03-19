package editorModulesDefinitions;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;

//for detailed comments look at performMeasurementGUI
public class FilterWheelGUI extends EditorModules{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComboBox filterSelection;
	transient MainFrameEditor mfe;
	private static String name = "Filter Wheel";
	JTextField filterSelectionNumber = new JTextField("1");
	
	public FilterWheelGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
	}
	
	public FilterWheelGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(2, 2,60,15));
		filterSelection = new JComboBox(mfe.getMainFrameReference().getFilterNames());
		filterSelection.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				filterSelectionNumber.setText((filterSelection.getSelectedIndex()+1)+"");
			}
		});
		//laserSelection = new JComboBox(dummyLaserNames);
		retPanel.add(new JLabel("Filter Selection:"));
		retPanel.add(filterSelection);
		retPanel.add(new JLabel("Filter Number:"));
		retPanel.add(filterSelectionNumber);
		filterSelectionNumber.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					filterSelection.setSelectedIndex(Integer.parseInt(filterSelectionNumber.getText())-1);
				}
				catch (Exception e){
					
				}
			}	
		});
		
		return retPanel;
	}
	
		
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new FilterWheelGUI(mfe);
	}

	public String[] getSettings(){
		String[] tempString = new String[2];
		tempString[0] = String.valueOf(filterSelection.getSelectedIndex());
		tempString[1] = filterSelectionNumber.getText();
		return tempString;
	}
	public void setSettings(String[] tempString){
		filterSelection.setSelectedIndex(Integer.parseInt(tempString[0]));
		filterSelectionNumber.setText(tempString[1]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof FilterWheelGUI){
			FilterWheelGUI returnObject = new FilterWheelGUI(mfe);
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
		mfe.getMainFrameReference().setFilterWheelPosition(Integer.parseInt(Utility.parseParameter(filterSelectionNumber.getText(), mfe))-1);
		try {
			Thread.sleep(5000);
			setProgressbarValue(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		// TODO Auto-generated method stub
		return true;
	}
}
