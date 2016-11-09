# Getting Started
## Prerequisites
### Mandatory
01. SoftGrid is written in java, and therefore JDK 1.8.* or higher is required. However, due its tight dependency with PowerWorld it's Substation Module can be run only on windows platforms. Therefore, Windows 7 operating system is essential for the SoftGrid testbed.

02. PowerWorld 18 or higher with Auto Simulator COM API and the Transient add-on support.  
The Auto Simulator API is a critical component in the SoftGrid testbed. Therefore it is mandatory to have this API support. However, Transient Stability Analysis add-on is optional. SoftGrid will work without it, but you will not be able to analyze dynamics in transient state of the simulated power grid.

<!--
03. Java 1.8.* or higher.  
SoftGrid is mainly developed in Java. Other languages used along with Java are Python, XML, XSD, SCL (Substation Configuration Language) and PowerWorld Scripting Language.
-->

### Optional
04. Python Version 2.7.*
When SoftGrid is running, a background process running on Python generates the transient state data requests from PowerWorld. If the transient state analysis is not in your priority list, you can ignore Python installation.  

## SoftGrid Architecture  
Basic system architecture is shown in the figure below.   

![](https://github.com/smartgridadsc/smartpower/blob/master/API/Images/architecture/TestBed_Overview.png)

On top of this, to facilitate operation and experiments, SoftGrid offers centralized control of both the control center and substation via Testbed Client through Web Service, as shown in the figure below. In this document, we focus on how users can set up this architecture and start experiments.

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/testbed%20architecture.png)  

## Binary Release
SoftGrid release is available as a binary zip file, which contains an executable jar with sample configuration files.

<!--
Release 1 :  
   `SoftGridv1.0.zip`

