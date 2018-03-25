package br.ufpe.eonsimulator.domain;

import java.util.ArrayList;
import java.util.List;

public class ComplexPath {

	private int numberOfBlockingRequests;
	private int numberOfNonBlockingRequests;
	private List<Path> paths;

	public ComplexPath() {
		super();
		this.paths = new ArrayList<Path>();
	}

	public int getNumberOfBlockingRequests() {
		return numberOfBlockingRequests;
	}

	public void setNumberOfBlockingRequests(int numberOfBlockingRequests) {
		this.numberOfBlockingRequests = numberOfBlockingRequests;
	}

	public int getNumberOfNonBlockingRequests() {
		return numberOfNonBlockingRequests;
	}

	public void setNumberOfNonBlockingRequests(int numberOfNonBlockingRequests) {
		this.numberOfNonBlockingRequests = numberOfNonBlockingRequests;
	}

	public List<Path> getPaths() {
		return paths;
	}

	public void setPaths(List<Path> paths) {
		this.paths = paths;
	}

	public void add(Path path) {
		this.paths.add(path);
		
	}
}
