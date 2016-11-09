from __future__ import division



import os
import sys
import csv
import plotly
import shutil
import time
import datetime


csvpath = ""
resultMainpath = ""
lib_path = os.path.abspath('..\\pypw')
experimentNo = 0
deviceCountIncrementAmmount = 10
duration = 60.0
sys.path.append(lib_path)
generate = 1
summery = 1
plot = 0
experimentCount = 100
startTime = datetime.datetime.now()
import pypw

def generateSummeryComparisonPlots():
    import plotly.plotly as py
    import plotly.graph_objs as go
    resultPaths = []
    experimentTypes = []
    experimentCounts =[]
    # resultPaths = ["C:\\EMA\\New\\smartpower\\SmartPower\\ContingencyAux\\result\\"]
    # experimentTypes = ["test"]
    # Sign in to plotly
    resultPath = "C:\\EMA\\Experiments\\29-04-16-temp\\"
    for dirs in os.listdir(resultPath):
        dirName = str(dirs)
        # if(dirName.find("7Index") == -1 and dirName.find("0Index") == -1):
        print dirName[:14]
        if dirName[:14].find("ContingencyAux") != -1:
            resultPaths.append(resultPath + dirName + "\\result\\")
            experimentTypes.append(dirName[13:])
            experimentCounts.append(experimentCount)
    py.sign_in('prageeth', 'el57a7v46j') # Replace the username, and API key with your credentials.
    compFilePath = resultPath+"comparisons\\"
    files = os.listdir(resultPaths[0])
    files.append("OFViolationCount_F.csv")
    files.append("UFViolationCount_F.csv")
    for file in files:
        filename = str(file)
        print filename
        avg_data=[]
        pathCounter = 0
        isFrequency = 0
        with open(compFilePath + filename, "wb") as compFileWriter:
            for resultPath in resultPaths:
                filePath = resultPath + filename
                if(isFrequency == 1 or not filename[-6:].find("_F.csv") == -1):
                    isFrequency = 1
                    filePath = resultPath + filename[:-6] + ".csv"
                with open(filePath, "rb") as reader:
                    x = []
                    avg_y = []
                    csvReader = csv.reader(reader)
                    for row in csvReader:
                        if str(row[0]) is "ALLP":
                            x.append(100)
                        else:
                            x.append(row[0][:-1])
                        total = 0
                        for value in row:
                            if value is not row[0]:
                                if isFrequency == 1:
                                    if float(str(value)) > 0:
                                        total = total + 1
                                else:
                                    total = total + float(str(value))
                        avg_y.append(total/experimentCounts[pathCounter])
                    chartName = filename + "_Avg_" + experimentTypes[pathCounter]
                    compFileWriter.write(chartName+"_X = " + str(x) +"\n")
                    compFileWriter.write(chartName+"_Y = " + str(avg_y) +"\n")
                    avg_data.append(go.Scatter(x=x, y = avg_y, name =chartName ))
                pathCounter = pathCounter + 1
        avg_layout = go.Layout(title = filename)#,legend = list(x = 50, y = 0))
        avg_fig = go.Figure(data = avg_data , layout = avg_layout)
        py.image.save_as(avg_fig, filename=(filename+ ".png"))
        plotly.offline.plot({"data": avg_data,"layout": avg_layout})


def generateSummeryPlots():
    import plotly.plotly as py
    import plotly.graph_objs as go
    resultPath = "..\\..\\ContingencyAux\\result\\"
    # Sign in to plotly
    py.sign_in('prageeth', 'el57a7v46j') # Replace the username, and API key with your credentials.
    for file in os.listdir(resultPath):
        filename = str(file)
        with open(resultPath + filename, "rb") as headerFileReader:
            chartList = []
            x = []
            avg_y = []


            header = headerFileReader.next()
            i = 0
            for column in header:
                with open(resultPath + filename, "rb") as rowFileReader:
                    # rowFileReader.next() # Discard the header
                    reader = csv.reader(rowFileReader)
                    y = []
                    for row in reader:
                        if i is 0:
                            total = 0
                            if str(row[i]) is "ALLP":
                                x.append(experimentCount)
                            else:
                                x.append(row[i][:-1])
                            for value in row:
                                if value is not row[0]:
                                    total = total + float(str(value))
                            avg_y.append(total/experimentCount)
                        else:
                            y.append(row[i])

                if i is not 0:
                    chartList.append(go.Scatter(x = x, y=y, name = str(i)))
                    # print x
                    # print y
                # if i is 2:
                #     break;
                i = i + 1
                if i == experimentCount:
                    break
            # fo all plots of all the experiments
            # layout = go.Layout(title = filename)
            # fig = go.Figure(data = chartList, layout = layout)
            # py.image.save_as(fig, filename=(filename+ ".png"))
            # plotly.offline.plot({"data": chartList,"layout": layout})
            # for plot of avarage values
            avg_layout = go.Layout(title = filename)
            avg_data = [go.Scatter(x=x, y = avg_y, name = "Avg")]
            avg_fig = go.Figure(data = avg_data , layout = avg_layout)
            py.image.save_as(avg_fig, filename=(filename+ ".png"))
            plotly.offline.plot({"data": avg_data,"layout": avg_layout})


            # # Create a simple chart..
            # trace1 = go.Scatter(x=[2, 4, 6], y= [10, 12, 15],  name='yaxis data')
            # trace2 = go.Scatter(x=[2, 4, 10], y= [10, 56, 15],   name='yaxis2 data')
            # data = [trace1, trace2]
            # layout = go.Layout( title='fileName')
            # # layout = go.Layout(title='A Simple Plot', width=800, height=640)
            # fig = go.Figure(data=data, layout=layout)
            # py.image.save_as(fig, filename=(filename+ ".png"))