NOTE : It is recommended to have basic understanding on the SoftGrid modules, before proceeding to start and operate the SoftGrid modules and commands. Please refer to the [SoftGrid Architecture](https://github.com/smartgridadsc/SoftGrid/wiki/SoftGrid-Architecture) for more details on these SoftGrid components.
-->

## How to Install?

### PowerWorld Simulator
Before starting installation and configuration of SoftGrid, please prepare a PowerWorld case file that defines power grid to be simulated. SoftGrid automatically generates some of the required files, such as IED Capability Description (ICD) files required for IEC61850-based communication, and does some configuration based on the case file. Some of the sample PowerWorld case files are available online (e.g., http://icseg.iti.illinois.edu/), which may be useful when trying SoftGrid.

When preparing a PowerWorld case file, in addition to defining a power grid topology and models, a user must define one empty contingency in PowerWorld's Transient Stability Analysis Add-on, as shown below. A user can give an arbitrary name, and that name is used later in configuration. (In the example below, the name of the contingency is set to be "MY_CONTINGENCY").

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/PW_TSA.png)

In addition, because SoftGrid interacts with PowerWorld simulator via COM API, class ID of the PowerWorld simulator must be configured. The key can be usually found in HKEY_CLASSES_ROOT\pwrworld.SimulatorAuto\Clsid in Windows registry. 

### Get and Extract SoftGrid binary

01. Download the SoftGridV*.zip file from [here](Comming Soon).
02. Extract the zip file and you will get the below folder structure.
![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/Release%20Folder%20Structure.png)  

"config" folder in the file tree contains sample configuration files. To configure a substation with emurated intelligent electronic devices (IEDs) connected to the PowerWorld simulator, you must modify the "ied.properties" file according to your system environment. You may just point some of file paths to the files included in the release as well. For example, the PYTHON_FILE_NAME, PYTHON_FILE_PATH and PYTHON_START_BAT_FILE_PATH, must be pointed to the python folder and two of its files.

### Configuration  

Please note that you can specify relative paths for all the path configurations as needed.  

1. CASE_FILE_PATH       =(Optional)Folder that contains PowerWorld case files. This is an optional configuration and if not specified in the configuration file, then system will set the default relative path as this "..\\SoftGrid\\casefiles\\TempCaseFiles\\". So, all the downloaded case files should be copied to this folder.

2. CASE_FILE_NAME       =(Optional)PowerWorld case file name.  This is an optional configuration and if this is not specified, system will set the default file name as "CaseFile.PWB". So, any downloaded case files should be renamed to this default name.

3. LIMIT_VIOLATION_RECORD_FILE=File path and name of limit violation log file (*.csv)  

4. LOG_FILE             =File path and name of log file (*.log)  

5. PYTHON_FILE_PATH     =Path to Python package folder (\\SofrGrid\\python\\ in the extracted file tree)  

6. PYTHON_START_BAT_FILE_PATH=Full path to pythonproxy.bat in \\SofrGrid\\python\\ in the extracted file tree 

7. PYTHON_FILE_NAME     =PYCOB_PW_SM.py (Do not change.) 

8. EXP_DATA_FILE        =File path and name of a file that stores transient stability analysis data (*.csv)  

9. CLOCK_CONTINGENCY_NAME=The name of the contingency for transient stability analysis defined in the PowerWorld case to be used  

10. PW_TO_SCL_MAPPING   =File path and name of a file that defines mapping between PowerWorld variable name and variable name used in IEC 61850 Substation Configuration Language(SCL). Default configuration, which meets requirements in typical use cases is found in SoftGridService\\config\\properties\\PWtoSCLMapping.properties  

11. IED_TYPE_TO_FIELD_MAPPING=File path and name of a file that defines mapping between PowerWorld variable name and variable name used in IEC 61850 Substation Configuration Language(SCL). Default configuration, which meets requirements in typical use cases is found in SoftGridService\\config\\properties\\IEDTypeToFieldMapping.properties  

12. ConfFile            =Intermediate config file name (*.xml). This will be dynamically generated and no need to edit.  

13. ip                  =IP address of the PC on which substation is run.  

14. GENERATE_SCL        =Set true if SCD file needs to be generated automatically when starting up.  

15. VIRTUAL_CLOCK_CYCLE_DURATION=Time interval in millisecond at which power flow simulation is updated.  

16. POWER_WORLD_CLSID   =The class ID of PowerWorld found in Windows registry. Eg. {0BDBD63F-C4A1-4226-9546-8964CED2C29B}   

16. POWER_WORLD_EXE     =The file path of PowerWorld.exe  

17. TEMP_STATE_FILE_PATH=File path and name of a temporary file  

18. SCL_PATH            =File path of a folder to store SCL files  

19. ServerType          =IED (Do not change.) 

### Sample ied.properties file    
    'LIMIT_VIOLATION_RECORD_FILE=..\\SoftGrid\\auxFile\\ContingencyAux_auxFiles\\result\\ViolationCount.csv'
    'LOG_FILE=..\\SoftGrid\\log\\IEDLog.log'
    'PYTHON_FILE_PATH=..\\SoftGrid\\pypw\\main\\'
    'PYTHON_START_BAT_FILE_PATH=..\\SoftGrid\\pypw\\test\\pythonproxy.bat'    
    'PYTHON_FILE_NAME=PYCOB_PW_SM.py'
    'EXP_DATA_FILE=..\\SoftGrid\\auxFile\\ContingencyAux_auxFiles\\result\\ExperimentData.csv'
    'CLOCK_CONTINGENCY_NAME=CONTINGENCY1'
    'PW_TO_SCL_MAPPING=..\\SoftGrid\\config\\properties\\PWtoSCLMapping.properties'    
    'IED_TYPE_TO_FIELD_MAPPING=..\\SoftGrid\\config\\properties\\IEDTypeToFieldMapping.properties'
    'ConfFile=..\\SoftGrid\\PWModel.xml'
    'ip=192.168.0.228'
    'GENERATE_SCL=true'
    'VIRTUAL_CLOCK_CYCLE_DURATION=20'
    'POWER_WORLD_CLSID={0BDBD63F-C4A1-4226-9546-8964CED2C29B}'
    'POWER_WORLD_EXE=C\:\\Program Files (x86)\\PowerWorld\\Simulator19\\pwrworld.exe'    
    'CONFIG_FOLDER=..\\SoftGrid\\config\\scl\\'
    'TEMP_STATE_FILE_PATH=..\\SoftGrid\\casefiles\\state.file'
    'SCL_PATH=..\\SoftGrid\\scl\\'
    'ServerType=IED'

<!--
NOTE : Until you obtain some advanced knowledge on other configuration files, avoid modifying them and you may use them as they are.  
-->

### Testing Substation Setup  
The best way to check and validate the configurations, is manually running a substation. The term substation here represents a collection of IEDs connected to a simulated power grid. To start the substation IEDs, open a command prompt and go to SoftGridService folder in the SoftGrid file tree and execute the command below. 

* To start IEDs or the Substation  
`java -jar SoftGridv1.0.jar`  
or  
`java -jar SoftGridv1.0.jar IED`  

NOTE : If your extracted folder has is read only the program may not work properly. Therefore, make sure it is allowed to both read and write. You can check and change this, just by right clicking the extracted folder and going to properties option. You will see the read only check box and the hidden checkbox. Make sure both of these check boxes are unchecked.  

If everything goes fine, it will open the below operation console.  
![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/OperationConsole.png)  

Next, switch to the Configuration tab and make sure the correct configuration file is specified in the text field at the bottom as highlighted below.  
![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/ConfigurationSelection.png)  

To select a configuration file, use the folder button at the right of the text field to select the correct configuration file edited above. Once selected, all the configuration parameters will be listed in the table at the top of the window. If needed, you can further change any parameters in this configuration table to edit the config file.  
After verifying the configuration file and the parameters, we can start the IEDs. As annotated in the above image, to start the IED servers, simply click the blue "play" button. Then the system will do following actions.  

1. Start and connect to the PowerWorld.  

2. Load the PowerWorld Case file specified in the config file.  

3. If GENERATE_SCL is set to be true, then load the list of Power Grid Components and Devices from the PowerWorld case file, and instantiate an IED for each of them.  

4. Start a thread for each IED, which listens incoming control/interrogation commands at a unique network port.

5. When all the IEDs are successfully started, you will see the console output like below.  

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/StartedIEDs.png)  

