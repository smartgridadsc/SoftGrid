import comtypes.client as cc
from tabulate import tabulate
import time
import re
import numpy as np

from pypwconst import *

PW_OBJECTS = {}

class SimAuto:

	def getFieldNames(self, objType, fldType="PRIMARY"):
		return self.getFieldName(objType, fldType).keys()

    # used
	def ranContingencyAndPause(self, contingencyName, duration, isReset=False):
		assert isinstance(contingencyName, str)
		assert isinstance(duration, str)
		assert isinstance(isReset, bool)

		objType = "TSCONTINGENCY"
		fldList = self.getFieldNames(objType, "PRIMARY") + ["EndTime", "TimeStep", "UseCyclesForTimeStep"]
		Data = self.getMultipleValues(objType, fldList)
		stepSize = 10
		stepsInCycle = 10
		for data in Data:
			if data[0] == contingencyName:
				assert float(duration) <= float (data[-3])
				stepSize = data[-2]
				stepsInCycle = data[-1]
		ResetStartTime = YES_STATUS if isReset else NO_STATUS
		NumberOfTimeStepsToDo = 0
		print stepSize
		print stepsInCycle
		executableScript = "TSRunUntilSpecifiedTime(%s, [%s, %s, %s, %s, %s]);"%(contingencyName, duration, stepSize, stepsInCycle, ResetStartTime, NumberOfTimeStepsToDo)

		startTime = time.time()
		self.executeScriptStetmnts(executableScript)
		finishTime = time.time()
		return finishTime - startTime

    # used
	def getContingencyResults(self, contingencyName, fldList, startTime="", finishedTime="", isTranspose=False):
		assert isinstance(contingencyName, str)
		assert isinstance(fldList, list)
		assert isinstance(startTime, str)
		assert isinstance(finishedTime, str)
		assert isinstance(IsTranspose, bool)
		results = self.saco.TSGetContingencyResults(contingencyName, fldList, startTime, finishedTime)
		self.errorOccured(results[0])

		# Output[1][:][0] is ObjectType, Output[1][:][1] is PrimaryKey, Output[1][:][4] is VariableName
		# See www.powerworld.com/WebHelp/Content/html/TSGetContingencyResults%20Function.htm
		Header = ["TSTimeInSeconds"] + ["%s %s %s"%(h[0], h[1], h[4]) for h in results[1]]
		print results[1]
		Data = [map(float, data) for  data in results[2]]
		if IsTranspose:
			return Header, np.array(Data).T.tolist()
		else:
			return Header, Data
	def getFieldName(self, objType, fldType="PRIMARY"):
		assert isinstance(objType, str)
		assert isinstance(fldType, str)
		fldType = fldType.upper()

		# Generate object field
		if not objType in PW_OBJECTS:
			fieldList = self._GetFieldNameList(objType)
			tmp = str(fieldList[0])
			if tmp != "": raise Exception(tmp)

			newObj = {}
			for field in fieldList[1]:
				tmp = [o.encode("ascii", "ignore") for o in field]
				newObj[tmp[1]] = {"type":tmp[0], "fmt":tmp[2], "desc":tmp[3]}

			PW_OBJECTS[objType] = newObj

		Object = PW_OBJECTS[objType]

		if fldType == "PRIMARY":
			return {name:Object[name] for name in Object if re.match("^\*.*[1-9].*\*$", Object[name]["type"])}

		elif fldType == "SECONDARY":
			return {name:Object[name] for name in Object if re.match("^\*.*[A-Z].*\*$", Object[name]["type"])}

		elif fldType == "REQUIRED":
			return {name:Object[name] for name in Object if re.match("^\*.*\*$", Object[name]["type"])}

		elif fldType == "ALL":
			return {name:Object[name] for name in Object if re.match(".*", Object[name]["type"])}

		elif fldType == "TSSAVEOPTION":
			return {name:Object[name] for name in Object if re.match("TSSave.*", name)}

		elif fldType == "TSRESULT":
			return {name:Object[name] for name in Object if re.match("TS.*", name) and not re.match("TSSave.*", name)}
		else:
			raise Exception("FieldType\"%s\" not found." % fldType)
	def errorOccured(self, Msg):
		errorMsg = str(Msg)
		print errorMsg;
		if errorMsg != "":
			raise Exception(errorMsg)
	def connectToPW(self):
		cc.GetModule(POWER_WORLD_EXE_PATH)
		self.saco = cc.CreateObject("pwrworld.SimulatorAuto") # create SimAuto COM Object
	def connectToPW(self, POWER_WORLD_EXE):
		cc.GetModule(POWER_WORLD_EXE)
		self.saco = cc.CreateObject("pwrworld.SimulatorAuto") # create SimAuto COM Object
	def _SaveCurrentState(self):
		return self.saco.SaveState()
	def _GetFieldNameList(self, objType):
		return self.saco.GetFieldList(objType)
	def _getMultiPrameters(self, ObjType, fldList, flterName):
		return self.saco.GetParametersMultipleElement(ObjType, fldList, flterName)
	def disconnect(self):
		self.saco = None
	def openCaseFile(self, fldName):
		assert isinstance(fldName, str)
		results = self.saco.OpenCase(fldName)
		self.errorOccured(results[0])
	def saveCaseFile(self, fldName):
		assert isinstance(fldName, str)
		results = self.saco.SaveCase(fldName, "PWB", "Yes")
		self.errorOccured(results[0])
	def closeCaseFile(self):
		results = self.saco.CloseCase()
		self.errorOccured(results[0])
	def executeScriptStetmnts(self, stetmnts):
		assert isinstance(stetmnts, str)
		results = self.saco.RunScriptCommand(stetmnts)
		self.errorOccured(results[0])
	def getMultipleValues(self, objType, fldList, isTranspose=False):
		assert isinstance(objType, str)
		assert isinstance(fldList, list)
		assert isinstance(isTranspose, bool)

		results = self._getMultiPrameters(objType, fldList, "")
		self.errorOccured(results[0])

		Data = [map(str, output) for output in results[1]]
		return Data if isTranspose else np.array(Data).T.tolist()
	def processAuxScriptFile(self, fileName):
		assert isinstance(fileName, str)
		results = self.saco.ProcessAuxFile(fileName)
		self.errorOccured(results[0])
