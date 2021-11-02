# SSZG526 DISTRIBUTED COMPUTING ASSIGNMENT (EC-1) 
# Write a program to implement the Ricart-Agrawala algorithm for implementing distributed mutual exclusion. Assume the communication channels to be FIFO in nature.  

***

# Platform Used:
	Linux Ubuntu, openjdk (support for java compiler and runtime)

# Language Used:
	Java

# Other Packages:
	java.util, java.io, java.net (for TCP socket communication), java.sql.Timestamp (for timestamp) 


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
	1. Open a linux terminal and navigate to the project location wherever its placed in the linux system.
	   e.g. user@localhost:~$ cd /home/user/DME/Ricart-Agrawala-DME/
	
	2. Ensure `config.txt` is present in the location navigated in the step 1.
	3. Define the number of Sites `N` on the first line of `config.txt` file.
	4. Define site id (starting from 0) and port number (e.g. 5001) for each site linewise in the `config.txt` file.
	   e.g. 4
  		0 5001
 		1 6001
 		2 7001
 		3 8001

	5. Compile the program by typing `javac RA.java` in the opened terminal.
	   e.g. user@localhost:~/DME/Ricart-Agrawala-DME$ javac RA.java 

	6. Set up sites in `N` different terminals by typing `java RA <site id> <number_of_times_to_enter_cs>` in each.
	   e.g.
		***
		java RA 0 4
		***
		***
		java RA 1 4
		***
		***
		java RA 2 4
		***
		***
		java RA 3 4
		***

# Evaluation of the output: 
	1. Observe the terminal/console prints showing REQUEST and REPLY messages based on the algorithm's condition to enter-exit CS. 
	2. Observe the terminal/console prints showing request getting deferred when the receiving site in CS or itself requesting with lesser timestamp or same timestamp but the id is less than 		   the requesting site id.
	3. Observe the terminal/console prints for request deferred array (RD<site_id>) of each site whenver a request is replied or deferred.
	4. Observe the terminal/console prints for the respective site entering-exiting CS along with the timestamps.
	5. Observe the terminal/console prints showing how many times a respective site entered CS.

# Sample output:

## Site 0:
user@localhost:~/DME/Ricart-Agrawala-DME$ java RA 0 4
Max number of Sites in the current setup: 4
RD0 = [0, 0, 0, 0]
Server Listening @6001
Server Listening @7001
Server Listening @8001
Connection Accepted @7001
Connection Accepted @6001
Connection Accepted @8001
Sent REQUEST(1, 0) to Site: 1
Sent REQUEST(1, 0) to Site: 2
Sent REQUEST(1, 0) to Site: 3
Received REQUEST(1, 2) from Site: 2
Request from Site: 2 is deferred. Request ts: 1
RD0 = [0, 0, 1, 0]
Received REQUEST(1, 1) from Site: 1
Request from Site: 1 is deferred. Request ts: 1
RD0 = [0, 1, 1, 0]
Received reply from Site: 1
Received REQUEST(1, 3) from Site: 3
Request from Site: 3 is deferred. Request ts: 1
RD0 = [0, 1, 1, 1]
Received reply from Site: 2
Received reply from Site: 3
Site: 0 entered Critical Section @ 2021-10-23 21:56:39.026
Site: 0 exited Critical Section @ 2021-10-23 21:56:41.067
RD0 = [0, 1, 1, 1]
Sent reply to Site: 1
Sent reply to Site: 2
Sent reply to Site: 3
RD0 = [0, 0, 0, 0]
Sent REQUEST(2, 0) to Site: 1
Sent REQUEST(2, 0) to Site: 2
Sent REQUEST(2, 0) to Site: 3
Received reply from Site: 2
Received reply from Site: 1
Received REQUEST(3, 1) from Site: 1
Request from Site: 1 is deferred. Request ts: 3
RD0 = [0, 1, 0, 0]
Received reply from Site: 3
Site: 0 entered Critical Section @ 2021-10-23 21:56:59.104
Site: 0 exited Critical Section @ 2021-10-23 21:57:01.109
RD0 = [0, 1, 0, 0]
Sent reply to Site: 1
RD0 = [0, 0, 0, 0]
Received REQUEST(4, 2) from Site: 2
Sent reply to Site: 2
Received REQUEST(5, 3) from Site: 3
Sent reply to Site: 3
Sent REQUEST(6, 0) to Site: 1
Sent REQUEST(6, 0) to Site: 2
Sent REQUEST(6, 0) to Site: 3
Received reply from Site: 2
Received reply from Site: 1
Received REQUEST(7, 1) from Site: 1
Request from Site: 1 is deferred. Request ts: 7
RD0 = [0, 1, 0, 0]
Received reply from Site: 3
Site: 0 entered Critical Section @ 2021-10-23 21:57:19.179
Site: 0 exited Critical Section @ 2021-10-23 21:57:21.179
RD0 = [0, 1, 0, 0]
Sent reply to Site: 1
RD0 = [0, 0, 0, 0]
Received REQUEST(8, 2) from Site: 2
Sent reply to Site: 2
Received REQUEST(9, 3) from Site: 3
Sent reply to Site: 3
Sent REQUEST(10, 0) to Site: 1
Sent REQUEST(10, 0) to Site: 2
Sent REQUEST(10, 0) to Site: 3
Received reply from Site: 2
Received reply from Site: 1
Received REQUEST(11, 1) from Site: 1
Request from Site: 1 is deferred. Request ts: 11
RD0 = [0, 1, 0, 0]
Received reply from Site: 3
Site: 0 entered Critical Section @ 2021-10-23 21:57:39.253
Site: 0 exited Critical Section @ 2021-10-23 21:57:41.261
RD0 = [0, 1, 0, 0]
Sent reply to Site: 1
RD0 = [0, 0, 0, 0]
Received REQUEST(12, 2) from Site: 2
Sent reply to Site: 2
Received REQUEST(13, 3) from Site: 3
Sent reply to Site: 3
Site: 0 entered CS 4 times
Site: 0 completed execution

