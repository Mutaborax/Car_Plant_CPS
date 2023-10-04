package book.cpps.mechatronicsagents.horizontal;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;



public class RepairProposalsEvaluation extends SimpleBehaviour {

		private static final int EVALUTE_PROPOSALS = 0;
		private static final int ISSUE_CFP = 1;
		private static final int WAIT_FOR_NEGOTIATION = 2;
		private static final int END = 3;
		private static final int FAIL = 4;


		private int state = EVALUTE_PROPOSALS;
		private ArrayList<ACLMessage> allProposes = new ArrayList<>();
		private Component componentToFail;
		private boolean negotiationComplete = false;
		private Pair<TimeSlot, ACLMessage> selectedProposal=null;
		private boolean done = false;

		private ProductionRepairNegotiation productionRepairNegotiation;


		 public RepairProposalsEvaluation(Agent a, Component
		componentToFail) {
		 super (a) ;
		 this.componentToFail = componentToFail;
		 }

		 public void setSelectedProposal (Pair<TimeSlot,
		ACLMessage> selectedProposal) {
		 this.selectedProposal = selectedProposal;
		 }

		 public void setNegotiationComplete (boolean
		negotiationComplete) {
		 this.negotiationComplete = negotiationComplete;

		 }

		@Override
		public void action() {
		switch (state) {
			case EVALUTE_PROPOSALS:
				Vector<ACLMessage> replies =
						(Vector)this.getParent().getDataStore().get(((ContractNetInitiator)this.getParent()).ALL_RESPONSES_KEY);
				if (replies != null) {
	                for (ACLMessage msg : replies) {
	                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
	                        allProposes.add(msg);
	                    }
	                }
	            } else {
	                java.util.logging.Logger.getLogger(RepairProposalsEvaluation.class.getName()).log(Level.WARNING, "Replies vector is null.");
	                state = FAIL; // Set state to FAIL or handle this situation accordingly.
	                break;
	            }
				for (ACLMessage msg : replies) {
					if (msg.getPerformative() == ACLMessage.PROPOSE) {
				allProposes.add(msg);
					}
				}
				allProposes.sort((ACLMessage ol, ACLMessage o2)
						-> {
					try {
					return ((TimeSlot) ol.getContentObject()).compareTo((TimeSlot) o2.getContentObject());
					} catch (UnreadableException ex) {
					java.util.logging.Logger.getLogger (RepairProposalsEvaluation.class.getName()).log(Level.SEVERE, null, ex);
				}
				return 0;
				});

				System.out.println("Sorted Repair Proposals");
				allProposes.forEach((msg) -> {
				try {
					System.out.println("From " + ((TimeSlot) msg.
				getContentObject()).getFrom() + " with duration " +
				((TimeSlot) msg.getContentObject()).getDuration());
				} catch (UnreadableException ex) {
					java.util.logging.Logger.getLogger(RepairProposalsEvaluation.class.getName()).log(Level.SEVERE, null, ex);
				}
				});
				state = ISSUE_CFP;
			 break;
			case ISSUE_CFP:
			 try {
				ACLMessage productionCFP = new ACLMessage(
				ACLMessage.CFP) ;
					productionCFP.setContentObject(new Pair(componentToFail, allProposes));
					productionCFP.setOntology ("Order") ;
					DFAgentDescription dfd = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setName ("Factory") ;
					sd.setType ("Component Factory");
					dfd.addServices (sd);
					DFAgentDescription[]
				registeredComponentFactories = DFService.search(myAgent, dfd);
					for (DFAgentDescription dfad:
						registeredComponentFactories) {
						productionCFP.addReceiver(dfad.getName());
					}
					myAgent.addBehaviour(new ProductionNegotiation(
				myAgent, productionCFP, this));
					state = WAIT_FOR_NEGOTIATION;
				 }	catch (IOException | FIPAException ex) {
					java.util.logging.Logger.getLogger (RepairProposalsEvaluation.
				class.getName()).log(Level.SEVERE, null, ex);
				 state = FAIL;
				 }
			 	break;
			 case WAIT_FOR_NEGOTIATION:
				 if (negotiationComplete) {
					 if (selectedProposal == null) {
					 state = FAIL;
					 } else {
					 ACLMessage acceptedRepairProposal =selectedProposal.getSecond();
					  Vector<ACLMessage> acceptances = new Vector<>();
					 for (ACLMessage msg : allProposes) {
					 ACLMessage reply = msg.createReply();
					  try {
					 if (msg.getContent().equals(
					 acceptedRepairProposal.getContent())) {
					  reply.setContentObject (selectedProposal.getFirst());
					  reply.setPerformative (ACLMessage.ACCEPT_PROPOSAL);
					  } else {
					  reply.setPerformative (ACLMessage.REJECT_PROPOSAL);
					 }
					  } catch (IOException ex) {
					  java.util.logging.Logger.getLogger(
					 RepairProposalsEvaluation.class.getName()).log(Level.SEVERE, null, ex);
					  }
					  acceptances.add(reply);
					  }

					  this.getParent().getDataStore().put(((ContractNetInitiator) this.getParent()).ALL_ACCEPTANCES_KEY, acceptances);
					  state = END;
					  	}
					  }
				 break;
			 case FAIL:
				    // 1. Log the failure
				    java.util.logging.Logger.getLogger(RepairProposalsEvaluation.class.getName()).log(Level.SEVERE, "Negotiation failed.");
				    // 2. Notify other agents about the failure
				    ACLMessage failureNotification = new ACLMessage(ACLMessage.INFORM);
				    failureNotification.setOntology("Negotiation Failure");
				    failureNotification.setContent("Failed during RepairProposalsEvaluation.");
				    // Set the receivers depending on which agents need to know about the failure
				    DFAgentDescription dfd = new DFAgentDescription();
				    ServiceDescription sd = new ServiceDescription();
				    sd.setName("Factory");
				    sd.setType("Component Factory");
				    dfd.addServices(sd);
				    try {
				        DFAgentDescription[] registeredComponentFactories = DFService.search(myAgent, dfd);
				        for (DFAgentDescription dfad: registeredComponentFactories) {
				            failureNotification.addReceiver(dfad.getName());
				        }
				        myAgent.send(failureNotification);
				    } catch (FIPAException ex) {
				        java.util.logging.Logger.getLogger(RepairProposalsEvaluation.class.getName()).log(Level.SEVERE, "Error notifying other agents about negotiation failure.", ex);
				    }
				    // 3. Cleanup and Reset
				    allProposes.clear(); // Clear proposals as they're no longer valid after a failure
				    // May add more cleanup code depending on the specific requirements.
				    // If you want to start a new negotiation after a certain time or under certain conditions, reset the state
				    state = EVALUTE_PROPOSALS;
				    //state = END;
				    break;
			case END:
				done = true;
				break;
		 	}
		 }
		 @Override
		 public boolean done() {
		return done;
		}
}



