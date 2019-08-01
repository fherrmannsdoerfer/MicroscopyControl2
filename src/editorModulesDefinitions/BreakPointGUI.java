package editorModulesDefinitions;

import java.awt.Dialog;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;

//for detailed comments look at performMeasurementGUI
public class BreakPointGUI extends EditorModules{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	transient MainFrameEditor mfe;
	JTextField message = new JTextField("Place message here.");
	private static String name = "Break Point";

	public BreakPointGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
	}
	
	public BreakPointGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(1, 2,20,15));
		//MainFrame mf = mfe.getMainFrameReference();
		//laserSelection = new JComboBox(mf.getLaserNames());
		retPanel.add(new JLabel("Insert message here:         "));
		retPanel.add(message);

		return retPanel;
	}
	
		
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new BreakPointGUI(mfe);
	}

	public String[] getSettings(){
		String[] tempString = new String[1];
		tempString[0] = message.getText();
		return tempString;
	}
	public void setSettings(String[] tempString){
		message.setText(tempString[0]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof BreakPointGUI){
			BreakPointGUI returnObject = new BreakPointGUI(mfe);
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
		JDialog d3 = new JDialog(mfe.getOwner(), "Close this window to proceed.", Dialog.ModalityType.DOCUMENT_MODAL);
		d3.setSize(400,200);
		d3.add(new JLabel(message.getText()));
		d3.setVisible(true);
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		
		return true;
		
	}
}