The started IEDs generate periodic status data extracted from the PowerWorld simulator and store them in the log file. These log data can be queried by a SQL-like command-line front end shown at the top of Controller tab.

#### SQL-like Commands to query logs

Currently SoftGrid supports 5 types of IEDs.  
1. Buses  

2. Branch status monitors with Circuit Breakers  

3. Transformers 

4. Generators 

5. Shunt Reactors 

6. Loads  

7. Virtual  

The Virtual IEDs are IED placeholders to monitor power grid status, and they are not mapped to any real device or don't support any control commands (i.e., available only for interrogation). You can query status data of any of these IEDs with a simple filter logic as below two examples.  
  
E.g.,

  `Select BusKVVolt from Bus Where BusNum=53;`  
  `Select ButRatio from Gen Where GenID=31;`  
  `Select overloadrank from virtual;`

"overloadrank" is a field variable in the PowerWorld. It is a value calculated based on the overall power flow in all the branches of the simulated power grid.

When a command is entered and press enter, Control console will parse the script and start querying the corresponding log file. Then, the extracted data will be passed to the Chart Panel to display in a dynamically changing chart panel as a time series line chat as below. (Note that currently this char is only available when IED is started manually, not via web service explained later.) 

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/IED%20Status%20Charts.png)  

If you are able to come up to this point and see the chart is slowly moving ahead with the time,  you can consider that the substation configuration is completed. 

## Set up SoftGrid Service and Client Console  

