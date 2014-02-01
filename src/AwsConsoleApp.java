import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyResult;
//import com.amazonaws.services.dynamodb.datamodeling.KeyPair;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.ec2.model.AllocateAddressResult;
import com.amazonaws.services.ec2.model.AssociateAddressRequest;
import com.amazonaws.services.ec2.model.DisassociateAddressRequest;


import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImageAttributeRequest;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.AttachVolumeRequest;
import com.amazonaws.services.ec2.model.CreateVolumeRequest;
import com.amazonaws.services.ec2.model.CreateVolumeResult;
import com.amazonaws.services.ec2.model.DetachVolumeRequest;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;



public class AwsConsoleApp {

	static AmazonEC2      ec2;
	public static String keyPath = " ";

    public static void main(String[] args) throws Exception {

        System.out.println("===========================================");
        System.out.println("Welcome to the AWS Java SDK!");
        System.out.println("===========================================");

        AWSCredentials credentials = new PropertiesCredentials(AwsConsoleApp.class.getResourceAsStream("AwsCredentials.properties"));

   	  
        ec2 = new AmazonEC2Client(credentials);
     	AmazonS3Client s3  = new AmazonS3Client(credentials);
     //AmazonAutoScalingClient autoScale  = new AmazonAutoScalingClient(credentials);
     	AmazonCloudWatchClient cloudWatch = new AmazonCloudWatchClient(credentials);

     	String securityGroup = "Group_1";
     	String keyName = "Jingzhe";
     	String zone = "us-east-1d";
     	String imageId = "ami-35792c5c";
     	String bucketName = "songsong";
        
     	//createSecurityGroup(ec2, securityGroup);
		//createKey(keyName, ec2);
     	createBucket(s3, bucketName, zone);
		createS3File(s3, bucketName);
		
		
     	AWS_instance Song = new AWS_instance(keyName, securityGroup, zone, imageId, "Song's PC");
     	AWS_instance Lei = new AWS_instance(keyName, securityGroup, zone, imageId, "Lei's PC");
     	
     	
     	
     	/************************************************************************************ 
     	 * TEST CASE 1:
     	 * 
     	 * 
     	 * 1. the SSH API occasionally fail, we need to SSH into the machine by command line,
     	 add some work load to increase the CPU, then use cloudWatch to get the CPU Utilization.
     	 The code for increase CPU in the command line is: 
     	 
     	 while true
     	 do
     	 date
     	 usleep 60000
     	 done
     	 
     	 
     	 * 2. sleep 0.06s in the infinite loop to keep CPU between 0 and 100%
     	 
     	 * 3. When completing monitoring, using Ctrl+C to terminate the infinite loop
     	 ************************************************************************************/
     	
     	/*
     	//TEST CASE 1
     	Song.createInstance();
     	//Sleep 3 minutes for the instance to get fully started and to ssh into the machine
     	Thread.sleep(3*60*1000);
     	
     	
     	//increaseCPU(Song, keyName);
     	
     	//print the CPU usage every 6 seconds, but actually the cloud watch can only get the data every 1 minute.
     	int count = 0;
     	
       // We test for 10 minutes, so choose count as 100
		while(count<100){
			System.out.println("Song cpu = " + getCPUUsage(cloudWatch, Song.instanceId));
			Thread.sleep(6*1000);
			count++;
		}*/
		
     	

     	/************************************************************************************ 
     	 * TEST CASE 2:
     	 * 
     	 * 
     	 ************************************************************************************/
     	
     	List<AWS_instance> machines = Arrays.asList(Song, Lei);
     	
     	int days=1;
     		
		System.out.println("This is the first day");	
				
		//Create all instances and start them up;
		for (AWS_instance vm : machines)
		//Create instances and attach EBS & S3
		createAndStartUpVM(vm, bucketName);
     	
		if (days== 1){
			System.out.println("Sleep 3 minutes for the instance to get fully started");
			System.out.println("Please use the command line to add some workload to the machine.");
			System.out.println("Smaple command line code is:  while true; do date usleep 60000; done");
			
			Thread.sleep(3*60*1000);
				
			int count = 0;
		     	
			// We test for 6 minutes
		while(count<14){
			System.out.println("Machine Song's cpu = " + getCPUUsage(cloudWatch, Song.instanceId)+ "for the last 1 minutes");
			System.out.println("Machine Lei's cpu=" +getCPUUsage(cloudWatch, Lei.instanceId)+ "for the last 1 minutes");
			Thread.sleep(30*1000);
			count++;
			}
				
		System.out.println("Now it is 5:00 pm, the machine will be terminated.");
		for (AWS_instance vm : machines)
			terminateVM(vm);
            		
			days++;
		}
			
		if(days==2){
			System.out.println("This is the second day");	
			System.out.println("Employees are back, they create VMs from stored AMIs and restore the data partitions.");
			for (AWS_instance vm : machines)
				//Create instances and attach EBS & S3
				createAndStartUpVM(vm, bucketName);
		     	
				System.out.println("Sleep 3 minutes for the instance to get fully started");
				System.out.println("Please use the command line to add some workload to the machine.");
				System.out.println("Smaple command line code is:  while true; do date usleep 60000; done");
					
				Thread.sleep(3*60*1000);
						
				int count = 0;
				     	
					// We test for 7 minutes, so choose count as 80
				while(count<14){
					System.out.println("Machine Song's cpu = " + getCPUUsage(cloudWatch, Song.instanceId)+"  " +"  for the last 1 minutes");
					System.out.println("Machine Lei's cpu=" +getCPUUsage(cloudWatch, Lei.instanceId)+ "  "+  "for the last 1 minutes");
					Thread.sleep(30*1000);
					count++;
					}
						
				System.out.println("Now it is 5:00 pm, the machine will be terminated.");
				for (AWS_instance vm : machines)
					terminateVM(vm);
				}
	 	
		
		ec2.shutdown();
		s3.shutdown();
		cloudWatch.shutdown();
     	System.out.println("Done!!");
    }
    
