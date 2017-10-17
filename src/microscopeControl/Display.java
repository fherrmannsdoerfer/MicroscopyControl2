package microscopeControl;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.JSlider;

import java.awt.Dimension;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import utility.Utility;


public class Display extends JPanel {
	ImagePlus currImagePlus;
	BufferedImage impb;
	JLabel lblImageLabel;
	JLabel txtBoxMaxVal;
	JLabel txtBoxMeanVal;
	JLabel txtBoxMinVal;
	JLabel lblScale;
	JLabel lblMinVal;
	JLabel lblMeanVal;
	JLabel lblMaxVal;
	RescaleOp rescale;
	ImageIcon currImage;
	private JTextField txtMinRange;
	private JTextField txtMaxRange;
	JSlider slrMinRange;
	JSlider slrMaxRange;
	JCheckBox chkboxLockValues;
	
	double scale = 1;
	boolean isPressed;
	
	Rectangle rect = new Rectangle();
	
	MainFrame mf;


	public Display(MainFrame mf) {
		this.mf = mf;
		
		setMaximumSize(new Dimension(570, 720));
		setMinimumSize(new Dimension(570, 655));
		setPreferredSize(new Dimension(600, 655));
		setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		Box verticalBox = Box.createVerticalBox();
				
		//on this component the image from the camera is displayed and the rectangle can be drawn
		lblImageLabel = new JLabel();
		lblImageLabel.addMouseMotionListener(new MyMouseMotionAdapter());
		lblImageLabel.addMouseListener(new MyMouseListener());
		lblImageLabel.setMaximumSize(new Dimension(530, 530));
		lblImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblImageLabel.setMinimumSize(new Dimension(512, 512));
		lblImageLabel.setPreferredSize(new Dimension(512,512));
		
		//create scroll pane in case of zoomed in image
		JScrollPane scrollPane = new JScrollPane(lblImageLabel);
		scrollPane.setPreferredSize(new Dimension(570, 530));
		verticalBox.add(scrollPane);
		
		//Panel that hold all other components, organized in 3 columns and one 4 by 2 matrix
		JPanel panel1 = new JPanel();
		panel1.setLayout(new FlowLayout());
		
		JPanel column1 = new JPanel();
		column1.setLayout(new GridLayout(3,1,10,6));
		JPanel column2 = new JPanel();
		column2.setLayout(new GridLayout(3,1,10,6));
		JPanel column3 = new JPanel();
		column3.setLayout(new GridLayout(3,1,10,6));
		JPanel column4 = new JPanel();
		column4.setLayout(new GridLayout(4,2,0,0));
		
		
		JButton btnZoomIn = new JButton("Zoom +");
		btnZoomIn.addActionListener(btnZoomInActionListener);
		
		JLabel lblMinRange = new JLabel("min Range");
		
		JLabel lblMaxRange = new JLabel("max Range");
		
		column1.add(btnZoomIn);
		column1.add(lblMinRange);
		column1.add(lblMaxRange);
		
		JLabel lbllbl = new JLabel("Scale:     ");
		
		lblScale = new JLabel("1");
		lblScale.setMaximumSize(new Dimension(50, 16));
		lblScale.setPreferredSize(new Dimension(50, 20));
		lblScale.setName("lblScale");
		
		Box horizontalBoxScale = Box.createHorizontalBox();
		horizontalBoxScale.add(lbllbl);
		horizontalBoxScale.add(lblScale);
		
		slrMinRange = new JSlider();
		slrMinRange.addChangeListener(slrMinRange_changeListener);
		
		slrMaxRange = new JSlider();
		slrMaxRange.addChangeListener(slrMaxRange_changeListener);
		
		column2.add(horizontalBoxScale);
		column2.add(slrMinRange);
		column2.add(slrMaxRange);
		
		JButton btnZoomOut = new JButton("Zoom -");
		btnZoomOut.addActionListener(btnZoomOutActionListener);
		
		txtMinRange = new JTextField();
		txtMinRange.setText("-1");
		Utility.setFormatTextFields(txtMinRange, 20, 20, 5);

		txtMaxRange = new JTextField();
		txtMaxRange.setText("-1");
		Utility.setFormatTextFields(txtMaxRange, 20, 20, 5);
		
		column3.add(btnZoomOut);
		column3.add(txtMinRange);
		column3.add(txtMaxRange);
		
		lblMinVal = new JLabel("min val:");
		
		txtBoxMinVal = new JLabel("0");
		txtBoxMinVal.setPreferredSize(new Dimension(20, 16));
		txtBoxMinVal.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lblMeanVal = new JLabel("mean val:");
		
		txtBoxMeanVal = new JLabel("0");
		txtBoxMeanVal.setHorizontalAlignment(SwingConstants.RIGHT);
		txtBoxMeanVal.setPreferredSize(new Dimension(20, 16));
		
		lblMaxVal = new JLabel("max val:");
		
		txtBoxMaxVal = new JLabel("0");
		txtBoxMaxVal.setHorizontalAlignment(SwingConstants.RIGHT);
		txtBoxMaxVal.setPreferredSize(new Dimension(20, 16));
		
		chkboxLockValues = new JCheckBox("fix values");
		
		column4.add(lblMinVal);
		column4.add(txtBoxMinVal);
		column4.add(lblMeanVal);
		column4.add(txtBoxMeanVal);
		column4.add(lblMaxVal);
		column4.add(txtBoxMaxVal);
		column4.add(chkboxLockValues);
		column4.add(new JLabel());
		
		panel1.add(column1);
		panel1.add(column2);
		panel1.add(column3);
		panel1.add(column4);
		
		verticalBox.add(panel1);
		this.add(verticalBox);
	}
	