This section explains how to configure and start the SoftGrid web service, which manages the control center and IEDs, and its client interface.  

* To start SoftGrid Web Service on localhost (i.e., service IP address is localhost/127.0.0.1)
`java -jar SoftGridv1.0.jar Service`  

* To start a standalone Web Service for remote access, a user can specify an IP address to be used.
`java -jar SoftGridv1.0.jar Service 192.168.0.111`  

When the SoftGrid Web Service is started with no errors, you will be able to see below log messages in the command prompt.    

    `init.....................................!`  
    `Oct 06, 2016 11:47:24 AM org.glassfish.grizzly.http.server.NetworkListener start`  

    `INFO: Started listener bound to [localhost:8080]`  
    `Oct 06, 2016 11:47:24 AM org.glassfish.grizzly.http.server.HttpServer start`  
    `INFO: [HttpServer] Started.`  
    `Jersey app started with WADL available at http://localhost:8080/softgrid/application.wadl`  
    `Hit enter to stop it...`  

As show in the above log messages the wadl url pattern will be http://[IP Address]:8080/softgrid/application.wadl. If the service is started correctly you should be able to access the wadl in a web browser as follows.

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/wadl%20browser.png)

* To start a Web Service Client  
`java -jar SoftGridv1.0.jar ServiceClient`  

If Web Service is started for remote access, Web Service Client can be run on the different PC from Service. (In this case, IP address of the PC running Web Service must be specified in the config file explained later.)
When the Service Client is started, it will display below Service Client Console.  

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/ServiceClient.png)  

## Start Substation via Web Service  
Earlier we have explained how to start substation IED Servers Manually.  
In this section, we will learn how to start substation IEDs via Web Service. Firstly, make sure Web Service is started without errors and the SoftGrid Web Client Console is started as explained in the previous section. 

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/ServiceClient-Annotated-Big.png)

In this window, you can create and manage multiple experiments using the "Create New Experiment" button. Each experiment should be associated with one PowerWorld case file. When you start substation IEDs, the PowerWorld case file will be automatically uploaded to the web service to create ICD files for each IED.  

<!--
For example, our release may contain a sample Case file of 37 bus system. In overall, it simulates 120 power grid devices in the power world, such as branches, transformers, generators, etc. Therefore, the Substation server will create at least 120 IEDs. Additionaly few more Virtual(Ref Section : Manual Substation Setup) IEDs will be started in paralel, to handle overall power grid variables, such as overload rank.
-->

To associate a case file, it should be copied to the corresponding experiment folder, and the file name should be added to the config file in the experiment folder. 

