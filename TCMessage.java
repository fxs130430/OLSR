/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author asus
 */
public class TCMessage {
    public int m_nOriginatorId;
    public int m_nSenderId;
    public int m_nSeqNo;
    public ArrayList<Integer> m_lstMS;
    
    public TCMessage(int x_nOriginator, int x_nSenderId,int x_nSeqNo)
    {
        m_nOriginatorId = x_nOriginator;
        m_nSenderId = x_nSenderId;
        m_nSeqNo = x_nSeqNo;
        m_lstMS = new ArrayList<>();
    }
    public TCMessage(String x_str)
    {
        m_nOriginatorId = -1;
        m_nSenderId = -1;
        m_nSeqNo = -1;
        m_lstMS = new ArrayList<>();
        FillFromString(x_str);
    }
    public String getMessage()
    {
        String msg="* ";
        msg += "<" + m_nSenderId + ">";
        msg += " TC ";
        msg += "<" + m_nOriginatorId + ">";
        msg += "<" + m_nSeqNo + ">";
        msg += " MS ";
        for(int i = 0 ; i < m_lstMS.size() ; i++)
        {
            msg +="<";
            msg+= m_lstMS.get(i);
            msg += ">";
        }
        return msg;
    }
    public void FillFromString(String x_strHello)
    {
        Pattern p = Pattern.compile("(<\\d+>)|TC|MS");
        Matcher m = p.matcher(x_strHello);
        String strWhichPart = "fromnbr";
        boolean bSRCRead = false;
        while(m.find())
        {
            if(m.group().equals("TC"))
            {
                strWhichPart = "TC";
            }
            else if(m.group().equals("MS"))
            {
                strWhichPart = "MS";
            }
            else
            {
                int nId = extractIntFromTag(m.group());
                if (nId >= 0)
                {                         
                   switch(strWhichPart)
                   {
                       case "fromnbr":
                       {
                           m_nSenderId = nId;
                           break;
                       }
                       case "TC":
                       {
                           if(!bSRCRead)
                           {
                               bSRCRead = true;
                               m_nOriginatorId = nId;
                           }
                           else
                           {
                               m_nSeqNo = nId;
                           }
                           break;
                       }
                       case "MS":
                       {
                           if(!m_lstMS.contains(nId))
                               m_lstMS.add(nId);
                           break;
                       }
                   }
                }
            }
        }
    }
    private int extractIntFromTag(String x_strTag)
    {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(x_strTag);
        int n = -1;
        try
        {
            if(m.find())
            {
                n = Integer.parseInt(m.group(0));
            }
        }
        catch(Exception ex)
        {
            
        }
        return n;
    }
}
