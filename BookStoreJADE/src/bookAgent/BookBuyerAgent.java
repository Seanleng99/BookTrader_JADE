package bookAgent;

import bookBehaviour.RequestPerformer;
import bookGUI.BookBuyerGui;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import javax.swing.*;

public class BookBuyerAgent extends Agent {

	// The title of the book to buy
	private String bookTitle;
	// The list of known seller agents
	private AID[] sellerAgents;

	private int ticker_timer = 30000;
	private BookBuyerAgent this_agent = this;
	// The GUI by means of which the buyer can enter book in the catalogue to buy
	private BookBuyerGui gui;

	// Agent initializations
	protected void setup() {
		System.out.println("Buyer agent " + getAID().getName() + " is ready.");

		// Start GUI
		gui = new BookBuyerGui(this);
		gui.showGui();

	}

	public void startProcess() {
		// Get the title of the book to buy
		if (bookTitle.length() > 0) {
			System.out.println("Book: " + bookTitle);

			// Add a TickerBehaviour that schedules a request to seller agents every 30 seconds
			addBehaviour(new TickerBehaviour(this, ticker_timer) {
				protected void onTick() {
					System.out.println("Trying to buy " + bookTitle);

					// Update the list of seller agents
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("book-selling");
					template.addServices(sd);

					try {
						DFAgentDescription[] result = DFService.search(myAgent, template);
						System.out.println("Found the following " + result.length + " seller agents:");
						sellerAgents = new AID[result.length];
						for (int i = 0; i < result.length; i++) {
							sellerAgents[i] = result[i].getName();
							System.out.println(sellerAgents[i].getName());
						}

					}

					catch (FIPAException fe) {
						fe.printStackTrace();
					}

					// Perform the request
					myAgent.addBehaviour(new RequestPerformer(this_agent));
				}
			});
		}

		else {

			// If user enter nothing, alert and ask for input again
			// Pop up a message dialog
			System.out.println("No target book title specified.");
			JFrame f = new JFrame();
			JOptionPane.showMessageDialog(f, "Sorry. No target book title specified. Please try enter again.");
		}
	}

	// Agent clean-up operations
	protected void takeDown() {
		System.out.println("Buyer agent " + getAID().getName() + " is terminating");
		gui.dispose();
	}

	public AID[] getSellerAgents() {
		return sellerAgents;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}
}