	void updateImage(String path){
		ImagePlus imp = IJ.openImage(path); 
		if (imp != null) {
			drawImage(imp);
		}
	}
	
	void updateImage(ImagePlus imp){
		drawImage(imp);
	}
	
	//draws given image to the label and updates the min, mean and max intensity labels
	void drawImage(ImagePlus imp) {
		currImagePlus = imp;
		imp = setRange(imp);
		setSliders(imp);
		addRect(imp);
		if (mf.getShowMiddleLine()){
			addMiddleLine(imp);
		}
		impb = imp.getBufferedImage();
		impb = scaleImage(impb,scale);
		currImage = new ImageIcon(impb);
		if (currImage != null) {
			lblImageLabel.setIcon(currImage);
			setValues();
		}
		else {
			System.out.println("nullpointer");
		}
	}
	
	void addMiddleLine(ImagePlus imp){
		try{
			imp.getProcessor().setColor(10);
			imp.getProcessor().drawLine(255, 0, 255, 511);
			imp.getProcessor().drawLine(256, 0, 256, 511);
		}
		catch(Error e){}
	}
	
	//Draws the rectangle chosen by the user onto both channels 
	void addRect(ImagePlus imp){
		try{
			imp.getProcessor().draw(new Roi(rect.x, rect.y, rect.width, rect.height));
			imp.getProcessor().drawRoi(new Roi(rect.x+(mf.getShiftX()), (rect.y+mf.getShiftY()), rect.width, rect.height));
			imp.getProcessor().drawRect(rect.x+(mf.getShiftX()), (rect.y+mf.getShiftY()), rect.width, rect.height);
		}
		catch(Error e){System.out.println(e.toString());}
	}
	
	//if the image is zoomed in or out it has to be up or down scaled
	BufferedImage scaleImage(BufferedImage impb, double scale){
		BufferedImage impbScaled = new BufferedImage((int) (impb.getWidth()*scale), (int) (impb.getHeight()*scale), impb.getType());
		Graphics2D g = impbScaled.createGraphics();
		g.drawImage(impb, 0, 0, (int) (impb.getWidth()*scale), (int) (impb.getHeight()*scale), null);
		g.dispose();
		return impbScaled;
	}
	
	//handles the dynamic increase of minimal and maximal intensity values, to prevent flickering
	void setSliders(ImagePlus imp) {
		int currentSlrMinRangeMinimum = slrMinRange.getMinimum();
		int currentSlrMinRangeMaximum = slrMinRange.getMaximum();
		int currentImageMinimum = (int) imp.getStatistics().min;
		int currentImageMaximum = (int) imp.getStatistics().max;
		int currentImageRange = currentImageMaximum-currentImageMinimum;
		
		if (Math.abs(currentImageMinimum - currentSlrMinRangeMinimum) / (1.0*currentImageRange) > 0.1 && !chkboxLockValues.isSelected()) {
			slrMinRange.setMinimum((int) (currentImageMinimum - 0.1*currentImageRange));
			slrMaxRange.setMinimum((int) (currentImageMinimum - 0.1*currentImageRange));
			slrMinRange.setValue((int) currentImageMinimum);
		}
		
		if (Math.abs(currentImageMaximum - currentSlrMinRangeMaximum) / (1.0*currentImageRange) > 0.1 && !chkboxLockValues.isSelected()) {
			slrMinRange.setMaximum((int) (currentImageMaximum + 0.1*currentImageRange));
			slrMaxRange.setMaximum((int) (currentImageMaximum + 0.1*currentImageRange));
			slrMaxRange.setValue((int) currentImageMaximum);
		}
		int oldslrMinValue = slrMinRange.getValue();
		int oldslrMaxValue = slrMaxRange.getValue();
		
		if (!chkboxLockValues.isSelected()){
			int currentMinValue = (int) imp.getStatistics().min;
			int currentMaxValue = (int) imp.getStatistics().max;
			
			//increase the maximal slider value by 10 % to avoid rapid changes of the slider range
			int newMinRange = (int) slrMinRange.getMinimum();

			if (slrMinRange.getMinimum()<currentMinValue) {
				newMinRange = (int) 0.9 * currentMinValue; //if the new minimum is too low a new lower bound is set
			}
			int newMaxRange = (int) slrMinRange.getMaximum(); //limits can be calculated for only one slider but will be applied to both

			if (slrMinRange.getMaximum() <currentMaxValue || slrMinRange.getMaximum() > 1.1 * currentMaxValue) {
				newMaxRange = (int) 1.1 * currentMaxValue; //if the new maximum is too high
			}
			slrMinRange.setMinimum((int) newMinRange);
			slrMinRange.setMaximum((int) newMaxRange);
			slrMinRange.setValue(Math.min(oldslrMinValue, currentMinValue));
			
			slrMaxRange.setMinimum((int) newMinRange);
			slrMaxRange.setMaximum((int) newMaxRange);
			slrMaxRange.setValue((int) Math.max(oldslrMaxValue,  currentMaxValue));
		}
		
	}
	
