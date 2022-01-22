package bookGUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import bookAgent.BookSellerAgent;

public class BookSellerGui extends JFrame {

	private BookSellerAgent myAgent;

	private JTextField titleField, priceField;
	private JLabel soldField;
	Image img = Toolkit.getDefaultToolkit().getImage("C:\\Users\\seanl\\Pictures\\Saved Pictures\\books.jpg");

	public BookSellerGui(BookSellerAgent a) {
		super(a.getLocalName());

		myAgent = a;
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(3, 2));
		p.add(new JLabel("Book Title:"));
		titleField = new JTextField(20);
		p.add(titleField);
		p.add(new JLabel("Price (RM):"));
		priceField = new JTextField(20);
		p.add(priceField);
		getContentPane().add(p, BorderLayout.CENTER);
		
		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					String title = titleField.getText().trim();
					String price = priceField.getText().trim();

					myAgent.updateCatalogue(title, Integer.parseInt(price));
					titleField.setText("");
					priceField.setText("");
					soldField.setText("");
				} catch (Exception e) {
					JOptionPane.showMessageDialog(BookSellerGui.this, "Invalid Value", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		p = new JPanel();
		p.setLayout(new GridLayout(2, 1));
		soldField = new JLabel("");
		p.add(soldField);
		p.add(addButton);
		getContentPane().add(p, BorderLayout.SOUTH);

		// Make the agent terminate when the user closes
		// the GUI using the button on the upper right corner
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		});

		setResizable(true);
	}

	// To update the confirmation purchase
	public void setSoldField(String confirmation) {
		soldField.setText(confirmation);
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
