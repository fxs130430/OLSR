/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;

/**
 *
 * @author asus
 */
public class RoutingTable {
    
    private int   m_nNodeId;
    private Graph m_Graph;
    
    public RoutingTable(int x_nNodeId)
    {
        m_nNodeId = x_nNodeId;
        m_Graph = new Graph();
        for(int i = 0 ; i < Controller.MAX_NODE_COUNT ;i++)
            m_Graph.addVertex(String.valueOf(i));
    }
    public void AddLink(int x_nId1,int x_nId2)
    {
        String strId1 = String.valueOf(x_nId1);
        String strId2 = String.valueOf(x_nId2);
        m_Graph.addEdge(strId1, strId2);
    }
    public ArrayList<String> getPathToDestination(int x_nDestId)
    {
        AllPaths ap = new AllPaths(m_Graph, String.valueOf(m_nNodeId), String.valueOf(x_nDestId));
        return ap.getPaths();
    }
    public void ClearTable()
    {
        m_Graph = new Graph();
        for(int i = 0 ; i < Controller.MAX_NODE_COUNT ;i++)
            m_Graph.addVertex(String.valueOf(i));
    }
    public int getNextHopToThisDestination(int x_nDest)
    {
        ArrayList<String> lst = getPathToDestination(x_nDest);
        int nNextHop = -1;
        
        int nMaxHopCount = 99;
        for(int i = 0 ; i < lst.size() ; i++)
        {
            String[] arrStr = lst.get(i).split(",");
            
            if(arrStr.length > 1)
            {
                if(arrStr[0].equals(String.valueOf(m_nNodeId)))
                {
                    if(arrStr.length < nMaxHopCount) // Try to find the shortest path
                    {
                        nMaxHopCount = arrStr.length;
                        nNextHop = Integer.parseInt(arrStr[1].trim());
                    }
                }
            }
        }
        return nNextHop;
    }    
}
