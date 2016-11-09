# from __future__ import division
#
# import os
#
# import sys
#
# import time
#
# import csv
#
# import numpy as np
#
# import matplotlib.pyplot as plt
#
# lib_path = os.path.abspath('..\\pypw')
#
# sys.path.append(lib_path)
#
# import pypw
#
#
#
# # ##########################################
#
# # From Kundur pg. 607:					   #
#
# # ACE_1 = A_1 \Delta P_{12} + B_1 \Delta f #
#
# # ##########################################
#
#
#
# # -------------- Model Setting ------------------------------------------------
#
#
#
# # Nominal frequency
#
# FREQUENCY = 60.0
#
# # The 37-Bus System is divided into 3 areas
#
# AREA = [1, 2, 3]
#
# # Buses at each area
#
# BUS = {1: [3, 5, 14, 18, 20, 30, 32, 33, 34, 37, 41, 44, 50],
#
#        2: [1, 10, 12, 13, 17, 19, 28, 29, 31, 35, 38, 39, 40, 56],
#
#        3: [15, 16, 21, 24, 27, 47, 48, 53, 54, 55]}
#
# # Tie Line is used to connect an area to another
#
# # First 4 are between area 1 and area 2
#
# # The following 3 are between area 2 and area 3
#
# # The last one is between area 1 abd area 3
#
# # come from Explore->Aggregations->Tielines Between Area
#
# TIELINE = [(3, 40, 1), (12, 18, 1), (32, 29, 1), (29, 41, 1),
#
#            (12, 27, 1), (13, 55, 1), (39, 47, 1),
#
#            (24, 44, 1)]
#
# TIELINE_NOMINAL = np.array([20.31944656, 37.58109665, -66.40814209, 42.53021622,
#
#                             37.71585083, 11.84276867, 68.13191223,
#
#                             -50.39382172])
#
# # Load Number of Bus and ID and MW
#
# # The consumption of the load
#
# LOAD = {(12, 1): 22.9,   (15, 1): 58.2, (48, 1): 55.8, (5, 1): 14.0,
#
#         (54, 1): 12.43,  (18, 1): 45.0, (44, 1): 59.8, (21, 1): 74.4,
#
#         (34, 1): 22.743, (14, 1): 22.2, (24, 1): 36.3, (37, 1): 27.0,
#
#         (30, 1): 23.4,   (27, 1): 20.0, (53, 1): 59.5, (20, 1): 15.3,
#
#         (17, 1): 32.8,   (10, 1): 16.8, (56, 1): 14.0, (33, 1): 28.0,
#
#         (13, 1): 23.0,   (3, 1): 12.3,  (16, 1): 57.8, (55, 1): 22.65,
#
#         (19, 1): 18.3}
#
# # Number of Bus, ID
#
# GEN = [(14, 1), (28, 1), (28, 2), (31, 1), (44, 1), (48, 1), (50, 1), (53, 1),
#
#        (54, 1)]
#
# # Transient Stability -> Summary -> Generator Mode Use -> Gen MW / MVA BASE
#
# GOV_INPUT_SETPOINT = {(14, 1): 0.25, (28, 1): 0.83333337307,
#
#                       (28, 2): 0.83333337307, (31, 1): 0.299679249525,
#
#                       (44, 1): 0.9375, (48, 1): 0.280701756477,
#
#                       (50, 1): 0.447058796883, (53, 1): 0.933333337307,
#
#                       (54, 1): 0.654140233994}
#
# # Generator mechanical input
#
# GEN_MECH_INPUT = [10.0, 150.0, 150.0, 74.9198150634766, 150.0, 16.0, 38.0,
#
#                   140.0, 75.2261276245117]
#
# # AGC generator at a specific area
#
# AGC_GEN = {1: [(50, 1), (14, 1)],
#
#            2: [(31, 1), (28, 1)],
#
#            3: [(54, 1), (48, 1)]}
#
# ALL_GEN = {1: [(50, 1), (14, 1), (44, 1)],
#
#            2: [(31, 1), (28, 1), (28, 2)],
#
#            3: [(54, 1), (48, 1), (53, 1)]}
#
# # Get the sum of load demand in a particular area
#
# # This is the nominal value
#
# AREA_DEMAND_NOMINAL = {area: sum([LOAD[load] for load in LOAD if load[0] in BUS[area]]) for area in AREA}
#
# # define the FROM TO relation
#
# FROMTO = [12, 23, 31]
#
# # describe the direction of power flow
#
# AREA_FROMTO = {12: np.array([1, -1, 1, -1, 0, 0, 0, 0]),
#
#                23: np.array([0, 0, 0, 0, 1, 1, 1, 0]),
#
#                31: np.array([0, 0, 0, 0, 0, 0, 0, 1])}
#
# # calculate the export direction from each area to each area
#
# # the export direction is the difference of area export
#
# AREA_EXPORT = {1: AREA_FROMTO[12] - AREA_FROMTO[31],
#
#                2: AREA_FROMTO[23] - AREA_FROMTO[12],
#
#                3: AREA_FROMTO[31] - AREA_FROMTO[23]}
#
# # calculate the exported power value for each area
#
# AREA_EXPORT_NOMINAL = {area: np.dot(TIELINE_NOMINAL, AREA_EXPORT[area]) for area in AREA}
#
# # stores the path to the PowerWorld Case file
#
# PW_FILE = "%s\\%s" % (os.getcwd(), "GSO_37Bus.pwb")
#
#
#
#
#
# def open_tie_line():
#
#     """
#
#     Open an important tieline to see the effect on the power system
#
#     :return: None
#
#     """
#
#     # create the FieldList with "LineStatus" field
#
#     FieldList = sa.GetFieldList("branch", "PRIMARY") + ["LineStatus"]
#
#     # get the current data value and change a particular to open
#
#     output = sa.GetData("branch", FieldList)
#
#     for i in range(len(output)):
#
#         if i == 49:
#
#             output[i][-1] = str("Open")
#
#     sa.SetData("branch", FieldList, output)
#
#     # output = sa.GetData("branch", FieldList)
#
#
#
#
#
# def close_tie_line():
#
#     """
#
#     Close an important tieline to see the effect on the power system
#
#     :return: None
#
#     """
#
#
#
#     # create the FieldList with "LineStatus" field
#
#     FieldList = sa.GetFieldList("branch", "PRIMARY") + ["LineStatus"]
#
#     # get the current data value and choose a particular to close
#
#     output = sa.GetData("branch", FieldList)
#
#     for i in range(len(output)):
#
#         if i == 49:
#
#             output[i][-1] = str("Close")
#
#     sa.SetData("branch", FieldList, output)
#
#     # output = sa.GetData("branch", FieldList)
#
#
#
#
#
# def change_load(mean_input):
#
#     FieldList = sa.GetFieldList("LOAD", "REQUIRED")
#
#     FieldList = [FieldList[1], FieldList[3], FieldList[0]]
#
#     output = sa.GetData("LOAD", FieldList)
#
#     for i in range(len(output)):
#
#         output[i][2] = str(float(output[i][2]) + mean_input)
#
#
#
#     sa.SetData("LOAD", FieldList, output)
#
#
#
#
#
# def set_power(mean_input):
#
#     with open(GEN_FILE_NAME % mean_input, 'rb') as f:
#
#         reader = csv.reader(f)
#
#         list_temp = list(reader)
#
#     for i in range(len(list_temp[0])):
#
#         gen_new_power[i] = float(list_temp[0][i])
#
#
#
#     print "gen_new_power for mean = %d" % mean_input
#
#     print gen_new_power
#
#
#
#     FieldList = sa.GetFieldList("GEN", "REQUIRED")
#
#     # BusNum, BusID, GenMW GenMWMin, GENMWMax
#
#     FieldList = [FieldList[2], FieldList[10], FieldList[9], FieldList[4], FieldList[1]]
#
#
#
#     output = sa.GetData("GEN", FieldList)
#
#
#
#     MVA = [40, 180, 180, 250, 160, 57, 85, 150, 115]
#
#     # 50, 14, 31, 28.1, 54, 48
#
#
#
#     for i, gen in enumerate(GEN):
#
#         GOV_INPUT_SETPOINT[gen] = gen_new_power[i]/MVA[i]
#
#         if GOV_INPUT_SETPOINT[gen] > 1:
#
#             print "The GOV_INPUT_SETPOINT is larger than 1.0"
#
#             print "gen = %d, gov_input_setpoint = %f" %(gen[0], GOV_INPUT_SETPOINT[gen])
#
#
#
#     for i in range(9):
#
#         output[i][2] = str(gen_new_power[i])
#
#         output[i][3] = str(0)
#
#         output[i][4] = str(1000)
#
#
#
#     sa.SetData("GEN", FieldList, output)
#
#
#
#
#
# def open_tie_line_test():
#
#     """
#
#     Open an important tieline to see the effect on the power system
#
#     :return: None
#
#     """
#
#
#
#     # create the FieldList with "LineStatus" field
#
#     FieldList = sa.GetFieldList("branch", "PRIMARY") + ["LineStatus"]
#
#     # get the current data value and change a particular to open
#
#     output = sa.GetData("branch", FieldList)
#
#     for i in range(len(output)):
#
#         if i == 49:
#
#             output[i][-1] = str("Open")
#
#     sa.SetData("branch", FieldList, output)
#
#     # output = sa.GetData("branch", FieldList)
#
#
#
#
#
# def RunAGC(DELAY, EndTime, logFile):
#
#     # 0, AGC_TIME_STEP, 2*AGC_TIME_STEP and so on
#
#     Time = np.arange(0, EndTime, AGC_TIME_STEP)
#
#
#
#     # ACE for each area at each time step
#
#     Ace = {area: [0 for t in Time] for area in AREA}
#
#     # AGC for each generator at each time step
#
#     Agc = {gen: [0 for t in Time] for area in AREA for gen in AGC_GEN[area]}
#
#
#
#     # make a copy of load dictionary
#
#     CurrentLoad = dict(LOAD)
#
#
#
#     # ############# Log Section ################################
#
#     # demand for each area at each timestep
#
#     LogDemand = {area: [0 for t in Time] for area in AREA}
#
#     # frequency at each timestep
#
#     LogFrequency = [0 for t in Time]
#
#     # tieline value for each tieline at each timestep
#
#     LogTieline = {tieline: [0 for t in Time] for tieline in TIELINE}
#
#     # export value for each area at each timestep
#
#     LogExport = {area: [0 for t in Time] for area in AREA}
#
#     # mechanical power for each generator at each time step
#
#     LogGenPMech = {gen: [0 for t in Time] for gen in GEN}
#
#     LogGenPMech2 = {gen: [0 for t in Time] for gen in GEN}
#
#
#
#     # ############ Open the case file ###############
#
#     sa.OpenCase(PW_FILE)
#
#
#
#     # ############ Change system settings ####################
#
#     set_power(mean)
#
#     change_load(mean)
#
#
#
#     # Set new simulation end time
#
#     ObjectType = "TSCONTINGENCY"
#
#     FieldList = sa.GetFieldList(ObjectType, "PRIMARY") + ["EndTime"]
#
#     Data = sa.GetData(ObjectType, FieldList)
#
#     Data[0][-1] = str(EndTime)
#
#     sa.SetData(ObjectType, FieldList, Data)
#
#
#
#     # get the current time
#
#     startTime = time.time()
#
#
#
#     ################################################
#
#     # 1. Create events at time step i			   #
#
#     # 2. Run simulation from time step i to i+1	   #
#
#     # 3. Obtain results at time step i+1		   #
#
#     ################################################
#
#     if AGC_ENABLED:
#
#         AGC_CYCLE = 40
#
#         delay_time_list = np.arange(DELAY, len(Time), AGC_CYCLE)
#
#         print "delay_time_list"
#
#         print delay_time_list
#
#
#
#     # i will change from 0 to 3998 inclusively
#
#     for i in range(len(Time)-1):
#         # ####################################### Load Variation #####################################################
#         # for each i, the load is changing
#         # this simulates the changing of load from time to time
#         for l in LOAD:
#             if i < 200:
#                 newLoad = LOAD[l]
#             else:
#                 newLoad = LOAD[l] * LOAD_CHANGE_PERCENTAGE
#             changeBy = newLoad - CurrentLoad[l]
#             sa.TSCreateEvent(pypw.TS_DEFAULT_CTG, str(Time[i]), "LOAD %d %d" % (l[0], l[1]), pypw.TS_LOAD_CHANGEBY_VAL % changeBy)
#             CurrentLoad[l] = newLoad
#         ########################################### AGC ##############################################################
#         if AGC_ENABLED:
#             # Calculate AGC every 0.1*AGC_CYCLEs and apply it with and without delay
#             if i % AGC_CYCLE == 0:
#                 print "i = %d, AGC calculation here" %i
#                 for area in AREA:
#                     for gen in AGC_GEN[area]:
#                         Agc[gen][i] = GOV_INPUT_SETPOINT[gen] - 2 * AGC_GAIN * AGC_TIME_STEP * sum(Ace[area])
#             if i < 0:
#                 if i % AGC_CYCLE == 0:
#                     print "i = %d, Apply AGC without delay" %i
#                     for area in AREA:
#                         for gen in AGC_GEN[area]:
#                             genID = "GEN %d %d" % (gen[0], gen[1])
#                             sa.TSCreateEvent(pypw.TS_DEFAULT_CTG, str(Time[i]), genID, pypw.TS_GOV_SETPOINT_VAL % Agc[gen][i])
#             elif i >= 0:
#                 if i in delay_time_list:
#                     print "i = %d, Apply AGC with delay = %d" %(i, DELAY)
#                     for area in AREA:
#                         for gen in AGC_GEN[area]:
#                             genID = "GEN %d %d" % (gen[0], gen[1])
#                             sa.TSCreateEvent(pypw.TS_DEFAULT_CTG, str(Time[i]), genID, pypw.TS_GOV_SETPOINT_VAL % Agc[gen][i - DELAY])
#         # #############################################################################################################
#         # Run the simulation from i to i + 1
#         sa.TSRunAndPause(pypw.TS_DEFAULT_CTG, str(Time[i+1]))
#         # Get average system frequency
#         FieldList = ["GEN %d %d | TSGenW" % (gen[0], gen[1]) for gen in GEN]
#         # get the header and data for the generator fieldlist
#         # Header, Data = sa.TSGetContingencyResults(pypw.TS_DEFAULT_CTG,FieldList, str(Time[i]), str(Time[i+1]))
#         Header, Data = sa.TSGetContingencyResults(pypw.TS_DEFAULT_CTG, FieldList)
#         # Take *second last* value if StartTime/StopTime is used
#         frequency = np.mean(Data[-2][1:])
#         # calculate the frequency change
#         dFrequency = frequency - FREQUENCY
#         # set the changed frequency to the log array
#         LogFrequency[i+1] = dFrequency
#         # Get tieline flow
#         FieldList = ["BRANCH %d %d %d | TSACLineFromP" % (tieline[0], tieline[1], tieline[2]) for tieline in TIELINE]
#         # get the TS result based on the fieldlist
#         Header, Data = sa.TSGetContingencyResults(pypw.TS_DEFAULT_CTG, FieldList)
#
#         # Take *second last* value if StartTime/StopTime is used
#
#         # store the tie line value
#
#         Tieline = np.array(Data[-2][1:])
#
#         # the following is for attack
#
#         # create different attack array based on the settings
#
#         #Attack = np.array([0.0 for tieline in TIELINE])
#
#
#
#         # set tieline log
#
#         for j, tieline in enumerate(TIELINE):
#
#             #LogTieline[tieline][i+1] = (Tieline + Attack - TIELINE_NOMINAL)[j]
#
#             LogTieline[tieline][i+1] = (Tieline - TIELINE_NOMINAL)[j]
#
#
#
#         # Calculate area control error for each area
#
#         for area in AREA:
#
#             # calculate the export value
#
#             export = np.dot(Tieline, AREA_EXPORT[area])
#
#             # calculate the export difference
#
#             dExport = export - AREA_EXPORT_NOMINAL[area]
#
#             # get the ace value for the particular area
#
#             Ace[area][i+1] = ACE_TIELINE_GAIN * dExport + ACE_FREQ_GAIN * dFrequency
#
#             # and store the value in the log array
#
#             LogExport[area][i+1] = dExport
#
#
#
#             # Obtain area demand fluctuation
#
#             FieldList = ["LOAD %d %d | TSLoadP" % (load[0], load[1]) for load in LOAD if load[0] in BUS[area]]
#
#             # first get the load demand
#
#             Header, Data = sa.TSGetContingencyResults(pypw.TS_DEFAULT_CTG, FieldList)
#
#             # then store the area demand fluctuation
#
#             LogDemand[area][i+1] = sum(Data[-2][1:]) - AREA_DEMAND_NOMINAL[area]
#
#
#
#         # store the generator mechanical value and store in the log array
#
#         FieldList = ["GEN %d %d | TSGenPMech" % (gen[0], gen[1]) for gen in GEN]
#
#         Header, Data = sa.TSGetContingencyResults(pypw.TS_DEFAULT_CTG, FieldList)
#
#         for j, gen in enumerate(GEN):
#
#             LogGenPMech[gen][i+1] = Data[-2][j+1] - GEN_MECH_INPUT[j]
#
#             LogGenPMech2[gen][i+1] = Data[-2][j+1]
#
#
#
#     # ############## End of simulation part ##################
#
#
#
#     # ############## Log part ###############################################
#
#     # store the above calculate value in the log file in the file system
#
#     if logFile is not None:
#
#         openfile = open(logFile, "w")
#
#         for i in range(len(Time)-1):
#
#             data = [Time[i]]
#
#             # data += [LogDemand[area][i] + AREA_DEMAND_NOMINAL[area]
#
#             # for area in AREA]
#
#             # data += [LogExport[area][i] + AREA_EXPORT_NOMINAL[area]
#
#             # for area in AREA]
#
#             # data += [LogAttack[fromto][i] for fromto in FROMTO]
#
#             data += [LogFrequency[i] + FREQUENCY]
#
#             data += [Agc[gen][i] for gen in AGC_GEN[area] for area in AREA]
#
#             data += [LogGenPMech2[gen][i] for gen in GEN]
#
#
#
#             openfile.write("%s\n" % ",".join(map(str, data)))
#
#         openfile.close()
#
#
#
#     # ############### Plot part #################
#
#     fig = plt.figure()
#
#
#
#     # ############# generator output power ################
#
#     fig.add_subplot(611)
#
#     # FieldList = ["GEN %d %d | TSGenPMech" % (gen[0], gen[1])
#
#     #             for area in AREA for gen in AGC_GEN[area]]
#
#     FieldList = ["GEN %d %d | TSGenPMech" % (gen[0], gen[1])
#
#                  for area in AREA for gen in ALL_GEN[area]]
#
#     Header, Data = sa.TSGetContingencyResults(pypw.TS_DEFAULT_CTG, FieldList, str(0), str(EndTime),IsTranspose=True)
#
#     for i in range(1, len(Data)):
#
#         plt.plot(Data[0][:-1], Data[i][:-1], "-", label=Header[i])
#
#         # plt.legend(loc=3)
#
#         plt.xlim(0, EndTime)
#
#         plt.grid()
#
#
#
#     # ######## Tieline log ######################################
#
#     fig.add_subplot(612)
#
#     for tieline in TIELINE:
#
#         plt.plot(LogTieline[tieline], '-', label="Tieline %s" % str(tieline))
#
#         # plt.legend(loc=2, ncol=2)
#
#         plt.grid()
#
#
#
#     # ######## Export log ####################################
#
#     fig.add_subplot(613)
#
#     for area in AREA:
#
#         plt.plot(LogExport[area], '-')
#
#         plt.grid()
#
#
#
#     # ######## Load log ######################################
#
#     fig.add_subplot(614)
#
#     FieldList = ["LOAD %d %d | TSLoadP" % (load[0], load[1])
#
#                  for load in LOAD]
#
#     Header, Data = sa.TSGetContingencyResults(pypw.TS_DEFAULT_CTG, FieldList, str(0), str(EndTime),IsTranspose=True)
#
#     for i in range(1, len(Data)):
#
#         plt.plot(Data[0][:-1], Data[i][:-1], "-", label=Header[i])
#
#         plt.xlim(0, EndTime)
#
#         # plt.legend(loc=2, ncol=2)
#
#         plt.grid()
#
#
#
#     # ######### ACE ###################################
#
#     fig.add_subplot(615)
#
#     for area in AREA:
#
#         plt.plot(Ace[area], "-", label="Ace area %d" % area)
#
#         # plt.legend(loc=2)
#
#         plt.grid()
#
#
#
#     # ########## Frequeny #########################################
#
#     fig.add_subplot(616)
#
#     plt.plot(LogFrequency, "-k", label="Frequency")
#
#     # plt.legend(loc=2)
#
#     plt.grid()
#
#
#
#     sa.CloseCase()
#
#
#
#     return LogFrequency
#
#
#
#
#
# if __name__ == "__main__":
#
#     """
#
#     @AGC_GAIN: usually changes from 0.2*10^(-4) to 2.1*10^(-4)
#
#                 optimal in terms of settling time 1*10^(-4)
#
#     @LOAD_ADDED: mean value added to each load
#
#     @LOAD_CHANGE_PERCENTAGE: step change percentage for each load
#
#     @DELAY_TIME: the time the agc control signal will be delayed
#
#     """
#
#     try:
#
#         AGC_GAIN = float(sys.argv[1])
#
#         LOAD_ADDED = int(sys.argv[2])
#
#         # 4% load change gives nearest frequency deviation to 0.5 Hz
#
#         # So we can study the delay impact under this case
#
#         LOAD_CHANGE_PERCENTAGE = float(sys.argv[3])
#
#         DELAY_TIME = int(sys.argv[4])
#
#         AGC_ENABLED = sys.argv[5]
#
#     except IndexError:
#
#         print "Usage: python main_paper_command_line.py " \
#
#               "<arg1:AGC_GAIN> <arg2:LOAD_ADDED> <arg3:LOAD_CHANGE_PERCENTAGE> <arg4:DELAY_TIME>"
#
#         sys.exit(1)
#
#
#
#     # ################ File paths and names ######################
#
#     GEN_FILE_NAME = "../code_output/gen_settings/gen_final_settings_%d1.csv"
#
#
#
#     if AGC_ENABLED == "True" or AGC_ENABLED == "true" or AGC_ENABLED == "TRUE":
#
#         AGC_ENABLED = True
#
#     else:
#
#         AGC_ENABLED = False
#
#
#
#     if AGC_ENABLED:
#
#         OUTPUT_FILE_PATH = "../code_output/with_agc/"
#
#         # Naming gain, added_load, load_percentage, delay
#
#         LOG_FILE_NAME = OUTPUT_FILE_PATH + "with_agc_%f_%d_%f_%d.csv"
#
#         image_file_name = OUTPUT_FILE_PATH + "with_agc_%f_%d_%f_%d.png"
#
#     else:
#
#         OUTPUT_FILE_PATH = "../code_output/no_agc/"
#
#         # Naming gain, added_load, load_percentage, delay
#
#         LOG_FILE_NAME = OUTPUT_FILE_PATH + "no_agc_%f_%d_%f_%d.csv"
#
#         image_file_name = OUTPUT_FILE_PATH + "no_agc_%f_%d_%f_%d.png"
#
#
#
#     # ################ Simulation Initialization ####################
#
#     # in seconds
#
#     END_TIME = 400
#
#
#
#     # AGC control setting
#
#     AGC_TIME_STEP = 0.1		# AGC time step in seconds
#
#     AGC_GAIN *= 1           # AGC final gain
#
#     ACE_TIELINE_GAIN = 0.8	# ACE tieline gain
#
#     ACE_FREQ_GAIN = 85		# ACE frequency gain
#
#
#
#     NUM_OF_TIME_STEPS = int(END_TIME / AGC_TIME_STEP)
#
#
#
#     print "AGC_GAIN = {0}".format(AGC_GAIN)
#
#     print "LOAD_ADDED = {0}".format(LOAD_ADDED)
#
#     print "LOAD_CHANGE_PERCENTAGE = {0}".format(LOAD_CHANGE_PERCENTAGE)
#
#     print "DELAY_TIME = {0}".format(DELAY_TIME)
#
#
#
#     # ################ Simulation Begins #############################
#
#     # Create SimAuto COM object
#
#     sa = pypw.SimAuto()
#
#
#
#     MEAN_LOOP_COUNT = 1
#
#     DELAY_LOOP_COUNT = 1
#
#
#
#     for mean in range(LOAD_ADDED, LOAD_ADDED + MEAN_LOOP_COUNT, 1):
#
#         print "The mean {} loop begins".format(mean)
#
#         # some initial settings for generators based on mean value
#
#         gen_new_power = [10 + 13*mean/22, 150 + 26*mean/7, 150 + 26*mean/6,
#
#                          74.9198150634766 + 13*mean/6,  150 + 29*mean/5,
#
#                          16 + 2*mean/6, 38 + 13*mean/7,  140 + 6*mean,
#
#                          75.2261276245117 + 10*mean/7]
#
#
#
#         for delay in range(DELAY_TIME, DELAY_TIME + DELAY_LOOP_COUNT, 1):
#
#             print "The delay {} loop begins".format(delay)
#
#             sa.Connect()
#
#
#
#             RunAGC(delay, END_TIME, logFile=LOG_FILE_NAME % (AGC_GAIN, LOAD_ADDED, LOAD_CHANGE_PERCENTAGE, delay))
#
#             plt.savefig(image_file_name % (AGC_GAIN, LOAD_ADDED, LOAD_CHANGE_PERCENTAGE, delay))
#
#
#
#             sa.Disconnect()
#
# # ######################### END OF FILE ##############################
#
