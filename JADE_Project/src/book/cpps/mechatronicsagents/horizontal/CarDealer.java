package book.cpps.mechatronicsagents.horizontal;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;

import jade.core.Agent;
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


public class CarDealer extends Agent {
	private Random rnd = new Random();

	@Override
	protected void setup() {
	    DFAgentDescription dfd = new DFAgentDescription();
	    ServiceDescription sd = new ServiceDescription();
	    sd.setName("Car Dealer");
	    sd.setType("Repair");
	    dfd.addServices(sd);
	    try {
	        DFService.register(this, dfd);
	    } catch (FIPAException ex) {
	        java.util.logging.Logger.getLogger(CarDealer.class.getName()).log(Level.SEVERE, null, ex);
	    }

	    addBehaviour(new ContractNetResponder(this, MessageTemplate.and(MessageTemplate.MatchOntology("Repair"),
	    	MessageTemplate.MatchPerformative(ACLMessage.CFP))) {
	            @Override
	            protected ACLMessage handleCfp(ACLMessage cfp)
	                    throws RefuseException, FailureException, NotUnderstoodException {
	                ACLMessage reply = cfp.createReply();
	                try {
	                    Component componentToFail = (Component) cfp.getContentObject();
	                    reply.setPerformative(ACLMessage.PROPOSE);
	                    TimeSlot repairTimeSlot = getNextAvailableRepairTimeSlot(componentToFail);
	                    reply.setContentObject(repairTimeSlot);
	                    System.out.println("Making a repair proposal for " + componentToFail.getComponentName() + " from "
	                            + cfp.getSender().getLocalName());
	                } catch (UnreadableException | IOException ex) {
	                    java.util.logging.Logger.getLogger(CarDealer.class.getName()).log(Level.SEVERE, null, ex);
	                    reply.setPerformative(ACLMessage.REFUSE);
	                }
	                return reply;
	            }

	            // Introducing a delay before sending the repair completion messenger
	            // Based on logical Communication with the agent
	            
	            @Override
	            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept)
	                    throws FailureException {
	                ACLMessage reply = accept.createReply();
	                reply.setPerformative(ACLMessage.INFORM);
	                repair(accept.getSender().getLocalName());
	                
	                try {
	                    Thread.sleep(2000);  // sleep for 2 seconds
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	                
	                reply.setPerformative(ACLMessage.INFORM);
	                return reply;
	            }
	        });

	        System.out.println("Car Dealer " + this.getLocalName() + " is active now!");
	    }

	    private TimeSlot getNextAvailableRepairTimeSlot(Component componentToFail) {
	        Date lastestRepairDate = TimeSlot.addDaysToDate(new Date(), componentToFail.predictedFailure.getWhen());
	        int repairDuration = determineRepairDuration();
	        return new TimeSlot(lastestRepairDate, repairDuration);
	    }

	    private int determineRepairDuration() {
	        return rnd.nextInt(4);
	    }

	    private void repair(String carName) {
	        System.out.println("Repairing a component from " + carName);
	    }
	}

