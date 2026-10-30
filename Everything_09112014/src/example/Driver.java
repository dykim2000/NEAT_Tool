package example;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;


import com.anji.integration.ActivatorTranscriber;
import com.anji.neat.Evolver;
import com.anji.util.Properties;

public class Driver {

	public static void main(String[] args){
		Properties properties = new Properties();
		final String propertiesFilename = "example.properties";
		try{
			properties.loadFromResource(propertiesFilename);
		}catch(IOException e){
			e.printStackTrace();
		}
		BasicConfigurator.configure();
		ExampleFitnessFunction fitnessFunction = (ExampleFitnessFunction) properties.singletonObjectProperty(Evolver.FITNESS_FUNCTION_CLASS_KEY);
		Evolver evolver = new Evolver();
		try{
			evolver.init(properties);
		}catch(Exception e){
			e.printStackTrace();
		}
		fitnessFunction.setActivatorTranscriber((ActivatorTranscriber) properties.singletonObjectProperty(ActivatorTranscriber.class));
		try {
			evolver.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
