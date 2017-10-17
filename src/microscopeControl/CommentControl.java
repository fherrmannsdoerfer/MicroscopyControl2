package microscopeControl;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;


public class CommentControl extends JPanel{
	MainFrame mf;
	CommentControl(MainFrame mf, Dimension minSize, Dimension prefSize, Dimension maxSize){
		this.mf = mf;
		setMinimumSize(minSize);
		setPreferredSize(prefSize);
		setMaximumSize(maxSize);
		
		setBorder(new TitledBorder(null, "Commentary Section", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
		setLayout(new BorderLayout(0, 0));
		
		JTextPane textPane = new JTextPane();
		JScrollPane scrollPane = new JScrollPane(textPane);
		add(scrollPane);
		
		textPane.setSize(prefSize);
		textPane.setMinimumSize(minSize);
		textPane.setMaximumSize(maxSize);
	}
}
