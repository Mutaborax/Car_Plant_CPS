package book.cpps.mechatronicsagents.horizontal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;


public class ComponentFactory extends Factory {
    private Random rnd = new Random();

    public static Date dateMax(Date d1, Date d2) {
        if (d1 == null && d2 == null) {
            return null;
        }
        if (d1 == null) {
            return d2;
        }
        if (d2 == null) {
            return d1;
        }
        return (d1.after(d2)) ? d1 : d2;
    }

    @Override
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName("Factory");
        sd.setType("Component Factory");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException ex) {
            java.util.logging.Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        addBehaviour(new ContractNetResponder(this, MessageTemplate.and(MessageTemplate.MatchOntology("Order"), MessageTemplate.MatchPerformative(ACLMessage.CFP))) {

        	@Override
            protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
                ACLMessage reply = cfp.createReply();
                try {
                    Pair<Component, ArrayList<ACLMessage>> repairProposals =
                    		(Pair<Component, ArrayList<ACLMessage>>)cfp.getContentObject();

                    int selected = rnd.nextInt(repairProposals.getSecond().size());
                    ACLMessage msg = repairProposals.getSecond().get(selected);
                    TimeSlot repairSlot = (TimeSlot) msg.getContentObject();

                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContentObject(new Pair(new TimeSlot(
                            dateMax(TimeSlot.addDaysToDate(repairSlot.getFrom(), -rnd.nextInt(20)), new Date()),
                            new Random().nextInt(10)),msg));

                } catch (UnreadableException | IOException ex) {
                    java.util.logging.Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
                    reply.setPerformative(ACLMessage.REFUSE);
                }
                return reply;
        	}


            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
                ACLMessage reply = accept.createReply();
                try {
                	reply.setPerformative(ACLMessage.INFORM);
                	Pair<Component, ArrayList<ACLMessage>>
                	repairProposals = (Pair<Component, ArrayList<ACLMessage>>)cfp.getContentObject();

                		produceComponent(repairProposals.getFirst());
                		shipComponent(repairProposals.getFirst());
                		return reply;

                		} catch (UnreadableException ex) {
                		    java.util.logging.Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
                		    reply.setPerformative(ACLMessage.FAILURE);
                		}
                		return reply;

            }
        }
        );
    }

    private void produceComponent(Component component) {
        System.out.println(this.getLocalName() + " producing component " + component.getComponentName());
    }

    private void shipComponent(Component component) {
        System.out.println(this.getLocalName() + " shipping component " + component.getComponentName());
    }
}
