/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author asus
 */
public class IOHandler {
    
    public int m_nNode;
    public final String m_strURLInbox;
    public final String m_strURLOutbox;
    BufferedReader buffered_reader;
    PrintWriter print_writer;
    PrintWriter print_writer_datafile;

    public IOHandler(int x_nNode) throws FileNotFoundException, IOException
    {
        m_nNode = x_nNode;
        m_strURLInbox  = "To" + m_nNode + ".txt";
        m_strURLOutbox = "From" + m_nNode + ".txt";
        buffered_reader = new BufferedReader(new FileReader(m_strURLInbox));
        print_writer = new PrintWriter(new BufferedWriter(new FileWriter(m_strURLOutbox, false)));
        print_writer_datafile = new PrintWriter(new BufferedWriter(new FileWriter(m_nNode+"Received.txt",false)));
        
    }
    public void StoreReceivedMessage(DataMessage x_DataMessage)
    {
        print_writer_datafile.println(x_DataMessage.getMessage());
        print_writer_datafile.flush();
    }
    public void SendHelloMessage(HelloMessage x_Hello)
    {
       print_writer.println(x_Hello.getMessage());
       print_writer.flush();
    }
    public void SendDataMessage(DataMessage x_DataMassage)
    {
        System.out.println("DataMessage: "+x_DataMassage.getMessage());
        print_writer.println(x_DataMassage.getMessage());
        print_writer.flush();
    }
    public void SendTCMessage(TCMessage x_TCMessage)
    {
        print_writer.println(x_TCMessage.getMessage());
        print_writer.flush();
    }
    public ArrayList<Object> ReadMessages() throws IOException
    {
        String str;
        ArrayList<Object> lstMessages = new ArrayList<>();
        while((str = buffered_reader.readLine()) != null)
        {
            if(IsHelloMessage(str))
            {
                lstMessages.add(new HelloMessage(str));
            }
            else if(IsTCMessage(str))
            {
                lstMessages.add(new TCMessage(str));
            }
            else if(IsDataMessage(str)) 
            {
                lstMessages.add(new DataMessage(str));
            }
        }
        return lstMessages;
    }
    private boolean IsHelloMessage(String x_strMessage)
    {
        if(x_strMessage.indexOf("HELLO UNIDIR") != -1)
           return true;
        else
            return false;
    }
    private boolean IsTCMessage(String x_strMessage)
    {
        if(x_strMessage.indexOf("TC") != -1)
           return true;
        else
            return false;        
    }
    private boolean IsDataMessage(String x_strMessage)
    {
        if(x_strMessage.indexOf("DATA") != -1)
           return true;
        else
            return false;  
    }
    public void CloseFiles() throws IOException
    {
        buffered_reader.close();
        print_writer.close();
        print_writer_datafile.close();
    }
}
