package microscopeControl;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import utility.Utility;
import dataTypes.ROIParameters;
import mmcorej.CMMCore;


public class ROISettings extends JPanel{
	//object used to control all hardware
	CMMCore core;
	
	MainFrame mf;
	
	JComboBox comboBoxWhichPart;
	JCheckBox chckbxShowMiddleLine;
	JCheckBox chckbxApplyRect; 
	
	JTextField txtPosX;
	JTextField txtPosY;
	JTextField txtShiftX;
	JTextField txtShiftY;
	JTextField txtWidth;
	JTextField txtHeight;
	
	public ROISettings(MainFrame mf, Dimension minSize, Dimension prefSize, Dimension maxSize){
		this.mf = mf;
		setMinimumSize(minSize);
		setPreferredSize(prefSize);
		setMaximumSize(maxSize);
		setBorder(new TitledBorder(null, "ROI Settings", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
		setLayout(new GridLayout(6, 2,10,5));
		
		ArrayList<JLabel> labelList = new ArrayList<JLabel>();
		labelList.add(new JLabel("X:"));
		labelList.add(new JLabel("Width:"));
		labelList.add(new JLabel("Y:"));
		labelList.add(new JLabel("Height:"));
		labelList.add(new JLabel("Shift X:"));
		labelList.add(new JLabel("Shift Y:"));


		ArrayList<JTextField> textFieldList = new ArrayList<JTextField>();
		txtPosX = new JTextField("0");
		txtPosY = new JTextField("0");
		txtShiftX = new JTextField("256");
		txtShiftY = new JTextField("0");
		txtWidth = new JTextField("256");
		txtHeight = new JTextField("512");
		
		Utility.setFormatTextFields(txtPosX, 30, 20, 5);
		Utility.setFormatTextFields(txtPosY, 30, 20, 5);
		Utility.setFormatTextFields(txtShiftX, 30, 20, 5);
		Utility.setFormatTextFields(txtShiftY, 30, 20, 5);
		Utility.setFormatTextFields(txtWidth, 30, 20, 5);
		Utility.setFormatTextFields(txtHeight, 30, 20, 5);
		textFieldList.add(txtPosX);
		textFieldList.add(txtWidth);
		textFieldList.add(txtPosY);
		textFieldList.add(txtHeight);
		textFieldList.add(txtShiftX);
		textFieldList.add(txtShiftY);
		
		
		
		Vector<String> items = new Vector<String>();
		items.add("Both Channels");
		items.add("Left Channel Only");
		items.add("Right Channel Only");
		comboBoxWhichPart = new JComboBox(items);
		comboBoxWhichPart.setSelectedIndex(0);
		
		chckbxShowMiddleLine = new JCheckBox("Show Middle Line");
		chckbxApplyRect = new JCheckBox("Apply Rectangle");
		
		JButton btnClearRect = new JButton("Clear Rect");
		btnClearRect.addActionListener(new btnClearRectActionListener());
		
		for (int i=0; i<6; i++){
			Box hbox = Box.createHorizontalBox();
			hbox.add(labelList.get(i));
			hbox.add(Box.createHorizontalGlue());
			hbox.add(textFieldList.get(i));
			this.add(hbox);
		}
		this.add(new JLabel("Which part?"));
		this.add(comboBoxWhichPart);
		this.add(chckbxShowMiddleLine);
		this.add(chckbxApplyRect);
		this.add(btnClearRect);
		
		
	}
	
	class btnClearRectActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			mf.resetRect();
		}
		
	}

	public int getSelectedChannel() {
		return comboBoxWhichPart.getSelectedIndex();
	}

	//check if the default values have been altered
	public boolean isCustomROISet() {
		return (Integer.parseInt(txtWidth.getText())<256 && Integer.parseInt(txtHeight.getText())<512);
	}

	public boolean isROIApplied() {return chckbxApplyRect.isSelected();}

	public void setROIApplied(boolean state) {chckbxApplyRect.setSelected(state);}

	public boolean isROITooSmall() {return (Integer.parseInt(txtWidth.getText())<50 || Integer.parseInt(txtHeight.getText())<50);}

	public int getROIWidth() {return Integer.parseInt(txtWidth.getText());}
	public int getROIHeight() {return Integer.parseInt(txtHeight.getText());}
	public int getShiftX() {return Integer.parseInt(txtShiftX.getText());}
	public int getShiftY() {return Integer.parseInt(txtShiftY.getText());}
	public int getPosX() {return Integer.parseInt(txtPosX.getText());}
	public int getPosY() {return Integer.parseInt(txtPosY.getText());}
	public ROIParameters getROIParameters() {return new ROIParameters(getROIWidth(), getROIHeight(),getShiftX(), getShiftY(), getPosX(), getPosY());}
}
