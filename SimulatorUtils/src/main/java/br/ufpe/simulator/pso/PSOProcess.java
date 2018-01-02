package br.ufpe.simulator.pso;

import java.util.List;

/* author: gandhi - gandhi.mtm [at] gmail [dot] com - Depok, Indonesia */

// this is the heart of the PSO program
// the code is for 2-dimensional space problem
// but you can easily modify it to solve higher dimensional space problem

import java.util.Random;
import java.util.Vector;

import br.ufpe.simulator.utils.ConvertUtils;

public class PSOProcess implements PSOConstants {
	private Vector<Particle> swarm = new Vector<Particle>();
	private double[] pBest = new double[SWARM_SIZE];
	private Vector<Location> pBestLocation = new Vector<Location>();
	private double gBest;
	private Location gBestLocation;
	private double[] fitnessValueList = new double[SWARM_SIZE];

	Random generator = new Random();

	public void execute(IProblemSet iProblemSet) {
		initializeSwarm(iProblemSet);
		updateFitnessList(iProblemSet);

		for (int i = 0; i < SWARM_SIZE; i++) {
			pBest[i] = fitnessValueList[i];
			pBestLocation.add(swarm.get(i).getLocation());
		}

		int t = 0;
		double w;
		double err = 9999;

		while (t < MAX_ITERATION && err > ExampleProblemSet.ERR_TOLERANCE) {
			// step 1 - update pBest
			for (int i = 0; i < SWARM_SIZE; i++) {
				if (fitnessValueList[i] < pBest[i]) {
					pBest[i] = fitnessValueList[i];
					pBestLocation.set(i, swarm.get(i).getLocation());
				}
			}

			// step 2 - update gBest
			int bestParticleIndex = PSOUtility.getMinPos(fitnessValueList);
			if (t == 0 || fitnessValueList[bestParticleIndex] < gBest) {
				gBest = fitnessValueList[bestParticleIndex];
				gBestLocation = swarm.get(bestParticleIndex).getLocation();
			}

			w = W_UPPERBOUND - (((double) t) / MAX_ITERATION) * (W_UPPERBOUND - W_LOWERBOUND);

			for (int i = 0; i < SWARM_SIZE; i++) {
				double r1 = generator.nextDouble();
				double r2 = generator.nextDouble();

				Particle p = swarm.get(i);

				// step 3 - update velocity
				double[] newVel = new double[iProblemSet.getProblemDimension()];
				for (int j = 0; j < newVel.length; j++) {
					double newVelocity = (w * p.getVelocity().getPos()[j])
							+ (r1 * C1) * (pBestLocation.get(i).getLoc()[j] - p.getLocation().getLoc()[j])
							+ (r2 * C2) * (gBestLocation.getLoc()[j] - p.getLocation().getLoc()[j]);
					newVel[j] = newVelocity;
				}
				Velocity vel = new Velocity(newVel);
				p.setVelocity(vel);

				// step 4 - update location
				double[] newLoc = new double[iProblemSet.getProblemDimension()];
				for (int j = 0; j < newLoc.length; j++) {
					double newLocation = p.getLocation().getLoc()[j] + newVel[j];
					newLoc[j] = Math.abs(newLocation);
				}
				Location loc = new Location(newLoc);
				p.setLocation(loc);
			}

			err = iProblemSet.evaluate(gBestLocation) - 0; // minimizing the
															// functions means
															// it's getting
															// closer to 0
			t++;
			System.out.println("------------" + t + "---------------------");
			System.out.println("--------------GBEST--------------------");
			System.out.println(ConvertUtils.convertToLocaleString(gBest));
			System.out.println("--------------GBEST--LOCATION---------");
			System.out.println(gBestLocation);
			System.out.println("---------------------------------------");
			updateFitnessList(iProblemSet);
		}
	}

	public void initializeSwarm(IProblemSet iProblemSet) {
		Particle p;
		List<Double[]> linkCosts = iProblemSet.getLinkCosts();
		for (int i = 0; i < SWARM_SIZE; i++) {
			p = new Particle();

			double[] loc = new double[iProblemSet.getProblemDimension()];
			for (int j = 0; j < loc.length; j++) {
				if (linkCosts != null) {
					loc[j] = linkCosts.get(i)[j];
				} else {
					// randomize location inside a space defined in Problem Set
					loc[j] = iProblemSet.getProblemRanges().get(j).getLow()
							+ generator.nextDouble() * (iProblemSet.getProblemRanges().get(j).getHigh()
									- iProblemSet.getProblemRanges().get(j).getLow());
				}
			}
			Location location = new Location(loc);

			// randomize velocity in the range defined in Problem Set
			double[] vel = new double[iProblemSet.getProblemDimension()];
			for (int j = 0; j < vel.length; j++) {
				vel[j] = iProblemSet.getProblemRanges().get(j).getVelocityLow()
						+ generator.nextDouble() * (iProblemSet.getProblemRanges().get(j).getVelocityHigh()
								- iProblemSet.getProblemRanges().get(j).getVelocityLow());
			}
			Velocity velocity = new Velocity(vel);

			p.setLocation(location);
			p.setVelocity(velocity);
			swarm.add(p);
		}
	}

	public void updateFitnessList(IProblemSet iProblemSet) {
		for (int i = 0; i < SWARM_SIZE; i++) {
			fitnessValueList[i] = swarm.get(i).getFitnessValue(iProblemSet);
		}
	}
}