Experiment folder = [Extracted Folder Path]\Experiments\[Experiment Name]\
E.g., If the zip extracted folder is   
    `C:\SoftGrid\SoftGridService\`
 Then the Experiment folder of "Experiment 1" will be  
    `C:\SoftGrid\SoftGridService\Experiments\Experiment 1`
In config.properties in the experiment folder, the parameter called CASE_FILE is used to specify the case file name. Apart from the PowerWorld case file, there are other configuration parameters that should be setup for each experiment. You can edit them directly in the Web Client Console as well. Just click on the project tree in the left pane, which shows the Configuration Table at the top of the center panel.

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/Configuration%20Table.png)

Configuration parameters and example values in the config.properties file are shown below.    

    `DESCRIPTION=Muliiple Attack Commands using command Script File`
    `START_TIME=0`
    `CASE_FILE=GSO_37Bus.PWB`
    `MAX_DURATION=13000`
    `REPEAT_COUNT=1`
    `EXP_NAME=Attack Command Script`
    `CMD_FILE_PATH=commandScript.cmd`
    `GATEWAY_IP=192.168.0.173`
    `SERVICE_IP=localhost`

SERVICE_IP must match the Web Service's IP address used above (either localhsot or the machine's IP address). If the Web Service is running at the same machine as Web Service Client, you can specify SERVICE_IP=localhost.  

Control Center will not communicate directly with the IEDs. It must communicate through a protocol translator ( and any security devices) in the gateway. Therefore, GATEWAY_IP is the IP address of gateway device ( or programe) with which a control center directly interacts.  

The Power World case file name should be given for the CASE_FILE parameter. Before starting a Substation server(IEDs)  this case file will be uploaded to the SoftGRID service. The service client assumes, that the CASE_FILE is in the experiment folder. Therefore, if you specify a case file name and if it is not available in the experiment folder, an error message will be shown in the Service Client.  

You can ignore the REPEAT_COUNT and MAX_DURATION in the release 1.0 as its operations are not implemented yet. However, if you are willing to use a command script file to store control center commands, those files should be copied to the experiment folder and its name should be specified under CMD_FILE_PATH parameter.  

Next, to start the substation press the Start Substation Button with the blue play icon. Then, the Web Client will send a API call to make the Web Service load the case file, and start the substation IEDs. It may take few minutes to start all the IEDs, depending on system performance as well as the number of IEDs. If everything goes fine, you will see the below output in the Log Window of Web Client console.  

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/IEDs%20Started-small.png)

On the other hand, at the substation side, the Substation Control Console will appear in a popup window, and its log window will indicate the current status of the server initialization. However, you may not see the status chart panel this time, because its only available only for manual substation execution as explained previously.   

## IED to Gateway Connection  

After starting all the IEDs, we can setup and connect a substation gateway and/or a security device to be tested. For the sake of completeness, SoftGrid provides a sample substation gateway, which implements simple protocol translation between IEC 60870-5-104 and IEC 61850 MMS, as well. In this user manual, we will explain how to configure the SoftGrid substation gateway to connect to the SoftGrid substation IEDs, as an example. In the similar way you will be able to plug other substation security solutions of interest (gateway, firewall, etc.).

<!--
The security solution under test is placed between the control center and the IED servers or substation. If that device is a substation gateway, it should be able to connect to the IED servers that we started locally or remotely.  
-->

Lets learn how to start SoftGrid Substation Gateway.

### Environment to Run SoftGrid Substation Gateway  

1. Windows or Linux Operating System  
We have tested SoftGrid Gateway on Windows 7, Red Hat Linux, Ubuntu and Linux Light.   

2. Java 1.8  
SoftGrid Gateway is written purely in Java-8. Therefore Java 1.8 or higher is essential.  

### Installing SoftGrid Substation Gateway  
To start the SoftGrid Substation Gateway, first download the same SoftGrid zip file and extract it on computer or a virtual machine you want to use. Then, open a command prompt or a Linux terminal and go to the extracted folder and type the command below.  

* To start SoftGrid Gateway  
    `java -jar SoftGridv1.0.jar PRX`  

When you run the above command the Substation Control Console will appear as below. Note that, although GUI is the same, we now started a substation gateway system.

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/Gateway_Not%20Started-small.png)

Go to Configuration tab and select the sample gateway configuration file in the below path and press save.    

    `[Extracted Folder]\config\proxy.properties`  

Now you will see the configuration parameters spear at the top of the configuration tab as shown below.  

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/GatewayConf_small.png)

Make necessary changes according to the instructions below.  

    `ip=IP Address of the machine running substation IEDs`  
    `PW_TO_SCL_MAPPING=[Extracted Folder Path]\\config\\properties\\PWtoSCLMapping.properties`  
    `IED_TYPE_TO_FIELD_MAPPING=[Extracted Folder Path]\\config\\properties\\IEDTypeToFieldMapping.properties`  

The same files PW_TO_SCL_MAPPING and IED_TYPE_TO_FIELD_MAPPING as the ones used in substation IDS must be copied and used here. 
<!--
So when u extract the zip file these files are available at 'Extracted Folder Path\\config\\properties\\' folder as mentioned above.
-->   

    `SCL_PATH=SCL File Path`  

The SCL files (ICD Files) generated at the substation IED side should be copied to some folder in the the gateway system. Then its path should be specified under SCL_PATH.  

    `ServerType=PRX`  

<!--
Server type configuration parameter is used to integrate different type of Gateways to the SoftGrid. At the initial release the Gateway Server Type is "PRX".  
-->

Do not change ServerType since this instructs the system to be started as a substation gateway.

    `ConfFile=Extracted Folder Path\\PWModel.xml`  

You can specify some xml file path for the ConfFile. However, it is recommended to use the extracted folder for this temporary xml file used for internal operations.  

    `PROXY_SERVER_LOCAL_API_MODE=true`  

Always keep the PROXY_SERVER_LOCAL_API_MODE=true as it is used for internal operations.  

When all the configurations are set up correctly, you may save the file and go to the control tab again. Press the play button to start the SoftGrid substation Gateway. If all the configurations are correct, Console will start the gateway and you will be able to see the below output at the top of the control tab panel.

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/Gateway_Started-small.png)

At the same time you will be able to see large number of log outputs with ip address and connection ports appearing in the command prompt or terminal as below.  If you see any errors in between these log outputs, they are likely be due to wrong configurations. 

    `[SwingWorker-pool-1-thread-1] INFO it.ilinois.adsc.ema.control.proxy.client.SubstationProxyClient - Default Proxy Server Port: 2404`
    `[SwingWorker-pool-1-thread-1] INFO it.ilinois.adsc.ema.control.proxy.client.SubstationProxyClient - usage:     org.openmuc.openiec61850.SubstationProxyClient <host> <port>`
    `[SwingWorker-pool-1-thread-1] INFO it.ilinois.adsc.ema.control.proxy.client.SubstationProxyClient - Default Host Address : 192.168.0.228`
    `[SwingWorker-pool-1-thread-1] INFO it.ilinois.adsc.ema.control.proxy.client.SubstationProxyClient - Default Client Port: 10003`
    `it.ilinois.adsc.ema.control.ied.pw.PWModelDetails@4a8c9909`
    `[SwingWorker-pool-1-thread-1] INFO it.ilinois.adsc.ema.control.proxy.client.SubstationProxyClient - Attempting to connect to server 192.168.0.228 on port 10003`
    `it.ilinois.adsc.ema.control.ied.pw.PWModelDetails@2747156d`
    `[SwingWorker-pool-1-thread-1] INFO it.ilinois.adsc.ema.control.proxy.client.SubstationProxyClient - Default Proxy Server Port: 2404`
    `[SwingWorker-pool-1-thread-1] INFO it.ilinois.adsc.ema.control.proxy.client.SubstationProxyClient - usage: org.openmuc.openiec61850.SubstationProxyClient <host> <port>`
    `[SwingWorker-pool-1-thread-1] INFO it.ilinois.adsc.ema.control.proxy.client.SubstationProxyClient - Default Host Address : 192.168.0.228`
    `[SwingWorker-pool-1-thread-1] INFO it.ilinois.adsc.ema.control.proxy.client.SubstationProxyClient - Default Client Port: 10004`
    `[SwingWorker-pool-1-thread-1] INFO it.ilinois.adsc.ema.control.proxy.client.SubstationProxyClient - Attempting to connect to server 192.168.0.228 on port 10004`

## Control Center Setup  
If you have done above sections successfully, you must be having already running substation IEDs, and a substation gateway translating the protocols from IEC 60870-5-104 to IEC 61850. The next step is on how to configure a control center and send control/interrogation commands to the substation.

SoftGrid comes with a inbuilt control center, which is compliant with IEC 60870-5-104, and a simple scripting language to generate control and interrogation commands. The only configuration needed is correct GATEWAY_IP (IP Address of the machine that runs the substation gateway) in configuration panel of the Web Client console as follows.

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/GatewayIPSetup.png)

Now, all you have to do is just enter commands in Command field in Web Service Client window. Then the Web Service Client will send this command to the SoftGrid Web Service to automatically start a control center client on the same computer. The started control center will send the entered command to the substation gateway in IEC 60870-5-104. The substation gateway will validate the command and act as a protocol translator to convert the command into IEC 61850. Then the translated command will reach a targeted IED to do the necessary processing (either changing or querying status of the associated device in a simulated power grid). If it is an interrogation command, it immediately sends the current status values on the IED to the control center via the gateway. You will be able to see the outputs appearing in the log panels of the console on the substation gateway. In the case of a control command, the instructed change will applied immediately and outcome of the command will be visible through a later interrogation.

## Control Center Command Scripts  
There are lot of parameters and constraints in IEC 60870-5-104 protocol. And also it is not practically easy to handle large number of commands at the same time in a simple user interface. Therefore, besides inerface to accept manually-entered commands, SoftGrid provides a very simple scripting language to specify a series of control center commands.

All the IEDs are indexed based on their port numbers. Port numbers are starting from 10003. For example, 25th IEDs port number will be 10003 + 25 = 10028. So we use port number - 10003 as the Information Object Address (IOA) of each IED.  

You can use the download logs button to download the IOA-number to IED mapping table file in csv format. So after starting all the IEDs, press the download button in the Service Client. Then go to experiment folder and you will see the IEDDataSheet.csv file. when you open this file you may see a similar table as below.  
1st Column : IED Type  
2nd Column : IOA-Number  
3rd Column : Key-Value pairs needed to use in the SQL-Like scripts in the Substation Control Console  
4th Column : The Port number of the IED in the substation    

Gen,	94,	BusNum=14 GenID=1 ,	10097  
Gen,	95,	BusNum=28 GenID=1 ,	10098  
Gen,	96,	BusNum=28 GenID=2 ,	10099  
Gen,	97,	BusNum=31 GenID=1 ,	10100  



In the current version, it supports only following commands.  

* Interrogation command  

    Use Case : To interrogate a specific IED or all the IEDs  

    Format-1 : interrogation [IOA] 
    Format-2 : interrogation all 
    Eg.  
      `interrogation 53`

      `interrogation all`  

* Single control command  

    Use Case : To control some parameter in the power grid component such as opening or closing a circuit breaker.    

    Format-1 : scommand [IOA] [SCL Field Name]=[Value]  
    Eg.  
      `scommand 53 linestatus=true`  
    (linestatus is the circuit breaker parameter, which can be used to open or close it.)  

* Run Command Scripts  
    Instead of running individual commands, it is helpful to store a series of multiple commands and run them at the scheduled timestamp. This feaure is very important in cyber-physical security research and development. For example, this feature would allow you to run the same experiment multiple times under the same power grid configurations with different set of controlled devices, so that the results can be compared. 
SoftGrid Web Service Client console has a configuration parameter called CMD_FILE_PATH to specify pre-shceduled control command scripts. The script format is shown below.  

    Format : [Delay in Millisecond]>[command]   
    E.g.,  
      `1000>interrogation 53;`  
      `2000>interrogation 43;`  
      `10000>attack 100 linestatus=true CB;`  
      `10000>scommand 53 linestatus=true`  

    According to the above script,  
    1st command will be executed after 1000 millisecond,  
    2nd command will be executed after 2 seconds (2000 milliseconds),  
    3rd and 4th commands will executed at the same time after 10 seconds (10000 milliseconds).  
    
    After writing commands in a script file, copy the file into the experiment folder. Then, specify the file name in the configuration parameter CMD_FILE_PATH on Web Service Client as follows.

    ![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/Control%20Command%20Script%20file.png)

    Now enter the below command in the Command field of Web Service Client and press enter to send the script file to the control center.  
    `run script`  

* Run Circuit Breaker Attack Script    

    To facilitate evaluation of security devices in the SoftGrid, we are planing to add inbuilt attack command scripts to simulate attacks. In the current release, SoftGrid provides one attack command to issue "open" commands to randomly selected circuit breakers, similarly to the Ukraine incident in 2015.   

    Format : attack [Circuit Breaker Percentage] linestatus=true CB    

    E.g.,  
      `attack 100 linestatus=true CB`  
      `attack 25 linestatus=true CB`  
    1st command opens all(100%) the circuit breakers randomly one at a time.  
    2nd command opens 25% of circuit breakers randomly one at a time.   

## Evaluating Experiment Results  

You can do the evaluation based on following methods.  

### Evaluating the data in log files  

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/log%20file%20structure.png)  

As shown in the above folder structure, log files can be downloaded to the experiment folder. Mainly there are 4 types of logs based on their prefix.
    
#### Log files with "CCMSGCount.log" as prefix    
These log files are generated based on control center data.  

Log message format : < response time > , < total message count > , < current pending message count >  
Sample Log message : "346 , 3 , 1"  

#### Log files with "IEDLog.log" as prefix  
These log files contain the state data of all the IEDs. i.e., Each IED periodically check the actual power grid value and log them in this file. The SQL-like script in the monitoring window can be used to plot these values in the chart window dynamically in real time.    

Log message format : Data:< Current time in millisecond >:Type:< IED Type >:< VariableName : Variable Value pairs of all the IED variables >  
Sample Log message : "Data:1476955859864:Type:BUS:BusNum:    54:BusKVVolt: 69.68999934"  

#### Log files with "IEDLog.logPW" as prefix    
These log files contain all the commands executed on IEDs in the PowerWorld Scripting language. By default These commands are collected withing the cycle of 8 seconds and dump into these log files periodically. 

Log message format : "OPEN_ALL" < Executed Offset Time ( within 8 second cycles ) in Second.Millisecond format > "< PowerWold device type and Key values" "< PowerWorld Command >" "CHECK" ""  
Sample Log message : "OPEN_ALL" 648.361 "Branch '54' '53' '1'" "OPEN BOTH" "CHECK" ""    

#### Log files with "PWLog.log" as prefix  
These are used only internal processing. Please ignore them.  

### Evaluating the data in Transient Data Sheets  

![](https://github.com/smartgridadsc/SoftGrid/blob/master/API/Images/TransientDataSheet%20Files.png)  

Transient Data Sheets are available in the above location in the Extracted Folder. In every 8 second( Default cycle ), SoftGRID generate PowerWorld script files by consolidating all the executed commands during last 8 second. Then, based on these script files, it calculates the transient values and dump into data sheets in the < extracted folder > /auxfile/ContingencyAux_auxFiles/csv/ folder. As it creates large number of files, SoftGRID clean them after processing. Based on these files limit violations are calculated and dumped in to the below file.  

Limit Violation File : < Extracted folder > /auxfile/ContingencyAux_auxFiles/result/ViolationCount.csv   
Log message format : < Time in Millisecond >,< Violation Type >,< Violated Value >,< Timestamp >,< PowerWorld object and data reference >  
Sample Log message : 1476954472227,Under Voltage(5) :0.94,Time :17:07:51:662000, u'Bus   15  TSBusVPU '    

### Evaluating based on Real time Chart Panel
You can directly type SQL-Like commands in the monitoring window and observe the real time effect on the chart panel. Currently, chart panel is functional only when substation is manually started (i.e., not started via Web Service).


<!--
There are multiple ways that you can observe the communication.  
01. Via Gateway Control Console log panel  
02. Chat Panel in Substation Control Console  
03. Command Prompt or terminal of Gateway, Substation, Web Service  
04. Transient Panel ( To see limit violations such as over/under frequency or voltage)  
05. Downloading and analyzing log files in Web Client console  
06. Downloading and analyzing transient changes in Web Client Console.  

NOTE : Click on Download Log file button at any time to download transient changes and log files into the below path.  
    `< Experiment Folder >\logs_downloaded\`
-->

