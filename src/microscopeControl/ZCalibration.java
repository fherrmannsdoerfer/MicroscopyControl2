package microscopeControl;
import ij.ImagePlus;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import utility.Utility;

public class ZCalibration extends JPanel {

	MainFrame mf;
	
	JButton btnSetUpperBound;
	JButton btnSetLowerBound;
	JButton btnRecordStack;
	JTextField txtUpperBound;
	JTextField txtLowerBound;
	JComboBox comboBoxStepSizeCalibration;
	
	//Conversion factor from nanometer to micrometer
	double scale = 1000.0;
	//default stepsize in micrometer (1 nm)
	double calibStepSize =  0.001;

	public ZCalibration(MainFrame mf, Dimension minSize, Dimension prefSize, Dimension maxSize){
		this.mf = mf;
		setMinimumSize(minSize);
		setPreferredSize(prefSize);
		setMaximumSize(maxSize);
		setBorder(new TitledBorder(null, "Z-Calibration", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
		setLayout(new GridLayout(5, 2,40,15));

		JLabel lblBorder = new JLabel("Range for recorded Z-Stack");
		
		btnSetUpperBound = new JButton("Set Upper Bound");
		btnSetUpperBound.addActionListener(new BtnSetUpperBound_ActionListener());
		
		txtUpperBound = new JTextField();
		txtUpperBound.setText("100");
		Utility.setFormatTextFields(txtUpperBound, 30, 20, 5);
		Box horizontalBoxUpperBound = Box.createHorizontalBox();
		horizontalBoxUpperBound.add(Box.createHorizontalGlue());
		horizontalBoxUpperBound.add(txtUpperBound);
		
		btnSetLowerBound = new JButton("Set Lower Bound");
		btnSetLowerBound.addActionListener(new BtnSetLowerBound_ActionListener());
		
		txtLowerBound = new JTextField();
		txtLowerBound.setText("0");
		Utility.setFormatTextFields(txtLowerBound, 30, 20, 5);
		Box horizontalBoxLowerBound = Box.createHorizontalBox();
		horizontalBoxLowerBound.add(Box.createHorizontalGlue());
		horizontalBoxLowerBound.add(txtLowerBound);
		
		JLabel lblStepSize = new JLabel("Step Size:");
		
		comboBoxStepSizeCalibration = new JComboBox();
		comboBoxStepSizeCalibration.setPreferredSize(new Dimension(80, 22));
		((JLabel)comboBoxStepSizeCalibration.getRenderer()).setHorizontalAlignment(SwingConstants.RIGHT);
		
		comboBoxStepSizeCalibration.addItem("1 nm");
		comboBoxStepSizeCalibration.addItem("5 nm");
		comboBoxStepSizeCalibration.addItem("10 nm");
		comboBoxStepSizeCalibration.addItem("20 nm");
		comboBoxStepSizeCalibration.addItem("50 nm");
		comboBoxStepSizeCalibration.addItem("100 nm");
		comboBoxStepSizeCalibration.addActionListener(new ComboBoxStepSizeCalibrationActionListener());
		
		btnRecordStack = new JButton("Record Stack");
		btnRecordStack.addActionListener(new BtnRecordStack_ActionListener());
		
		this.add(lblBorder);
		this.add(new JLabel());
		this.add(btnSetUpperBound);
		this.add(horizontalBoxUpperBound);
		this.add(btnSetLowerBound);
		this.add(horizontalBoxLowerBound);
		this.add(lblStepSize);
		this.add(comboBoxStepSizeCalibration);
		this.add(new JLabel());
		this.add(btnRecordStack);
		
	}
	
	class BtnSetUpperBound_ActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			try {
				if (mf.getZStagePosition()>0 && mf.getZStagePosition() < 100 && mf.getZStagePosition()> Double.valueOf(txtLowerBound.getText())) {
					txtUpperBound.setText(String.valueOf(mf.getZStagePosition()));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
	};
	
	class BtnSetLowerBound_ActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				if (mf.getZStagePosition()>0 && mf.getZStagePosition() < 100 && mf.getZStagePosition()< Double.valueOf(txtUpperBound.getText())) {
					txtLowerBound.setText(String.valueOf(mf.getZStagePosition()));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
	};
	
	class BtnRecordStack_ActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			Thread CalibStackThread;
			try {
				CalibStackThread = new Thread(new CalibStack());
				//livePreviewThread = new Thread(new LivePreviewWithoutCamera());
				CalibStackThread.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	};
	
	class ComboBoxStepSizeCalibrationActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			String selectedItem = (String) comboBoxStepSizeCalibration.getSelectedItem();
			int stepSize = 1;
			if (selectedItem.equalsIgnoreCase("1 nm")) {stepSize = 1;}
			if (selectedItem.equalsIgnoreCase("5 nm")) {stepSize = 5;}
			if (selectedItem.equalsIgnoreCase("10 nm")) {stepSize = 10;}
			if (selectedItem.equalsIgnoreCase("20 nm")) {stepSize = 20;}
			if (selectedItem.equalsIgnoreCase("50 nm")) {stepSize = 50;}
			if (selectedItem.equalsIgnoreCase("100 nm")) {stepSize = 100;}
			calibStepSize = stepSize/scale;
		}
		
	};
	
	class CalibStack implements Runnable {
		public CalibStack(){}
		
		public void run(){
			String path = mf.getOutputFolder();
			boolean success = OutputControl.createFolder(path+"\\Calibration");
			
			double start = Double.parseDouble(txtLowerBound.getText());
			double ende = Double.parseDouble(txtUpperBound.getText());
			int numberFrames = (int)((ende - start)/calibStepSize);
			mf.setAction("Acquisition of calibration data");
			for (double i = 0, counter = 0; i<ende-start;i=i+calibStepSize, counter ++) {
				try {
					mf.setZStagePosition(start+i);
					mf.setFrameCount(String.valueOf((int)(counter))+ " \\ "+String.valueOf(numberFrames));
					mf.sleep(50);

					String fname = path+"\\Calibration\\calib_"+String.format("_%05d", (int)(i*scale))+".tiff";
					ImagePlus img = mf.captureImage();
					OutputControl.saveSingleImage(img,fname);
					mf.showCurrentImage(img);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
}