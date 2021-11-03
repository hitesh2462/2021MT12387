# SSZG526 Assignment (Distributed Computing) 
# Write a program to implement the Ricart-Agrawala algorithm for implementing distributed mutual exclusion. Assume the communication channels to be FIFO in nature.  

***

# Platform Used:
	Linux Ubuntu, openjdk (support for java compiler and runtime)

# Language Used:
	Java

# Other Packages:
	java.util, java.io, java.net (for TCP socket communication), java.sql.Timestamp (for timestamp) 

# Caution
	Both the program and configuration file must be kept in the same directory for the code to run without any errors


# Overview of the Algorithm (N Sites in the Distributed System):
	1. Two type of messages (REQUEST and REPLY) are used.
	2. A site send (N-1) REQUEST messages to all other sites to get their permission to enter critical section.
	3. A site send a REPLY message to other site to give its permission to enter the critical section.
	4. A timestamp is given to each critical section request using Lamport’s logical clock.
	   Timestamp is used to determine priority of critical section requests. Smaller timestamp gets high priority over larger timestamp. 
	
# Algorithm:
	To enter Critical section:
	 	1. When a site Si wants to enter the critical section, it broadcasts a timestamped REQUEST message to all the other sites.
	 	2. When a site Sj receives a REQUEST message from site Si, It sends a REPLY message to site Si if and only if
		 	* Site Sj is neither requesting nor currently executing the critical section.
		 	* In case Site Sj is requesting, the timestamp of Site Si‘s request is smaller than its own request. Otherwise the request is deferred Sj sets RDj[i] = 1.
	To execute the critical section:
	 	1. Site Si enters the critical section if it has received the REPLY message from all the other sites whom it sent REQUEST.
	To release the critical section:
        	1. Upon exiting site Si sends all the deferred REPLY message(s) and reset the deferred request(s) i.e. RDi[j] = 0.
    	
	Message Complexity:
 		1. (N – 1) request messages
 		2. (N – 1) reply messages
 		3. 2(N – 1) messages per critical section. 

# Assumptions:
	1. The Sites do not fail.
	2. The number of Sites are known upfront.
 	3. The important assumption made here is the faultless Network communication.
	4. Communication channels are assumed to follow FIFO order.

# Implementation Details:
	1. There are N Sites in the system, numbered from 0 to N-1. The Sites communicate with each other using TCP sockets.
	2. Each Site is designed with a Server and Client components. On start up, the Site starts listening using its Server component to receive messages from other Sites. 
 	   In order to receive messages in concurrent manner, Server components are implemented in N-1 threads.
	3. When a Site wants to enter Critical Section it uses its Client component to connect to each of the other Sites and send the REQUEST messages. 
 	   Once the REPLYs are received from all the other Sites, it enters the Critical Section. On exiting from the Critical Section the deferred requests, if any are processed.

	For demonstration each Site requests for entering the Critical Section for a predefined number. Once a Site enters the Critical Section it exits from there after a defined time duration.

# Compiling and Running the program with the user inputs:
	1. Connect to the Linux VM (ubuntu in this case) and switch to "~/home/vmadmin/2021MT12387" (this is where program files are present)
		vmadmin@linux-vm: ~/home/vmadmin/2021MT12387
	
	2. Validate that the `config.txt` is present in the present directory

	3. Define the number of Sites as N in the config file

	4. Define site id (starting from 0) and port number (e.g. 5001) for each site linewise in the `config.txt` file.
	 	3
  		0 5001
 		1 6001
 		2 7001
 		
	5. Compile and Run the program by executing javac RA.java on the Linux VM
		vmadmin@linux-vm:~/2021MT12387$ javac RA.java

	6. Set up sites in 3 different terminals
	   
		---------------
		java RA 0 3
		---------------
		java RA 1 3
		---------------
		java RA 2 3
		---------------
		
# Evaluation of the output: 
	1. Observe REQUEST and REPLY messages based on the algorithm's condition to enter-exit CS. 
	2. Observe requests getting deferred when the receiving site in CS or itself requesting with lesser timestamp or same timestamp but the id is less than 		   the requesting site id.
	3. Observe requests deferred array (RD<site_id>) of each site whenver a request is replied or deferred.
	4. Observe the respective site entering-exiting CS along with the timestamps.
	5. Observe how many times a respective site entered CS.

# Sample output:

## Site 0:
vmadmin@linux-vm:~/2021MT12387$ java RA 0 3
Max number of Sites in the current setup: 3
RD0 = [0, 0, 0]
Server Listening @6001
Server Listening @7001
Connection Accepted @6001
Connection Accepted @7001
Sent REQUEST(1, 0) to Site: 1
Sent REQUEST(1, 0) to Site: 2
Received reply from Site: 1
Received REQUEST(2, 1) from Site: 1
Request from Site: 1 is deferred. Request ts: 2
RD0 = [0, 1, 0]
Received reply from Site: 2
Site: 0 entered Critical Section @ 2021-11-03 06:22:35.094
Received REQUEST(3, 2) from Site: 2
Request from Site: 2 is deferred. Request ts: 3
RD0 = [0, 1, 1]
Site: 0 exited Critical Section @ 2021-11-03 06:22:37.105
RD0 = [0, 1, 1]
Sent reply to Site: 1
Sent reply to Site: 2
RD0 = [0, 0, 0]
Sent REQUEST(4, 0) to Site: 1
Sent REQUEST(4, 0) to Site: 2
Received reply from Site: 1
Received reply from Site: 2
Site: 0 entered Critical Section @ 2021-11-03 06:22:50.157
Site: 0 exited Critical Section @ 2021-11-03 06:22:52.157
RD0 = [0, 0, 0]
RD0 = [0, 0, 0]
Received REQUEST(5, 1) from Site: 1
Sent reply to Site: 1
Received REQUEST(6, 2) from Site: 2
Sent reply to Site: 2
Sent REQUEST(7, 0) to Site: 1
Sent REQUEST(7, 0) to Site: 2
Received reply from Site: 1
Received reply from Site: 2
Site: 0 entered Critical Section @ 2021-11-03 06:23:10.23
Site: 0 exited Critical Section @ 2021-11-03 06:23:12.23
RD0 = [0, 0, 0]
RD0 = [0, 0, 0]
Received REQUEST(8, 1) from Site: 1
Sent reply to Site: 1
Received REQUEST(9, 2) from Site: 2
Sent reply to Site: 2
Site: 0 entered CS 3 times
Site: 0 completed execution

