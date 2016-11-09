# from __future__ import division
#
# import os
# import sys
# import csv
# import plotly
#
# csvpath = ""
# resultMainpath = ""
# lib_path = os.path.abspath('..\\pypw')
# experimentNo = 0
# deviceCountIncrementAmmount = 10
# duration = 60.0
# sys.path.append(lib_path)
# generate = 1
# summery = 1
# plot = 0
# experimentCount = 100
# import pypw
#
# def generateSummeryComparisonPlots():
#     import plotly.plotly as py
#     import plotly.graph_objs as go
#     resultPaths = []
#         # "C:\\EMA\\Experiments\\12-04-2016\\ContingencyAux_NO_Mitigation RANDOM 1\\result\\",
#         # "C:\\EMA\\Experiments\\12-04-2016\\ContingencyAux_NO_Mitigation RANDOM 2_20Exp 0.7MIT\\result\\",
#         # "C:\\EMA\\Experiments\\12-04-2016\\ContingencyAux_NO_Mitigation RANDOM 1_50Exp_Fixed5\\result\\",
#         # "C:\\EMA\\Experiments\\12-04-2016\\ContingencyAux_NO_Mitigation RANDOM 5_50Exp\\result\\",
#         # "C:\\EMA\\Experiments\\12-04-2016\\ContingencyAux_NO_Mitigation RANDOM 1_50Exp\\result\\",
#         # "C:\\EMA\\Experiments\\12-04-2016\\ContingencyAux_RB_Mitigation_5DUB_0.02RAN\\result\\",
#         # "C:\\EMA\\Experiments\\12-04-2016\\ContingencyAux_RB_Mitigation_5DUB_0.02RAN 9REPO_50Exp\\result\\",
#         # "C:\\EMA\\Experiments\\12-04-2016\\ContingencyAux_RB_Mitigation_5DUB_0.02RAN 6REPO\\result\\",
#         # "C:\\EMA\\Experiments\\12-04-2016\\ContingencyAux_RB_Mitigation_5DUB_0.02RAN 9REPO\\result\\",
#         # "C:\\EMA\\Experiments\\12-04-2016\\ContingencyAux_RB_Mitigation_2.5DUB_0.02RAN 2MIT\\result\\",
#         # "C:\\EMA\\Experiments\\12-04-2016\\ContingencyAux_RB_Mitigation_2.5DUB_0.02RAN 3MIT\\result\\"]
#     experimentTypes = []
#         # "NO Mitigation RANDOM 1",
#         # "NO_Mitigation RANDOM 2_20Exp 0.7MIT",
#         # "NO Mitigation RANDOM 5 50 EXP Fixed5",
#         # "NO Mitigation RANDOM 5 50 EXP",
#         # "NO Mitigation RANDOM 1 50 EXP",
#         # "RB Mitigation with 5 DUB and 0.02RAN",
#         # "RB Mitigation with 5 DUB and 0.02RAN 9 REPO 50 EXP",
#         # "RB Mitigation with 5 DUB and 0.02RAN 6 REPO",
#         # "RB Mitigation with 5 DUB and 0.02RAN 9 REPO",
#         # "RB Mitigation with 5 DUB and 0.02RAN 2MIT",
#         # "RB Mitigation with 5 DUB and 0.02RAN 3MIT"]
#     experimentCounts =[]#20,50,50,50,20,50,20,20,20,20]
#     # resultPaths = ["C:\\EMA\\New\\smartpower\\SmartPower\\ContingencyAux\\result\\"]
#     # experimentTypes = ["test"]
#     # Sign in to plotly
#     resultPath = "C:\\EMA\\Experiments\\29-04-16-temp\\"
#     for dirs in os.listdir(resultPath):
#         dirName = str(dirs)
#         # if(dirName.find("7Index") == -1 and dirName.find("0Index") == -1):
#         print dirName[:14]
#         if dirName[:14].find("ContingencyAux") != -1:
#             resultPaths.append(resultPath + dirName + "\\result\\")
#             experimentTypes.append(dirName[13:])
#             experimentCounts.append(experimentCount)
#     py.sign_in('prageeth', 'el57a7v46j') # Replace the username, and API key with your credentials.
#     compFilePath = resultPath+"comparisons\\"
#     files = os.listdir(resultPaths[0])
#     files.append("OFViolationCount_F.csv")
#     files.append("UFViolationCount_F.csv")
#     for file in files:
#         filename = str(file)
#         print filename
#         avg_data=[]
#         pathCounter = 0
#         isFrequency = 0
#         with open(compFilePath + filename, "wb") as compFileWriter:
#             for resultPath in resultPaths:
#                 filePath = resultPath + filename
#                 if(isFrequency == 1 or not filename[-6:].find("_F.csv") == -1):
#                     isFrequency = 1
#                     filePath = resultPath + filename[:-6] + ".csv"
#                 with open(filePath, "rb") as reader:
#                     x = []
#                     avg_y = []
#                     csvReader = csv.reader(reader)
#                     for row in csvReader:
#                         if str(row[0]) is "ALLP":
#                             x.append(100)
#                         else:
#                             x.append(row[0][:-1])
#                         total = 0
#                         for value in row:
#                             if value is not row[0]:
#                                 if isFrequency == 1:
#                                     if float(str(value)) > 0:
#                                         total = total + 1
#                                 else:
#                                     total = total + float(str(value))
#                         avg_y.append(total/experimentCounts[pathCounter])
#                     chartName = filename + "_Avg_" + experimentTypes[pathCounter]
#                     compFileWriter.write(chartName+"_X = " + str(x) +"\n")
#                     compFileWriter.write(chartName+"_Y = " + str(avg_y) +"\n")
#                     avg_data.append(go.Scatter(x=x, y = avg_y, name =chartName ))
#                 pathCounter = pathCounter + 1
#         avg_layout = go.Layout(title = filename)#,legend = list(x = 50, y = 0))
#         avg_fig = go.Figure(data = avg_data , layout = avg_layout)
#         py.image.save_as(avg_fig, filename=(filename+ ".png"))
#         plotly.offline.plot({"data": avg_data,"layout": avg_layout})
#
#
# def generateSummeryPlots():
#     import plotly.plotly as py
#     import plotly.graph_objs as go
#     resultPath = "..\\..\\ContingencyAux\\result\\"
#     # Sign in to plotly
#     py.sign_in('prageeth', 'el57a7v46j') # Replace the username, and API key with your credentials.
#     for file in os.listdir(resultPath):
#         filename = str(file)
#         with open(resultPath + filename, "rb") as headerFileReader:
#             chartList = []
#             x = []
#             avg_y = []
#
#
#             header = headerFileReader.next()
#             i = 0
#             for column in header:
#                 with open(resultPath + filename, "rb") as rowFileReader:
#                     # rowFileReader.next() # Discard the header
#                     reader = csv.reader(rowFileReader)
#                     y = []
#                     for row in reader:
#                         if i is 0:
#                             total = 0
#                             if str(row[i]) is "ALLP":
#                                 x.append(experimentCount)
#                             else:
#                                 x.append(row[i][:-1])
#                             for value in row:
#                                 if value is not row[0]:
#                                     total = total + float(str(value))
#                             avg_y.append(total/experimentCount)
#                         else:
#                             y.append(row[i])
#
#                 if i is not 0:
#                     chartList.append(go.Scatter(x = x, y=y, name = str(i)))
#                     # print x
#                     # print y
#                 # if i is 2:
#                 #     break;
#                 i = i + 1
#                 if i == experimentCount:
#                     break
#             # fo all plots of all the experiments
#             # layout = go.Layout(title = filename)
#             # fig = go.Figure(data = chartList, layout = layout)
#             # py.image.save_as(fig, filename=(filename+ ".png"))
#             # plotly.offline.plot({"data": chartList,"layout": layout})
#             # for plot of avarage values
#             avg_layout = go.Layout(title = filename)
#             avg_data = [go.Scatter(x=x, y = avg_y, name = "Avg")]
#             avg_fig = go.Figure(data = avg_data , layout = avg_layout)
#             py.image.save_as(avg_fig, filename=(filename+ ".png"))
#             plotly.offline.plot({"data": avg_data,"layout": avg_layout})
#
#
#     # # Create a simple chart..
#     # trace1 = go.Scatter(x=[2, 4, 6], y= [10, 12, 15],  name='yaxis data')
#     # trace2 = go.Scatter(x=[2, 4, 10], y= [10, 56, 15],   name='yaxis2 data')
#     # data = [trace1, trace2]
#     # layout = go.Layout( title='fileName')
#     # # layout = go.Layout(title='A Simple Plot', width=800, height=640)
#     # fig = go.Figure(data=data, layout=layout)
#     # py.image.save_as(fig, filename=(filename+ ".png"))
#
#
# def writeToFile(fileName, Header, Data):
#     with open(csvpath + fileName, 'w') as target:
#         column = str(Header)
#         column = column[1:]
#         column =column[:-1]
#         target.write(str(column))
#         target.write("\n")
#         for dataline in Data:
#             data = str(dataline)
#             data = data[1:]
#             data = data[:-1]
#             target.write(data)
#             target.write("\n")
#
# def processFrequencyCSV(type):
#     if(summery is 0):
#         return
#
#     resultPath = resultMainpath + type + "ViolationCount.csv"
#
#     percentage = 0
#     percentageString = ""
#     resultString = ""
#     with open(resultPath, "w") as writeFile:
#         while(percentage < 100):
#             overFlowList = []
#             underFlowList = []
#             fileCount = -1
#             percentage = percentage + deviceCountIncrementAmmount
#             if percentage is 100:
#                 percentageString = "ALLP"
#             else:
#                 percentageString = str(percentage) + "P"
#             resultString = resultString + percentageString + ","
#             for file in os.listdir(csvpath):
#                 filename = str(file)
#                 if not filename.startswith(percentageString):
#                     continue
#                 experimentNo = filename.split('_')[-3][:-4]
#                 print "experimentNo = " + experimentNo
#                 isvalid = ("BUS_FREQ.csv" in filename and (type is "OF" or type is "UF"))
#                 isvalid = isvalid or ("BUS_VOLT.csv" in filename and (type is "UV5" or type is "UV20" or type is "OV5" or type is "OV20"))
#                 isvalid = isvalid or ("BRANCH_MWTO.csv" in filename and type is "BranchLimit" )
#                 isvalid = isvalid or ("LOAD_MW.csv" in filename and (type is "UnservedLoad" or type is "Incomplete"))
#                 if isvalid:
#                     fileCount = fileCount + 1
#                     overFlowList.append(0)
#                     underFlowList.append(0)
#                     with open(csvpath + filename, 'rb') as f:
#                         reader = csv.reader(f)
#                         header = reader.next()
#                         i = 0
#                         for column in header:
#                             if i is not 0 or type is "Incomplete":
#                                 if type is "Incomplete":
#                                     underFlowList[fileCount] = 1
#                                 with open(csvpath + filename, 'rb') as tempF:
#                                     tempReader = csv.reader(tempF)
#                                     tempReader.next() # discard the header
#                                     firstLoad = 0
#                                     lastLoad = 0
#                                     if type is "UnservedLoad":
#                                         row = tempReader.next()
#                                         firstLoad = float(str(row[i]))
#                                     for row in tempReader:
#                                         if type is "UOF" and (float(str(row[i])) <= 59.5 or float(str(row[i])) >= 60.5):
#                                             underFlowList[fileCount] = underFlowList[fileCount] + 1
#                                             break
#                                         if type is "UF" and float(str(row[i])) <= 59.5:
#                                             underFlowList[fileCount] = underFlowList[fileCount] + 1
#                                             break
#                                         if type is "OF" and float(str(row[i])) >= 60.5:
#                                             underFlowList[fileCount] = underFlowList[fileCount] + 1
#                                             break
#                                         if type is "BranchLimit" and float(str(row[i])) >= 100.0:
#                                             underFlowList[fileCount] = underFlowList[fileCount] + 1
#                                             break
#                                         if type is "UV5" and float(str(row[i])) <= 0.95:
#                                             underFlowList[fileCount] = underFlowList[fileCount] + 1
#                                             break
#                                         if type is "OV5" and float(str(row[i])) >= 1.05:
#                                             underFlowList[fileCount] = underFlowList[fileCount] + 1
#                                             break
#                                         if type is "UV20" and float(str(row[i])) <= 0.8:
#                                             underFlowList[fileCount] = underFlowList[fileCount] + 1
#                                             break
#                                         if type is "OV20" and float(str(row[i])) >= 1.20:
#                                             underFlowList[fileCount] = underFlowList[fileCount] + 1
#                                             break
#                                         if type is "Incomplete" and float(str(row[i])) == duration and i == 0:
#                                             underFlowList[fileCount] = 0
#                                             break
#                                         if type is "UnservedLoad":
#                                             lastLoad = float(str(row[i]))
#                                     if type is "UnservedLoad" and  (firstLoad - lastLoad) > 0:
#                                         if (firstLoad - lastLoad) > 0:
#                                             underFlowList[fileCount] = underFlowList[fileCount] + firstLoad - lastLoad
#                             if(type is "Incomplete"):
#                                 break
#                             i = i + 1
#             for value in underFlowList:
#                 resultString = resultString + str(value) +","
#             resultString = resultString[:-1]+"\n"
#             writeFile.write(resultString)
#             resultString = ""
#         print resultString
#          # for column in reader:
#             # for item in column:
#             #     print item
#             #     print "\n"
# PW_FILE = "%s\\%s"%(os.getcwd(), "..\\..\\GSO_37Bus_dm.PWB")
# loadrank = []
# mainExperimentPath = "C:\\EMA\\Demo\\smartpower\\SmartPower\\auxFile\\"
# temp = ["abc","dec"]
# print temp
# # mainExperimentPath = "..\\..\\ContingencyAux\\"
# for dirs in os.listdir(mainExperimentPath):
#     # try:
#         filename = str(dirs)
#         dirName = str(dirs) +"\\"
#
#         if filename[:13].find("ContingencyAux") and dirName.find("result") == -1 and dirName.find("csv") == -1 and generate == 1:
#             csvpath = mainExperimentPath+dirName+"csv\\"
#             resultMainpath = mainExperimentPath+dirName+"result\\"
#             for file in os.listdir(mainExperimentPath + dirName ):
#                 filename = str(file)
#                 if filename.find("result") == -1 and filename.find("csv") == -1 and generate == 1:
#                     print filename
#                     # AUX_FILE = "%s\\%s"%(os.getcwd(), mainExperimentPath+dirName+file)
#                     AUX_FILE = "%s\\%s"%(mainExperimentPath,dirName+file)
#                     sa = pypw.SimAuto()
#                     sa.Connect()
#                     sa.OpenCase(PW_FILE)
#                     print "loading aux file..."
#                     print sa.ProcessAuxFile(AUX_FILE)
#                     print ""
#                     print "runing contingency..."
#                     print sa.TSRunAndPause("OPEN_ALL", "20")
#                     print""
#                     # Get average system frequency
#                     # FieldList = ["Bus %d | Frequency" % 1]
#
#                     # get the header and data for the generator fieldlist
#                     # Header, Data = sa.TSGetContingencyResults(pypw.TS_DEFAULT_CTG,FieldList, str(Time[i]), str(Time[i+1]))
#                     # file = file[:-4]
#
#                     print "loading Bus frequency data..."
#                     FieldList = ["Plot \'BUS_FREQ\'"]
#                     Header, Data = sa.TSGetContingencyResults("OPEN_ALL", FieldList)
#                     writeToFile(file + "_BUS_FREQ.csv", Header, Data)
#
#                     print "loading Bus voltage data..."
#                     FieldList = ["Plot \'BUS_VOLT\'"]
#                     Header, Data = sa.TSGetContingencyResults("OPEN_ALL", FieldList)
#                     writeToFile(file + "_BUS_VOLT.csv", Header, Data)
#
#                     print "loading Bus branch MW at To bus data..."
#                     FieldList = ["Plot \'BRANCH_MWTO\'"]
#                     Header, Data = sa.TSGetContingencyResults("OPEN_ALL", FieldList)
#                     writeToFile(file + "_BRANCH_MWTO.csv", Header, Data)
#
#                     print "loading Bus Load MW data..."
#                     FieldList = ["Plot \'LOAD_MW\'"]
#                     Header, Data = sa.TSGetContingencyResults("OPEN_ALL", FieldList)
#                     writeToFile(file + "_LOAD_MW.csv", Header, Data)
#
#                     print "loading Bus Generator MW data..."
#                     FieldList = ["Plot \'GEN_MW\'"]
#                     Header, Data = sa.TSGetContingencyResults("OPEN_ALL", FieldList)
#                     writeToFile(file + "_GEN_MW.csv", Header, Data)
#
#                     # loadrankValue = sa._GetParametersSingleElement( "PWCaseInformation", ["OverloadRank"], [""] )
#                     # print loadrankValue
#                     # loadrank.append(loadrankValue)
#
#                     # print "loading Overload Rank data..."
#                     # FieldList = ["PWCaseInformation | OverloadRank"]
#                     # Header, Data = sa.TSGetContingencyResults("OPEN_ALL", FieldList)
#                     # print Header
#                     # print Data
#                     # print Header
#                     # print Data
#                     # ObjectType = "TSCONTINGENCY"
#                     # print sa.GetField(ObjectType, "ALL")
#                     sa.Disconnect()
#     # except:
#     #     print "Error ignored...!"
# # writeToFile(file + "_LOAD_RANK.csv","", loadrank)
#
#             processFrequencyCSV("OF")
#             processFrequencyCSV("UF")
#             processFrequencyCSV("BranchLimit")
#             processFrequencyCSV("UV5")
#             processFrequencyCSV("UV20")
#             processFrequencyCSV("OV5")
#             processFrequencyCSV("OV20")
#             processFrequencyCSV("Incomplete")
#             processFrequencyCSV("UnservedLoad")
# if plot is 1:
#     # generateSummeryPlots()
#     generateSummeryComparisonPlots()
#
#