    public static double getCPUUsage(AmazonCloudWatchClient cloudWatch, String instanceId){	

		try{
			//create request message
			GetMetricStatisticsRequest statRequest = new GetMetricStatisticsRequest();
			//set up request message
			statRequest.setNamespace("AWS/EC2"); //namespace
			statRequest.setPeriod(60); //period of data
			ArrayList<String> stats = new ArrayList<String>();
			//Use one of these strings: Average, Maximum, Minimum, SampleCount, Sum 
			stats.add("Average"); 
			stats.add("Sum");
			statRequest.setStatistics(stats);
			//Use one of these strings: CPUUtilization, NetworkIn, NetworkOut, DiskReadBytes, DiskWriteBytes, DiskReadOperations  
			statRequest.setMetricName("CPUUtilization"); 
			// set time
			GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
			calendar.add(GregorianCalendar.SECOND, -1 * calendar.get(GregorianCalendar.SECOND)); // 1 second ago
			Date endTime = calendar.getTime();
			calendar.add(GregorianCalendar.MINUTE, -1); // 1 minutes ago
			Date startTime = calendar.getTime();
			statRequest.setStartTime(startTime);
			statRequest.setEndTime(endTime);

			//specify an instance
			ArrayList<Dimension> dimensions = new ArrayList<Dimension>();
			dimensions.add(new Dimension().withName("InstanceId").withValue(instanceId));
			statRequest.setDimensions(dimensions);

			//get statistics
			GetMetricStatisticsResult statResult = cloudWatch.getMetricStatistics(statRequest);

			//display
			//System.out.println(statResult.toString());
			List<Datapoint> dataList = statResult.getDatapoints();
			Double averageCPU = null;
			Date timeStamp = null;
			for (Datapoint data : dataList){
				averageCPU = data.getAverage();
				timeStamp = data.getTimestamp();
			}
			if (averageCPU == null){
				return 0;
			}else{ 
				return averageCPU;
			}
		}catch (AmazonServiceException ase) {
			System.out.println("Caught Exception: " + ase.getMessage());
			System.out.println("Reponse Status Code: " + ase.getStatusCode());
			System.out.println("Error Code: " + ase.getErrorCode());
			System.out.println("Request ID: " + ase.getRequestId());
			return 0;
		}
	}
    
