package microscopeControl;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class PifocPositionAndMonitor extends JPanel{
	MainFrame mf;
	JSpinner spinner;
	JCheckBox chkBoxFocusLock;
	JComboBox comboBoxStepSize;
	
	public PifocPositionAndMonitor(MainFrame mf, Dimension minSize, Dimension prefSize, Dimension maxSize){
		this.mf = mf;
		setMinimumSize(minSize);
		setPreferredSize(prefSize);
		setMaximumSize(maxSize);
		setBorder(new TitledBorder(null, "Focus Control", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		Box horizontalBoxZStageControls = Box.createHorizontalBox();
		spinner = new JSpinner();
		spinner.setPreferredSize(new Dimension(70, 22));
		spinner.setMinimumSize(new Dimension(70, 22));
		spinner.setModel(new SpinnerNumberModel(50.0, 0.0, 100.0, 0.001));
		spinner.setMaximumSize(new Dimension(70, 22));
		spinner.addChangeListener(new SpinnerChangeListener());
		
		JLabel lblStepSize = new JLabel("Step size");
		
		comboBoxStepSize = new JComboBox();
		comboBoxStepSize.setMaximumSize(new Dimension(32767, 22));
		comboBoxStepSize.addItem("1000 nm");
		comboBoxStepSize.addItem("100 nm");
		comboBoxStepSize.addItem("10 nm");
		comboBoxStepSize.addItem("1 nm");
		((JLabel)comboBoxStepSize.getRenderer()).setHorizontalAlignment(SwingConstants.RIGHT);
		comboBoxStepSize.addActionListener(new ComboBoxStepSizeActionListener());
				
		chkBoxFocusLock = new JCheckBox("Focus Lock");
		chkBoxFocusLock.addActionListener(new FocusLockActionListener());
		
		horizontalBoxZStageControls.add(spinner);
		horizontalBoxZStageControls.add(Box.createHorizontalGlue());
		horizontalBoxZStageControls.add(lblStepSize);
		horizontalBoxZStageControls.add(Box.createHorizontalStrut(10));
		horizontalBoxZStageControls.add(comboBoxStepSize);
		horizontalBoxZStageControls.add(Box.createHorizontalGlue());
		horizontalBoxZStageControls.add(chkBoxFocusLock);
		
		this.add(horizontalBoxZStageControls);
		this.add(new ZMonitor(mf));
	}
	
	class SpinnerChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent arg0) {
			try {
				mf.setZStagePosition((Double)spinner.getValue());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	class ComboBoxStepSizeActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String selectedItem = (String) comboBoxStepSize.getSelectedItem();
			int majorTickSpacing = 1;
			if (selectedItem.equalsIgnoreCase("1000 nm")) {majorTickSpacing = 1000;}
			if (selectedItem.equalsIgnoreCase("100 nm")) {majorTickSpacing = 100;}
			if (selectedItem.equalsIgnoreCase("10 nm")) {majorTickSpacing = 10;}
			if (selectedItem.equalsIgnoreCase("1 nm")) {majorTickSpacing = 1;}
			//System.out.println(majorTickSpacing/1000.0);
			//System.out.println(spinner.getValue());
			double curValSpinner = (Double) spinner.getValue();
			spinner.setModel(new SpinnerNumberModel(curValSpinner,0,100,majorTickSpacing/1000.0));
		}
	};
	
	class FocusLockActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				if (chkBoxFocusLock.isSelected()){
					mf.setFocusLockState(1);
				}
				else {
					mf.setFocusLockState(0);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}