## Site 1:
vmadmin@linux-vm:~/2021MT12387$ java RA 1 3
Max number of Sites in the current setup: 3
RD1 = [0, 0, 0]
Server Listening @5002
Server Listening @7002
Connection Accepted @5002
Connection Accepted @7002
Received REQUEST(1, 0) from Site: 0
Sent reply to Site: 0
Sent REQUEST(2, 1) to Site: 0
Sent REQUEST(2, 1) to Site: 2
Received reply from Site: 2
Received REQUEST(3, 2) from Site: 2
Request from Site: 2 is deferred. Request ts: 3
RD1 = [0, 0, 1]
Received reply from Site: 0
Site: 1 entered Critical Section @ 2021-11-03 06:22:38.8
Site: 1 exited Critical Section @ 2021-11-03 06:22:40.807
RD1 = [0, 0, 1]
Sent reply to Site: 2
RD1 = [0, 0, 0]
Received REQUEST(4, 0) from Site: 0
Sent reply to Site: 0
Sent REQUEST(5, 1) to Site: 0
Sent REQUEST(5, 1) to Site: 2
Received reply from Site: 2
Received reply from Site: 0
Site: 1 entered Critical Section @ 2021-11-03 06:22:58.867
Received REQUEST(6, 2) from Site: 2
Request from Site: 2 is deferred. Request ts: 6
RD1 = [0, 0, 1]
Site: 1 exited Critical Section @ 2021-11-03 06:23:00.867
RD1 = [0, 0, 1]
Sent reply to Site: 2
RD1 = [0, 0, 0]
Received REQUEST(7, 0) from Site: 0
Sent reply to Site: 0
Sent REQUEST(8, 1) to Site: 0
Sent REQUEST(8, 1) to Site: 2
Received reply from Site: 2
Received reply from Site: 0
Site: 1 entered Critical Section @ 2021-11-03 06:23:18.938
Received REQUEST(9, 2) from Site: 2
Request from Site: 2 is deferred. Request ts: 9
RD1 = [0, 0, 1]
Site: 1 exited Critical Section @ 2021-11-03 06:23:20.938
RD1 = [0, 0, 1]
Sent reply to Site: 2
RD1 = [0, 0, 0]
Site: 1 entered CS 3 times
Site: 1 completed execution

## Site 2:
vmadmin@linux-vm:~/2021MT12387$ java RA 2 3
Max number of Sites in the current setup: 3
RD2 = [0, 0, 0]
Server Listening @5003
Server Listening @6003
Connection Accepted @5003
Connection Accepted @6003
Received REQUEST(1, 0) from Site: 0
Sent reply to Site: 0
Received REQUEST(2, 1) from Site: 1
Sent reply to Site: 1
Sent REQUEST(3, 2) to Site: 0
Sent REQUEST(3, 2) to Site: 1
Received reply from Site: 0
Received reply from Site: 1
Site: 2 entered Critical Section @ 2021-11-03 06:22:44.439
Site: 2 exited Critical Section @ 2021-11-03 06:22:46.447
RD2 = [0, 0, 0]
RD2 = [0, 0, 0]
Received REQUEST(4, 0) from Site: 0
Sent reply to Site: 0
Received REQUEST(5, 1) from Site: 1
Sent reply to Site: 1
Sent REQUEST(6, 2) to Site: 0
Sent REQUEST(6, 2) to Site: 1
Received reply from Site: 0
Received REQUEST(7, 0) from Site: 0
Request from Site: 0 is deferred. Request ts: 7
RD2 = [1, 0, 0]
Received reply from Site: 1
Site: 2 entered Critical Section @ 2021-11-03 06:23:04.508
Site: 2 exited Critical Section @ 2021-11-03 06:23:06.508
RD2 = [1, 0, 0]
Sent reply to Site: 0
RD2 = [0, 0, 0]
Received REQUEST(8, 1) from Site: 1
Sent reply to Site: 1
Sent REQUEST(9, 2) to Site: 0
Sent REQUEST(9, 2) to Site: 1
Received reply from Site: 0
Received reply from Site: 1
Site: 2 entered Critical Section @ 2021-11-03 06:23:24.579
Site: 2 exited Critical Section @ 2021-11-03 06:23:26.579
RD2 = [0, 0, 0]
RD2 = [0, 0, 0]
Site: 2 entered CS 3 times
Site: 2 completed execution

#
Hitesh Kumar - 2021MT12387
Repo - https://github.com/hitesh2462/2021MT12387
*******