    private static void createKey(String keyName, AmazonEC2 ec2){
		try {
			List<KeyPairInfo> keyPairList = ec2.describeKeyPairs().getKeyPairs();
			for (KeyPairInfo keyPair : keyPairList){
				if ( keyName.equalsIgnoreCase(keyPair.getKeyName()) ){
					System.out.println("Using key " + keyName);
					return;
				}
			}
			System.out.println("Creating key " + keyName + "in local directory");
			CreateKeyPairRequest newKeyRequest = new CreateKeyPairRequest();
			newKeyRequest.setKeyName(keyName);
			CreateKeyPairResult keyresult = ec2.createKeyPair(newKeyRequest);
			KeyPair keyPair = new KeyPair();
			keyPair = keyresult.getKeyPair();
			String privateKey = keyPair.getKeyMaterial();
			writeKeytoFile(keyName, privateKey);

		} catch (AmazonServiceException ase) {
			System.out.println("Caught Exception: " + ase.getMessage());
			System.out.println("Reponse Status Code: " + ase.getStatusCode());
			System.out.println("Error Code: " + ase.getErrorCode());
			System.out.println("Request ID: " + ase.getRequestId());
		}
	}
    
    private static void writeKeytoFile(String keyName, String privateKey) {
		FileWriter fileWriter = null;
        try {
            File keyFile = new File(keyName + ".pem");
            System.out.println("The new created Key File is written to" + System.getProperty("user.dir")); 
            keyPath = System.getProperty("user.dir"); 
            fileWriter = new FileWriter(keyFile);
            fileWriter.write(privateKey);
            fileWriter.close();
    	} catch (IOException e) {
			e.printStackTrace();
		} 
    }
    
        public static void createSecurityGroup(AmazonEC2 ec2, String securityGroup){
    		List <SecurityGroup> secGroupList = ec2.describeSecurityGroups().getSecurityGroups();
    		for (SecurityGroup secGroup : secGroupList){
    			if ( securityGroup.equalsIgnoreCase(secGroup.getGroupName()) ){
    				System.out.println("Using Security Group " + securityGroup);
    				return;
    			}
    		}

    		CreateSecurityGroupRequest createSecurityGroupRequest = new CreateSecurityGroupRequest();

    		createSecurityGroupRequest.withGroupName(securityGroup)
    		.withDescription("Assign1");

    		CreateSecurityGroupResult createSecurityGroupResult = ec2.createSecurityGroup(createSecurityGroupRequest);
    		
    		IpPermission ipPermission1 = new IpPermission();
    		ipPermission1.withIpRanges("0.0.0.0/0").withIpProtocol("tcp").withFromPort(22).withToPort(22);
    		
    		IpPermission ipPermission2 = new IpPermission();
    		ipPermission2.withIpRanges("0.0.0.0/0").withIpProtocol("tcp").withFromPort(80).withToPort(80);
    		
    		IpPermission ipPermission3 = new IpPermission();
    		ipPermission3.withIpRanges("0.0.0.0/0").withIpProtocol("tcp").withFromPort(0).withToPort(65535);

    		List<IpPermission> permissions = new ArrayList<IpPermission>();
    		permissions.add(ipPermission1);
    		permissions.add(ipPermission2);
    		permissions.add(ipPermission3);

    		AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest =
    				new AuthorizeSecurityGroupIngressRequest();

    		authorizeSecurityGroupIngressRequest.withGroupName(securityGroup)
    		.withIpPermissions(permissions);

    		ec2.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);

