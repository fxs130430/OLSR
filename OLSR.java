/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author asus
 */
public class OLSR {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException {
        // TODO code application logic here
        /*
        Graph g = new Graph();
        g.addEdge("6", "0");
        g.addEdge("6", "7");
        g.addEdge("0", "1");
        g.addEdge("0", "2");
        g.addEdge("0", "5");
        g.addEdge("7", "8");
        g.addEdge("1", "0");
        g.addEdge("2", "0");
        g.addEdge("5", "0");
        g.addEdge("6", "0");
        g.addEdge("1", "7");
        g.addEdge("6", "7");
        g.addEdge("8", "7");
        g.addEdge("0", "1");
        g.addEdge("4", "1");
        g.addEdge("7", "1");
        g.addEdge("0", "2");
        g.addEdge("3", "2");        
        AllPaths ap = new AllPaths(g,"6", "5");
        ArrayList<String> lst = ap.getPaths();
        int nNextHop = -1;
        for(int i = 0 ; i < lst.size() ; i++)
        {
            System.out.println(lst.get(i));
            String[] arrStr = lst.get(i).split(",");
            if(arrStr.length > 1)
            {
                if(arrStr[0].equals(String.valueOf(3)))
                    nNextHop = Integer.parseInt(arrStr[1].trim());
            }
        }
        
        
        
        
        System.exit(0);
        
        */
        
        
        
        if(args.length < 1)
        {
            System.out.println("Invalid number of arguments...");
            System.exit(-1);
        }
        if(args[0].equals("filemaker"))
        {
            for(int i = 0 ; i < Controller.MAX_NODE_COUNT ; i++)
            {
                File f_To   = new File("To"+i+".txt");
                File f_From = new File("From"+i+".txt");
                File f_Data = new File(i+"Received.txt");
                f_To.delete();
                f_From.delete();
                f_Data.delete();
                f_To.createNewFile();
                f_From.createNewFile();                
            }
        }
        if(args[0].equals("ctlr"))
        {
            System.out.println("Controller is Running...");
            Controller ctlr = new Controller();
            int i = 0 ;
            while ( i < 140)
            {
                ctlr.DoOneCycle(i);
                i++;
                Thread.sleep(1000);
            }
            System.out.println("Controller is finishing...");
            ctlr.Close();
            System.out.println("Controller finished...");
        }
        else if(args[0].equals("router"))
        {
            int nNodeId = Integer.parseInt(args[1]);
            int nDestinationForData = Integer.parseInt(args[2]);
            String strDataToSend = "";
            int nTimeToSend = -1;
            if(nDestinationForData != nNodeId)
            {
                strDataToSend = args[3];
                nTimeToSend = Integer.parseInt(args[4]);
            }
            
            IOHandler IO_handler = new IOHandler(nNodeId);
            MPRManager mpr = new MPRManager(nNodeId, IO_handler);
            if(nTimeToSend > -1)
                mpr.SetDataTransmissionParameters(nTimeToSend, nDestinationForData, strDataToSend);
            int i = 0;
            System.out.println("Node "+nNodeId+" is Running...");
            while( i < 120)
            {
                //System.out.println("Node "+nNodeId+", cycle "+i);
            
                ArrayList<Object> lstMessages = IO_handler.ReadMessages();
                mpr.HandleReceivedMessages(lstMessages);
                if( i % 5 == 0)
                {
                    mpr.SendHelloMessage();
                }
                if(i % 10 == 0)
                {
                    mpr.SendTCMessage();
                }
                mpr.OnUpdateState(i);
                i++;
                Thread.sleep(1000);
            }
            IO_handler.CloseFiles(); 
            System.out.println("Node "+nNodeId+" exiting...");
        }
    }
}
