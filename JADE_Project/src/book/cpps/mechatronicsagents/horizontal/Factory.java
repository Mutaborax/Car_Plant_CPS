package book.cpps.mechatronicsagents.horizontal;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Factory extends Agent {

    private static final String SERVICE_TYPE = "production";

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                String productName = (String) arg;
                registerService(productName);
            }
        }

        addBehaviour(new HandleProposalRequests());
    }

    private void registerService(String productName) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(SERVICE_TYPE);
        sd.setName(productName + "-production");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private class HandleProposalRequests extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                String content = msg.getContent();
                ACLMessage reply = msg.createReply();

                // If the content requests car parts, ask CarFactory
                if (content != null && content.equals("request-car-parts")) {
                    requestCarPartsFromCarFactory();
                } else if (content != null && content.equals("are-components-available")) {
                    handleQuery(msg, reply);
                }
            } else {
                block();
            }
        }

        private void requestCarPartsFromCarFactory() {
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.setContent("need-car-parts");
            // You need to know the AID of CarFactory or search for it using DFService
            request.addReceiver(getAID());
            send(request);
        }

        private void handleQuery(ACLMessage queryMsg, ACLMessage reply) {
            if ("are-components-available".equals(queryMsg.getContent())) {
                // Check component availability
                boolean available = true; // Example
                if (available) {
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("components-available");
                } else {
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("components-not-available");
                }
            }

            send(reply);
        }
    }
}