    		System.out.println("A new Security Group is created " + securityGroup);
    	}   
        
        public static void createBucket(AmazonS3Client s3, String bucketName, String zone){
    		try{

    			List <Bucket> bucketList = s3.listBuckets();
    			for (Bucket bucket : bucketList){
    				if ( bucketName.equalsIgnoreCase(bucket.getName() )){
    					System.out.println("Using bucket " + bucketName );
    					return;
    				}
    			}
    			
    			s3.createBucket(bucketName);
    			System.out.println("A new S3 bucket is created:  " + bucketName );
    			
    			return;	

    		}catch (AmazonServiceException ase) {
    			System.out.println("Caught Exception: " + ase.getMessage());
    			System.out.println("Reponse Status Code: " + ase.getStatusCode());
    			System.out.println("Error Code: " + ase.getErrorCode());
    			System.out.println("Request ID: " + ase.getRequestId());
    		}
    	}
        
        public static void createS3File(AmazonS3Client s3, String bucketName) throws IOException {
            //set key
            String key = "object-name.txt";
            
            //set value
            File file = File.createTempFile("temp", ".txt");
            //file.deleteOnExit();
            Writer writer = new OutputStreamWriter(new FileOutputStream(file));
            writer.write("This is a sample sentence for S3.");
            writer.close();
            
            //put object - bucket, key, value(file)
            s3.putObject(new PutObjectRequest(bucketName, key, file));
            System.out.println("Wrote object-name.txt to S3 bucket");
    	}
        
        private static void createAndStartUpVM(AWS_instance machine, String bucketName) throws Exception {
    		System.out.println("Creating machine...");
    		machine.createInstance();			

    		//Sleep before starting up
    		sleep(10);

    		machine.startUpAWS_instance();
    		machine.attachEBS();
    		System.out.println("The created volume has been attached to  " + machine.volumeId);
    		machine.attachS3(bucketName);
    		System.out.println("The S3 has been attached to the machine");
    		System.out.println("The new machine has been created");
    	}
        
        public static void sleep(int seconds) {
    		try {
    			Thread.sleep(seconds*1000);
    		} catch (InterruptedException e) {
    			System.out.println("Caught Exception: " + e.getMessage());
    		}
    	}
        
        
      //Terminates a machine and takes a snapshot
    	private static void terminateVM(AWS_instance machine) {
    		if (machine.getIsTerminated(true))
    			return;

    		//Try to shut down the machine			
    		machine.detachEBS();
    		System.out.println("The attched volume has been detached");		
    
    		machine.saveSnapShot();		
    		System.out.println("Now, we are taking a snapshot for the machine");
    		//wait for the snapshot to be created and then shutdown the machine
    		do {
    			sleep(10);
    		} while(!machine.getSnapShotState().equalsIgnoreCase("available"));
    		System.out.println("A snapshot has been taken and an AMI has been created.");

    		machine.shutDownAWS_instance();
    		System.out.println("The machine has been shut down");
    	}

        
        
        
        
        
        
        
        
        
        
        
        
        
        /************************************************************************************
         *  The sincreaseCPU method and stopCPU method use SSH API, which fail occasionally,
         *  we recommend using command line to SSH into the machine
         
        ***********************************************************************************/
        static void increaseCPU(AWS_instance pc, String keyName) throws InterruptedException{

    		File keyfile = new File(keyName + ".pem"); 
    		String keyfilePass = "none"; 

    		try
    		{
    			Connection conn = new Connection(pc.ipAddress);
    			conn.connect();

    			boolean isAuthenticated = conn.authenticateWithPublicKey("ec2-user", keyfile, keyfilePass);
    			if (isAuthenticated == false)
    				throw new IOException("Authentication failed.");

    			Session sess = conn.openSession();
    			sess.execCommand("while true; do usleep 100000; done");
    			sess.close();
    			conn.close();
    			System.out.println("The CPU of " + pc.machineName+"has been increased");
    		}
    		catch (IOException e)
    		{
    			e.printStackTrace(System.err);
    			System.out.println("Please use command line to increase CPU");
    		}
    	}
        
        /************************************************************************************
         *  The sincreaseCPU method and stopCPU method use SSH API, which fail occasionally,
         *  we recommend using command line to SSH into the machine
         
        ***********************************************************************************/
        static void stopCPU(AWS_instance pc, String keyName) throws InterruptedException{
    		File keyfile = new File(keyName + ".pem"); 
    		String keyfilePass = "none"; 

    		try
    		{
    			Connection conn = new Connection(pc.ipAddress);
    			conn.connect();

    			boolean isAuthenticated = conn.authenticateWithPublicKey("ec2-user", keyfile, keyfilePass);
    			if (isAuthenticated == false)
    				throw new IOException("Authentication failed.");


    			Session sess = conn.openSession();
    			sess.execCommand("killall bash");
    			sess.close();
    			conn.close();

    			System.out.println("Stopped CPU on " + pc.machineName);
    		}
    		catch (IOException e)
    		{
    			e.printStackTrace(System.err);
    			System.out.println("Please use the attached script to start and stop cpu remotely");
    		}
    	}
        
}







