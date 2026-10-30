package example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.util.Properties;

public class ExampleFitnessFunction implements BulkFitnessFunction{

	private final static int DEFAULT_TIMESTEPS = 500;  // The maximum steps each trial
	private int maxTimesteps = DEFAULT_TIMESTEPS;

	private final static int DEFAULT_NUM_TRIALS = 100;  // The maximum number of trials
	private int numTrials = DEFAULT_NUM_TRIALS;
	
	boolean enableDisplay = false;
	boolean testMode = false;
	
	boolean neuronActLog = false;
	FileWriter outFile = null;
	PrintWriter out = null;
	String filenameNActLog = "./neurons_actLog.txt";
	
	//private ActivatorTranscriber factory;
	private ActivatorTranscriber activatorTranscriber;
	private int maxFitnessValue;
	
	private int test_set[][];
	
	public void setEnableDisplay(boolean tf) {
		enableDisplay = tf;
	}
	//This is called when Evaluatedit is run to set neuronActLog true
	public void setNeuronActLog(boolean tf) {
		neuronActLog = tf;
	}
	
	public ExampleFitnessFunction() throws IOException {
		if( testMode )
		{
			try {
				BufferedReader reader = new BufferedReader(new FileReader("/Users/Jaewook/Documents/workspace/Everything_09112014/db/test/test_set.txt"));
				int size, x, y;
				String input;
				input = reader.readLine();
				size = Integer.parseInt(input);
				test_set = new int[size][2];
				for( int i = 0; i < size; i++ ) {
					input = reader.readLine();
					x = Integer.parseInt(input);
					input = reader.readLine();
					y = Integer.parseInt(input);
					test_set[i][0] = x;
					test_set[i][1] = y;
					System.out.println(x);
					System.out.println(y);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
     * @see com.anji.util.Configurable#init(com.anji.util.Properties)
     */
	public void init( Properties props ) throws Exception {
		try {
			// 1. Init activator transcriber
			// This seems never called for our case
			//factory = (ActivatorTranscriber) props.singletonObjectProperty( ActivatorTranscriber.class );
		}
		catch ( Exception e ) {
			throw new IllegalArgumentException( "invalid properties: " + e.getClass().toString() + ": "
					+ e.getMessage() );
		}
	}
    
    public void evaluate( Chromosome c ) throws InterruptedException {
    	Activator activator;
    	
		try {
			activator = activatorTranscriber.newActivator(c);
			//activator = factory.newActivator( c );
		
			double input[] = new double[8];
			double output[];
			double output0, output1;
			double fitness = 0.0;
			
			int bx = 300;
			int by = 400;
			int l1 = 80;
			int l2 = 100;
			int tool_len = 80;
			
			double distance = 0.0; //the distance to the target from the end effector
			double finalDistanceSum = 0.0;
			double finalStepSum = 0.0;
			double countReaches = 0.0;
			double countHold = 0.0;
			
			if(neuronActLog) {
				try {
					outFile = new FileWriter(filenameNActLog);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				out = new PrintWriter(outFile);
			}
			
			ArmReaching arm = new ArmReaching( bx, by, l1, l2, tool_len );
			Display canvas = new Display();
			if( enableDisplay )
				canvas.init( bx, by, 600, 600 );
			
			int j = 0;
			
			for( int i = 0; i < numTrials; i++ ) {
				
				if(neuronActLog) {
					out.println("numTrial: " + i);
				}
				
				//reset
				arm.resetArm();
				arm.resetTool();
				
				if( testMode )
					arm.setTarget(test_set[i][0], test_set[i][1]);
				else
					arm.resetTargetBeyond();
//				
				
				for(j = 0; j < maxTimesteps; j++ ) {
					
					input[0] = arm.theta1/90;
					input[1] = arm.theta2/90; //should be divided by 170?

					//straight angle to target (degree_theta_bt)
					//straight distance to target (d_bt)
					double d_bt = Math.sqrt((arm.bx-arm.tx)*(arm.bx-arm.tx) + (arm.by-arm.ty)*(arm.by-arm.ty));
					double theta_bt = Math.asin((arm.bx-arm.tx) / -d_bt);
					double degree_theta_bt = theta_bt * 180 / Math.PI;
					
					//straight angle to the end effector (degree_theta_be)
					//straight distance to the end effector (d_be)
					double d_be = Math.sqrt((arm.bx-arm.ex)*(arm.bx-arm.ex) + (arm.by-arm.ey)*(arm.by-arm.ey));
					double theta_be = Math.asin((arm.bx-arm.ex) / -d_be);
					double degree_theta_be = theta_be * 180 / Math.PI;
					if( arm.ey > arm.by )
					{
						if( arm.ex > arm.bx )
							degree_theta_be = 180 - degree_theta_be;
						else
							degree_theta_be = -180 - degree_theta_be;
					}

					
					double r_d = d_bt - d_be; // relative distance
					double r_degree_theta = degree_theta_bt - degree_theta_be;  // relative angle
					
					
					double d_bto = Math.sqrt((arm.bx-arm.pick_x)*(arm.bx-arm.pick_x) + (arm.by-arm.pick_y)*(arm.by-arm.pick_y));
					double theta_bto = Math.asin((arm.bx-arm.pick_x) / -d_bto);
					double degree_theta_bto = theta_bto * 180 / Math.PI;
					if( arm.pick_y > arm.by ) {
						if( arm.pick_x > arm.bx )
							degree_theta_bto = 180 - degree_theta_bto;
						else
							degree_theta_bto = -180 - degree_theta_bto;
					}
					
					double r_d_pick = d_bto - d_be;
					//double r_d_drop = l1+l2;
					double r_degree_pick = degree_theta_bto - degree_theta_be;
					//double r_degree_drop = -90 - degree_theta_be;
					
					input[2] = r_d/260;
					input[3] = r_degree_theta / 90;
					
					// the relative location to pick
					input[4] = r_d_pick/260;
					input[5] = r_degree_pick/90;
					
					if( arm.holding == true )
					{
						input[4] = 0;
						input[5] = 0;
					}
					
					// the relative location to drop
					//input[6] = r_d_drop;
					//input[7] = r_degree_drop/90;


					//These are to give some (sensory) feedback if the joints reach the limit
					//So hopefully learn to change the direction instead of pushing to the limit continuously  
					input[6] = 0;
					input[7] = 0;

					if ( Math.abs(arm.theta1 - 150.0 ) < 0.0001 ) {
						input[6] = 1.0;
					}
					else if( Math.abs(arm.theta1 + 150.0 ) < 0.0001 ) {
						input[6] = -1.0;
					}
					if ( Math.abs(arm.theta2 - 150.0 ) < 0.0001 ) {
						input[7] = 1.0;
					}
					else if( Math.abs(arm.theta2 + 150.0 ) < 0.0001 ) {
						input[7] = -1.0;
					}
					
					//input[8] = d_bt - l1 - l2;

					if(neuronActLog) {
						out.println("numTrial: " + i + " timeStep: " + j);
						//yschoe: output = activator.next(input, out, neuronActLog);
						output = activator.next(input);
						out.printf("\n");
					} else {
						output = activator.next(input);
					}
					//activator.reset(); //reset here is correct?
					
					//				output[0] = 2*(output[0]-0.5);
					//				output[1] = 2*(output[1]-0.5);
					// activation function
					// sigmoid: 0 ~ 1
					// tanh: -1 ~ 1
					output0 = 3*(output[0]-0.5);
					output1 = 3*(output[1]-0.5);
					//System.out.println(output[0]);
					//System.out.println(output[1]);
					//fitness += arm.move(output0, output1);
					arm.move(output0, output1);
					//distance = arm.getDist2Target();

					if( enableDisplay ) {
						canvas.nextFrame( arm );
					}
					
					if(arm.hasReach()) {
						break;
					}
					
				} //end for( int j = 0; j < maxTimesteps; j++ )
				
				//add up distance and step values to calculate the fitness later
				if(arm.hasReach()) {
					//should we put some extra reward when it reaches to the target?
					//add up the number of reaches?
					countReaches += 1.0;
				}
				if( arm.holding == true ) {
					countHold += 1.0;
				}
				
				distance = arm.getDist2Target();
				finalDistanceSum += (distance/200);
				finalStepSum += j;
								
			} //end for( int i = 0; i < numTrials; i++ )
			
			//calculate fitness
			double distFactor = (finalDistanceSum/(double)numTrials)/Math.sqrt(2.0);
			double stepFactor = ((finalStepSum-1)/numTrials)/(maxTimesteps); 
			double countFactor = (2.0*countReaches)/(double)numTrials;
			double toolFactor = countHold/(double)numTrials;
			
			//fitness = (1.0-stepFactor)*toolFactor*countFactor;
			//fitness = fitness * 1000;
			
			fitness = (1.0-distFactor)*(1.0-stepFactor)*toolFactor;
			//fitness = (1.0-stepFactor)*(1.0-stepFactor)*toolFactor;
			fitness = fitness * 1000;
			
			System.out.println("num of reaches: " + countReaches);
			System.out.println("finalDistanceSum: " + finalDistanceSum + " distFactor: " + distFactor);
			System.out.println("finalStepSum: " + finalStepSum + " stepFactor: " + stepFactor);
			System.out.println("finished one with fitness " + fitness);
			
			c.setFitnessValue((int)fitness);
			
		} catch (TranscriberException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(neuronActLog) {
			out.close();
		}
    }

	public void evaluate(List genotypes) {
		Iterator it = genotypes.iterator();
		maxFitnessValue = 0;
		while( it.hasNext() )
		{
			Chromosome c = (Chromosome) it.next();
			try {
				evaluate(c);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if( maxFitnessValue < c.getFitnessValue() )
				maxFitnessValue = c.getFitnessValue();
		}
	}


	public int getMaxFitnessValue() {
		return maxFitnessValue;
	}
	
	
	public void setActivatorTranscriber(ActivatorTranscriber activatorTranscriber){
		this.activatorTranscriber = activatorTranscriber;
	}

}
