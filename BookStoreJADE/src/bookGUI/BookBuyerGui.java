package bookGUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import bookAgent.BookBuyerAgent;

public class BookBuyerGui extends JFrame {

	private BookBuyerAgent myAgent;

	private JTextField titleField, priceField;

	public BookBuyerGui(BookBuyerAgent a) {
		super(a.getLocalName());

		myAgent = a;

		JPanel p = new JPanel();
		p.setLayout(new GridLayout(3, 2));
		p.add(new JLabel("Book Title:"));
		titleField = new JTextField(20);
		p.add(titleField);
		getContentPane().add(p, BorderLayout.CENTER);

		JButton addButton = new JButton("Buy");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					String title = titleField.getText().trim();

					myAgent.setBookTitle(title);
					titleField.setText("");

					myAgent.startProcess();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(BookBuyerGui.this, "Invalid Value", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		p = new JPanel();
		p.add(addButton);
		getContentPane().add(p, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		});

		setResizable(true);
	}

	public void showGui() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int) screenSize.getWidth() / 2;
		int centerY = (int) screenSize.getHeight() / 2;

		setTitle(myAgent.getLocalName());
		setSize(400, 200);
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}
}