def writeToFile(fileName, Header, Data):
    with open(csvpath + fileName, 'w') as target:
        column = str(Header)
        column = column[1:]
        column =column[:-1]
        target.write(str(column))
        target.write("\n")
        for dataline in Data:
            data = str(dataline)
            data = data[1:]
            data = data[:-1]
            target.write(data)
            target.write("\n")

def processFrequencyCSV(type):
    if(summery is 0):
        return
    resultPath = resultMainpath + "ViolationCount.csv"
    with open(resultPath, "a") as writeFile:
        for file in os.listdir(csvpath):
            filename = str(file)
            isvalid = ("BUS_FREQ.csv" in filename and (type is "OF" or type is "UF"))
            isvalid = isvalid or ("BUS_VOLT.csv" in filename and (type is "UV5" or type is "UV20" or type is "OV5" or type is "OV20"))
            isvalid = isvalid or ("BRANCH_MWTO.csv" in filename and type is "BranchLimit" )
            # isvalid = isvalid or "LOAD_MW.csv" in filename
            if isvalid:
                with open(csvpath + filename, 'rb') as f:
                    reader = csv.reader(f)
                    header = reader.next()
                    i = 0
                    for column in header:
                        if i is not 0:
                            with open(csvpath + filename, 'rb') as tempF:
                                tempReader = csv.reader(tempF)
                                tempReader.next() # discard the header
                                value = ""
                                for row in tempReader:
                                    x = time.strptime(row[0],'%S.%f')
                                    newTime = startTime + datetime.timedelta(seconds=x.tm_sec)
                                    violatedValue = int(float(row[i])*100)/100
                                    # if type is "UOF" and (float(str(row[i])) <= 59.5 or float(str(row[i])) >= 60.5):
                                    #     value = type + "," + str(row[i]) + "," +column +",Time" +"," + row[0]
                                    #     break
                                    if type is "UF" and float(str(row[i])) <= 59.5:
                                        value = "Under Frequency :" + str(violatedValue) + ",Time :" + newTime.strftime('%H:%M:%S:%f') +"," +column
                                        break
                                    if type is "OF" and float(str(row[i])) >= 60.5:
                                        value = "Over Frequency :" +  str(violatedValue) + ",Time :" + newTime.strftime('%H:%M:%S:%f') +"," +column
                                        break
                                    if type is "BranchLimit" and float(str(row[i])) >= 100.0:
                                        value = "Branch Limit :" + str(violatedValue) + ",Time :" + newTime.strftime('%H:%M:%S:%f') +"," +column
                                        break
                                    if type is "UV5" and float(str(row[i])) <= 0.95:
                                        value = "Under Voltage(5) :" + str(violatedValue) + ",Time :" + newTime.strftime('%H:%M:%S:%f') +"," +column
                                        break
                                    if type is "OV5" and float(str(row[i])) >= 1.05:
                                        value = "Over Voltage(5) :" + str(violatedValue) + ",Time :" + newTime.strftime('%H:%M:%S:%f') +"," +column
                                        break
                                    if type is "UV20" and float(str(row[i])) <= 0.8:
                                        value = "Under Voltage(20) :" + str(violatedValue) + ",Time :" + newTime.strftime('%H:%M:%S:%f') +"," +column
                                        break
                                    if type is "OV20" and float(str(row[i])) >= 1.20:
                                        value = "Over Voltage(20) :" + str(violatedValue) + ",Time :" + newTime.strftime('%H:%M:%S:%f') +"," +column
                                        break

                                if value != "":
                                    value = value + "\n"
                                    createdTime = lambda: int(round(time.time() * 1000))
                                    writeFile.write(str(createdTime()) +"," + value)
                                    print value
                        i = i + 1
                if type is "UF" or type is "OV20" or type is "BranchLimit":
                    os.remove(csvpath + filename)


logFile = "%s\\%s"%(os.getcwd(), "..\\..\\sample log\\Traslation-2015-1104.txt")
count = 0
MSGCount = 0;
PREtimeString = "";
with open(logFile, "rb") as headerFileReader:
     for abc in headerFileReader:
         if((abc.find("->") > -1 ) and abc.find("TESTFR") == -1 and abc.find("S_FORMAT") == -1 and abc.find("START") == -1):
             timeString = abc[:16]
             timeString = timeString[1:]
             print timeString
             timeString = timeString[:-4]

             if(PREtimeString == timeString):
                 MSGCount = MSGCount+1
             else:
                 # second = timeString[-2:]
                 # min = timeString[:-3]
                 # min = min[-2:]
                 # hour = timeString[:-6]
                 # hour = hour[2:]
                 #
                 # PREsecond = PREtimeString[-2:]
                 # PREmin = PREtimeString[:-3]
                 # PREmin = min[-2:]
                 # PREhour = PREtimeString[:-6]
                 # PREhour = hour[2:]
                 #
                 # if PREhour == hour:
                 #     if PREmin == hour:


                 if(MSGCount > 1):
                    PREtimeString = PREtimeString + "," + str(MSGCount)
                    print PREtimeString + "  "+str(hour) + ":" + str(min) + ":" + str(second)
                 MSGCount = 1
                 PREtimeString = timeString
             count = count + 1
             # if(count == 1000):
             #     break
     # if((abc.find("->") > -1 or abc.find("<-") > -1 ) and abc.find("TESTFR") == -1 and abc.find("S_FORMAT") == -1 and abc.find("START") == -1):




