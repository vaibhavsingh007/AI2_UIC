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
    //private Random rand;
    
	//[][][safe/w/w?, p/p?, bump location]
	//(0=w,1=w?,3=safe; 0,1=p,p?; 0,1,2,3=BE,BW,BN,BS; -1,1=unexplored,visited)
	private int[][][] world;

	// Set static world size
	private int rowSize = 4;
	private int colSize = 4;
	private int cellSize = 3;
	
	private String agentMove = '';	// R,L,F
	private String agentOrientation = '';	// E,W,N,S
	private int agentXLoc;
	private int agentYLoc;


    public AgentFunction() {
        // for illustration purposes; you may delete all code
        // inside this constructor when implementing your 
        // own intelligent agent

        // this integer array will store the agent actions
        actionTable = new ArrayList();
		world = new int[rowSize][colSize][cellSize];
		initializeWorld(world);
		agentOrientation = 'E';		// Agent spawns facing E
		agentXLoc = rowSize-1;
		agentYLoc = 0;
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
    }

	private void initializeWorld()
	{
		for (x=0; x<rowSize; x++)
		{
			for (y=0; y<colSize; y++)
			{
				world[x][y] = new int[]{-1, -1, -1};
				//for (z=0; z<cellSize; z++)
				//{
				//	world[x][y][z] = -1;
				//}
			}
		}

		// Mark initial location (bottom left)
		world[rowSize-1][0] = new int[]{2, -1, -1};
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
    
    private void updateWorld()
    {
		int[] previousAgentLoc = {agentXLoc, agentYLoc};	// Useful if agent executed successful F move.
		updateAgentLocation();

		// Start with the highest priority updates.
        if (bump) {
            // Mark bump location
			world[agentXLoc][agentYLoc][2] = getBumpPosition();
        }

        if (stench) {
            performWumpusUpdate(previousAgentLoc);
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

		// If no stench or breeze and move was F, mark adjacent cells safe.
		if (!stench && !breeze)
		{
			markWorldCells(2, 0, getAdjacentCells(), previousAgentLoc);
		}
    }

	private void updateAgentLocation()
	{
		// Ignore if move not F, or bumped.
		if (agentMove != 'F' || bump)
		{
			return;
		}

		if (agentOrientation == 'E')
		{
			agentYLoc++;
		}
		else if (agentOrientation == 'W')
		{
			agentYLoc--;
		}
		else if (agentOrientation == 'N')
		{
			agentXLoc--;
		}
		else if (agentOrientation == 'S')
		{
			agentXLoc++;
		}
	}

	private void performWumpusUpdate(int[] previousAgentLoc)
	{
		// Update adjacent w? to w
		ArrayList<int[]> adjacentCells = getAdjacentCells();
		wumpusConfirmed = false;

		for (int i=0; i<adjacentCells.size(); i++)
		{
			int[] cell = adjacentCells.get(i);

			if (world[cell[0]][cell[1]][0] == 1)	// w?
			{
				world[cell[0]][cell[1]][0] = 0;		// w
				wumpusConfirmed = true;
			}
		}

		if (!wumpusConfirmed)
		{
			// Mark all adjacent as w?
			markWorldCells(1, 0, adjacentCells, previousAgentLoc);
		}
		else
		{
			// Once wumpus is confirmed, remove all other w?. And if no p?/p, mark safe.
			removeWAndMarkSafe();
		}
	}

	// Only deals with marking w/p/w?/p?/safe
	private void markWorldCells(int marker, int markerIndex, ArrayList<int[]> cells, int[] previousAgentLoc)
	{
		// Proceed only if F and no bump
		if (agentMove != 'F' || bump)
		{
			return;
		}

		for (int i=0; i<cells.size(); i++)
		{
			currentCell = cells.get(i);

			// Exclude the cell agent came from, or safe.
			if (Arrays.equals(currentCell, previousAgentLoc) || world[currentCell[0]][currentCell[1]][0] == 2)
			{
				continue;
			}

			world[currentCell[0]][currentCell[1]][markerIndex] = marker;
		}
	}

	// Remove w? and marks world cells safe.
	private void removeWAndMarkSafe()
	{
		for (x=0; x<rowSize; x++)
		{
			for (y=0; y<colSize; y++)
			{
				if (world[x][y][0] == 1 && world[x][y][1] == -1)	// w? and not p?/p
				{
					world[x][y][0] = 2;
				}
			}
		}
	}

	private ArrayList<int[]> getAdjacentCells()
	{
		ArrayList<int[]> retval = new ArrayList<int[]>();

		// Check bounds
		if (agentYLoc > 0)
		{
			retval.add(new int[]{agentXLoc, agentYLoc-1});
		}
		if (agentYLoc < colSize -1)
		{
			retval.add(new int[]{agentXLoc, agentYLoc+1});
		}
		if (agentXLoc > 0)
		{
			retval.add(new int[]{agentXLoc-1, agentYLoc});
		}
		if (agentXLoc < rowSize-1)
		{
			retval.add(new int[]{agentXLoc+1, agentYLoc});
		}

		return retval;
	}

	private int getBumpPosition()
	{
		if (agentOrientation == 'E')
		{
			return 0;
		}
		if (agentOrientation == 'W')
		{
			return 1;
		}
		if (agentOrientation == 'N')
		{
			return 2;
		}
		if (agentOrientation == 'S')
		{
			return 3;
		}
		return -1;
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


    // public method to return the agent's name
    // do not remove this method
    public String getAgentName() {
        return agentName;
    }
}
