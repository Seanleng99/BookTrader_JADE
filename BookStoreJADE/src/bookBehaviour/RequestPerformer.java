package bookBehaviour;

import javax.swing.*;

import bookAgent.BookBuyerAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Date;

// This is the behaviour used by Book-buyer agents to request seller agents the target book
public class RequestPerformer extends Behaviour {

	private AID bestSeller; // The agent who provides the best offer
	private int bestPrice; // The best offered price
	private int repliesCount = 0; // The counter of replies from seller agents
	private MessageTemplate mt; // The template to receive replies
	private int step = 0;
	private BookBuyerAgent bbAgent;
	private String bookTitle;

	public RequestPerformer(BookBuyerAgent a) {
		bbAgent = a;
		bookTitle = a.getBookTitle();
	}

	public void action() {
		switch (step) {
		case 0:
			// Send the cfp to all sellers
			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			for (int i = 0; i < bbAgent.getSellerAgents().length; i++) {
				cfp.addReceiver(bbAgent.getSellerAgents()[i]);
			}

			cfp.setContent(bookTitle);
			cfp.setConversationId("book-trade");
			cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
			myAgent.send(cfp);

			// Prepare the template to get proposals
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
					MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
			step = 1;
			break;

		case 1:
			// Receive all proposals/refusals from seller agents
			ACLMessage reply = bbAgent.receive(mt);
			if (reply != null) {
				// Reply received
				if (reply.getPerformative() == ACLMessage.PROPOSE) {
					// This is an offer
					int price = Integer.parseInt(reply.getContent());
					if (bestSeller == null || price < bestPrice) {
						// This is the best offer at present
						bestPrice = price;
						bestSeller = reply.getSender();
					}
				}
				repliesCount++;
				if (repliesCount >= bbAgent.getSellerAgents().length) {
					// Received all replies
					step = 2;
				}
			}

			else {
				block();
			}

			break;

		case 2:
			// Send the purchase order to the seller that provided the best offer
			ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			order.addReceiver(bestSeller);
			order.setContent(bookTitle);
			order.setConversationId("book-trade");
			order.setReplyWith("order" + System.currentTimeMillis());
			bbAgent.send(order);

			// Prepare the template to get the purchase order reply
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
					MessageTemplate.MatchInReplyTo(order.getReplyWith()));

			step = 3;

			break;

		case 3:
			// Receive the purchase order reply
			reply = myAgent.receive(mt);
			if (reply != null) {
				// Purchase order reply received
				if (reply.getPerformative() == ACLMessage.INFORM) {
					// Purchase successful. Terminate trading.
					System.out.println(
							bookTitle + " is successfully purchased from agent " + reply.getSender().getName());
					System.out.println("Price = " + bestPrice);
					// Pop up a message dialog
					Date timenow = new Date();
					JFrame f = new JFrame();
					JOptionPane.showMessageDialog(f,
							"Thank You! " + "Your ordered book : " + bookTitle + "'s price is RM" + bestPrice
									+ ". The book will start the delivery process by now: " + timenow);
					myAgent.doDelete();
				}

				else {
					// Pop up a message dialog
					JFrame f = new JFrame();
					JOptionPane.showMessageDialog(f,
							"Sorry. Attempt failed: Requested book " + bookTitle + " is not available for sale.");
					System.out.println("Attempt failed: " + bookTitle + " is not available for sale");
				}

				step = 4;
			}

			else {
				block();
			}

			break;
		}
	}

	public boolean done() {
		if (step == 2 && bestSeller == null) {
			// Pop up a message dialog
			JFrame f = new JFrame();
			JOptionPane.showMessageDialog(f,
					"Sorry. Attempt failed: Requested book " + bookTitle + " is not available for sale.");
			System.out.println("Attempt failed: " + bookTitle + " is not available for sale");
		}
		return ((step == 2 && bestSeller == null) || step == 4);
	}
}