## Site 1:
user@localhost:~/DME/Ricart-Agrawala-DME$ java RA 1 4
Max number of Sites in the current setup: 4
RD1 = [0, 0, 0, 0]
Server Listening @5002
Server Listening @7002
Server Listening @8002
Connection Accepted @7002
Connection Accepted @8002
Connection Accepted @5002
Sent REQUEST(1, 1) to Site: 0
Sent REQUEST(1, 1) to Site: 2
Sent REQUEST(1, 1) to Site: 3
Received REQUEST(1, 0) from Site: 0
Sent reply to Site: 0
Received REQUEST(1, 2) from Site: 2
Request from Site: 2 is deferred. Request ts: 1
RD1 = [0, 0, 1, 0]
Received REQUEST(1, 3) from Site: 3
Request from Site: 3 is deferred. Request ts: 1
RD1 = [0, 0, 1, 1]
Received reply from Site: 3
Received reply from Site: 2
Received reply from Site: 0
Site: 1 entered Critical Section @ 2021-10-23 21:56:45.342
Site: 1 exited Critical Section @ 2021-10-23 21:56:47.364
RD1 = [0, 0, 1, 1]
Sent reply to Site: 2
Sent reply to Site: 3
RD1 = [0, 0, 0, 0]
Received REQUEST(2, 0) from Site: 0
Sent reply to Site: 0
Sent REQUEST(3, 1) to Site: 0
Sent REQUEST(3, 1) to Site: 2
Sent REQUEST(3, 1) to Site: 3
Received reply from Site: 3
Received reply from Site: 2
Received REQUEST(4, 2) from Site: 2
Request from Site: 2 is deferred. Request ts: 4
RD1 = [0, 0, 1, 0]
Received reply from Site: 0
Site: 1 entered Critical Section @ 2021-10-23 21:57:05.415
Site: 1 exited Critical Section @ 2021-10-23 21:57:07.415
RD1 = [0, 0, 1, 0]
Sent reply to Site: 2
RD1 = [0, 0, 0, 0]
Received REQUEST(5, 3) from Site: 3
Sent reply to Site: 3
Received REQUEST(6, 0) from Site: 0
Sent reply to Site: 0
Sent REQUEST(7, 1) to Site: 0
Sent REQUEST(7, 1) to Site: 2
Sent REQUEST(7, 1) to Site: 3
Received reply from Site: 3
Received reply from Site: 2
Received REQUEST(8, 2) from Site: 2
Request from Site: 2 is deferred. Request ts: 8
RD1 = [0, 0, 1, 0]
Received reply from Site: 0
Site: 1 entered Critical Section @ 2021-10-23 21:57:25.507
Site: 1 exited Critical Section @ 2021-10-23 21:57:27.536
RD1 = [0, 0, 1, 0]
Sent reply to Site: 2
RD1 = [0, 0, 0, 0]
Received REQUEST(9, 3) from Site: 3
Sent reply to Site: 3
Received REQUEST(10, 0) from Site: 0
Sent reply to Site: 0
Sent REQUEST(11, 1) to Site: 0
Sent REQUEST(11, 1) to Site: 2
Sent REQUEST(11, 1) to Site: 3
Received reply from Site: 3
Received reply from Site: 2
Received REQUEST(12, 2) from Site: 2
Request from Site: 2 is deferred. Request ts: 12
RD1 = [0, 0, 1, 0]
Received reply from Site: 0
Site: 1 entered Critical Section @ 2021-10-23 21:57:45.659
Site: 1 exited Critical Section @ 2021-10-23 21:57:47.66
RD1 = [0, 0, 1, 0]
Sent reply to Site: 2
RD1 = [0, 0, 0, 0]
Received REQUEST(13, 3) from Site: 3
Sent reply to Site: 3
Site: 1 entered CS 4 times
Site: 1 completed execution

