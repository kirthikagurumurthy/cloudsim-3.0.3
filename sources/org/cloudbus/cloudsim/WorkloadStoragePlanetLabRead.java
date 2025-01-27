package org.cloudbus.cloudsim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * The Class UtilizationModelPlanetLab.
 */
public class WorkloadStoragePlanetLabRead implements WorkloadStorage {
	
	/** The scheduling interval. */
	private double schedulingInterval;

	/** The data (5 min * 288 = 24 hours). */
	private final double[] data; 
	
	private final  double[] speed; //size per read.
	
	private double Sizeread = 0;
	
	
	
	private int numberOfreads;
	/**
	 * Instantiates a new utilization model PlanetLab.
	 * 
	 * @param inputPath the input path
	 * @throws NumberFormatException the number format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public WorkloadStoragePlanetLabRead(String inputPath, double schedulingInterval)
			throws NumberFormatException,
			IOException {
		data = new double[289];
		speed = new double[289];
		setSchedulingInterval(schedulingInterval);
		BufferedReader input = new BufferedReader(new FileReader(inputPath));
		int n = data.length;
	
		for (int i = 0; i < n - 1; i++) {
			try {
			int length = ((input.readLine()).split(" ",-1)).length;
			String[] workloadvalues = new String[length];
			if(length <= 1) {
				data[i] = 0;
				speed[i] = 0;
			}
			else {
			workloadvalues = (input.readLine()).split(" ",-1);
			data[i] = Integer.valueOf(workloadvalues[4]);
			speed[i] = Integer.valueOf(workloadvalues[6]);
			
			}
			}
			catch(Exception e) {
				data[i]=0;
				speed[i] = 0;
			}
		}
		data[n - 1] = data[n - 2];
		speed[n-1] = speed[n-2];
		input.close();
	}
		
	
	
	/**
	 * Instantiates a new utilization model PlanetLab with variable data samples.
	 * 
	 * @param inputPath the input path
	 * @param dataSamples number of samples in the file
	 * @throws NumberFormatException the number format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public WorkloadStoragePlanetLabRead(String inputPath, double schedulingInterval, int dataSamples)
			throws NumberFormatException,
			IOException {
		setSchedulingInterval(schedulingInterval);
		data = new double[dataSamples];
		speed = new double[dataSamples];
		BufferedReader input = new BufferedReader(new FileReader(inputPath));
		int n = data.length;
	
		for (int i = 0; i < n - 1; i++) {
			try {
			int length = ((input.readLine()).split(" ",-1)).length;
			String[] workloadvalues = new String[length];
			if(length <= 1) {
				data[i] = 0;
				speed[i] = 0;
			}
			else {
			workloadvalues = (input.readLine()).split(" ",-1);
			data[i] = Integer.valueOf(workloadvalues[4]);
			speed[i] = Integer.valueOf(workloadvalues[6]);
			
			}
			}
			catch(Exception e) {
				data[i]=0;
				speed[i] = 0;
			}
		}
		data[n - 1] = data[n - 2];
		speed[n-1] = speed[n-2];
		input.close();
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.power.UtilizationModel#getUtilization(double)
	 */
	@Override
	public double getSize(double time, int fileSize)  // size read at a given time.
	{	
		if(fileSize>0) {
		if (time % getSchedulingInterval() == 0) {
		//	int fileSize = fileSize;
			//file.setFileSize(fileSize -(new Double(data[(int) time / (int) getSchedulingInterval()]*percentage*fileSize )).intValue());
			if(data[(int) time / (int) getSchedulingInterval()]*speed[(int) time / (int) getSchedulingInterval()]<fileSize) {
				return data[(int) time / (int) getSchedulingInterval()]*speed[(int) time / (int) getSchedulingInterval()];
			}
			else {
				return fileSize;
			}
		}
		
		int time1 = (int) Math.floor(time / getSchedulingInterval());
	//	int fileSize = fileSize;
		//file.setFileSize(fileSize -(new Double(data[time1]*percentage*fileSize )).intValue());
		if(data[time1]*speed[time1] < fileSize) {
			this.Sizeread = data[time1]*speed[time1]; // gives the size read. .
			return Sizeread;
		}
		else {
			return fileSize;
		}
		}
		else {
			return 0;
		}
	}
	
	
	public int  getNumber(double time, int fileSize) {
		if (fileSize < 0 ) {	
			return 0;
		}
		
		int time1 = (int) Math.floor(time / getSchedulingInterval());
		@SuppressWarnings("deprecation")
		Double NoW = new Double(data[time1]);
		int numberOfreads = NoW.intValue();
		return numberOfreads;
	}
	
	//Gets total number of writes .  SizePerWrite can be made as a constant value for a particular storage type.
	public int getTotalNumber(int fileSize ) {
		double size=0;
		double now = 0; //initial value of total number of reads
		for(int i=0; i<data.length;i++) {
			double numberOfreads = data[i];
			double ReadSpeed = speed[i];
			if(Math.floor(size + numberOfreads*ReadSpeed) < fileSize) { //checking if the writing again makes the size read greater than the file size
			now = now + numberOfreads;
			size = size + numberOfreads*ReadSpeed;
			}
			else if(size !=  fileSize){
				now = now+1; // If the above condition fails, then adding one more read to minimize error.
			}
			else {
				break;
			}
		}
		@SuppressWarnings("deprecation")
		Double tnw = new Double(now);
		int totalNumberOfreads = tnw.intValue();
		return totalNumberOfreads;
		
	}

	/**
	 * Sets the scheduling interval.
	 * 
	 * @param schedulingInterval the new scheduling interval
	 */
	public void setSchedulingInterval(double schedulingInterval) {
		this.schedulingInterval = schedulingInterval;
	}

	/**
	 * Gets the scheduling interval.
	 * 
	 * @return the scheduling interval
	 */
	public double getSchedulingInterval() {
		return schedulingInterval;
	}
}
