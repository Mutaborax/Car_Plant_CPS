package book.cpps.mechatronicsagents.horizontal;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class CarFactory extends Agent {

    @Override
    protected void setup() {
        // Register this agent's service with the Directory Facilitator (DF)
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("car-production");
        sd.setName(getLocalName() + "-car-production");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // Add a behavior to handle received messages (e.g., calls for proposals)
        addBehaviour(new HandleMessagesBehaviour());
    }

    @Override
    protected void takeDown() {
        // Deregister from the Directory Facilitator (DF)
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    private class HandleMessagesBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive();

            if (msg != null) {
                switch (msg.getPerformative()) {
                    case ACLMessage.CFP:
                        handleCallForProposal(msg);
                        break;
                    case ACLMessage.REQUEST:
                        handleRequest(msg);
                        break;
                    // Handle other types of messages if necessary
                }
            } else {
                block();
            }
        }

        private void handleCallForProposal(ACLMessage cfp) {
            ACLMessage reply = cfp.createReply();

            // Logic to decide whether to submit a proposal or not.
            // For this example, we'll always propose a fixed cost for car production/repair.

            double proposedCost = 1000.0; // Example fixed cost

            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setContent(String.valueOf(proposedCost));

            send(reply);
        }

        private void handleRequest(ACLMessage requestMsg) {
            ACLMessage reply = requestMsg.createReply();

            if ("need-car-parts".equals(requestMsg.getContent())) {
                // Check availability or production capability
                boolean canProduce = true; // Example
                if (canProduce) {
                    reply.setPerformative(ACLMessage.AGREE);
                    reply.setContent("car-parts-ready");
                } else {
                    reply.setPerformative(ACLMessage.REFUSE);
                }
            }

            send(reply);
        }
    }
}


