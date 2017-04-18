package com.discovery.network;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class Metrics {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private String name;
	private long createdTimestamp;
	private long operationStartedTimestamp;
	private long operationFinishedTimestamp;
	private long totalTime;
	private String operationResult;
	
	public Metrics(){
		this.name = "";
		this.operationResult = "NOT_SET";
		this.createdTimestamp = System.currentTimeMillis();
	}
	
	public Metrics(String name){
		this.name = name;
		this.createdTimestamp = System.currentTimeMillis();
	}
	
	public synchronized void updateTotalTime() {
		// TODO: validate values exist and assign defaults if not.
		this.totalTime = this.operationFinishedTimestamp - this.operationStartedTimestamp;
	}
	
	public synchronized void operationStarted() {
		this.operationStartedTimestamp = System.currentTimeMillis();
	}
	
	public synchronized void operationFinished() {
		this.operationFinishedTimestamp = System.currentTimeMillis();
		this.updateTotalTime();
	}
	
	public synchronized long getId() {
		return id;
	}
	
	public synchronized String getName() {
		return name;
	}
	
	public synchronized void setId(Integer id) {
		this.id = id;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized long getCreatedTimestamp() {
		return createdTimestamp;
	}

	public synchronized void setCreatedTimestamp(long createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public synchronized long getOperationStartedTimestamp() {
		return operationStartedTimestamp;
	}

	public synchronized void setOperationStartedTimestamp(long operationStartedTimestamp) {
		this.operationStartedTimestamp = operationStartedTimestamp;
	}

	public synchronized long getOperationFinishedTimestamp() {
		return operationFinishedTimestamp;
	}

	public synchronized void setOperationFinishedTimestamp(long operationFinishedTimestamp) {
		this.operationFinishedTimestamp = operationFinishedTimestamp;
	}

	public synchronized long getTotalTime() {
		return totalTime;
	}

	public synchronized void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public synchronized String getOperationResult() {
		return operationResult;
	}

	public synchronized void setOperationResult(String operationResult) {
		this.operationResult = operationResult;
	}

}
