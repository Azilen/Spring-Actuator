package com.azilen.spring.actuate.endpoint.health;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.azilen.spring.actuate.endpoint.health.Health.Builder;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.util.features.Version;

public class CouchbaseHealthIndicator extends AbstractHealthIndicator{

	private CouchbaseOperations couchbaseOperations;

	public CouchbaseHealthIndicator(CouchbaseOperations couchbaseOperations) {
		Assert.notNull(couchbaseOperations, "CouchbaseOperations must not be null");
		this.couchbaseOperations = couchbaseOperations;
	}

	@Override
	public void doHealthCheck(Builder builder) throws Exception {
		int nodeCount = 0;
		Integer timeoutInMs = 2000;
		String nodeName = "";
		String nodeAddress = "";
		Boolean isNodeReachable = false;
		try{
			Bucket bucket = this.couchbaseOperations.getCouchbaseBucket();
			this.couchbaseOperations.getCouchbaseBucket().getAndTouch("id",4);
			List<Version> versions = this.couchbaseOperations.getCouchbaseClusterInfo()
					.getAllVersions();
			/*Start :: Fetching node details*/
			Map<String,Boolean> nodeDetails=new HashMap<String,Boolean>();
			nodeCount = bucket.bucketManager().info().nodeCount();
			for(int i=0;i<nodeCount;i++){
				nodeName = bucket.bucketManager().info().nodeList().get(i).getHostName();
				nodeAddress = bucket.bucketManager().info().nodeList().get(i).getHostAddress();
				isNodeReachable=bucket.bucketManager().info().nodeList().get(i).isReachable(timeoutInMs);
				nodeDetails.put(nodeAddress,isNodeReachable);
			}
			/*End :: Fetching node details*/
			builder.up().withDetail("versions",
					StringUtils.collectionToCommaDelimitedString(versions));
			builder.up().withDetail("nodes",nodeDetails);
		}
		catch(Exception e){
			builder.down();
		}
			
		
	}

}
