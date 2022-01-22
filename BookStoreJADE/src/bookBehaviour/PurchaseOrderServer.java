package bookBehaviour;

import bookAgent.BookSellerAgent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.swing.*;
import java.util.Date;

import bookGUI.BookSellerGui;

// This is the behaviour used by Book seller agents to serve incoming 
// offer acceptances (i.e. purchase orders) from buyer agents.
// The seller agent removes the purchased book from its catalogue 
// and replies with an INFORM message to notify the buyer that the
// purchase has been successfully completed.

public class PurchaseOrderServer extends CyclicBehaviour {

	BookSellerAgent bsAgent;
	BookSellerGui gui;

	public PurchaseOrderServer(BookSellerAgent a, BookSellerGui g) {
		bsAgent = a;
		gui = g;
	}

	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
		ACLMessage msg = bsAgent.receive(mt);

		if (msg != null) {
			// ACCEPT_PROPOSAL Message received. Process it
			String title = msg.getContent();
			ACLMessage reply = msg.createReply();

			Integer price = (Integer) bsAgent.getCatalogue().remove(title);
			if (price != null) {
				reply.setPerformative(ACLMessage.INFORM);

				// Generate the string for confirmation
				String response = title + " sold to agent " + msg.getSender().getName();
				gui.setSoldField(response); // Update the GUI for the seller
				System.out.println(response); // Confirm the sold on console
			}

			else {
				// The requested book has been sold to another buyer in the meanwhile
				reply.setPerformative(ACLMessage.FAILURE);
				reply.setContent("Not available.");
			}

			bsAgent.send(reply);
		}

		else {
			block();
		}
	}
}
