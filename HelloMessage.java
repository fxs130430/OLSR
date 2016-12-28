/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.regex.*;

/**
 *
 * @author asus
 */
public class HelloMessage {
    
    public ArrayList<Integer> m_lstUniDirectionalNeighbors;
    public ArrayList<Integer> m_lstBiDirectionalNeighbors;
    public ArrayList<Integer> m_lstMPRs;
    public int m_nSenderId;
    public HelloMessage()
    {
        m_nSenderId = -1;
        m_lstBiDirectionalNeighbors = new ArrayList<Integer>();
        m_lstUniDirectionalNeighbors = new ArrayList<Integer>();
        m_lstMPRs = new ArrayList<>();
    }
    public HelloMessage(String x_strHello)
    {
        m_nSenderId = -1;
        m_lstBiDirectionalNeighbors = new ArrayList<Integer>();
        m_lstUniDirectionalNeighbors = new ArrayList<Integer>();
        m_lstMPRs = new ArrayList<>();
        FillFromString(x_strHello);
    }
 
    public String getMessage()
    {
        String msg="* ";
        msg += "<" + m_nSenderId + ">";
        msg += " HELLO UNIDIR ";
        /*
        if(m_lstUniDirectionalNeighbors.size() == 0)
            m_lstUniDirectionalNeighbors.add(-1);
        if(m_lstBiDirectionalNeighbors.size() == 0)
            m_lstBiDirectionalNeighbors.add(-1);
        if(m_lstMPRs.size() == 0)
            m_lstMPRs.add(-1);
        */
        
        
        for(int i = 0 ; i < m_lstUniDirectionalNeighbors.size() ; i++)
        {
            msg +="<";
            msg+= m_lstUniDirectionalNeighbors.get(i);
            msg += ">";
        }
        msg += " BIDIR ";
        for(int i = 0 ; i < m_lstBiDirectionalNeighbors.size() ; i++)
        {
            msg +="<";
            msg+= m_lstBiDirectionalNeighbors.get(i);
            msg += ">";
        }
        msg += " MPR ";
        for(int i = 0 ; i < m_lstMPRs.size() ; i++)
        {
            msg +="<";
            msg+= m_lstMPRs.get(i);
            msg += ">";
        }
        return msg;
    }
    public void FillFromString(String x_strHello)
    {
        Pattern p = Pattern.compile("(<\\d+>)|HELLO UNIDIR|BIDIR|MPR");
        Matcher m = p.matcher(x_strHello);
        String strWhichPart = "SENDER ID";
        while(m.find())
        {
            if(m.group().equals("HELLO UNIDIR"))
            {
                strWhichPart = "HELLO UNIDIR";
            }
            else if(m.group().equals("BIDIR"))
            {
                strWhichPart = "BIDIR";
            }
            else if(m.group().equals("MPR"))
            {
                strWhichPart = "MPR";
            }
            else
            {
                int nId = extractIntFromTag(m.group());
                if (nId >= 0)
                {                         
                    switch(strWhichPart)
                    {
                        case "SENDER ID":
                        {
                            m_nSenderId = nId;
                            break;
                        }
                        case "HELLO UNIDIR":
                        {
                            if(!m_lstUniDirectionalNeighbors.contains(nId))
                                m_lstUniDirectionalNeighbors.add(nId);
                            break;
                        }
                        case "BIDIR":
                        {
                            if(!m_lstBiDirectionalNeighbors.contains(nId))
                                m_lstBiDirectionalNeighbors.add(nId);
                            break;
                        }
                        case "MPR":
                        {
                            if(!m_lstMPRs.contains(nId))
                                m_lstMPRs.add(nId);
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
