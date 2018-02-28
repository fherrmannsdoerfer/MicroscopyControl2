package editorModulesDefinitions;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import editor.EditorModules;
import editor.MainFrameEditor;

public class CommentaryBarGUI extends EditorModules{

	
		
	private static final long serialVersionUID = 1L;
	private EditorModules beginLoop;
	private static String name = "Commentary Function";
	private JTextField newButtonText = new JTextField("hier könnte Ihre Werbung stehen!");
	transient MainFrameEditor mfe;
	
	public CommentaryBarGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorComment());
		this.setOptionPanel(createOptionPanel());
		setProgressBarInvisible();
		setModuleToFullWidth();
		mfe.updatePanels();
	}
	
	public CommentaryBarGUI(){
		setModuleToFullWidth();
	}
	
		
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		Box horizontalBox = Box.createHorizontalBox();
		
		horizontalBox.add(new JLabel("Just a comment to help to structure the workflow."));
		horizontalBox.add(newButtonText);
		newButtonText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setButtonText(newButtonText.getText());
			}
			
		});
		retPanel.add(horizontalBox);
		
		return retPanel;
	}

	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new CommentaryBarGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		return null;
	}

	@Override
	public void setSettings(String[] tempString) {
		
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof CommentaryBarGUI){
			CommentaryBarGUI returnObject = new CommentaryBarGUI(mfe);
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
