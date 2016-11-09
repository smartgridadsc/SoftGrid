//package it.adsc.smartpower.substation.monitoring.ui.message;
//
//import com.mysql.jdbc.Connection;
//
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
///**
// * Created by prageethmahendra on 22/7/2016.
// */
//public class Response {
//    private int date;
//    private int hour;
//    private int min;
//    private int sec;
//    private int milis;
//    private String messageType;
//    private int typeId;
//    private String commandType;
//    private String description;
//    private String comString;
//    private int sequence;
//
//    public void save(Connection conn)
//    {
//        String query = " insert into RESPONSE (SEQ, DATE, HOUR, MIN, SEC,  MILIS,MESSAGE_TYPE, TYPE_ID, COMMAND_TYPE, DESCRIPTION,  COM_STRING,OTHER ) values (?,?,?,?,? ,?,?,?,?,? ,?,?)";
//
//        // create the mysql insert preparedstatement
//        try {
//            PreparedStatement preparedStmt = conn.prepareStatement(query);
//
//            int count = 1;
//            preparedStmt.setInt(count++, sequence);
//            preparedStmt.setInt(count++, date);
//            preparedStmt.setInt(count++, hour);
//            preparedStmt.setInt(count++, min);
//            preparedStmt.setInt(count++, sec);
//            preparedStmt.setInt(count++, milis);
//            preparedStmt.setString(count++, messageType);
//            preparedStmt.setInt(count++, typeId);
//            preparedStmt.setString(count++, commandType);
//            preparedStmt.setString(count++, description);
//            preparedStmt.setString(count++, comString);
//            preparedStmt.setString(count++, "");
//
//            // execute the preparedstatement
//            preparedStmt.execute();
//            preparedStmt.close();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public String getComString() {
//        return comString;
//    }
//
//    public void setComString(String comString, int sequence) {
//        this.comString = comString;
//        if (comString != null) {
//            String[] breakdown = comString.split(" ");
//            date = Integer.parseInt(breakdown[0].substring(1));
//            String[] time = breakdown[1].split(":");
//            hour = Integer.parseInt(time[0]);
//            min = Integer.parseInt(time[1]);
//            sec = Integer.parseInt(time[2].substring(0,2));
//            milis = Integer.parseInt(time[2].substring(3,6));
//            messageType = breakdown[3].substring(0,8);
//            typeId = Integer.parseInt(breakdown[5].split(",")[0]);
//            commandType = breakdown[6].split(",")[0];
//            description = "";
//            for (int i = 7; i < breakdown.length; i++) {
//                description = description +" " +breakdown[i];
//            }
//        }
//        this.sequence = sequence;
//    }
//
//    public int getSequence() {
//        return sequence;
//    }
//
//    public void setSequence(int sequence) {
//        this.sequence = sequence;
//    }
//
//    public int getDate() {
//        return date;
//    }
//
//    public void setDate(int date) {
//        this.date = date;
//    }
//
//    public int getHour() {
//        return hour;
//    }
//
//    public void setHour(int hour) {
//        this.hour = hour;
//    }
//
//    public int getMin() {
//        return min;
//    }
//
//    public void setMin(int min) {
//        this.min = min;
//    }
//
//    public int getSec() {
//        return sec;
//    }
//
//    public void setSec(int sec) {
//        this.sec = sec;
//    }
//
//    public int getMilis() {
//        return milis;
//    }
//
//    public void setMilis(int milis) {
//        this.milis = milis;
//    }
//
//    public String getMessageType() {
//        return messageType;
//    }
//
//    public void setMessageType(String messageType) {
//        this.messageType = messageType;
//    }
//
//    public int getTypeId() {
//        return typeId;
//    }
//
//    public void setTypeId(int typeId) {
//        this.typeId = typeId;
//    }
//
//    public String getCommandType() {
//        return commandType;
//    }
//
//    public void setCommandType(String commandType) {
//        this.commandType = commandType;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String toCSV()
//    {
//        return date + "," + hour+","+(min<10?""+min : min)+","+(sec < 10 ? ""+sec : sec)+","+(milis<10?""+milis : milis< 100 ? ""+milis : milis)+"," + "" + messageType +","+ typeId+","+commandType+","+ description;
//    }
//
//    @Override
//    public String toString() {
//        return "[0" + date + " " + hour+":"+(min<10?"0"+min : min)+":"+(sec < 10 ? "0"+sec : sec)+"."+(milis<10?"00"+milis : milis< 100 ? "0"+milis : milis)+"] " + "-> " + messageType +":Type ID: "+ typeId+", "+commandType+", "+ description;
//    }
//}
