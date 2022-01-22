package bookBehaviour;

import bookAgent.BookSellerAgent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

// This is the behaviour used by Book seller agents to serve incoming requests 
// for offer from buyer agents.
// If the requested book is in the local catalogue the seller agent replies 
// with a PROPOSE message specifying the price. Otherwise a REFUSE message is
// sent back.

public class OfferRequestServer extends CyclicBehaviour {

	BookSellerAgent bsAgent;

	public OfferRequestServer(BookSellerAgent a) {
		bsAgent = a;
	}

	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
		ACLMessage msg = bsAgent.receive(mt);

		if (msg != null) {
			// CFP Message received. Process it
			String title = msg.getContent();
			ACLMessage reply = msg.createReply();

			Integer price = (Integer) bsAgent.getCatalogue().get(title);

			if (price != null) {
				// The requested book is available for sale. Reply with the price
				reply.setPerformative(ACLMessage.PROPOSE);
				reply.setContent(String.valueOf(price.intValue()));
			}

			else {
				// The requested book is NOT available for sale.
				reply.setPerformative(ACLMessage.REFUSE);
				reply.setContent("Not available.");
			}

			bsAgent.send(reply);
		}

		else {
			block();
		}
	}
}
