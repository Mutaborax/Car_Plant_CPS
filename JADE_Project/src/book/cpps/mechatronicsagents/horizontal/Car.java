package book.cpps.mechatronicsagents.horizontal;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Car extends Agent {

	private static final int PREDICT = 0;
	private static final int WAIT_FOR_REPAIR = 1 ;
	private static final int FAIL = 2;
	private int state = PREDICT;



    private Motor motor;         //component id = 0;
    private Trim trim;           //component id = 1;
    private Electronics electronics; //component id = 2;
    private Body body;           //component id = 3;
    private boolean repaired = true;
    private Component componentThatMayFail;

    Random rnd = new Random();

    public void setRepaired(boolean repaired) {
        this.repaired = repaired;
    }

    private Component predictFailures() {
    	FailurePrediction prediction = null;
    	int componentID = rnd.nextInt(4);

    	switch (componentID) {
    	    case 0:
    	        prediction = motor.predictFailure();
    	        if (prediction != null) {
    	            return motor;
    	        }
    	        break;
    	    case 1:
    	        prediction = trim.predictFailure();
    	        if (prediction != null) {
    	            return trim;
    	        }
    	        break;
    	    case 2:
    	        prediction = electronics.predictFailure();
    	        if (prediction != null) {
    	            return electronics;
    	        }
    	        break;
    	    case 3:
    	    	if (this.body == null) {
    	    	    throw new IllegalStateException("Body component has not been initialized.");
    	    	}
    	    	this.body.predictFailure();

    	        prediction = body.predictFailure();
    	        if (prediction != null) {
    	            return body;
    	        }
    	        break;
    	    default:
    	        return null;
    	}
    	return null;


    }

	private void operate() {
	    switch (state) {
	        case PREDICT:
	            System.out.println("Predicting Failures");
	            componentThatMayFail = predictFailures();
	            if (componentThatMayFail != null) {
	                System.out.println(componentThatMayFail.toString() + " from class " + getClass().getSimpleName() + " was selected...");
	                System.out.println("The component will have the following failure "
	                                   + componentThatMayFail.predictedFailure.getFailureDescription()
	                                   + " in " + componentThatMayFail.predictedFailure.getWhen() + " days!");
	                repaired = false;
	                System.out.println("Negotiating");
	                try {
	                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
	                    cfp.setOntology("Repair");
	                    cfp.setContentObject(componentThatMayFail);
	                    DFAgentDescription dfd = new DFAgentDescription();
	                    ServiceDescription sd = new ServiceDescription();
	                    sd.setName("Car Dealer");
	                    sd.setType("Repair");
	                    dfd.addServices(sd);
	                    DFAgentDescription[] registeredComponentFactories = DFService.search(this, dfd);
	                    if (registeredComponentFactories.length != 0) {
	                        for (DFAgentDescription dfad : registeredComponentFactories) {
	                            cfp.addReceiver(dfad.getName());
	                        }
	                        addBehaviour(new ProductionRepairNegotiation(this, cfp, componentThatMayFail));
	                        state = WAIT_FOR_REPAIR;
	                    } else {
	                        System.out.println("Zero registered factories!");
	                        state = FAIL;
	                    }
	                } catch (IOException | FIPAException ex) {
	                    java.util.logging.Logger.getLogger(Car.class.getName()).log(Level.SEVERE, null, ex);
	                    state = FAIL;
	                }
	            } else {
	                System.out.println("No failure prediction for any component");
	            }
	            break;
	        case FAIL:
	            System.out.println("Agent Failure");
	            doDelete();
	            break;
	        case WAIT_FOR_REPAIR:
	            System.out.println("Waiting for repair");
	            if (repaired) {
	                state = PREDICT;
	            }
	            break;
	    }
	}


		private Component checkComponentForFailure(Component component) {
		    if(component != null) {
		        FailurePrediction prediction = component.predictFailure();
		        if (prediction != null) {
		            return component;
		        }
		    }
		    return null;
		}



		@Override
		protected void setup() {
			motor = new Motor("Motor of " + this.getLocalName());
			trim = new Trim("Trim of " + this.getLocalName());
			electronics = new Electronics("Electronics of " + this.getLocalName());
			body = new Body("Body of " + this.getLocalName());

			addBehaviour(new TickerBehaviour(this, 5000) {
			    @Override
			    protected void onTick() {
			        operate();
			    }
			});

		    System.out.println("The car with name " + this.getLocalName() + " is active now!");

		    addBehaviour(new TickerBehaviour(this, 5000) {
		        @Override
		        protected void onTick() {
		            Component cpn = predictFailures();
		            if (cpn != null) {
		                System.out.println(cpn.toString() + " from class " + getClass().getSimpleName() + " was selected");
		                System.out.println("The component will have the following failure " + cpn.predictedFailure.getFailureDescription() + " in " + cpn.predictedFailure.getWhen() + " days!");
		            } else {
		                System.out.println("No failure prediction for any component");
		            }
		        }
		    });
		}}

