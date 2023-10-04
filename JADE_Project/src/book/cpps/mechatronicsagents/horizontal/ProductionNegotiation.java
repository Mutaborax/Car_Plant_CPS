package book.cpps.mechatronicsagents.horizontal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;

public class ProductionNegotiation extends ContractNetInitiator {

    private final RepairProposalsEvaluation repairProposalsEvaluation;
    private final ArrayList<ACLMessage> allProposes = new ArrayList<>();

    public ProductionNegotiation(Agent a, ACLMessage cfp, RepairProposalsEvaluation repairProposalsEvaluation) {
        super(a, cfp);
        this.repairProposalsEvaluation = repairProposalsEvaluation;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
       responses.forEach((Object msgObj) -> {ACLMessage msg = (ACLMessage) msgObj;
            if (msg.getPerformative() == ACLMessage.PROPOSE) {
                allProposes.add(msg);
            }
       });

        allProposes.sort((ACLMessage o1, ACLMessage o2) -> {
            try {
                Pair<TimeSlot, ACLMessage> msg1 = ((Pair<TimeSlot, ACLMessage>) o1.getContentObject());
                Pair<TimeSlot, ACLMessage> msg2 = ((Pair<TimeSlot, ACLMessage>) o2.getContentObject());
                TimeSlot ts1 = msg1.getFirst();
                TimeSlot ts2 = msg2.getFirst();
                return ts1.compareTo(ts2);

            } catch (UnreadableException ex) {
                Logger.getLogger(RepairProposalsEvaluation.class.getName()).log(Level.SEVERE, null, ex);
                return 0;
            }
        });
        		System.out.println("Sorted Production Proposals");
        		allProposes.forEach((msg) ->{

		            try {
		                Pair<TimeSlot, ACLMessage> msg1 = ((Pair<TimeSlot, ACLMessage>) msg.getContentObject());
		                TimeSlot ts1 = msg1.getFirst();
		                System.out.println("From " + ts1.getFrom() + " with duration " + ts1.getDuration());
		            } catch (UnreadableException ex) {
		                Logger.getLogger(RepairProposalsEvaluation.class.getName()).log(Level.SEVERE, null, ex);
		            }
        		});

        		if (!allProposes.isEmpty()) {
        		    ACLMessage acceptedProduction = allProposes.remove(0);
        		    try {
        		        repairProposalsEvaluation.setSelectedProposal((Pair<TimeSlot, ACLMessage>) acceptedProduction.getContentObject());
        		        ACLMessage replyAccepted = acceptedProduction.createReply();
        		        replyAccepted.setContentObject(
        		            ((Pair<TimeSlot, ACLMessage>) acceptedProduction.getContentObject()).getFirst());
        		        replyAccepted.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        		        acceptances.add(replyAccepted);
        		        System.out.println("Accepted Production offer from " +
        		                           acceptedProduction.getSender().getLocalName());
        		    } catch (UnreadableException | IOException ex) {
        		        Logger.getLogger(ProductionNegotiation.class.getName()).log(Level.SEVERE, null, ex);
        		    }

        		    allProposes.forEach((ACLMessage msg) -> {
        		        ACLMessage replyRejected = msg.createReply();
        		        replyRejected.setPerformative(ACLMessage.REJECT_PROPOSAL);
        		        acceptances.add(replyRejected);
        		    });
        		} else {
        		    repairProposalsEvaluation.setSelectedProposal(null);
        		}
        		repairProposalsEvaluation.setNegotiationComplete(true);
    }
    		//Introducing logic to check the content of the Car agent messages to print the correct order of operation

			    @Override
			    protected void handleInform(ACLMessage inform) {
			        String messageContent = inform.getContent();
			
			        if ("part-shipped".equals(messageContent)) {
			            System.out.println(myAgent.getLocalName() + " Received inform that the part has been shipped");
			        } else if ("repair-done".equals(messageContent)) {
			            System.out.println(myAgent.getLocalName() + " Received inform that the repair is done");
        }
    }
}


