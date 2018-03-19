package editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import utility.Utility;


public abstract class EditorModules extends JPanel implements PropertyChangeListener, Transferable, Serializable{
	/**
	 *Parent class for all modules of the experiment editor.  
	 *Each module has a button which when pressed shows the option pane for the module.
	 *There is also a progress bar which is disabled for certain subclasses like the commentaryBar class
	 *All information about the size comes from the StyleClass and can be altered there
	 */
	private static final long serialVersionUID = 1L;
	private int id = 0;
	public JButton parameterButton;
	public JButton removeButton;
	public transient MainFrameEditor mfe;
	private EditorModules thisModule;
	private Color color;
	private boolean visibilityOptionPanel = false;
	//option panel is displayed on the right side and holds the parameters
	private JPanel optionPanel;
	private JProgressBar progressbar;
	private int indentation = 1;
	//jpanel containing the buttons and progressbar but not the possible right shift
	private JPanel module = new JPanel();
	//when a module is placed within a loop it shifted to the right, this is managed in adding invisible
	//placehoders in the indent component
	Component indent;
	Dimension dimensionWholeBox;
	Dimension dimensionModule;
	boolean useFullWidth = false;
	public EditorModules(){};
	
	public EditorModules(final MainFrameEditor mfe){
		thisModule = this;
		this.mfe = mfe;
		
		this.addMouseListener(new MyDraggableMouseListener());
		this.setTransferHandler(new DragAndDropTransferHandler());
	

		parameterButton = new JButton();
		parameterButton.setBackground(null);
		parameterButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				mfe.hideAllOptionPanels();
				setVisibilityOptionPanel(true);
				mfe.repaint();
			}
		});
				
		removeButton = new JButton();
		
		removeButton.setPressedIcon(createIcon("removeButtonPressedAlternativ2.png"));
		removeButton.setIcon(createIcon("removeButtonNormalAlternativ2.png"));
		        
		removeButton.setBorder(null);
		removeButton.setBackground(null);
		//removeButton.setForeground(mf.style.getRemoveButtonColor());
		removeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				mfe.optionPanel.remove(optionPanel);
				mfe.removePanel(thisModule);
				mfe.repaint();
			}
		});
		
		
		progressbar = new JProgressBar(0,100);
		progressbar.setValue(0);
		
		assembleModule();
	}
	
	//in this function the module is constructed
	private void assembleModule(){
		this.removeAll();
		module.removeAll();
		//this.setBorder(BorderFactory.createLineBorder(Color.black));
		Box outerHorizontalBox = Box.createHorizontalBox();	
		Box horizontalBox = Box.createHorizontalBox();
		Box verticalBox = Box.createVerticalBox();
		
		
		
		Component ui = Box.createVerticalStrut(mfe.style.getUpperIndent());
		indent = Box.createHorizontalStrut(indentation*mfe.style.getDefaultIndentation());
		if (useFullWidth) {
			indent = Box.createHorizontalStrut(0*mfe.style.getDefaultIndentation());
		}
		verticalBox.setPreferredSize(dimensionModule);
		verticalBox.add(ui);
		verticalBox.add(horizontalBox);
		module.add(verticalBox);
		outerHorizontalBox.add(indent);
		outerHorizontalBox.add(module);
		
		if (useFullWidth) {
			dimensionWholeBox = new Dimension(mfe.style.getDimensionSelectedModules().width-30,mfe.style.getHeightProcessingStepsPanel());
			dimensionModule = new Dimension(mfe.style.getDimensionSelectedModules().width-50,mfe.style.getHeightProcessingStepsPanel());
		} else {
			dimensionWholeBox = new Dimension(mfe.style.getWidthEditorModules()+indentation*mfe.style.getDefaultIndentation(),mfe.style.getHeightProcessingStepsPanel());
			dimensionModule = new Dimension(mfe.style.getWidthEditorModules(),mfe.style.getHeightProcessingStepsPanel());
		}
		Component ls = Box.createHorizontalStrut(mfe.style.getLeftIndent());
		horizontalBox.add(ls);
		horizontalBox.add(Box.createHorizontalGlue());
		horizontalBox.add(parameterButton);
		Component hg = Box.createHorizontalGlue();
		horizontalBox.add(hg);
		horizontalBox.add(removeButton);
		Component rs = Box.createHorizontalStrut(mfe.style.getRightIndent());
		horizontalBox.add(rs);
		
		Component verticalGlue = Box.createVerticalGlue();
		verticalBox.add(verticalGlue);
		Box hb2 = Box.createHorizontalBox();
		hb2.add(Box.createHorizontalStrut(mfe.style.getLeftIndent()));
		progressbar.setPreferredSize(new Dimension(dimensionModule.width,20));
		hb2.add(progressbar);
		hb2.add(Box.createHorizontalStrut(mfe.style.getRightIndent()));
		verticalBox.add(hb2);
		Component li = Box.createVerticalStrut(mfe.style.getLowerIndent());
		verticalBox.add(li);
		
		this.add(outerHorizontalBox);
		//outerHorizontalBox.setBackground(Color.cyan);
		
		this.setMaximumSize(dimensionWholeBox);
		this.setPreferredSize(dimensionWholeBox);
		mfe.repaintOptionPanel();
	}

	
	public void setParameterButtonsName(String name){
		parameterButton.setText(name);
	}
	
	public void setColor(Color color){
		this.color = color;
		module.setBackground(color);
	}
	
	//needed for drag and drop
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] flavors ={null};
		 try {
	            flavors[0] = MainFrameEditor.getDragAndDropPanelDataFlavor();
	        } catch (Exception ex) {
	            ex.printStackTrace(System.err);
	            return null;
	        }
		return null;
	}

	//needed for drag and drop
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		DataFlavor[] flavors = {null};
        try {
            flavors[0] = MainFrameEditor.getDragAndDropPanelDataFlavor();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return false;
        }

        for (DataFlavor f : flavors) {
            if (f.equals(flavor)) {
                return true;
            }
        }

        return false;
	}
	//needed for drag and drop
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
       
        DataFlavor thisFlavor = null;

        try {
            thisFlavor = MainFrameEditor.getDragAndDropPanelDataFlavor();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
        
        if (thisFlavor != null && flavor.equals(thisFlavor)) {
            return EditorModules.this;
        }

        return null;
	}

	public JPanel getOptionPanel() {
		return optionPanel;
	}

	//once the parameter button is clicked the option pane corresponding to this module is displayed on the right column
	public void setOptionPanel(JPanel optionPanel) {
		this.optionPanel = optionPanel;
		mfe.optionPanel.add(optionPanel);
		optionPanel.setVisible(visibilityOptionPanel);
		mfe.hideAllOptionPanels();
		setVisibilityOptionPanel(true);
		mfe.repaint();
	}

	public boolean getIsVisibilityOptionPanel() {
		return visibilityOptionPanel;
	}

	public void setVisibilityOptionPanel(boolean visibilityOptionPanel) {
		this.visibilityOptionPanel = visibilityOptionPanel;
		optionPanel.setVisible(visibilityOptionPanel);
	}
	
	public void setProgressbarValue(int val){
		//System.out.println(val);
		progressbar.setValue(val);
	}
	
	public void getTextFieldTexts(JTextField[] listTextFields, int nbrOtherComponents, String[] parameterList){
		for (int i = 0; i<listTextFields.length; i++){
			parameterList[i+nbrOtherComponents] = listTextFields[i].getText();
		}
	}
		
	public void setTextFieldTexts(JTextField[] listTextFields, int nbrOtherComponents, String[] parameterList){
		for (int i = 0; i<listTextFields.length; i++){
			listTextFields[i].setText(parameterList[i+nbrOtherComponents]);
		}
	}
	public int getIndentation(){
		return indentation;
	}
	
	public void setIndentation(int ind){
		this.indentation = ind;
		assembleModule();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		int progress = (Integer) evt.getNewValue();
        setProgressbarValue(progress);
	}
	
	//abstract functions must be implemented by each subclass
	abstract public EditorModules getFunction(MainFrameEditor mfe);
	//this class returns the chosen parameters as a array of Strings information about checked checkboxes is also stored as a String
	abstract public String[] getSettings();
	//Once a module is imported or leaded the default parameters are overwritten with the loaded ones
	abstract public void setSettings(String[] tempString);
	abstract public EditorModules getEditorModulesObject(EditorModules processingStepsPanelObject, MainFrameEditor mfe);
	abstract public String getFunctionName();
	//This function contains the individual instructions for each module. For example in the PauseGUI module a Thread.sleep command is executed at this position
	abstract public void perform();
	//function used to perform a coarse check if every field has entries in it
	abstract public boolean checkForValidity();
	
	//logTimeStart-End are functions that are called at the beginning and the end of the perform block and will write a line in a logfile from which the order and runtime of the modules can be inferred
	public void logTimeStart() {
		String content = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		content = content + ":\nstart of module: " + this.getFunctionName();
		mfe.getMainFrameReference().writeToEditorLogfile(content);
		logParams();
	}
	
	public void logParams() {
		String[] params = this.getSettings();
		String content = "";
		for (int i =0;i<params.length;i++) {
			content = content+params[i]+" , ";
		}
		content = content + "\n";
		mfe.getMainFrameReference().writeToEditorLogfile(content);
	}
	
	public void logTimeEnd() {
		String content = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		content = content + ":\nend of module: " + this.getFunctionName()+"\n";
		mfe.getMainFrameReference().writeToEditorLogfile(content);
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	//used to load the pictures for the remove button
	protected Icon createIcon(String name) {	    
		try {
			Image img = ImageIO.read(ClassLoader.getSystemResourceAsStream(name));
			Icon icon = new ImageIcon(img);
	        return icon;
		} catch (IOException e) {
			 System.err.println("Couldn't find file: " + name);
		     return null;
		}
	}
	
	public void setProgressBarInvisible() {
		progressbar.setVisible(false);
	}
	
	public void setButtonText(String text) {
		parameterButton.setText(text);
	}

	public void setModuleToFullWidth() {
		useFullWidth = true;
	}

	public boolean isFullWidth() {
		return useFullWidth;
	}
	
	//the following methods check the validity of selected parameters getVolume for example knows which syringe will be used and
	//checks if the requested volume is within the possible range of volumes that the syring can hold
	
	//getVolume takes two parameters, first the text field from which the quantity (volume in this case) is extracted. Another parameter is whether or not the LS2 tool is used

	protected int getVolume(JTextField textfield, boolean useLS2) {
		//The volume will be an integer value (ganze Zahl). Integer.parseInt() is a function that is used
		//to convert a String (text) into a number. 
		//Utility.parseParameter(textfield.getText(),mfe) this does either return the input of the text field
		//e.g. "200" or if tags (e.g. %volume%) were used the correct String from the Loop which defined this specific tag.
		//textfield.getText() will give the entry entered in the text field (e.g. "200" or "%volume%"). The additional
		//argument (mfe) is a reference to the main class of the experiment editor and is needed to look up 
		//the value of the tag if one was used. 
		//the method which looks up which value corresponds to which tag is located in the Utility package and called
		//parseParameter().
		//For example lets assume in the given text field was "%volume% written.
		//1. textfield.getText() returns "%volume%
		//2. Utility.parseParameter("%volume%", mfe) finds the loop that defined the tag "%volume%" and yields the value specified there e.g. "255"
		//3. Integer.parseInt("255") returns the integer 255 
		//4. intVolume is created and the integer value 255 is assigned to it
		
		//the four steps above would have looked similar if the value "255" would have been direcly written to the textfield,
		//the function Utility.parseParameter(textfield.getText(), mfe) would have returned "255" in that case and would not have looked in any loop for a tag
		
		int intVolume = Integer.parseInt(Utility.parseParameter(textfield.getText(),mfe));
		//depending on the tool used (LS1 or LS2) the upper limit for the volume is specified 100 in case of LS1 
		int limit = 100;
		//1000 in case of LS2
		if (useLS2) {
			limit = 1000;
		}
		//Here it is checked if the given value of intVolume lies within the allowed limits
		if (intVolume<1 || intVolume>limit) {//if not an error is displayed and -1 is returned as the volume to use which will most likely stop the execution of the sampleList in Chronos
			System.err.println("Volume is not within limits of 1 to "+limit+"!");
			return -1;
		} else {//otherwise the correct value is returned
			return intVolume;
		}	
	}
	
	protected int getVialNumber(JTextField vialNumber) {
		int intVialNumber = Integer.parseInt(Utility.parseParameter(vialNumber.getText(),mfe));
		if (intVialNumber<1 || intVialNumber>54) {
			System.err.println("Vial Number is not within limits of 1 to 54!");
			return -1;
		} else {
			return intVialNumber;
		}
		
	}
	
	protected int getPumpTime(JTextField pumptime) {
		int pumpT = Integer.parseInt(Utility.parseParameter(pumptime.getText(),mfe));
		if (pumpT < 0 || pumpT >3600) {
			System.err.println("Pumptime lies not within limits of 0 to 3600!");
			return -1;
		}
		else {return pumpT;}
	}
	
	protected int getIndex(JTextField washingStationIndex) {
		int index = Integer.parseInt(Utility.parseParameter(washingStationIndex.getText(),mfe));
		if (index<1 || index>2) {
			System.err.println("Index of Washing Station must be 1 or 2!");
			return -1;
		} else {
			return index;
		}
		
	}
	protected int getNbrCycles(JTextField cycles) {
		int index = Integer.parseInt(Utility.parseParameter(cycles.getText(),mfe));
		if (index<0 || index>100) {
			System.err.println("Number vortex-cycles must be between 0 and 100!");
			return -1;
		} else {
			return index;
		}
	}
	protected int getVolumePerSpot(JTextField volumePerSpot) {
		int volume = Integer.parseInt(Utility.parseParameter(volumePerSpot.getText(),mfe));
		if (volume<1 || volume>250) {
			System.err.println("Volume per spot must be in range of 1 to 250!");
			return -1;
		} else {
			return volume;
		}
	}
	
	protected double getPositiveValue(JTextField valueTxt) {
		double value = Double.parseDouble(Utility.parseParameter(valueTxt.getText(), mfe));
		if (value >=0) {
			return value;
		} else {
			return -1;
		}
	}
}
