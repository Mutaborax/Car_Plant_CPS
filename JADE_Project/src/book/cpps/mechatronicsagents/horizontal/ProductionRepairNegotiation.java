package book.cpps.mechatronicsagents.horizontal;

import java.util.Vector;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;


public class ProductionRepairNegotiation extends ContractNetInitiator {

	private final Car carAgent;

	public ProductionRepairNegotiation(Agent a, ACLMessage cfp, Component componentToFail) {
		super(a, cfp);
		this.carAgent = (Car) a;
		registerHandleAllResponses(new RepairProposalsEvaluation(a, componentToFail));
	}

	@Override
	protected void handleInform(ACLMessage inform) {
		System.out.println("Received INFORM message. Repair has been done.");
		carAgent.setRepaired(true);
	}

	// Logging for refused proposals
	@Override
	protected void handleRefuse(ACLMessage refuse) {
		System.out.println("Received REFUSE message from: " + refuse.getSender().getName());
	}

	// Logging for failure notifications
	@Override
	protected void handleFailure(ACLMessage failure) {
		System.out.println("Received FAILURE message from: " + failure.getSender().getName());
		if (failure.getSender().equals(myAgent.getAMS())) {
			// FAILURE notification from the JADE runtime: the receiver
			// does not exist
			System.out.println("Responder does not exist");
		}
	}

	@Override
	protected void handleAllResultNotifications(Vector notifications) {
		for(Object notification : notifications) {
			ACLMessage msg = (ACLMessage) notification;
			if(msg.getPerformative() != ACLMessage.INFORM) {
				System.out.println("Received unexpected message: " + ACLMessage.getPerformative(msg.getPerformative()) + " from " + msg.getSender().getName());
			}
		}
	}

	@Override
	protected void handlePropose(ACLMessage propose, Vector v) {
		System.out.println("Received PROPOSE message from: " + propose.getSender().getName());

	}

	protected void handleAcceptProposal(ACLMessage acceptProposal, ACLMessage cfp, ACLMessage propose) {
	    System.out.println("Received ACCEPT_PROPOSAL message from: " + acceptProposal.getSender().getName());

	}

	protected void handleRejectProposal(ACLMessage rejectProposal, ACLMessage cfp, ACLMessage propose) {
	    System.out.println("Received REJECT_PROPOSAL message from: " + rejectProposal.getSender().getName());

	}

}