/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author asus
 */
public class DataMessage {
    //<nxthop>  <fromnbr> DATA <srcnode> <dstnode> <string>
    public int nSourceId;
    public int nNextHopId;
    public int nFromId;
    public int nDestId;
    public String strMessage;
    public DataMessage(int x_nSrc,int x_nNxtHopId,int x_nFrom,int x_nDestId,String x_strMessage)
    {
        nSourceId = x_nSrc;
        nNextHopId = x_nNxtHopId;
        nFromId = x_nFrom;
        nDestId = x_nDestId;
        strMessage = x_strMessage;
    }
    public DataMessage(String x_StrMessage)
    {
        FillFromString(x_StrMessage);
    }
    public String getMessage()
    {
        String msg="";
        msg += "<" + nNextHopId + ">";
        msg += "<" + nFromId + ">";
        msg += " DATA ";
        msg += "<" + nSourceId + ">";
        msg += "<" + nDestId + ">";
        msg += "<" + strMessage + ">";
        return msg;        
    }
    public void FillFromString(String x_strDataMessage)
    {
        int index_start_text  = x_strDataMessage.lastIndexOf("<");
        int index_finish_text = x_strDataMessage.lastIndexOf(">");
        strMessage = x_strDataMessage.substring(index_start_text + 1, index_finish_text);
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(x_strDataMessage);
        boolean bFormatOK = true;
        if(m.find())
        {
            nNextHopId = Integer.parseInt(m.group());
        }
        else
        {
            bFormatOK = false;
        }
        if(m.find())
        {    
            nFromId = Integer.parseInt(m.group());
        }
        else
        {
            bFormatOK = false;
        }
        if(m.find())
        {
            nSourceId = Integer.parseInt(m.group());
        }
        else
        {
            bFormatOK = false;
        }
        if(m.find())
        {    
            nDestId = Integer.parseInt(m.group());
        }
        else
        {
            bFormatOK = false;
        }
        if(!bFormatOK)
        {
            System.out.println("Invalid DataMessage Format...");
            System.exit(-1);
        }
    }
}
