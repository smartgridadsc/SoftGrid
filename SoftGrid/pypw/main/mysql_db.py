#!/usr/bin/python
import MySQLdb
import datetime
from datetime import datetime, timedelta
sCount = 0

# get connection
def getDB():
    print 'Connecting to the MySQL:trans database...!'
    return MySQLdb.connect(host="localhost",  # your host, usually localhost
                           user="root",  # your username
                           passwd="root",  # your password
                           db="trans")  # name of the data base


# create a connection and keep the connection open
db = getDB()
print 'Connected...!'

def saveData(now, Header, Data):
    # with open(csvpath + fileName, 'w') as target:
    # now = datetime.now()
    # nowTimeString = now.time().strftime('%Y-%m-%d %H:%M:%S:%f')
    # now = now + timedelta(seconds=3)
    # print now.time().strftime('%Y-%m-%d %H:%M:%S:%f')
    column = str(Header)
    global sCount
    sCount = sCount + 1
    column = column[1:]
    column = column[:-1]
    columns = column.split(",")
    for dataline in Data:
        data = str(dataline)
        data = data[1:]
        data = data[:-1]
        cells = data.split(",")
        if (len(cells) == len(columns)):
            count = 0
            for head in columns:
                if (count > 0):
                    # print "saving : time = " + cells[0] + ",id = " + head + ", mvalue" + cells[count]
                    try:
                        actualTime = now + timedelta(seconds=int(cells[0].split(".")[0]),
                                                     microseconds=1000 * int(cells[0].split(".")[1]))
                        # print actualTime.strftime('%Y-%m-%d %H:%M:%S.%f')
                        if (actualTime > datetime.now()):# and cells[count] != '60':
                            cur.execute(
                                "INSERT INTO TRANS_DATA ( SAVE_COUNT, START_TIME, TIME_IN_SEC, DEVICE_ID, MVALUE ) VALUES ( %s,'"
                                + actualTime.strftime('%Y-%m-%d %H:%M:%S.%f') + "', %s,%s,%s) ON DUPLICATE KEY UPDATE MVALUE = %s",
                                (sCount, cells[0], head, cells[count], cells[count]))
                    except MySQLdb.Error, e:
                        print e
                count += 1
    db.commit()
    # cur = db.cursor()
    # now = time.strftime('%Y-%m-%d %H:%M:%S')
    # print now;
    # sql = "INSERT INTO TRANS_DATA ( TIME_IN_SEC, DEVICE_ID, MVALUE ) VALUES ( %f,%s,%f)"
    # sp = 1.2
    # cur.execute("INSERT INTO TRANS_DATA ( START_TIME, TIME_IN_SEC, DEVICE_ID, MVALUE ) VALUES ( DEFAULT, %s,%s,%s)", (1.3,"abc",sp) )
    # db.commit()

def deleteOlderData(depTime):
    print " deleting data older than " + depTime.strftime('%Y-%m-%d %H:%M:%S.%f') + " and the save count > " + str(sCount)
    try:
        cur.execute(
            "DELETE FROM TRANS_DATA WHERE START_TIME < CAST('" + depTime.strftime('%Y-%m-%d %H:%M:%S') + "' as TIME) OR SAVE_COUNT < %s", sCount -1)
    except MySQLdb.Error, e:
        print e
    db.commit()


def deleteOlderNewData(depTime):
    print " deleting data older than " + depTime.strftime('%Y-%m-%d %H:%M:%S.%f')
    try:
        cur.execute(
            "DELETE FROM TRANS_DATA WHERE START_TIME > CAST('" + depTime.strftime('%Y-%m-%d %H:%M:%S') + "' as TIME)")
    except MySQLdb.Error, e:
        print e
    db.commit()


# you must create a Cursor object. It will let
#  you execute all the queries you need
cur = db.cursor()
try:
    sql = 'DROP DATABASE TRANS_DATA'
    cur.execute(sql)
except MySQLdb.Error, e:
    print e

try:
    sql = 'CREATE DATABASE TRANS_DATA'
    cur.execute(sql)
    sql = 'USE TRANS_DATA'
    cur.execute(sql)
except MySQLdb.Error, e:
    print e

print 'Creating new table : TRANS_DATA...!'
try:
    sql = 'CREATE TABLE TRANS_DATA (' \
      'DATA_ID int(11) NOT NULL AUTO_INCREMENT,'\
      'SAVE_COUNT INTEGER NOT NULL,' \
      'START_TIME TIMESTAMP(6) NOT NULL,' \
      'TIME_IN_SEC FLOAT NOT NULL,' \
      'DEVICE_ID VARCHAR(100) NOT NULL,' \
      'MVALUE FLOAT NULL,' \
      'PRIMARY KEY (DATA_ID))'
    cur.execute(sql)
except MySQLdb.Error, e:
    print e
# # Use all the SQL you like
# cur.execute("SELECT * FROM TRANS_DATA")
# # print all the first cell of all the rows
# for row in cur.fetchall():
#     print row[0]
#     print row[1]
#     print row[2]
#     print row[3]
print "DB Initialized...!"
