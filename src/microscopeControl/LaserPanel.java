package microscopeControl;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.BoxLayout;

import java.awt.Dimension;
import java.awt.Component;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;

import mmcorej.CMMCore;

//Class that provides the control element for an individual laser
public class LaserPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	//Text field where the laser intensity can be set and the current intensity is shown
	private JTextField txtLaserIntensity;
	//Slider to alter the laser intensity
	private JSlider slrLaserIntensity;
	//minimal and maximal laser power. This number is detected by Micro-Manager
	double minimalLaserPower;
	double maximalLaserPower;
	//instance of the CMMCore object
	CMMCore core;
	MainFrame mf;
	//used to transform integer slider states into float numbers, scale 10 means an intensity resolution of 0.1
	int scale = 10; 
	//images for both on and off state of the btnSwitchLaser button
	Image imgON;
	Image imgOFF;
	//state of the laser, either true for turned on or false for turned off
	boolean state = false;
	//button to switch the state of the laser
	JButton btnSwitchLaser;
	String laserName;
	
	/**
	 * Create the panel.
	 */
	public LaserPanel(MainFrame mf, String name, String waveLength) {
		this.mf = mf;
		this.core = mf.getCoreObject();
		laserName = name;

		//get the minimum and maximum laser powers from Micro-Manager does not work since for some lasers no values are available and the unit is sometimes mW and sometimes W 
		minimalLaserPower = 0.1;
		maximalLaserPower = 100;
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		//Define size and appearance of the laser panel
		Dimension d = new Dimension(140,250);
		
		this.setPreferredSize(d);
		this.setBorder(new TitledBorder(null, "Laser "+ waveLength, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
			
		Box verticalBox = Box.createVerticalBox();
		
		Box horizontalBoxWithoutButton = Box.createHorizontalBox();
		

		//create minimum and maximum intensity label with space in between
		Box verticalBoxLabels = Box.createVerticalBox();
		
		JLabel lblMaxPower = new JLabel(String.valueOf(maximalLaserPower));
		Component glueBetweenMinMaxIntensityLabels = Box.createVerticalGlue();
		JLabel lblMinPower = new JLabel(String.valueOf(minimalLaserPower));
		
		verticalBoxLabels.add(lblMaxPower);
		verticalBoxLabels.add(glueBetweenMinMaxIntensityLabels);
		verticalBoxLabels.add(lblMinPower);
		
		//create laser intensity slider
		setupLaserSlider();
		
		
		//create space over the text box
		Box verticalBoxTextbox = Box.createVerticalBox();
		
		Component verticalGlue = Box.createVerticalGlue();
		
		txtLaserIntensity = new JTextField();
		txtLaserIntensity.setText("0");
		txtLaserIntensity.addActionListener(txtLaserIntensity_ActionListener);
		txtLaserIntensity.setMaximumSize(new Dimension(50, 50));
		txtLaserIntensity.setColumns(10);
		
		verticalBoxTextbox.add(verticalGlue);
		verticalBoxTextbox.add(txtLaserIntensity);
		
		
		Component verticalStrut = Box.createVerticalStrut(20);
		
		btnSwitchLaser = new JButton("OFF");
		btnSwitchLaser.setFont(new Font("Tahoma", Font.PLAIN, 19));
		btnSwitchLaser.setForeground(Color.WHITE);
		btnSwitchLaser.setAlignmentX(Component.CENTER_ALIGNMENT);

		try {
            imgON = ImageIO.read(ClassLoader.getSystemResourceAsStream("on.png"));
            imgOFF = ImageIO.read(ClassLoader.getSystemResourceAsStream("off.png"));
            btnSwitchLaser.setIcon(new ImageIcon(imgOFF.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
            btnSwitchLaser.setHorizontalTextPosition(JButton.CENTER);
            btnSwitchLaser.setVerticalTextPosition(JButton.CENTER);
            btnSwitchLaser.setMargin(new Insets(0, 0, 0, 0));
            btnSwitchLaser.setBorder(null);
        } catch (Exception ex) {
        	System.err.println("pictures for the on off button of the laser panel not found!");
        }
		btnSwitchLaser.addActionListener(btnSwitchLaser_actionListener);
		
		//Put labels, slider and text box together
		horizontalBoxWithoutButton.add(verticalBoxLabels);
		horizontalBoxWithoutButton.add(slrLaserIntensity);
		horizontalBoxWithoutButton.add(verticalBoxTextbox);
		
		//Put everything together
		verticalBox.add(horizontalBoxWithoutButton);
		verticalBox.add(verticalStrut);
		verticalBox.add(btnSwitchLaser);
		
		this.add(verticalBox);
	}

	//create slider that is used to control the laser power
	private void setupLaserSlider() {
		slrLaserIntensity = new JSlider();
		slrLaserIntensity.setMinimum((int) minimalLaserPower*scale);
		slrLaserIntensity.setMaximum((int) maximalLaserPower*scale);
		slrLaserIntensity.setValue(0);
		slrLaserIntensity.addChangeListener(slrLaserIntensity_changeListener);
		slrLaserIntensity.setMinimumSize(new Dimension(20, 20));
		slrLaserIntensity.setPreferredSize(new Dimension(20, 100));
		slrLaserIntensity.setOrientation(SwingConstants.VERTICAL);
	}

	//this function will be used for the automated UV laser increment when too few blinking events per
	//frame are detected
	public void increaseLaserPower(double increment){	
		try {
			double currVal = Double.valueOf(core.getProperty(laserName, "PowerSetpoint"));
			double newVal = currVal + increment;
			if (newVal<= maximalLaserPower){
				core.setProperty(laserName, "PowerSetpoint", currVal + increment);
				slrLaserIntensity.setValue((int) (newVal)*scale);
				txtLaserIntensity.setText(String.valueOf(newVal));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//ActionListener that sets the slider position to the corresponding location whenever
	//a new laser power is entered in the laser power text field
	ActionListener txtLaserIntensity_ActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			float entry_text_field = (float) 0.1;
			try {
				entry_text_field = Float.parseFloat(txtLaserIntensity.getText());
				
			}
			catch (Exception e1) {
			}
			//if the entered value is outside of the possible range the slider is 
			//set to the lowest position
			if (entry_text_field>105 || entry_text_field<0.1) {
				slrLaserIntensity.setValue((int) (minimalLaserPower * scale));
			}
			else {
				slrLaserIntensity.setValue((int)(entry_text_field*scale));
			}
			
		}
	};
	
	//This listener is triggered whenever the slider is moved and updates the text field
	ChangeListener slrLaserIntensity_changeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent arg0) {
			txtLaserIntensity.setText(String.valueOf(slrLaserIntensity.getValue()/(float)scale));
		}
	};

	
	//Action listener for the on/off button
	ActionListener btnSwitchLaser_actionListener =new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			try {
				if (state) {
					state = false;
					core.setProperty(laserName, "State", state);
					btnSwitchLaser.setIcon(new ImageIcon(imgOFF.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
					btnSwitchLaser.setText("OFF");
					core.setProperty(laserName,"PowerSetpoint",minimalLaserPower);
				}
				else {
					state = true;
					core.setProperty(laserName, "State", state);
					btnSwitchLaser.setIcon(new ImageIcon(imgON.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
					btnSwitchLaser.setText("ON");
					core.setProperty(laserName,"PowerSetpoint",Double.parseDouble(txtLaserIntensity.getText()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	public void turnOffLaser(){
		try {
			core.setProperty(laserName, "State", false);
			core.setProperty(laserName,"PowerSetpoint",minimalLaserPower);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
