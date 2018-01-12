package editorModulesDefinitions;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;

public class PauseGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	JTextField durationOfPause = new JTextField("");
	
	private static String name = "Pause Microscope";
	transient MainFrameEditor mfe;
	
	public PauseGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
	}
	
	public PauseGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		Box verticalBox = Box.createVerticalBox();
		
		Box horizontalBox2 = Box.createHorizontalBox();
		horizontalBox2.add(new JLabel("Duration of the pause in milliseconds:"));
		horizontalBox2.add(Box.createHorizontalGlue());
		verticalBox.add(horizontalBox2);
		verticalBox.add(Box.createVerticalStrut(20));
		Box horizontalBox = Box.createHorizontalBox();
		//horizontalBox.add(Box.createHorizontalStrut(20));
		horizontalBox.add(Utility.setFormatTextFields(durationOfPause,400,20,30));
		verticalBox.add(horizontalBox);
		retPanel.add(verticalBox);
		
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new PauseGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[1];
		tempString[0] = durationOfPause.getText();
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		durationOfPause.setText(tempString[0]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof PauseGUI){
			PauseGUI returnObject = new PauseGUI(mfe);
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
			double duration = Long.parseLong(Utility.parseParameter(durationOfPause.getText(),mfe));
			double interval = 100; //in ms
			double elapsedTime = 0;
			while (elapsedTime< duration - interval) {
				Thread.sleep((long) interval);
				elapsedTime += interval;
				setProgressbarValue((int) (elapsedTime/duration*100));
			}
			Thread.sleep((long) (duration-elapsedTime));
			setProgressbarValue(100);
			//Thread.sleep(Long.parseLong(Utility.parseParameter(durationOfPause.getText(),mfe)));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
