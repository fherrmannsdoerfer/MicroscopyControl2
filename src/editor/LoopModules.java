package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import editor.LoopModules.ParameterTag;

public abstract class LoopModules extends EditorModules{
	private int currentIterationStep = 0;
	private int nbrIterations;
	protected ArrayList<ParameterTag> parameterTags = new ArrayList<ParameterTag>();
	protected Box verticalBox2 = Box.createVerticalBox();
	public LoopModules(MainFrameEditor mfe){
		super(mfe);
	}
	
	public LoopModules(){
		super();
	}

	abstract public EditorModules getFunction(MainFrameEditor mfe);

	abstract public String[] getSettings();

	abstract public void setSettings(String[] tempString);

	abstract public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe);

	abstract public String getFunctionName();

	abstract public void perform();
	abstract public void performIncrementalStep();
	abstract public EditorModules getEndLoopModule(MainFrameEditor mfe);
	public ArrayList<String> getParameterTags() {
		ArrayList<String> al = new ArrayList<String>();
		for (ParameterTag pt:parameterTags){
			al.add(pt.getParameterTag());
		}
		return al;
		//return iterationTag.getText();
	}
	public void nextStep(){
		currentIterationStep+=1;
		setProgressbarValue(currentIterationStep*100/nbrIterations);	
	}
	public int getCurrentIterationStep(){return (currentIterationStep%nbrIterations);}//the modulo is needed in case the for loop lies within a forloop to "reset" the counter
	public int getNbrIterations(){return nbrIterations;}
	public void setNbrIterations(int nbrIterations){this.nbrIterations = nbrIterations;}
	
	public ArrayList<ParameterTag> getParameters(){
		return this.parameterTags;
	}
	
	public class ParameterTag extends JPanel implements Serializable{
		JButton addParameterColumn = new JButton("Add New Param");
		private ArrayList<ParameterColumn> parameterList = new ArrayList<ParameterColumn>();
		JScrollPane scrollPane;
		JTextField iterationTagParameterFields = new JTextField();
		JPanel newLinePanel = new JPanel();
		JPanel middlePart;
		transient MainFrameEditor mfe;
		ParameterTag selfReference;
		
		public ParameterTag(MainFrameEditor mfe){
			this.mfe = mfe;
			this.setBorder(BorderFactory.createLineBorder(Color.black));
			selfReference = this;
			JPanel upperPart = new JPanel();
			upperPart.setLayout(new GridLayout(1, 2,60,15));

			upperPart.add(new JLabel("Tag For The Parameter Fields:"));
			upperPart.add(iterationTagParameterFields);
			
			Box verticalBox = Box.createVerticalBox();
			this.add(verticalBox);
			verticalBox.add(upperPart);
			
			middlePart = new JPanel();
			
			verticalBox.add(middlePart);
			middlePart.setLayout(new BoxLayout(middlePart,BoxLayout.Y_AXIS));
			scrollPane = new JScrollPane(middlePart);
			scrollPane.setPreferredSize(new Dimension(200+60,400));
			scrollPane.setMinimumSize(new Dimension(200+30,300));
			scrollPane.setMaximumSize(new Dimension(200+900,800));
			
			Box horizontalBox = Box.createHorizontalBox();
			
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
			horizontalBox.add(Box.createHorizontalStrut(120));
			JButton buttonRemoveThis = new JButton(" X ");
			buttonRemoveThis.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					removeParameterTag(selfReference);
				}
			});
			horizontalBox.add(buttonRemoveThis);
			newLinePanel.add(horizontalBox);
			verticalBox.add(Box.createVerticalStrut(20));
			verticalBox.add(scrollPane);
			middlePart.add(newLinePanel);
		}
		
		public void addRow(String entry) {
			parameterList.add(new ParameterColumn(entry));
		}
		
		public int getNumberOfColumns() {
			return parameterList.size();
		}
		
		public void fillScrollPane(){
			middlePart.removeAll();
			middlePart.add(newLinePanel);
			for (ParameterColumn pc:parameterList){
				middlePart.add(pc);
				middlePart.add(Box.createVerticalStrut(10));
			}
			for (int i = 0;i<100;i++) {
				middlePart.add(Box.createVerticalGlue());
			}

			mfe.repaintOptionPanel();
		}
		
		public void setParameterList(ArrayList<String> parameters){
			for (int i =0; i<parameters.size(); i++){
				ParameterColumn pc = new ParameterColumn(parameters.get(i)); 
				parameterList.add(pc);
			}
		}
		
		public ArrayList<String> getParameterList(){
			ArrayList<String> parameters = new ArrayList<String>();
			for (ParameterColumn pc:parameterList){
				parameters.add(pc.getParameter());
			}
			return parameters;
		}
		
		public String getParameterTag(){
			return iterationTagParameterFields.getText();
		}
		
		public void setParameterTag(String text){
			iterationTagParameterFields.setText(text);
		}
		
		public class ParameterColumn extends JPanel implements Serializable{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
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
		
		
		private void removeColumn(ParameterColumn column){
			parameterList.remove(column);
			fillScrollPane();
			mfe.repaintOptionPanel();
		}
	}
	
	public void createContentVerticalBox2() {
		verticalBox2.removeAll();
		for (int i=0;i<parameterTags.size();i++) {
			verticalBox2.add(parameterTags.get(i));
			verticalBox2.add(Box.createVerticalStrut(15));
		}
		mfe.repaintOptionPanel();
	}
	
	public void addParameterTag(){
		ParameterTag pt = new ParameterTag(mfe); 
		parameterTags.add(pt);
		createContentVerticalBox2();
	}
	
	public void removeParameterTag(ParameterTag pt) {
		parameterTags.remove(pt);
		createContentVerticalBox2();
		mfe.repaintOptionPanel();
	}

	public void replaceParameterTag(ParameterTag pt, int indexOfReplacement){
		try {
			parameterTags.remove(indexOfReplacement);
		} catch (Exception e) {}
		parameterTags.add(indexOfReplacement,pt);
		createContentVerticalBox2();
	}
}
