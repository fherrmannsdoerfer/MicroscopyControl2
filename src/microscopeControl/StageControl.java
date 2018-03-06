package microscopeControl;
import ij.ImagePlus;
import microscopeControl.CameraWorker.LivePreview;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import dataTypes.XYStagePosition;
import utility.Utility;

public class StageControl extends JPanel {
	
	double pixelSizeX = .133;
	double pixelSizeY = .133; //with 3d lense 122 nm
	MainFrame mf;
	
	JButton btnSetUpperLeft;
	JButton btnSetLowerRight;
	JComboBox comboBoxOverlap;
	JLabel lblUpperLeft;
	JLabel lblLowerRight;
	JButton btnStartTileScan;
	JButton btnStopTileScan;
	JTextField txtXPos;
	JTextField txtYPos;
	JButton btnMoveTo;
	
	boolean tileScanRunning;
	
	int[] percentageOverlap = {1,2,5,10,20,40,50};
	
	private XYStagePosition upperLeftCorner;
	private XYStagePosition lowerRightCorner;

	public StageControl(MainFrame mf, Dimension minSize, Dimension prefSize, Dimension maxSize){
		this.mf = mf;
		setMinimumSize(minSize);
		setPreferredSize(prefSize);
		setMaximumSize(maxSize);
		//setBorder(new TitledBorder(null, "Stage Control", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel tileScan = new JPanel();
		tileScan.setBorder(new TitledBorder(null,"Tile Scan", TitledBorder.LEADING, TitledBorder.TOP,mf.getTitelFont(),null));
		tileScan.setLayout(new GridLayout(4, 2,40, 5));

		btnSetUpperLeft = new JButton("Set Upper Left Corner");
		btnSetUpperLeft.addActionListener(new BtnSetUpperLeft_ActionListener());
		btnSetLowerRight = new JButton("Set Lower Right Corner");
		btnSetLowerRight.addActionListener(new BtnSetLowerRight_ActionListener());
		lblUpperLeft = new JLabel("not yet set");
		lblLowerRight = new JLabel("not yet set");
		btnStartTileScan = new JButton("Start Tile Scan");
		btnStartTileScan.addActionListener(new BtnStartTileScan_ActionListener());
		btnStopTileScan = new JButton("Stop Tile Scan");
		btnStopTileScan.addActionListener(new BtnStopTileScan_ActionListener());
		comboBoxOverlap = new JComboBox();
		for (int i = 0; i< percentageOverlap.length; i++){
			comboBoxOverlap.addItem(percentageOverlap[i]+" Percent");
		}
		
		tileScan.add(btnSetUpperLeft);
		tileScan.add(lblUpperLeft);
		tileScan.add(btnSetLowerRight);
		tileScan.add(lblLowerRight);
		tileScan.add(new JLabel("Overlap:"));
		tileScan.add(comboBoxOverlap);
		tileScan.add(btnStartTileScan);
		tileScan.add(btnStopTileScan);
		
		JPanel movementControl = new JPanel();
		movementControl.setBorder(new TitledBorder(null,"Movement Control", TitledBorder.LEADING, TitledBorder.TOP,mf.getTitelFont(),null));
		movementControl.setLayout(new GridLayout(3, 2,40,5));
		
		btnMoveTo = new JButton("Move To");
		btnMoveTo.addActionListener(new BtnMoveTo_ActionListener());
		txtXPos  = new JTextField("0");
		txtYPos  = new JTextField("0");
		
		movementControl.add(new JLabel("Position In X [μm]"));
		movementControl.add(txtXPos);
		movementControl.add(new JLabel("Position In Y [μm]"));
		movementControl.add(txtYPos);
		movementControl.add(new JLabel());
		movementControl.add(btnMoveTo);
		
		this.add(tileScan);
		this.add(movementControl);
		
			
	}
	
	class BtnSetUpperLeft_ActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			try {
				upperLeftCorner = mf.getXYStagePosition();
				lblUpperLeft.setText(upperLeftCorner.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
	};
	
	class BtnMoveTo_ActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			try{
				double xPos = Double.valueOf(txtXPos.getText());
				double yPos = Double.valueOf(txtYPos.getText());
				mf.moveXYStage(xPos, yPos);
				
				
			} catch(Exception e){
				System.out.println("no valid positions inserted;");
			}
		}
		
	};
	
	class BtnSetLowerRight_ActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				lowerRightCorner = mf.getXYStagePosition();
				lblLowerRight.setText(lowerRightCorner.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
	};
	
	class BtnStartTileScan_ActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Thread tileScan = new Thread(new TileScan());
			tileScan.start();
		}
		
	};
	
	class BtnStopTileScan_ActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			tileScanRunning = false;
		}
	};
	
	class TileScan implements Runnable{
		TileScan(){
			tileScanRunning =true;
		}
		@Override
		public void run() {
			mf.stopLivePreview();
			mf.setAction("Tile Scan");
			//only use large overlap in y direction
			int overlapInPixelsX = 6;//(int) Math.ceil(percentageOverlap[comboBoxOverlap.getSelectedIndex()]/100.*256);
			int overlapInPixelsY = (int) Math.floor(percentageOverlap[comboBoxOverlap.getSelectedIndex()]/100.*512);
			if (overlapInPixelsX%2!=0){
				overlapInPixelsX += 1;
			}
			if (overlapInPixelsY%2!=0){
				overlapInPixelsY += 1;
			}
			int numberStepsX = (int)Math.ceil((lowerRightCorner.getxPos()-upperLeftCorner.getxPos())/pixelSizeX/(256-overlapInPixelsX))+1;
			int numberStepsY = (int)Math.ceil((-lowerRightCorner.getyPos()+upperLeftCorner.getyPos())/pixelSizeY/(512-overlapInPixelsY))+1;
			System.out.println(numberStepsX+" "+numberStepsY);
			ImagePlus[][] tileScanImages = new ImagePlus[numberStepsX][numberStepsY];
			for (int x = 0; x< numberStepsX ;x++){
				for (int y =0; y<numberStepsY;y++){
					if (!tileScanRunning){
						break;
					}
					mf.setFrameCount(numberStepsY*x+y+1+"/"+numberStepsY*numberStepsX);
					double xPos = upperLeftCorner.getxPos() + x * pixelSizeX * (256-overlapInPixelsX);
					double yPos = upperLeftCorner.getyPos() - y * pixelSizeY * (512-overlapInPixelsY);
					mf.moveXYStage(xPos,yPos);
					try {
						Thread.sleep((long) (200+mf.getExposureTime()));
						ImagePlus temp = mf.captureImage();
						mf.showCurrentImage(temp);
						tileScanImages[x][y] = temp;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (!tileScanRunning){
						break;
					}
				}
			}
			if (tileScanRunning){
				ImagePlus stichedImage = Utility.stichTileScan(tileScanImages,numberStepsX, numberStepsY, overlapInPixelsX, overlapInPixelsY,true);
				mf.createOutputFolder();
				OutputControl.saveSingleImage(stichedImage, mf.getOutputFolder()+"stichedImage.tif");
			}
		}
	}
}