## Site 2:
user@localhost:~/DME/Ricart-Agrawala-DME$ java RA 2 4
Max number of Sites in the current setup: 4
RD2 = [0, 0, 0, 0]
Server Listening @5003
Server Listening @6003
Server Listening @8003
Connection Accepted @6003
Connection Accepted @8003
Connection Accepted @5003
Sent REQUEST(1, 2) to Site: 0
Sent REQUEST(1, 2) to Site: 1
Sent REQUEST(1, 2) to Site: 3
Received REQUEST(1, 1) from Site: 1
Sent reply to Site: 1
Received REQUEST(1, 3) from Site: 3
Request from Site: 3 is deferred. Request ts: 1
RD2 = [0, 0, 0, 1]
Received reply from Site: 3
Received REQUEST(1, 0) from Site: 0
Sent reply to Site: 0
Received reply from Site: 0
Received reply from Site: 1
Site: 2 entered Critical Section @ 2021-10-23 21:56:48.096
Site: 2 exited Critical Section @ 2021-10-23 21:56:50.123
RD2 = [0, 0, 0, 1]
Sent reply to Site: 3
RD2 = [0, 0, 0, 0]
Received REQUEST(2, 0) from Site: 0
Sent reply to Site: 0
Received REQUEST(3, 1) from Site: 1
Sent reply to Site: 1
Sent REQUEST(4, 2) to Site: 0
Sent REQUEST(4, 2) to Site: 1
Sent REQUEST(4, 2) to Site: 3
Received reply from Site: 3
Received reply from Site: 0
Received reply from Site: 1
Site: 2 entered Critical Section @ 2021-10-23 21:57:08.187
Received REQUEST(5, 3) from Site: 3
Request from Site: 3 is deferred. Request ts: 5
RD2 = [0, 0, 0, 1]
Site: 2 exited Critical Section @ 2021-10-23 21:57:10.188
RD2 = [0, 0, 0, 1]
Sent reply to Site: 3
RD2 = [0, 0, 0, 0]
Received REQUEST(6, 0) from Site: 0
Sent reply to Site: 0
Received REQUEST(7, 1) from Site: 1
Sent reply to Site: 1
Sent REQUEST(8, 2) to Site: 0
Sent REQUEST(8, 2) to Site: 1
Sent REQUEST(8, 2) to Site: 3
Received reply from Site: 3
Received reply from Site: 0
Received reply from Site: 1
Site: 2 entered Critical Section @ 2021-10-23 21:57:28.274
Received REQUEST(9, 3) from Site: 3
Request from Site: 3 is deferred. Request ts: 9
RD2 = [0, 0, 0, 1]
Site: 2 exited Critical Section @ 2021-10-23 21:57:30.275
RD2 = [0, 0, 0, 1]
Sent reply to Site: 3
RD2 = [0, 0, 0, 0]
Received REQUEST(10, 0) from Site: 0
Sent reply to Site: 0
Received REQUEST(11, 1) from Site: 1
Sent reply to Site: 1
Sent REQUEST(12, 2) to Site: 0
Sent REQUEST(12, 2) to Site: 1
Sent REQUEST(12, 2) to Site: 3
Received reply from Site: 3
Received reply from Site: 0
Received reply from Site: 1
Site: 2 entered Critical Section @ 2021-10-23 21:57:48.359
Received REQUEST(13, 3) from Site: 3
Request from Site: 3 is deferred. Request ts: 13
RD2 = [0, 0, 0, 1]
Site: 2 exited Critical Section @ 2021-10-23 21:57:50.36
RD2 = [0, 0, 0, 1]
Sent reply to Site: 3
RD2 = [0, 0, 0, 0]
Site: 2 entered CS 4 times
Site: 2 completed execution

