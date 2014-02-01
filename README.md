On-Demand-Elastic-IT-Infrastructure
===================================

Here is the scenario: You are the IT administrator for a large Enterprise. You have transformed your company's IT into a virtual infrastructure that you provision, manage and even provide the backup support. You are also making sure you minimize the cost of this virtual infrastructure that you are providing for your business. You release resources when someone is not using it, but making sure that resources are available (mostly) when needed based on some policy, schedule or active monitoring. In addition, you are providing Elastic infrastructure to set of users who require elasticity support.
<br>
Part 1: On-demand Infrastructure:

1. Design an on-demand provision and release mechanism as the base service. You shall need ability to snapshot a VM and restore it later on from this snapshot.

2. You need to persist data on a persistent storage.

3. You shall need the ability to detect when machines are idle. This you should do using CloudWatch service.

4. You need to provide some form of static IP to make sure login remains the same.

<br>
Part 2: Elastic Service: You may use AWS Auto-scale/ELB or develop your own Elasticity capability. We shall discuss this in the next class.

AWS Auto-scale/ELB: You shall use Autoscale to support Elastic infrastructure for the set of users that need such an support. You can download Autoscale SDK and libraries to configure such a service. It does not need to be integrated with Java code. It could be command line based that you shall set up separately. Please watch the youtube video link I had provided as part of Elasticity lecture.

Your own Elastic code: You may develop your own elasticity code. You could use cloudwatch to perform the monitoring.

<br>

What you need to show:

1. Assume that your company has 2 employees. You create two EC2 VMs using any base OS. The data storage for each VMs should be from persistent storage. So allocate persistent storage once VMs created. You need to demonstrate persistent storage both with EBS and S3

2. Monitor each VM. If the VM is no used (CPU Util less than some threshold or after 5:00pm), snapshot each VM to create the new AMI. Dettach each data volume before snapshot. For this task, you need to create some workload such that CPU is busy for sometime and then later on comes to idle state. Also you need to use CloudWatch APIs to obtain monitoring information. You could choose other means to obtain monitoring information as well.

3. Next morning when your employees are back, create VMs from stored AMIs and also restore the data partitions.


Elastic support

4. For one user, set up wth Autoscale such that when the CPU load increases beyond a limit, it automatically provisions two VMs for that user. You need to show in the AWS dashboard that two VMs are running when CPU load is high. You may additionally show that CPU load drops below a certain level and you may need to shut down the machine for that day.
