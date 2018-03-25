package br.ufpe.simulator.pso;

import java.util.ArrayList;
import java.util.List;

/* author: gandhi - gandhi.mtm [at] gmail [dot] com - Depok, Indonesia */

// this is the heart of the PSO program
// the code is for 2-dimensional space problem
// but you can easily modify it to solve higher dimensional space problem

import java.util.Random;
import java.util.Vector;

import org.apache.log4j.Logger;

import br.ufpe.simulator.messages.MessageUtils;
import br.ufpe.simulator.utils.ConvertUtils;

public class PSOProcess implements PSOConstants {

	private static Logger logger = Logger.getLogger(PSOProcess.class);

	private static final String SIMULATION_PSO_SWARM_SIZE = "simulation.pso.swarm.size";
	private static final String SIMULATION_PSO_PARTICLE_DATA = "simulation.pso.particle.data";
	private static final String SIMULATION_PSO_PARTICLE_FITNESS = "simulation.pso.particle.fitness";
	private static final String SIMULATION_PSO_PARTICLE_PBEST = "simulation.pso.particle.pbest";

	private Vector<Particle> swarm = new Vector<Particle>();
	private double[] pBest = new double[SWARM_SIZE];
	private Vector<Location> pBestLocation = new Vector<Location>();
	private double gBest;
	private Location gBestLocation;
	private double[] fitnessValueList = new double[SWARM_SIZE];
	private double[] lBest = new double[SWARM_SIZE];
	private Vector<Location> lBestLocation = new Vector<Location>();
	private List<String> messages = new ArrayList<String>();
	Random generator = new Random();

	public void execute(IProblemSet iProblemSet) {
		System.out.println("Initializing swarm ...");
		initializeSwarm(iProblemSet);
		System.out.println("Updating Fitness ...");
		updateFitnessList(iProblemSet);

		for (int i = 0; i < SWARM_SIZE; i++) {
			pBest[i] = fitnessValueList[i];
			lBest[i] = fitnessValueList[i];
			pBestLocation.add(swarm.get(i).getLocation());
			lBestLocation.add(swarm.get(i).getLocation());
			if (logger.isInfoEnabled()) {
				logger.info(MessageUtils.createMessage(SIMULATION_PSO_PARTICLE_PBEST, i,
						ConvertUtils.convertToString(fitnessValueList[i])));
			}
		}

		int t = 0;
		double err = 9999;

		while (t < MAX_ITERATION && err > IProblemSet.ERR_TOLERANCE) {
			System.out.println("Iteration " + t);
			// step 1 - update pBest
			for (int i = 0; i < SWARM_SIZE; i++) {
				if (fitnessValueList[i] < pBest[i]) {
					pBest[i] = fitnessValueList[i];
					pBestLocation.set(i, swarm.get(i).getLocation());
					if (logger.isInfoEnabled()) {
						logger.info(MessageUtils.createMessage(SIMULATION_PSO_PARTICLE_PBEST, i, fitnessValueList[i]));
					}
				}
			}

			// step 2 - update gBest and lBest
			int bestParticleIndex = PSOUtility.getMinPos(fitnessValueList);
			if (t == 0 || fitnessValueList[bestParticleIndex] < gBest) {
				gBest = fitnessValueList[bestParticleIndex];
				gBestLocation = swarm.get(bestParticleIndex).getLocation();
			}
			System.out.println("	GBest " + gBest);
			// ring topology
			for (int i = 0; i < SWARM_SIZE; i++) {
				double[] localFitnessValueList = new double[3];
				if (i == 0) {
					localFitnessValueList[0] = fitnessValueList[SWARM_SIZE - 1];
					localFitnessValueList[2] = fitnessValueList[1];
				} else if (i == SWARM_SIZE - 1) {
					localFitnessValueList[0] = fitnessValueList[SWARM_SIZE - 2];
					localFitnessValueList[2] = fitnessValueList[0];
				} else {
					localFitnessValueList[0] = fitnessValueList[i - 1];
					localFitnessValueList[2] = fitnessValueList[i + 1];
				}
				localFitnessValueList[1] = fitnessValueList[i];

				int bestLocalParticleIndex = PSOUtility.getMinPos(localFitnessValueList);
				if (t == 0 || localFitnessValueList[bestLocalParticleIndex] < lBest[bestLocalParticleIndex]) {
					lBest[i] = localFitnessValueList[bestLocalParticleIndex];
					lBestLocation.set(i, swarm.get(bestParticleIndex).getLocation());
				}
			}

			for (int i = 0; i < SWARM_SIZE; i++) {
				Particle p = swarm.get(i);

				// step 3 - update velocity based on gBest

				double[] newVel = new double[PSOUtility.getDimension(iProblemSet)];
				for (int j = 0; j < newVel.length; j++) {
					newVel[j] = p.getVelocity().getPos()[j]
							+ C1 * (pBestLocation.get(i).getLoc()[j] - p.getLocation().getLoc()[j])
							+ C2 * (gBestLocation.getLoc()[j] - p.getLocation().getLoc()[j]);
				}

				// step 3 - update velocity based on lBest
				/*
				 * double[] newVel = new
				 * double[PSOUtility.getDimension(iProblemSet)]; for (int j = 0;
				 * j < newVel.length; j++) { newVel[j] =
				 * (p.getVelocity().getPos()[j]) + (r1 * C1) *
				 * (pBestLocation.get(i).getLoc()[j] -
				 * p.getLocation().getLoc()[j]) + (r2 * C2) *
				 * (lBestLocation.get(i).getLoc()[j] -
				 * p.getLocation().getLoc()[j]); }
				 */

				Velocity vel = new Velocity(newVel);
				p.setVelocity(vel);

				// step 4 - update location
				double[] newLoc = new double[PSOUtility.getDimension(iProblemSet)];
				for (int j = 0; j < newLoc.length; j++) {
					newLoc[j] = p.getLocation().getLoc()[j] + newVel[j];
				}
				Location loc = new Location(newLoc);
				p.setLocation(loc);
			}

			err = gBest - 0; // minimizing the
								// functions means
								// it's getting
								// closer to 0

			messages.add("ITERATION " + t + ": ");
			for (int i = 0; i < gBestLocation.getLoc().length; i++) {
				messages.add(Double.toString(gBestLocation.getLoc()[i]));
			}
			messages.add("     Value: " + gBest);

			t++;
			updateFitnessList(iProblemSet);
		}

		messages.add("\nSolution found at iteration " + (t - 1) + ", the solutions is:");
		for (int i = 0; i < gBestLocation.getLoc().length; i++) {
			messages.add(Double.toString(gBestLocation.getLoc()[i]));
		}
	}

