package editorModulesDefinitions;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;


public class PrepareMEABufferGUI extends EditorModules{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField volumePBSForStock = new JTextField("500");
	JCheckBox createStockSolution = new JCheckBox("Create Stock Solution");
	JTextField indexVialMeaPowder = new JTextField("-1");
	JTextField indexVialMeaFinal = new JTextField("-1");
	JTextField volumePBSForFinal = new JTextField("900");
	JTextField volumeMEAStockForFinal = new JTextField("100");
	JTextField volumeNaOHForFinal = new JTextField("20");
	JTextField indexVialNaOH = new JTextField("-1");
	transient MainFrameEditor mfe;
	JTextField tagROILoop = new JTextField();
	private static String name = "Prepare MEA Buffer";
	
	public PrepareMEABufferGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public PrepareMEABufferGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(8, 2,60,15));
		
		retPanel.add(new JLabel("Volume Of PBS For Stock Solution [Microliter]:"));
		retPanel.add(volumePBSForStock);
		retPanel.add(new JLabel(""));
		retPanel.add(createStockSolution);
		retPanel.add(new JLabel("Vial Number From Rack 3 (MEA Stock):"));
		retPanel.add(indexVialMeaPowder);
		createStockSolution.addActionListener(new chkBoxActionListener());
		retPanel.add(new JLabel("Vial Number From Rack 3 (Final MEA):"));
		retPanel.add(indexVialMeaFinal);
		retPanel.add(new JLabel("Vial Number From Rack 3 (NaOH):"));
		retPanel.add(indexVialNaOH);
		retPanel.add(new JLabel("Volume MEA Stock For Final MEA [Microliters]:"));
		retPanel.add(volumeMEAStockForFinal);
		retPanel.add(new JLabel("Volume PBS For Final MEA [Microliters]:"));
		retPanel.add(volumePBSForFinal);
		retPanel.add(new JLabel("Volume NaOH For Final MEA [Microliters]:"));
		retPanel.add(volumeNaOHForFinal);

		return retPanel;
	}
	
	class chkBoxActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			volumePBSForStock.setEnabled(!createStockSolution.isSelected());
		}
		
	}
	
		
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new PrepareMEABufferGUI(mfe);
	}

	public String[] getSettings(){
		String[] tempString = new String[8];
		tempString[0] = volumePBSForStock.getText();
		tempString[1] = indexVialMeaPowder.getText();
		tempString[2] = indexVialMeaFinal.getText();
		tempString[3] = indexVialNaOH.getText();
		tempString[4] = volumeMEAStockForFinal.getText();
		tempString[5] = volumePBSForFinal.getText();
		tempString[6] = volumeNaOHForFinal.getText();
		if (createStockSolution.isSelected()){
			tempString[7] = "selected";
		}
		else {
			tempString[7] = "notSelected";
		}
		return tempString;
	}
	public void setSettings(String[] tempString){
		volumePBSForStock.setText(tempString[0]);
		indexVialMeaPowder.setText(tempString[1]);
		indexVialMeaFinal.setText(tempString[2]);
		indexVialNaOH.setText(tempString[3]);
		volumeMEAStockForFinal.setText(tempString[4]);
		volumePBSForFinal.setText(tempString[5]);
		volumeNaOHForFinal.setText(tempString[6]);
		if (tempString[7].equals("selected")){
			createStockSolution.setSelected(true);
		}
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof PrepareMEABufferGUI){
			PrepareMEABufferGUI returnObject = new PrepareMEABufferGUI(mfe);
			return returnObject;
		}
		return null;
	}

	@Override
	public String getFunctionName() {
		return name;
	}
	
	private int getVolumePBSForStock() {
		int reps = Integer.parseInt(volumePBSForStock.getText());
		if (reps<1 || reps>1000) {
			System.err.println("Volume is not within limits of 1 to 1000!");
			return -1;
		} else {
			return reps;
		}
	}
	
	private int getIndexVialMeaStock() {
		int index = Integer.parseInt(indexVialMeaPowder.getText());
		if (index<1 || index>54) {
			System.err.println("Vial Number is not within limits of 1 to 54!");
			return -1;
		} else {
			return index;
		}
	}
	private int getIndexVialMeaFinal() {
		int index = Integer.parseInt(indexVialMeaFinal.getText());
		if (index<1 || index>54) {
			System.err.println("Vial Number is not within limits of 1 to 54!");
			return -1;
		} else {
			return index;
		}
	}
	private int getVolumePBSForFinal() {
		int reps = Integer.parseInt(volumePBSForFinal.getText());
		if (reps<1 || reps>1000) {
			System.err.println("Volume is not within limits of 1 to 1000!");
			return -1;
		} else {
			return reps;
		}
	}
	private int getVolumeMEAStockForFinal() {
		int reps = Integer.parseInt(volumeMEAStockForFinal.getText());
		if (reps<1 || reps>1000) {
			System.err.println("Volume is not within limits of 1 to 1000!");
			return -1;
		} else {
			return reps;
		}
	}
	private int getVolumeNaOHForFinal() {
		int reps = Integer.parseInt(volumeNaOHForFinal.getText());
		if (reps<1 || reps>100) {
			System.err.println("Volume is not within limits of 1 to 100!");
			return -1;
		} else {
			return reps;
		}
	}
	private int getIndexVialNaOH() {
		int index = Integer.parseInt(indexVialNaOH.getText());
		if (index<1 || index>54) {
			System.err.println("Vial Number is not within limits of 1 to 54!");
			return -1;
		} else {
			return index;
		}
	}
	
	


	@Override
	public void perform() {
		Utility.createSampleListForPreparationOfMEA(getVolumePBSForStock(), getIndexVialMeaStock(), getIndexVialMeaFinal(), getVolumePBSForFinal(),getVolumeMEAStockForFinal(),getVolumeNaOHForFinal(),getIndexVialNaOH(),mfe.getMainFrameReference().getPathToExchangeFolder(), createStockSolution.isSelected());
		setProgressbarValue(100);
	}
}
