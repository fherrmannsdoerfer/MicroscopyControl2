package microscopeControl;
import ij.ImagePlus;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;


//Provides buttons to control the camere
//start, stop image acquisition and live preview
//and capture Widefield image
public class CameraControl extends JPanel{

	MainFrame mf;
	JButton btnStartLivePreview;
	JButton btnStopLivePreview;
	JButton btnStartAcquisition;
	JButton btnStopAcquisition;
	JButton btnCaptureSingleImage;
	
	public CameraControl(MainFrame mf, Dimension minSize, Dimension prefSize, Dimension maxSize){
			this.mf = mf;
			setMinimumSize(minSize);
			setPreferredSize(prefSize);
			setMaximumSize(maxSize);
			setBorder(new TitledBorder(null, "Camera Control", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
			setLayout(new GridLayout(3, 3,40,10));
			
			
			btnStartLivePreview = new JButton("Start Live Preview");
			btnStartLivePreview.addActionListener(new BtnStartLivePreviewActionListener());
			
			btnStopLivePreview = new JButton("Stop Live Preview");
			btnStopLivePreview.addActionListener(new BtnStopLivePreviewActionListener());
			
			btnStartAcquisition = new JButton("Start Acquisition");
			btnStartAcquisition.addActionListener(new BtnStartAcquisitionActionListener());
			
			btnStopAcquisition = new JButton("Stop Acquisition");
			btnStopAcquisition.addActionListener(new BtnStopAcquisitionActionListener());
			
			btnCaptureSingleImage = new JButton("Capture Single Image");
			btnCaptureSingleImage.addActionListener(new BtnCaptureSingleImageActionListener());
			
			this.add(btnStartLivePreview);
			this.add(btnStopLivePreview);
			this.add(btnStartAcquisition);
			this.add(btnStopAcquisition);
			this.add(btnCaptureSingleImage);
			
	}
	
	class BtnStartAcquisitionActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			mf.startSequenceAcquisition();
		}
	};

	class BtnStopAcquisitionActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			try {
				mf.stopSequenceAcquisition();
			} catch (Exception e) {
				e.printStackTrace();
			}
			btnStartAcquisition.setEnabled(true);
		}
	};
	
		
	class BtnStartLivePreviewActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			try {
				mf.startLivePreview();
			} catch (Exception e) {
				e.printStackTrace();
			}
			btnStartLivePreview.setEnabled(false);
		}
	};
	
	class BtnStopLivePreviewActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			try {
				mf.stopLivePreview();
			} catch (Exception e) {
				e.printStackTrace();
			}
			btnStartLivePreview.setEnabled(true);
		}
	};
	
	class BtnCaptureSingleImageActionListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			ImagePlus img = mf.captureImage();
			int counter = 0;
    		while(true){
    			counter = counter + 1;
    			File f = new File(mf.getOutputFolder()+"\\widefieldimg_"+counter+"_"+mf.getMeasurementTag()+".tiff");
    			if (f.exists()){}
    			else{
    				OutputControl.saveSingleImage(img, mf.getOutputFolder()+"\\WidefieldImage_"+counter+"_"+mf.getMeasurementTag()+".tiff");
    				break;
    			}
    		}
		}
	};

	public void setStartAcquisitionButtonState(boolean state) {this.btnStartAcquisition.setEnabled(state);}
	public void setStopAcquisitionButtonState(boolean state) {this.btnStopAcquisition.setEnabled(state);}
	public void setStartLivePreviewButtonState(boolean state) {this.btnStartLivePreview.setEnabled(state);}
	public void setStopLivePreviewButtonState(boolean state) {this.btnStopLivePreview.setEnabled(state);}
	
}