	public void initializeSwarm(IProblemSet iProblemSet) {
		if (logger.isInfoEnabled()) {
			logger.info(MessageUtils.createMessage(SIMULATION_PSO_SWARM_SIZE, SWARM_SIZE));
		}
		Particle p;
		for (int i = 0; i < SWARM_SIZE; i++) {
			p = new Particle();

			// randomize location inside a space defined in Problem Set
			double[] loc = new double[PSOUtility.getDimension(iProblemSet)];
			for (int j = 0; j < loc.length; j++) {
				if (iProblemSet != null && iProblemSet.getLinkCosts() != null
						&& !iProblemSet.getLinkCosts().isEmpty()) {
					loc[j] = iProblemSet.getLinkCosts().get(i)[j];
				} else {
					loc[j] = IProblemSet.LOC_LOW
							+ generator.nextDouble() * (IProblemSet.LOC_HIGH - IProblemSet.LOC_LOW);
				}
			}

			Location location = new Location(loc);

			// randomize velocity in the range defined in Problem Set
			double[] vel = new double[PSOUtility.getDimension(iProblemSet)];
			for (int j = 0; j < vel.length; j++) {
				vel[j] = IProblemSet.VEL_LOW + generator.nextDouble() * (IProblemSet.VEL_HIGH - IProblemSet.VEL_LOW);
			}
			Velocity velocity = new Velocity(vel);

			p.setLocation(location);
			p.setVelocity(velocity);
			if (logger.isInfoEnabled()) {
				logger.info(MessageUtils.createMessage(SIMULATION_PSO_PARTICLE_DATA, i, p));
			}
			swarm.add(p);
		}
	}

	public void updateFitnessList(IProblemSet iProblemSet) {
		for (int i = 0; i < SWARM_SIZE; i++) {
			fitnessValueList[i] = swarm.get(i).getFitnessValue(iProblemSet);
			if (logger.isInfoEnabled()) {
				logger.info(MessageUtils.createMessage(SIMULATION_PSO_PARTICLE_FITNESS, i,
						ConvertUtils.convertToString(fitnessValueList[i])));
			}
		}
	}

	public List<String> getMessages() {
		return messages;
	}
}
