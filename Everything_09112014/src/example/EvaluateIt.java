package example;

import org.apache.log4j.Logger;
import org.jgap.Chromosome;
import org.jgap.Configuration;

import com.anji.integration.ActivatorTranscriber;
import com.anji.persistence.*;
import com.anji.util.*;
import org.apache.log4j.BasicConfigurator;

public class EvaluateIt {
	public static void main( String[] args ) throws Exception {
		Properties props = new Properties();
		
		props.loadFromResource( "example.properties" );
		
		BasicConfigurator.configure();
		
		ExampleFitnessFunction ff = new ExampleFitnessFunction();
		
		ff.setNeuronActLog(true);//record neuron activation log 
		ff.setEnableDisplay(true);//enable display : turn this off for fast computation
		ff.setActivatorTranscriber((ActivatorTranscriber) props.singletonObjectProperty(ActivatorTranscriber.class));
		ff.init( props );
		Persistence db = (Persistence) props.newObjectProperty( Persistence.PERSISTENCE_CLASS_KEY );
		Configuration config = new DummyConfiguration();
		Chromosome chrom = db.loadChromosome( "14456725", config );
		if ( chrom == null )
		    throw new IllegalArgumentException( "no chromosome found: " );
		ff.evaluate(chrom);
		logger.info( "Fitness = " + chrom.getFitnessValue() );
	}
	
	private static final Logger logger = Logger.getLogger( EvaluateIt.class );
}
