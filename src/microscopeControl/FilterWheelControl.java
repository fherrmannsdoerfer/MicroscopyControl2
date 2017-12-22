package microscopeControl;
import java.awt.Dimension;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JRadioButton;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class FilterWheelControl extends JPanel {
	MainFrame mf;
	
	FilterWheelControl(MainFrame mf, Dimension minSize, Dimension prefSize, Dimension maxSize){
		this.mf = mf;
		setMinimumSize(minSize);
		setPreferredSize(prefSize);
		setMaximumSize(maxSize);
		setBorder(new TitledBorder(null, "Filter Wheel Control", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{80, 80, 80, 80, 0};
		gridBagLayout.rowHeights = new int[]{0, 25, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
				
		JRadioButton rdbtnPos1 = new JRadioButton(mf.getFilterName(0));
		rdbtnPos1.setAlignmentX(LEFT_ALIGNMENT);
		GridBagConstraints gbc_rdbtnNewRadioButton = new GridBagConstraints();
		gbc_rdbtnNewRadioButton.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdbtnNewRadioButton.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnNewRadioButton.gridx = 1;
		gbc_rdbtnNewRadioButton.gridy = 1;
		rdbtnPos1.setActionCommand("0");
		rdbtnPos1.addActionListener(new rdbtnActionListener());
		add(rdbtnPos1, gbc_rdbtnNewRadioButton);
		
		JRadioButton rdbtnPos2 = new JRadioButton(mf.getFilterName(1));
		rdbtnPos2.setAlignmentX(LEFT_ALIGNMENT);
		GridBagConstraints gbc_rdbtnNewRadioButton_1 = new GridBagConstraints();
		gbc_rdbtnNewRadioButton_1.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnNewRadioButton_1.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdbtnNewRadioButton_1.gridx = 2;
		gbc_rdbtnNewRadioButton_1.gridy = 1;
		rdbtnPos2.setActionCommand("1");
		rdbtnPos2.addActionListener(new rdbtnActionListener());
		add(rdbtnPos2, gbc_rdbtnNewRadioButton_1);
		
		JRadioButton rdbtnPos3 = new JRadioButton(mf.getFilterName(2));
		rdbtnPos3.setAlignmentX(LEFT_ALIGNMENT);
		GridBagConstraints gbc_rdbtnNewRadioButton_2 = new GridBagConstraints();
		gbc_rdbtnNewRadioButton_2.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdbtnNewRadioButton_2.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnNewRadioButton_2.gridx = 3;
		gbc_rdbtnNewRadioButton_2.gridy = 2;
		rdbtnPos3.setActionCommand("2");
		rdbtnPos3.addActionListener(new rdbtnActionListener());
		add(rdbtnPos3, gbc_rdbtnNewRadioButton_2);
		
		JRadioButton rdbtnPos4 = new JRadioButton(mf.getFilterName(3));
		rdbtnPos4.setAlignmentX(LEFT_ALIGNMENT);
		GridBagConstraints gbc_rdbtnNewRadioButton_5 = new GridBagConstraints();
		gbc_rdbtnNewRadioButton_5.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdbtnNewRadioButton_5.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnNewRadioButton_5.gridx = 2;
		gbc_rdbtnNewRadioButton_5.gridy = 4;
		rdbtnPos4.setActionCommand("3");
		rdbtnPos4.addActionListener(new rdbtnActionListener());
		add(rdbtnPos4, gbc_rdbtnNewRadioButton_5);
		
		JRadioButton rdbtnPos5 = new JRadioButton(mf.getFilterName(4));
		rdbtnPos5.setAlignmentX(LEFT_ALIGNMENT);
		GridBagConstraints gbc_rdbtnNewRadioButton_3 = new GridBagConstraints();
		gbc_rdbtnNewRadioButton_3.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdbtnNewRadioButton_3.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnNewRadioButton_3.gridx = 1;
		gbc_rdbtnNewRadioButton_3.gridy = 4;
		rdbtnPos5.setActionCommand("4");
		rdbtnPos5.addActionListener(new rdbtnActionListener());
		add(rdbtnPos5, gbc_rdbtnNewRadioButton_3);
		
		JRadioButton rdbtnPos6 = new JRadioButton(mf.getFilterName(5));
		rdbtnPos6.setAlignmentX(LEFT_ALIGNMENT);
		GridBagConstraints gbc_rdbtnNewRadioButton_4 = new GridBagConstraints();
		gbc_rdbtnNewRadioButton_4.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdbtnNewRadioButton_4.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnNewRadioButton_4.gridx = 0;
		gbc_rdbtnNewRadioButton_4.gridy = 2;
		rdbtnPos6.setActionCommand("5");
		rdbtnPos6.addActionListener(new rdbtnActionListener());
		add(rdbtnPos6, gbc_rdbtnNewRadioButton_4);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnPos1);
		group.add(rdbtnPos2);
		group.add(rdbtnPos3);
		group.add(rdbtnPos4);
		group.add(rdbtnPos5);
		group.add(rdbtnPos6);
	}
	
	class rdbtnActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			System.out.println(arg0.getActionCommand());
			mf.setFilterWheelPosition(Integer.parseInt(arg0.getActionCommand()));
		}

		
	}
}
