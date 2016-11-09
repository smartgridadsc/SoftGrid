'''
Python interface to PowerWorld simulator, includes direct wrapping from SimAuto functions
Currently work with comtypes 0.6.2, do *not* work with comtypes 1.1.0

Dictionary:
    ObjectType: name of object such as GEN, LOAD, etc.
	FieldList: list of object fields such as BusNum, GenID, etc.
	Add more
'''

import comtypes.client as cc
from tabulate import tabulate
import time
import re
import numpy as np

from pypw.pypw.pypwconst import *

PW_OBJECTS = {}

class SimAuto:

	##############################################################
	# 1. General functions										 #
	##############################################################

	# def GetObject(self):
	# 	return OBJECT


	def GetField(self, ObjectType, FieldType="PRIMARY"):
		assert isinstance(ObjectType, str)
		assert isinstance(FieldType, str)
		FieldType = FieldType.upper()

		# Generate object field
		if not ObjectType in PW_OBJECTS:
			Output = self._GetFieldList(ObjectType)
			tmp = str(Output[0])
			if tmp != "": raise Exception(tmp)

			NewObject = {}
			for output in Output[1]:
				tmp = [o.encode("ascii", "ignore") for o in output]
				NewObject[tmp[1]] = {"type":tmp[0], "fmt":tmp[2], "desc":tmp[3]}

			PW_OBJECTS[ObjectType] = NewObject

		Object = PW_OBJECTS[ObjectType]

		if FieldType == "PRIMARY":
			return {name:Object[name] for name in Object if re.match("^\*.*[1-9].*\*$", Object[name]["type"])}

		elif FieldType == "SECONDARY":
			return {name:Object[name] for name in Object if re.match("^\*.*[A-Z].*\*$", Object[name]["type"])}

		elif FieldType == "REQUIRED":
			return {name:Object[name] for name in Object if re.match("^\*.*\*$", Object[name]["type"])}

		elif FieldType == "ALL":
			return {name:Object[name] for name in Object if re.match(".*", Object[name]["type"])}

		elif FieldType == "TSSAVEOPTION":
			return {name:Object[name] for name in Object if re.match("TSSave.*", name)}

		elif FieldType == "TSRESULT":
			return {name:Object[name] for name in Object if re.match("TS.*", name) and not re.match("TSSave.*", name)}
		else:
			raise Exception("FieldType\"%s\" not found."%FieldType)


	# Reimplement SimAuto's func:GetFieldList
	def GetFieldList(self, ObjectType, FieldType="PRIMARY"):
		return self.GetField(ObjectType, FieldType).keys()


	def PrintTable(self, Header, Data, Title="", IsTranspose=False):
		assert isinstance(Header, list)
		assert isinstance(Data, list)
		assert isinstance(Title, str)
		assert isinstance(IsTranspose, bool)
		assert all([len(Header) == len(data) for data in Data])

		if Title != "":
			print "# TABLE: %s"%Title

		if IsTranspose:
			tmp = Data.insert(0, Header)
			NewData = np.array(Data).T.tolist()
			print tabulate(NewData, tablefmt="orgtbl")

		else:
			print tabulate(Data, headers=Header, tablefmt="orgtbl")

		print


	def ReportError(self, Message):
		Message = str(Message)
		print Message;
		if Message != "":
			raise Exception(Message)


	def Connect(self):
		cc.GetModule(POWER_WORLD_EXE_PATH)
		self.saco = cc.CreateObject("pwrworld.SimulatorAuto") # create SimAuto COM Object

	def Connect(self, POWER_WORLD_EXE):
		cc.GetModule(POWER_WORLD_EXE)
		self.saco = cc.CreateObject("pwrworld.SimulatorAuto") # create SimAuto COM Object
	
	def Disconnect(self):
		self.saco = None


	def OpenCase(self, FileName):
		assert isinstance(FileName, str)
		Output = self.saco.OpenCase(FileName)
		self.ReportError(Output[0])


	def SaveCase(self, FileName):
		assert isinstance(FileName, str)
		Output = self.saco.SaveCase(FileName, "PWB", "Yes")
		self.ReportError(Output[0])


	def CloseCase(self):
		Output = self.saco.CloseCase()
		self.ReportError(Output[0])


	# The MOST powerful function, which can invoke any aux function
	def RunScriptCommand(self, Statements):
		assert isinstance(Statements, str)
		Output = self.saco.RunScriptCommand(Statements)
		self.ReportError(Output[0])


	def CreateData(self, ObjectType, FieldList, Data):
		assert isinstance(ObjectType, str)
		assert isinstance(FieldList, list)
		assert isinstance(Data, list)
		assert len(FieldList) == len(Data)
		assert all([isinstance(field, str) for field in FieldList])
		assert all([isinstance(data, str) for data in Data])

		Script = "CreateData(%s, [%s], [%s]);"%(ObjectType, ",".join(FieldList), ",".join(Data))
		self.saco.RunScriptCommand(Script)


	def GetData(self, ObjectType, FieldList, IsTranspose=False):
		assert isinstance(ObjectType, str)
		assert isinstance(FieldList, list)
		assert isinstance(IsTranspose, bool)

		Output = self._GetParametersMultipleElement(ObjectType, FieldList, "")
		self.ReportError(Output[0])

		Data = [map(str, output) for output in Output[1]]
		return Data if IsTranspose else np.array(Data).T.tolist()


	def GetFieldListAndData(self, ObjectType, FieldType, IsTranspose=False):
		assert isinstance(ObjectType, str)
		assert isinstance(FieldType, str)
		assert isinstance(IsTranspose, bool)

		FieldList = self.GetFieldList(ObjectType, FieldType)
		Data = self.GetData(ObjectType, FieldList, IsTranspose=IsTranspose)
		return FieldList, Data


	# Merge 2 SimAuto functions together to avoid confusion
	def SetData(self, ObjectType, FieldList, Data):
		assert isinstance(ObjectType, str)
		assert isinstance(FieldList, list)
		assert isinstance(Data, list)
		assert all([field in PW_OBJECTS[ObjectType] for field in FieldList])

		if isinstance(Data[0], list):
			assert all([len(FieldList) == len(data) for data in Data])
			assert all([all([isinstance(d, str) for d in data]) for data in Data])
			Output = self.saco.ChangeParametersMultipleElement(ObjectType, FieldList, Data)

		else:
			assert len(FieldList) == len(Data)
			assert all([isinstance(data, str) for data in Data])
			Output = self.saco.ChangeParametersSingleElement(ObjectType, FieldList, Data)

		self.ReportError(Output[0])


	def DeleteData(self, ObjectType):
		assert isinstance(ObjectType, str)
		Script = "Delete(%s);"%ObjectType
		self.saco.RunScriptCommand(Script)


	# Same as func:ChangeParametersMultipleElement, except param:Data is flattened to a 1d array
	def _ChangeParametersMultipleElementFlatInput(self, ObjectType, FieldList, NoOfObjects, Data):
		return self.saco.ChangeParametersMultipleElementFlatInput(ObjectType, FieldList, NoOfObjects, Data)


	# Use func:SetData or func:SetDataMultiple instead
	def _ChangeParameters(self, ObjectType, FieldList, Data):
		return self.saco.ChangeParameters(ObjectType, FieldList, Data)


	def _GetCaseHeader(self, filename):
		return self.saco.GetCaseHeader(filename)


	def _GetFieldList(self, ObjectType):
		return self.saco.GetFieldList(ObjectType)


	# Same as func:GetParametersMultipleElement, except that the output is flattened to 1d array
	def _GetParametersMultipleElementFlatOutput(self, ObjectType, FieldList, FilterName):
		return self.saco.GetParametersMultipleElementFlatOutput(ObjectType, FieldList, FilterName)


	# Reimplemented by func:GetData which provides better output format
	def _GetParametersMultipleElement(self, ObjectType, FieldList, FilterName):
		return self.saco.GetParametersMultipleElement(ObjectType, FieldList, FilterName)


	# What is param:Values ???
	def _GetParametersSingleElement(self, ObjectType, FieldList, Values):
		return self.saco.GetParametersSingleElement(ObjectType, FieldList, Values)


	def _GetSpecificFieldList(self, ObjectType, FieldList):
		return self.saco.GetSpecificFieldList(ObjectType, FieldList)


	def _GetSpecificFieldMaxNum(self, ObjectType, Field):
		return self.saco.GetSpecificFieldMaxNum(ObjectType, Field)

	
	# Almost the same as func:ListOfDevices ???
	def _ListOfDevicesAsVariantStrings(self, ObjectType, FilterName):
		return self.ListOfDevicesAsVariantStrings(ObjectType, FilterName)


	# Almost the same as func:ListOfDevices ???
	def _ListOfDevicesFlatOutput(self, ObjectType, FilterName):
		return self.ListOfDevicesFlatOutput(ObjectType, FilterName)


	def _ListOfDevices(self, ObjectType, FilterName):
		return self.saco.ListOfDevices(ObjectType, FilterName)


	def _LoadState(self):
		return self.saco.LoadState()


	# Quite powerful function, can load any setting from aux files
	def ProcessAuxFile(self, FileName):
		assert isinstance(FileName, str)
		Output = self.saco.ProcessAuxFile(FileName)
		self.ReportError(Output[0])


	def _SaveState(self):
		return self.saco.SaveState()


	def _SendToExcel(self, ObjectType, FilterName, FieldList):
		return self.saco.SendToExcel(ObjectType, FilterName, FieldList)


	def _WriteAuxFile(self, FileName, FilterName, ObjectType, EString, ToAppend, FieldList):
		return self.saco.WriteAuxFile(FileName, FilterName, ObjectType, EString, ToAppend, FieldList)


	# def Export(self, FileName, FieldType):
	# 	assert isinstance(FileName, str)
	# 	assert isinstance(FieldType, str)

	# 	OpenFile = open(FileName, "w")
	# 	for ObjectType in GetObject():
	# 		OpenFile.write("# %s\n"%ObjectType)
	# 		FieldList, Data = self.GetFieldListAndData(ObjectType, FieldType)
	# 		OpenFile.write("%s\n"%",".join(FieldList))
	# 		for data in Data:
	# 			OpenFile.write("%s\n"%",".join(data))
	# 		OpenFile.write("\n")
	# 	OpenFile.close()


	# Is this function helpful?
	def EnterMode(self, Mode):
		assert isinstance(Mode, str)
		self.saco.RunScriptCommand("EnterMode(%s);"%Mode)


	##########################
    # 2. Power flow study	 #
    ##########################

	def SolvePowerFlow(self, Method=PFMRECTNEWT):
		assert isinstance(Method, str)
		self.saco.RunScriptCommand("SolvePowerFlow(%s);"%Method)


	def _OPF_Objective(self):
		return None # Undefined


	def _OPF_Reserves_Objective(self):
		return None # Undefined


	def _SCOPF_Objective(self):
		return None # Undefined


	###################################
    # 3. Transient stability analysis #
    ###################################
		
	# This function is rarely invoked because most of the time only TS_DEFAULT_CTG is used
	def TSCreateContingency(self, CtgName):
		assert isinstance(CtgName, str)
		Script = "CreateData(%s,[%s],[%s]);"%(TSCONTINGENCY, "TSCTGName", CtgName)
		self.saco.RunScriptCommand(Script)

	def TSGetContingency(self):
		return self.GetFieldListAndData("TSCONTINGENCY", "REQUIRED") 

	# Delete all transient contingencies, only the default TS_DEFAULT_CTG remains
	# All events from TS_DEFAULT_CTG are deleted though
	def TSDeleteContingency(self):
		self.DeleteData("TSCONTINGENCY")

	# param:WhoAmI has the following format "<ObjectType> <PrimaryKey>"
	# Example: "GEN 1 1" refers to Generator #1 at Bus #1
	# The most frequently used param:EventString are declared as constants in SimAutoConstant
	def TSCreateEvent(self, CtgName, EventTime, WhoAmI, EventString):
		assert isinstance(CtgName, str)
		assert isinstance(EventTime, str)
		assert isinstance(WhoAmI, str)
		assert isinstance(EventString, str)

		ObjectType = "TSCONTINGENCYELEMENT"
		FieldList = ["TSCTGName", "TSTimeInSeconds", "WhoAmI", "TSEventString"]
		Data = [CtgName, str(EventTime), WhoAmI, EventString]
		Script = "CreateData(%s,[%s],[%s]);"%(ObjectType, ",".join(FieldList), ",".join(Data))
		self.saco.RunScriptCommand(Script)

	def TSGetEvent(self):
		return self.GetFieldListAndData("TSCONTINGENCYELEMENT", "REQUIRED")

	def TSDeleteEvent(self):
		self.DeleteData("TSCONTINGENCYELEMENT")

	def TSGetStorageOption(self, ObjectType):
		assert isinstance(ObjectType, str)
		return self.GetFieldListAndData(ObjectType, "TSSAVEOPTION") 

	# Potential bug: wrong data when storage option other than TSSaveAll is enabled
	# Example: Set ObjectType=BUS, TSSaveAll=NO, TSSaveBusLoadP=No, TSSaveBusLoadQ=Yes, then
	#          func:TSGetContingencyResults returns weird TSBusLoadP although it isn't enabled
	# Temporary fix: use TSSaveAll only although it increases computation time
	def TSSetStorageOption(self, ObjectType, FieldList=[], IsEnable=True):
		assert isinstance(ObjectType, str)
		assert isinstance(FieldList, list)
		assert isinstance(IsEnable, bool)

		if FieldList == []:
			Script = "TSResultStorageSetAll(%s, %s);"% \
				(ObjectType, YES_STATUS if IsEnable else NO_STATUS)
			self.RunScriptCommand(Script)

		else:
			FieldNum = len(FieldList)
			FieldList = self.GetFieldList(ObjectType, "PRIMARY") + FieldList
			Data = self.GetData(ObjectType, FieldList)
			for data in Data:
				for i in range(FieldNum):
					data[-(1+i)] = YES_STATUS if IsEnable else NO_STATUS
			self.SetData(ObjectType, FieldList, Data)


	def TSSolve(self, CtgName, StartTime="", StopTime=""):
		assert isinstance(CtgName, str)
		assert isinstance(StartTime, str)
		assert isinstance(StopTime, str)

		if StartTime != "" and StopTime != "":
			Script = "TSSolve(%s,[%s, %s]);"%(CtgName, StartTime, StopTime)
		else:
			Script = "TSSolve(%s,[]);"%CtgName

		StartTime = time.time()
		self.saco.RunScriptCommand(Script)
		EndTime = time.time()
		return EndTime - StartTime


	def runAndPause(self, CtgName, StopTime, IsReset=False):
		assert isinstance(CtgName, str)
		assert isinstance(StopTime, str)
		assert isinstance(IsReset, bool)

		ObjectType = "TSCONTINGENCY"
		FieldList = self.GetFieldList(ObjectType, "PRIMARY") + ["EndTime", "TimeStep", "UseCyclesForTimeStep"]
		Data = self.GetData(ObjectType, FieldList)
		StepSize = 10
		StepsInCycles = 10
		for data in Data:
			if data[0] == CtgName:
				assert float(StopTime) <= float (data[-3])
				StepSize = data[-2]
				StepsInCycles = data[-1]
		ResetStartTime = YES_STATUS if IsReset else NO_STATUS
		NumberOfTimeStepsToDo = 0
		print StepSize
		print StepsInCycles
		Script = "TSRunUntilSpecifiedTime(%s, [%s, %s, %s, %s, %s]);"%(CtgName, StopTime, StepSize, StepsInCycles, ResetStartTime, NumberOfTimeStepsToDo)

		StartTime = time.time()
		self.RunScriptCommand(Script)
		EndTime = time.time()
		return EndTime - StartTime


	# FieldList has the following format "<ObjectType> <PrimaryKey> | <VariableName>"
	# Example: "GEN 1 1 | TSGenP" refers to the output active power of Generator #1 at Bus #1
	# Set param:StartTime and param:StopTime to "" to use simulation time
	def TSGetContingencyResults(self, CtgName, FieldList, StartTime="", StopTime="", IsTranspose=False):
		assert isinstance(CtgName, str)
		assert isinstance(FieldList, list)
		assert isinstance(StartTime, str)
		assert isinstance(StopTime, str)
		assert isinstance(IsTranspose, bool)
		Output = self.saco.TSGetContingencyResults(CtgName, FieldList, StartTime, StopTime)
		self.ReportError(Output[0])

		# Output[1][:][0] is ObjectType, Output[1][:][1] is PrimaryKey, Output[1][:][4] is VariableName
		# See www.powerworld.com/WebHelp/Content/html/TSGetContingencyResults%20Function.htm
		Header = ["TSTimeInSeconds"] + ["%s %s %s"%(h[0], h[1], h[4]) for h in Output[1]]
		print Output[1]
		Data = [map(float, data) for  data in Output[2]]
		if IsTranspose:
			return Header, np.array(Data).T.tolist()
		else:
			return Header, Data
