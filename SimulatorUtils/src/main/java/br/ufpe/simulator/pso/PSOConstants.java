package br.ufpe.simulator.pso;

/* author: gandhi - gandhi.mtm [at] gmail [dot] com - Depok, Indonesia */

// this is an interface to keep the configuration for the PSO
// you can modify the value depends on your needs

public interface PSOConstants {
	int SWARM_SIZE = 30; //greater then 2
	int MAX_ITERATION = 100;
	double C1 = 2.00;
	double C2 = 2.00;
}
