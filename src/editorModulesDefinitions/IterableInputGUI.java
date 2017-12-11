package editorModulesDefinitions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import editor.EditorModules;
import editor.LoopModules;
import editor.MainFrameEditor;
import editor.RootPanel;
//the idea behind iterable inputs is that loops can be created that use different parameters for each run
//For example could different laser intensities as well as different exposure time be useful.
//In that case the iterationTagLoop can be set to e.g. experiment1. The same tag has to be used in the LoopIterableGUI module.
//then a second tag like laserInts should be set in the iterationTagParameterFields. 
//A second IteralbeInputGUI module has to be added also using experiment1 as the iterationTagLoop.
//Then a LoopIterableGUI module has to be added to the LoopIterable and a laser control module as well as a cameraParameter module.
//in the intensity field of the laser control gui the laserInts Tag has to be added.
//it is important that there are the same number of different parameters in all iterableInputGUI modules that share the same iterattionTagLoop value.
public class IterableInputGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	JTextField iterationTagLoop = new JTextField("StainingsLoop");
	JTextField iterationTagParameterFields = new JTextField("StainingsParam");
	JButton updateButton = new JButton("Update ROI List");
	MainFrameEditor mfe;
	JPanel dispList;
	IterableInputGUI selfReference;
	
	private static String name = "Iteration Input";
	EditorModules endLoop = new EndLoopGUI(this);
	
	JPanel newLinePanel = new JPanel();
	JPanel middlePart;
	
	private ArrayList<ParameterColumn> parameterList = new ArrayList<ParameterColumn>();
	JScrollPane scrollPane;
	
	public IterableInputGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
		this.selfReference = this;
	}
	
	public IterableInputGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		JPanel upperPart = new JPanel();
		upperPart.setLayout(new GridLayout(2, 2,60,15));
		upperPart.add(new JLabel("Tag For The IterationLoop:"));
		upperPart.add(iterationTagLoop);
		upperPart.add(new JLabel("Tag For The Parameter Fields:"));
		upperPart.add(iterationTagParameterFields);
		
		Box verticalBox = Box.createVerticalBox();
		retPanel.add(verticalBox);
		verticalBox.add(upperPart);
		
		middlePart = new JPanel();
		
		verticalBox.add(middlePart);
		middlePart.setLayout(new BoxLayout(middlePart,BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(middlePart);
		scrollPane.setPreferredSize(new Dimension(200+60,800));
		scrollPane.setMinimumSize(new Dimension(200+30,700));
		scrollPane.setMaximumSize(new Dimension(200+900,800));
		
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.add(Box.createVerticalGlue());
		JButton buttonAddLine = new JButton("Add New Parameter");
		buttonAddLine.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ParameterColumn pc = new ParameterColumn(""); 
				parameterList.add(pc);
				fillScrollPane();
			}
		});
		horizontalBox.add(buttonAddLine);
		newLinePanel.add(horizontalBox);
		verticalBox.add(Box.createVerticalStrut(20));
		verticalBox.add(scrollPane);
		middlePart.add(newLinePanel);
		return retPanel;
	}
	
	private void fillScrollPane(){
		middlePart.removeAll();
		for (ParameterColumn pc:parameterList){
			middlePart.add(pc);
			middlePart.add(Box.createVerticalStrut(10));
		}
		middlePart.add(newLinePanel);
		middlePart.add(Box.createVerticalGlue());
		mfe.repaintOptionPanel();
	}
	
	

	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new IterableInputGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[2+parameterList.size()];
		tempString[0] = iterationTagLoop.getText();
		tempString[1] = iterationTagParameterFields.getText();
		for (int i = 0; i<parameterList.size();i++){
			tempString[i+2] = parameterList.get(i).getParameter();
		}
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		iterationTagLoop.setText(tempString[0]);
		iterationTagParameterFields.setText(tempString[1]);
		for (int i = 2; i<tempString.length;i++){
			parameterList.add(new ParameterColumn(tempString[i]));
		}
		fillScrollPane();
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof IterableInputGUI){
			IterableInputGUI returnObject = new IterableInputGUI(mfe);
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
	
	private void removeColumn(ParameterColumn column){
		parameterList.remove(column);
		fillScrollPane();
		mfe.repaintOptionPanel();
	}
	
	class ParameterColumn extends JPanel{
		ParameterColumn selfReference;
		JTextField parameter;
		
		public ParameterColumn(String string) {
			selfReference = this;
			//this.setBorder(BorderFactory.createLineBorder(Color.black));
			Box horizontalBox = Box.createHorizontalBox();
			parameter = new JTextField(string);
			parameter.setMinimumSize(new Dimension(250,20));
			parameter.setColumns(20);
			horizontalBox.add(parameter);
			horizontalBox.add(Box.createHorizontalStrut(20));
			JButton removeButton = new JButton("X");
			removeButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					removeColumn(selfReference);
				}
			});
			horizontalBox.add(removeButton);
			this.add(horizontalBox);
			this.setMaximumSize(new Dimension(300,40));
		}
		public String getParameter(){
			return parameter.getText();
		}
		public void setParameter(String parameter){
			this.parameter.setText(parameter);
		}
	}

}
