package microscopeControl;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;



import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import dataTypes.CameraParameters;
import utility.Utility;
import mmcorej.CMMCore;


public class CameraParameter extends JPanel {
	//boolean used to terminate the TimeLoop thread
	boolean threadShouldStayRunning = true;
	//object used to control all hardware
	CMMCore core;
	//name of the camera set in Micro-Manager
	String camName;
	
	MainFrame mf;
	
	JLabel lblCurrTemp;
	
	JTextField txtEmGain;
	JTextField txtExposureTime;
	JTextField txtSetTemp;
	JTextField txtNumberFrames;
	
	JComboBox comboBoxShutter;
	
	JCheckBox chkboxFrameTransfer;
	
	public CameraParameter(MainFrame mf, Dimension minSize, Dimension prefSize, Dimension maxSize){
		this.mf = mf;
		core = mf.getCoreObject();
		setMinimumSize(minSize);
		setPreferredSize(prefSize);
		setMaximumSize(maxSize);
		setBorder(new TitledBorder(null, "Camera Control", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
		
		setLayout(new GridLayout(6, 2,40,15));
		
		this.camName = mf.getCamName();
		
		JLabel lblEmGain = new JLabel("EM gain");
		
		txtEmGain = new JTextField();
		txtEmGain.setText("10");
		txtEmGain.setHorizontalAlignment(JTextField.RIGHT);
		txtEmGain.setMaximumSize(new Dimension(30, 20));
		txtEmGain.setMinimumSize(new Dimension(30, 20));
		txtEmGain.setColumns(5);
		Box horizontalBoxEmGain = Box.createHorizontalBox();
		horizontalBoxEmGain.add(Box.createHorizontalGlue());
		horizontalBoxEmGain.add(txtEmGain);

		JLabel lblExposureTime = new JLabel("Exposure time");
		
		txtExposureTime = new JTextField();
		txtExposureTime.setText("100");
		Utility.setFormatTextFields(txtExposureTime, 30, 20, 5);
		Box horizontalBoxExposure = Box.createHorizontalBox();
		horizontalBoxExposure.add(Box.createHorizontalGlue());
		horizontalBoxExposure.add(txtExposureTime);
			
		JLabel lblTemperature = new JLabel("Temperature");
		
		Box horizontalBoxLabelTemp = Box.createHorizontalBox();
		horizontalBoxLabelTemp.add(new JLabel("Target:"));
		horizontalBoxLabelTemp.add(Box.createHorizontalGlue());
		horizontalBoxLabelTemp.add(new JLabel("Current:"));
		
		txtSetTemp = new JTextField();
		txtSetTemp.setText("-70");
		Utility.setFormatTextFields(txtSetTemp, 30, 20, 3);
		lblCurrTemp = new JLabel("New label");
		lblCurrTemp.setHorizontalAlignment(JLabel.RIGHT);
		
		Box horizontalBoxTargetTempTemp = Box.createHorizontalBox();
		horizontalBoxTargetTempTemp.add(txtSetTemp);
		horizontalBoxTargetTempTemp.add(Box.createHorizontalGlue());
		horizontalBoxTargetTempTemp.add(lblCurrTemp);
		
		JLabel lblShutter = new JLabel("Shutter");
		
		String options[] = {"open","closed"};
		comboBoxShutter = new JComboBox(options);
		comboBoxShutter.addActionListener(comboBoxShutterActionListener);

		chkboxFrameTransfer = new JCheckBox("Frame transfer");
		chkboxFrameTransfer.setSelected(true);
		chkboxFrameTransfer.addActionListener(chkboxFrameTransferActionListener);
		
		Box horizontalBoxShutterComboBox = Box.createHorizontalBox();
		horizontalBoxShutterComboBox.add(lblShutter);
		horizontalBoxShutterComboBox.add(new JLabel("  "));
		horizontalBoxShutterComboBox.add(comboBoxShutter);
		
		JLabel lblNumberFrames = new JLabel("Number of Frames");
		
		txtNumberFrames = new JTextField();
		txtNumberFrames.setText("1000");
		Utility.setFormatTextFields(txtNumberFrames, 30, 20, 5);
		Box horizontalBoxNumberOfFrames = Box.createHorizontalBox();
		horizontalBoxNumberOfFrames.add(Box.createHorizontalGlue());
		horizontalBoxNumberOfFrames.add(txtNumberFrames);
				
		this.add(lblEmGain);
		this.add(horizontalBoxEmGain);
		this.add(lblExposureTime);
		this.add(horizontalBoxExposure);
		this.add(lblTemperature);
		this.add(horizontalBoxLabelTemp);
		this.add(new JLabel());
		this.add(horizontalBoxTargetTempTemp);
		this.add(horizontalBoxShutterComboBox);
		this.add(chkboxFrameTransfer);
		this.add(lblNumberFrames);
		this.add(horizontalBoxNumberOfFrames);
		
		Thread timeLoopThread = new Thread(new TimeLoop(mf));
		timeLoopThread.start();
		
	}
	
	ActionListener txtSetTempActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			try {
				if ( Integer.parseInt(txtSetTemp.getText())>-80 &&  Integer.parseInt(txtSetTemp.getText())<30) {
					core.setProperty(camName, "CCDTemperatureSetPoint", Integer.parseInt(txtSetTemp.getText()));
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
		}
	};
	
	ActionListener comboBoxShutterActionListener =new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			if (comboBoxShutter.getSelectedItem().toString().equals("open")){
				mf.setLivePreviewRunning(false);
				mf.setEnableStartLivePreviewButton(true);
				mf.openShutter();
			}
			else {
				mf.setLivePreviewRunning(true);
				mf.setEnableStartLivePreviewButton(true);
				mf.closeShutter();
			}
		}
	};
	
	
	ActionListener chkboxFrameTransferActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			try{
				if (chkboxFrameTransfer.isSelected()) {
					core.setProperty(camName, "FrameTransfer", "On");
				}
				else {
					core.setProperty(camName, "FrameTransfer", "Off");
				}
			}
			catch (Exception e2){
				e2.printStackTrace();
			}
		}
	};
	
	//Thread that checks and compares the current temperature of the camera 
	class TimeLoop implements Runnable {
		MainFrame mf;
		public TimeLoop(MainFrame mf){this.mf=mf;}
		
		public void run(){
			while (threadShouldStayRunning) {
				try {
					String currTemp = core.getProperty(camName, "CCDTemperature");
					lblCurrTemp.setText(currTemp);
					if (Integer.parseInt(core.getProperty(camName, "CCDTemperature"))-Integer.parseInt(currTemp) != 0){
						mf.setCameraStatus("Cooling");
					}
					else {
						//lblStatus.setText("Stand by");
						mf.setCameraStatus("Stand by");
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					threadShouldStayRunning = false;
					e.printStackTrace();
				}
				
			}
		}
	}
	
	public CameraParameters getCameraParameter(){
		return new CameraParameters(getExposureTime(), getEmGain(), getFrameNumber(), isShutterOpen());
	}

	public void setCameraParameters(CameraParameters camParam){
		setEmGain(camParam.getEmGain());
		setExposureTime(camParam.getExposureTime());
		setFrameNumber(camParam.getNbrFrames());
		setShutter(camParam.isShutterOpen());
	}
	
	public boolean isShutterOpen(){
		return comboBoxShutter.getSelectedItem().toString().equals("open");
	}
	public int getEmGain() {return Integer.valueOf(txtEmGain.getText());}
	public double getExposureTime() {return Double.valueOf(txtExposureTime.getText());}
	public boolean isFrameTransferSelected() {return chkboxFrameTransfer.isSelected();}
	public int getFrameNumber() {return Integer.valueOf(txtNumberFrames.getText());}
	public void setEmGain(int emGain){txtEmGain.setText(""+emGain);}
	public void setExposureTime(double exposureTime) {txtExposureTime.setText(""+exposureTime);}
	public void setFrameNumber(int frameNumber){txtNumberFrames.setText(""+frameNumber);}
	public void setShutter(boolean isOpen){if (isOpen){comboBoxShutter.setSelectedIndex(0);}else{comboBoxShutter.setSelectedIndex(1);}}
	
}
