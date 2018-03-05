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
import java.util.ArrayList;

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


public abstract class EditorModules extends JPanel implements PropertyChangeListener, Transferable, Serializable{
	/**
	 * 
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
	abstract public EditorModules getFunction(MainFrameEditor mfe);
	abstract public String[] getSettings();
	abstract public void setSettings(String[] tempString);
	abstract public EditorModules getEditorModulesObject(EditorModules processingStepsPanelObject, MainFrameEditor mfe);
	abstract public String getFunctionName();
	abstract public void perform();
	abstract public boolean checkForValidity();
	
	
	/** Returns an ImageIcon, or null if the path was invalid. */
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
	
	protected int getVolume(JTextField textfield, boolean useLS2) {
		int intVolume = Integer.parseInt(textfield.getText());
		int limit = 100;
		if (useLS2) {
			limit = 1000;
		}
		if (intVolume<1 || intVolume>limit) {
			System.err.println("Volume is not within limits of 1 to "+limit+"!");
			return -1;
		} else {
			return intVolume;
		}	
	}
	
	protected int getVialNumber(JTextField vialNumber) {
		int intVialNumber = Integer.parseInt(vialNumber.getText());
		if (intVialNumber<1 || intVialNumber>54) {
			System.err.println("Vial Number is not within limits of 1 to 54!");
			return -1;
		} else {
			return intVialNumber;
		}
		
	}
	
	protected int getIndex(JTextField washingStationIndex) {
		int index = Integer.parseInt(washingStationIndex.getText());
		if (index<1 || index>2) {
			System.err.println("Index of Washing Station must be 1 or 2!");
			return -1;
		} else {
			return index;
		}
		
	}
	protected int getNbrCycles(JTextField cycles) {
		int index = Integer.parseInt(cycles.getText());
		if (index<1 || index>100) {
			System.err.println("Number vortex-cycles must be between 1 and 100!");
			return -1;
		} else {
			return index;
		}
	}
	protected int getVolumePerSpot(JTextField volumePerSpot) {
		int volume = Integer.parseInt(volumePerSpot.getText());
		if (volume<1 || volume>250) {
			System.err.println("Volume per spot must be in range of 1 to 250!");
			return -1;
		} else {
			return volume;
		}
	}
}
