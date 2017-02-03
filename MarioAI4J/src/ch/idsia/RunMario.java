package ch.idsia;

import ch.idsia.benchmark.mario.MarioSimulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunMario {

	private static Logger log = LoggerFactory.getLogger(RunMario.class);

	public static void main(String[] args) {
		MarioSimulator.main(args);
	}
	
}
