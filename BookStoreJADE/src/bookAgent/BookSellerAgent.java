package bookAgent;

import java.util.Hashtable;

import bookBehaviour.OfferRequestServer;
import bookBehaviour.PurchaseOrderServer;
import bookGUI.BookSellerGui;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BookSellerAgent extends Agent {

	// The catalogue of books for sale (maps the title of a book to its price)
	private Hashtable<String, Integer> catalogue;
	// The GUI by means of which the seller can add books in the catalogue
	private BookSellerGui gui;

	protected void setup() {
		// Create the catalogue
		catalogue = new Hashtable<String, Integer>();

		// Start the GUI
		gui = new BookSellerGui(this);
		gui.showGui();

		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		ServiceDescription sd = new ServiceDescription();
		sd.setType("book-selling");
		sd.setName("book-trading");
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Add the behaviour serving queries from buyer agents
		addBehaviour(new OfferRequestServer(this));

		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new PurchaseOrderServer(this, gui));

		System.out.println("Seller agent " + getAID().getName() + " is ready.");
	}

	// Agent clean-up operations
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		gui.dispose();

		System.out.println("Seller agent " + getAID().getName() + " is terminating");
	}

	public void updateCatalogue(final String title, final int price) {
		addBehaviour(new OneShotBehaviour() {
			public void action() {
				catalogue.put(title, price);
				System.out.println(title + " inserted with a price of RM" + price);
			}
		});
	}

	public Hashtable<String, Integer> getCatalogue() {
		return catalogue;
	}
}
