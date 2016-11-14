



# SoftGrid Service Architecture  
![](https://github.com/smartgridadsc/smartpower/blob/master/API/Images/testbed%20architecture.png)

SoftGrid Service Contains three main components.  

01. Web Service with the SoftGrid testbed. The testbed includes a Substation and a Control Center.   
NOTE : Please refer to the next section for more details of the SoftGrid Testbed Architecture.  

02. Security Solution or Gateway program/device manually connected by the user. There is a sample gateway program available with the SoftGrid, which can run even on a RPi.  
03. Web Service Client to remotely manage the life cycle of the substations and Control Center.   

# SoftGrid Testbed Architecture  

![](https://github.com/smartgridadsc/smartpower/blob/master/API/Images/architecture/TestBed_Overview.png)  

As shown in the above diagram, substation is totally build on PowerWorld. PowerWorld simulates the physical power line in the back-end. The power line configuration is defined by the PowerWorld case files. There can be many threads in the substation, at least 2 for each IED. Each IED consumes and responds to incoming commands in IEC 61850 packets in MSS protocol.
In the substation, mainly there are tow JVM to handle below operations. 

### 01. Steady State command processing.  
* Real time command execution  
* Generates real time log data for the Monitoring chart  
* Unable to monitor transient mode power variable fluctuations  
* Use Transient Clock to refresh and calculate the effect of already executed commands  

### 02. Transient state command processing for   
* Execute periodically after few seconds by consolidating multiple commands together  
* Generates transient mode power variable values  
* Generates alerts on Limit Violations( e.g. Over/Under Frequency or Voltage )  

# Control Center
Control Center is a sub module in the Web Service. The first control center command script initiates a singleton client instance and connects to the available Gateway. The only configuration required to define is the Gateway IP(Configuration Name : GATEWAY_IP) in the Web Service Client. Control Center sends commands in IEC 60870-5-104 protocol via TCP.

# Gateway

As the Minimum requirement, the gateway must convert the protocols from IEC 60870-5-104 in TCP to IEC 61850 in MMS and mediate the packets to the correct socket of the IEDs. In order to do so, SoftGrid provides a sample Protocol Translator gateway, which can even be run in a RPi. However, user can connect any other translator devices or programs including industrial versions. In additions to the gateway, user can integrate additional security or non-security devices/programs such as firewalls in between the control center and the substation.

### SoftGrid Web Service
### SoftGrid Web Service Client
## Operation Logics
## State Transition
