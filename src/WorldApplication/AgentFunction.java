/*
 * Class that defines the agent function.
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 * 
 * Last modified 2/19/07 
 * 
 * DISCLAIMER:
 * Elements of this application were borrowed from
 * the client-server implementation of the Wumpus
 * World Simulator written by Kruti Mehta at
 * The University of Texas at Arlington.
 * 
 */

import java.util.Random;
import java.util.*;

class AgentFunction {

    // string to store the agent's name
    // do not remove this variable
    private String agentName = "Agent Smith";

    // all of these variables are created and used
    // for illustration purposes; you may delete them
    // when implementing your own intelligent agent
    private List actionTable;
    private boolean bump;
    private boolean glitter;
    private boolean breeze;
    private boolean stench;
    private boolean scream;
    private Random rand;

    public AgentFunction() {
        // for illustration purposes; you may delete all code
        // inside this constructor when implementing your 
        // own intelligent agent

        // this integer array will store the agent actions
        actionTable = new ArrayList();

        //actionTable.add(0, Action.GO_FORWARD;
        //actionTable.add(1, Action.GO_FORWARD;
        //actionTable.add(2, Action.GO_FORWARD;
        //actionTable.add(3, Action.GO_FORWARD;
        //actionTable.add(4, Action.TURN_RIGHT;
        //actionTable.add(5, Action.TURN_LEFT;
        //actionTable.add(6, Action.GRAB;
        //actionTable.add(7, Action.SHOOT;
        // new random number generator, for
        // randomly picking actions to execute
        rand = new Random();
    }

    public int process(TransferPercept tp) {
        // To build your own intelligent agent, replace
        // all code below this comment block. You have
        // access to all percepts through the object
        // 'tp' as illustrated here:

        // read in the current percepts
        bump = tp.getBump();
        glitter = tp.getGlitter();
        breeze = tp.getBreeze();
        stench = tp.getStench();
        scream = tp.getScream();

        actionTable = new ArrayList();
        populateValidActions();

        // return action to be performed
        int actionSize = actionTable.size();
        int actionNo = 0;

        // Handling 0 issue with Rand.nextInt
        if (actionSize > 1) {
            actionNo = rand.nextInt(actionSize - 1);
        }

        //System.out.println("blahh..");
        return (int) actionTable.get(actionNo);
    }

    private void populateValidActions() {
        // Start with the highest priority actions
        if (glitter) {
            actionTable.add(0, Action.GRAB);
            return;
        }

        if (bump) {
            populateNavigationActions(true);
            return;
        }

        if (stench) {
            populateEquiProbNavigationActionsWithShoot();
            return;
        }

        if (scream) {
            actionTable.add(0, Action.GO_FORWARD);
            return;
        }

        if (breeze) {
            populateNavigationActions(true);
//            populateEquiProbNavigationActionsWithShoot(false);
            return;
        }

        populateNavigationActions(false);
    }

    // Helper method to populate normal navigation actions with P(F) = 67%
    private void populateNavigationActions(boolean onlySideways) {
        actionTable.add(0, Action.TURN_RIGHT);
        actionTable.add(1, Action.TURN_LEFT);

        if (!onlySideways) {
            actionTable.add(2, Action.GO_FORWARD);
            actionTable.add(3, Action.GO_FORWARD);
            actionTable.add(4, Action.GO_FORWARD);
            actionTable.add(5, Action.GO_FORWARD);
        }
    }

    // Populates all navigation actions and shoot with equal probabilities.
    private void populateEquiProbNavigationActionsWithShoot() {
        actionTable.add(0, Action.GO_FORWARD);
        actionTable.add(1, Action.TURN_RIGHT);
        actionTable.add(2, Action.TURN_LEFT);

        // Appreciate probability of SHOOT to 57%
        actionTable.add(3, Action.SHOOT);
        actionTable.add(4, Action.SHOOT);
        actionTable.add(5, Action.SHOOT);
        actionTable.add(6, Action.SHOOT);
    }

    // public method to return the agent's name
    // do not remove this method
    public String getAgentName() {
        return agentName;
    }
}
