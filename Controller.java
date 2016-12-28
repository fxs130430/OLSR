/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author asus
 */
public class Controller {
    
    public static final int                        MAX_NODE_COUNT = 10;
    private BufferedReader[]                m_arrBuffered_reader;
    private PrintWriter[]                   m_arrPrint_writer;
    private String[]                        m_arrFromURL;
    private String[]                        m_arrToURL;
    private ArrayList<Controller_Record>    m_lstCurrentLinkStatus;
    private ArrayList<Controller_Record>    m_lstLoadedStatus;
    public Controller() throws IOException  
    {
        m_arrBuffered_reader = new BufferedReader[MAX_NODE_COUNT];
        m_arrPrint_writer = new PrintWriter[MAX_NODE_COUNT];
        m_arrFromURL = new String[MAX_NODE_COUNT];
        m_arrToURL = new String[MAX_NODE_COUNT];
        m_lstCurrentLinkStatus = new ArrayList<>();
        m_lstLoadedStatus = new ArrayList<>();
        
        for(int i = 0 ; i < MAX_NODE_COUNT ; i++)
        {
            m_arrFromURL[i] = "From"+ String.valueOf(i) + ".txt";
            m_arrToURL[i] = "To"+ String.valueOf(i) + ".txt";
            m_arrBuffered_reader[i] = new BufferedReader(new FileReader(m_arrFromURL[i]));
            m_arrPrint_writer[i] = new PrintWriter(new BufferedWriter(new FileWriter(m_arrToURL[i],false)));
            for(int j = 0 ; j < MAX_NODE_COUNT ; j++)
            {
                if( i != j)
                {
                    m_lstCurrentLinkStatus.add(new Controller_Record(0, i, j, false));
                }
            }
        }
        //--> Load Scenario of Links from file
        BufferedReader buff_reader = new BufferedReader(new FileReader("topology.txt"));
        String str;
        while((str = buff_reader.readLine()) != null)
        {
            str = str.trim();
            if(str.equals(""))
                continue;
            String[] arrTemp = str.split("\\s+");
            if(arrTemp.length != 4)
            {
                System.out.println("Invalid Format in topology.txt");
                System.exit(-1);
            }
            int Time = Integer.parseInt(arrTemp[0]);
            boolean connected = false;
            if(arrTemp[1].equals("UP"))
                connected = true;
            int Source = Integer.parseInt(arrTemp[2]);
            int Dest = Integer.parseInt(arrTemp[3]);
            m_lstLoadedStatus.add(new Controller_Record(Time, Source, Dest, connected));
        }
        buff_reader.close();
        //<--
    }
    
    private void UpdateAllLinkStatus(int x_nCurrentTime)
    {
        m_lstCurrentLinkStatus.clear();
        for(int i = 0 ; i < m_lstLoadedStatus.size() ; i++)
        {
            if(m_lstLoadedStatus.get(i).Time <= x_nCurrentTime)
            {
                int Source = m_lstLoadedStatus.get(i).Source_LinkId;
                int Dest = m_lstLoadedStatus.get(i).Dest_LinkId;
                boolean bConnected = m_lstLoadedStatus.get(i).bConnected;
                UpdateLink(Source, Dest, bConnected);
            }
        }
    }
    private void UpdateLink(int x_nSource, int x_nDest,boolean x_bConn)
    {
        boolean bFound = false;
        for(int i = 0 ; i < m_lstCurrentLinkStatus.size() ; i++)
        {
            if(m_lstCurrentLinkStatus.get(i).Source_LinkId == x_nSource && m_lstCurrentLinkStatus.get(i).Dest_LinkId == x_nDest)
            {
                m_lstCurrentLinkStatus.get(i).bConnected = x_bConn;
                bFound = true;
            }
        }
        if(!bFound)
            m_lstCurrentLinkStatus.add(new Controller_Record(0, x_nSource, x_nDest, x_bConn));
    }
    
    public void DoOneCycle(int x_nCycleNo) throws IOException
    {
        UpdateAllLinkStatus(x_nCycleNo);
        //--> Read and Deliver Messages
        for(int i = 0 ; i < MAX_NODE_COUNT ; i++)
        {
            String str;
            while((str = m_arrBuffered_reader[i].readLine()) != null)
            {
                if(str.startsWith("*")) // It's a broadcast message
                {
                    BroadcastMessage(i, str);
                }
                else // DataMessage
                {
                    DataMessage data_message = new DataMessage(str);
                    UnicastMessage(data_message);                    
                }
            }
        }        
    }
    private void BroadcastMessage(int x_nSender, String x_strMessage)
    {
        for(int i = 0 ; i < MAX_NODE_COUNT ; i++)
        {
            if( i != x_nSender && IsConnected(x_nSender, i))
            {
                m_arrPrint_writer[i].println(x_strMessage);
                m_arrPrint_writer[i].flush();
            }
        }
    }
    private void UnicastMessage(DataMessage x_dataMessage)
    {       
        int nSource = x_dataMessage.nFromId;
        int nDest = x_dataMessage.nNextHopId;
        if(IsConnected(nSource, nDest))
        {
            System.out.println(x_dataMessage.getMessage());
            m_arrPrint_writer[nDest].println(x_dataMessage.getMessage());
            m_arrPrint_writer[nDest].flush();
        }
    }
    
    private boolean IsConnected(int x_nSource, int x_nDest)
    {
        boolean bConnected = false;
        for(int i = 0 ; i < m_lstCurrentLinkStatus.size() ; i++)
        {
            if(m_lstCurrentLinkStatus.get(i).Source_LinkId == x_nSource && m_lstCurrentLinkStatus.get(i).Dest_LinkId == x_nDest)
            {
                bConnected = m_lstCurrentLinkStatus.get(i).bConnected;
                break;
            }
        }
        return bConnected;
    }
    public void Close() throws IOException
    {
        for(int i = 0 ; i < MAX_NODE_COUNT ; i++)
        {
            m_arrBuffered_reader[i].close();
            m_arrPrint_writer[i].close();
        }
    }
    
}