	//applies the set ranges for the contrast to the image
	ImagePlus setRange(ImagePlus imp) {
		if (Integer.parseInt(txtMinRange.getText()) == -1) {
			txtMinRange.setText("" + (int) imp.getStatistics().min);
		}
		if (Integer.parseInt(txtMaxRange.getText()) == -1) {
			txtMaxRange.setText("" + (int) imp.getStatistics().max);
		}
		if (slrMaxRange.getValue() == 50){ //happens for first frame
			slrMaxRange.setMaximum((int) (1.1*imp.getStatistics().max));
			slrMinRange.setMaximum((int) (1.1*imp.getStatistics().max));
			slrMaxRange.setValue((int) imp.getStatistics().max);
			slrMinRange.setMinimum((int) (0.9*imp.getStatistics().min));
			slrMaxRange.setMinimum((int) (0.9*imp.getStatistics().min));
			slrMinRange.setValue((int) imp.getStatistics().min);
		}
		
		int minVal = Integer.parseInt(txtMinRange.getText());
		int maxVal = Integer.parseInt(txtMaxRange.getText());
		imp.setDisplayRange(minVal,maxVal);
		return imp;
	}

	//updates the min, mean and maximum intensity labels
	void setValues(){
		txtBoxMinVal.setText(String.valueOf(currImagePlus.getStatistics().min));
		txtBoxMeanVal.setText(String.valueOf((int) currImagePlus.getStatistics().mean));
		txtBoxMaxVal.setText(String.valueOf(currImagePlus.getStatistics().max));
	};
	
	//change listener for the sliders to detect user interaction and change the 
	//number in the text field accordingly
	ChangeListener slrMinRange_changeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent arg0) {
			txtMinRange.setText(String.valueOf(slrMinRange.getValue()));
		//	updateImage(currImagePlus);
		}
	};
	ChangeListener slrMaxRange_changeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent arg0) {
			txtMaxRange.setText(String.valueOf(slrMaxRange.getValue()));
		//	updateImage(currImagePlus);
		}
	};
	
	//action listener for the zoom in and zoom out button, the zoom is limited to
	//doubling and reducing the image size by half
	ActionListener btnZoomInActionListener =new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			System.out.println(scale);
			scale = scale * 2;
			lblScale.setText(String.valueOf(scale));
		//	drawImage(currImagePlus);
		}
	};
	ActionListener btnZoomOutActionListener =new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			System.out.println(scale);
			scale = scale / 2;			
			lblScale.setText(String.valueOf(scale));
		//	drawImage(currImagePlus);
		}
	};
	
	void resetRect(){
		rect = new Rectangle();
	}
	
	class MyMouseMotionAdapter extends MouseMotionAdapter{
		public void mouseDragged(MouseEvent arg0) {
			if (arg0.getX()<256 && isPressed){
				System.out.println(" arg0.getX: "+arg0.getX()+" rect.width: "+rect.width);
				
				rect.width = arg0.getX() - rect.x;
				rect.height = arg0.getY() - rect.y;
				
				ImageIcon img =(ImageIcon) lblImageLabel.getIcon();
				BufferedImage buImg = new BufferedImage(img.getIconWidth(), img.getIconHeight(), BufferedImage.TYPE_INT_ARGB); 
				buImg.getGraphics().drawImage(img.getImage(), 0,0,img.getImageObserver());
				ImagePlus imp = new ImagePlus("",buImg);
				imp.getProcessor().setColor(255);
				imp.getProcessor().draw(new Roi(rect.x, rect.y, rect.width, rect.height));
				//imp.draw(rect.x, rect.y, rect.width, rect.height);
				lblImageLabel.setIcon(new ImageIcon(imp.getBufferedImage()));
			}
		}
	}
	
	class MyMouseListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			System.out.println("nothing");
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			if (arg0.getX()<256){
				rect.x = arg0.getX();
				rect.y = arg0.getY();
				System.out.println("anfang x:"+rect.x+" y: "+rect.y);
				isPressed = true;
			}
		}
		@Override
		public void mouseReleased(MouseEvent arg0) {
			if (arg0.getX()<256 && isPressed){
				rect.width = arg0.getX() - rect.x;
				rect.height = arg0.getY() - rect.y;
				System.out.println("ende x:"+(rect.x+rect.width)+" y: "+(rect.y+rect.height)+" ende x:"+ arg0.getX()+" y: "+ arg0.getY());
				isPressed = false;
			}
		}
		
	}
}