## Site 3:
user@localhost:~/DME/Ricart-Agrawala-DME$ java RA 3 4
Max number of Sites in the current setup: 4
RD3 = [0, 0, 0, 0]
Server Listening @5004
Server Listening @6004
Server Listening @7004
Connection Accepted @7004
Connection Accepted @6004
Connection Accepted @5004
Sent REQUEST(1, 3) to Site: 0
Sent REQUEST(1, 3) to Site: 1
Sent REQUEST(1, 3) to Site: 2
Received REQUEST(1, 2) from Site: 2
Sent reply to Site: 2
Received REQUEST(1, 1) from Site: 1
Sent reply to Site: 1
Received REQUEST(1, 0) from Site: 0
Sent reply to Site: 0
Received reply from Site: 0
Received reply from Site: 1
Received reply from Site: 2
Site: 3 entered Critical Section @ 2021-10-23 21:56:52.29
Site: 3 exited Critical Section @ 2021-10-23 21:56:54.316
RD3 = [0, 0, 0, 0]
RD3 = [0, 0, 0, 0]
Received REQUEST(2, 0) from Site: 0
Sent reply to Site: 0
Received REQUEST(3, 1) from Site: 1
Sent reply to Site: 1
Received REQUEST(4, 2) from Site: 2
Sent reply to Site: 2
Sent REQUEST(5, 3) to Site: 0
Sent REQUEST(5, 3) to Site: 1
Sent REQUEST(5, 3) to Site: 2
Received reply from Site: 0
Received reply from Site: 2
Received reply from Site: 1
Site: 3 entered Critical Section @ 2021-10-23 21:57:13.128
Site: 3 exited Critical Section @ 2021-10-23 21:57:15.129
RD3 = [0, 0, 0, 0]
RD3 = [0, 0, 0, 0]
Received REQUEST(6, 0) from Site: 0
Sent reply to Site: 0
Received REQUEST(7, 1) from Site: 1
Sent reply to Site: 1
Received REQUEST(8, 2) from Site: 2
Sent reply to Site: 2
Sent REQUEST(9, 3) to Site: 0
Sent REQUEST(9, 3) to Site: 1
Sent REQUEST(9, 3) to Site: 2
Received reply from Site: 0
Received reply from Site: 2
Received reply from Site: 1
Site: 3 entered Critical Section @ 2021-10-23 21:57:33.208
Site: 3 exited Critical Section @ 2021-10-23 21:57:35.209
RD3 = [0, 0, 0, 0]
RD3 = [0, 0, 0, 0]
Received REQUEST(10, 0) from Site: 0
Sent reply to Site: 0
Received REQUEST(11, 1) from Site: 1
Sent reply to Site: 1
Received REQUEST(12, 2) from Site: 2
Sent reply to Site: 2
Sent REQUEST(13, 3) to Site: 0
Sent REQUEST(13, 3) to Site: 1
Sent REQUEST(13, 3) to Site: 2
Received reply from Site: 0
Received reply from Site: 2
Received reply from Site: 1
Site: 3 entered Critical Section @ 2021-10-23 21:57:53.284
Site: 3 exited Critical Section @ 2021-10-23 21:57:55.284
RD3 = [0, 0, 0, 0]
RD3 = [0, 0, 0, 0]
Site: 3 entered CS 4 times
Site: 3 completed execution

#######
By Hitesh Kumar - 2021MT12387
*******
