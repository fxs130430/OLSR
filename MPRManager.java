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
public class MPRManager {
    
    private final int                   m_nNodeId;
    private ArrayList<OneHop_record>    m_lstOneHopNeighbors;
    public ArrayList<TwoHop_record>     m_lstTwoHopNeighbors;
    //My MPR Set
    public ArrayList<Integer>           m_lstMPR;
    // Those who have chosen me as their MPR
    private ArrayList<Integer>          m_lstMPRSelector;
    
    private RoutingTable                m_RoutingTable;
    private TopologyTable               m_TopologyTable;
    private IOHandler                   m_IOHandler;
    
    private int                         m_nSeqNo;
    private int                         m_nCycleNo;
    
    private int                         m_nTimeToSend;
    private DataMessage                 m_DataMessage;
        
    public MPRManager(int x_nNodeId,IOHandler x_IO)
    {
        m_nNodeId = x_nNodeId;
        m_lstOneHopNeighbors = new ArrayList<OneHop_record>();
        m_lstTwoHopNeighbors = new ArrayList<TwoHop_record>();
        m_lstMPR = new ArrayList<Integer>();
        m_lstMPRSelector = new ArrayList<Integer>();
        m_TopologyTable = new TopologyTable(x_nNodeId);
        m_RoutingTable = new RoutingTable(x_nNodeId);
        m_IOHandler = x_IO;
        m_nSeqNo = 0;
        m_nCycleNo = -1;
        m_nTimeToSend = -1;
    }
    public void SetDataTransmissionParameters(int x_nTime,int x_nDestination, String x_strMessage)
    {
        m_nTimeToSend = x_nTime;
        m_DataMessage = new DataMessage(m_nNodeId, m_nNodeId, m_nNodeId, x_nDestination, x_strMessage);
    }
    public void HandleReceivedMessages(ArrayList<Object> x_lstMessages)
    {
        for(int i = 0 ; i < x_lstMessages.size() ; i++)
        {
            if(x_lstMessages.get(i) instanceof HelloMessage)
            {
                OnReceiveHelloMessage((HelloMessage)x_lstMessages.get(i));
            }
            else if(x_lstMessages.get(i) instanceof TCMessage)
            {
                OnReceiveTCMessage((TCMessage)x_lstMessages.get(i));
            }
            else if(x_lstMessages.get(i) instanceof DataMessage)
            {
                OnReceiveDataMessage((DataMessage)x_lstMessages.get(i));
            }
        }
    }
    private void OnReceiveDataMessage(DataMessage x_data)
    {
        if(x_data.nDestId == m_nNodeId)
        {
            m_IOHandler.StoreReceivedMessage(x_data);            
        }
        else if(x_data.nNextHopId == m_nNodeId)
        {
            int nNextHop = m_RoutingTable.getNextHopToThisDestination(x_data.nDestId);
            if(nNextHop >= 0)
            {
                System.out.println("Src("+x_data.nSourceId+"), Dest("+x_data.nDestId+"), RcvdFrom("+x_data.nFromId+"), next hop -> "+nNextHop);
                x_data.nFromId = m_nNodeId;
                x_data.nNextHopId = nNextHop;
                m_IOHandler.SendDataMessage(x_data);
            }
        }
    }
    private void OnReceiveTCMessage(TCMessage x_TCMessage)
    {
        if(x_TCMessage.m_nOriginatorId == m_nNodeId)
        {
            return;
        }
            
        boolean bUseful = m_TopologyTable.OnReceiveTCMessage(x_TCMessage);
        //Shall I broadcast the message?
        if(m_lstMPRSelector.contains(x_TCMessage.m_nSenderId) && bUseful)
        {
            x_TCMessage.m_nSenderId = m_nNodeId;
            m_IOHandler.SendTCMessage(x_TCMessage);
        }
    }
    private void OnReceiveHelloMessage(HelloMessage x_hello)
    {
        if(x_hello.m_nSenderId < 0)
        {
            System.out.println("Invalid SenderId in Hello Message...");
            System.exit(-1);
        }
        //System.out.println(x_hello.getMessage());
        //--> Let's first update the timers
        for(int i = 0 ; i < m_lstOneHopNeighbors.size() ; i++)
        {
            if(x_hello.m_nSenderId == m_lstOneHopNeighbors.get(i).NodeId)
                m_lstOneHopNeighbors.get(i).Timeout = 0;
        }
        
        //<--
        //If my Id exists in its UniDirectional
        if(x_hello.m_lstUniDirectionalNeighbors.contains(m_nNodeId))
        {
            SetOneHopNeighbor(x_hello.m_nSenderId, LinkState.Bidirectional);
            removeFromTwoHopNeighbors(x_hello.m_nSenderId);
        }
        //If my Id exists in its Bidirectional
        else if(x_hello.m_lstBiDirectionalNeighbors.contains(m_nNodeId))
        {
            if(IsMyOneHopUnidirectionalNeighbor(x_hello.m_nSenderId))
            {
                SetOneHopNeighbor(x_hello.m_nSenderId, LinkState.Bidirectional);
                removeFromTwoHopNeighbors(x_hello.m_nSenderId);
            }
            else if(!IsMyOneHopBidirectionalNeighbor(x_hello.m_nSenderId))
            {
                System.out.println("Invalid Node "+x_hello.m_nSenderId+" with my name in its BiDirectional list");
                System.exit(-1);
            }
        }
        else // My Id does not exist
        {
            SetOneHopNeighbor(x_hello.m_nSenderId, LinkState.UniDirectional);
        }
        
        for(int i = 0 ; i < x_hello.m_lstBiDirectionalNeighbors.size() ; i++)
        {
            if(x_hello.m_lstBiDirectionalNeighbors.get(i) == m_nNodeId)
                continue;
            if(IsMyOneHopBidirectionalNeighbor(x_hello.m_nSenderId))
            {
                TwoHop_record rec= new TwoHop_record(x_hello.m_nSenderId, x_hello.m_lstBiDirectionalNeighbors.get(i));
                if(!IsMyOneHopBidirectionalNeighbor(x_hello.m_lstBiDirectionalNeighbors.get(i)))
                {
                    if(!ExistsInTwoHopNeighbors(rec.OneHopNeighborId, rec.TwoHopNeighborId))
                        m_lstTwoHopNeighbors.add(rec);
                }
            }
        }
        //--> I need to check if any two hop neighbor has died already
        for(int i = m_lstTwoHopNeighbors.size() -1 ; i >= 0 ; --i)
        {
            int nId_TwoHop = m_lstTwoHopNeighbors.get(i).TwoHopNeighborId;
            if(x_hello.m_nSenderId == m_lstTwoHopNeighbors.get(i).OneHopNeighborId)
            {
                if(!x_hello.m_lstBiDirectionalNeighbors.contains(nId_TwoHop))
                {
                    m_lstTwoHopNeighbors.remove(i);
                }
            }
        }        
               
        //-->MPR
        if(x_hello.m_lstMPRs.contains(m_nNodeId))
        {
            if(!m_lstMPRSelector.contains(x_hello.m_nSenderId))
                m_lstMPRSelector.add(x_hello.m_nSenderId);
        }
        else
        {
            if(m_lstMPRSelector.contains(x_hello.m_nSenderId))
            {
                int nSenderId = x_hello.m_nSenderId;
                int nIndex = m_lstMPRSelector.indexOf(nSenderId);
                m_lstMPRSelector.remove(nIndex);
            }
        }
        UpdateMPRList();
    }
    
    public void OnUpdateState(int x_nCycleNo)
    {
        //System.out.println(x_nCycleNo);
        m_nCycleNo = x_nCycleNo;
        m_TopologyTable.OnUpdateState();
        for(int i = m_lstOneHopNeighbors.size() -1 ; i >= 0 ; --i)
        {
            m_lstOneHopNeighbors.get(i).Timeout++;
            int OneHopId = m_lstOneHopNeighbors.get(i).NodeId;
            if(m_lstOneHopNeighbors.get(i).Timeout == 15)
            {
                m_lstOneHopNeighbors.remove(i);
                for(int j = m_lstTwoHopNeighbors.size() -1 ; j >= 0 ; --j)
                {
                    if(m_lstTwoHopNeighbors.get(j).OneHopNeighborId == OneHopId)
                        m_lstTwoHopNeighbors.remove(j);
                }
                if(m_lstMPRSelector.contains(OneHopId))
                {
                    m_lstMPRSelector.remove(m_lstMPRSelector.indexOf(OneHopId));
                }
            }
        }
        UpdateMPRList();
        RecalibrateRoutingTable();
        //--> Data Transmission task
        if(m_nCycleNo == m_nTimeToSend)
        {
            int nNextHop = m_RoutingTable.getNextHopToThisDestination(m_DataMessage.nDestId);
            System.out.println("nNextHop = "+nNextHop);
            if( nNextHop >= 0)
            {
                m_DataMessage.nNextHopId = nNextHop;
                m_IOHandler.SendDataMessage(m_DataMessage);
            }
        }
        
        //<--
        //ShowInfo();
        //ShowRoutingTable();
    }
    public void SendHelloMessage()
    {
        //System.out.println("Send Hello Message...");
        m_IOHandler.SendHelloMessage(GetHelloMessage());
    }
    public void SendTCMessage()
    {
        m_nSeqNo++;
        TCMessage tc = new TCMessage(m_nNodeId,m_nNodeId,m_nSeqNo);
        for(int i = 0 ; i < m_lstMPRSelector.size() ; i++)
            tc.m_lstMS.add(m_lstMPRSelector.get(i));
        if(tc.m_lstMS.size() > 0)
        {
            m_IOHandler.SendTCMessage(tc);
        }
    }
    public HelloMessage GetHelloMessage()
    {
        HelloMessage hello = new HelloMessage();
        hello.m_nSenderId = m_nNodeId;
        for(int i = 0 ; i < m_lstOneHopNeighbors.size() ; i++)
        {
            int nId = m_lstOneHopNeighbors.get(i).NodeId;
            if(m_lstOneHopNeighbors.get(i).link_status == LinkState.UniDirectional)
                hello.m_lstUniDirectionalNeighbors.add(nId);
            if(m_lstOneHopNeighbors.get(i).link_status == LinkState.Bidirectional)
                hello.m_lstBiDirectionalNeighbors.add(nId);
        }       
        for(int i = 0 ; i < m_lstMPR.size() ; i++)
        {
            int nMPRId = m_lstMPR.get(i);
            hello.m_lstMPRs.add(nMPRId);
        }
        return hello;
    }
    
    private void UpdateMPRList()
    {
        m_lstMPR.clear();
        ArrayList<Integer> lstTwoHopNeighborsId = getAllTwoHopNeighbors();
        
        //--> These should be added anyway, because there is no other choices
        ArrayList<Integer> lstOneHopCoverIsolated = getMPR_For_Isolated_TwoHopNeighbors();
        for(int i = 0 ; i < lstOneHopCoverIsolated.size() ; i++)
            m_lstMPR.add(lstOneHopCoverIsolated.get(i));
        //<--
        
        ArrayList<Integer> lstTwoHopCoveredByIsolation = getTwoHopNeighborSetOf(lstOneHopCoverIsolated);       
        lstTwoHopNeighborsId = Subtract(lstTwoHopNeighborsId, lstTwoHopCoveredByIsolation);
        
        
        ArrayList<Integer> lstAlreadyCovered = new ArrayList<Integer>(lstTwoHopCoveredByIsolation);
        while(lstTwoHopNeighborsId.size() > 0)
        {
            ArrayList<Integer> lstNonMPR = getNonMPRNeighbors();
            int nMaxCovered = 0;
            int nMaxId = -1;
            for(int i = 0 ; i < lstNonMPR.size() ; i++)
            {
                int Id = lstNonMPR.get(i);
                int nCount = 0;
                for(int j = 0 ; j < m_lstTwoHopNeighbors.size() ; j++)
                {
                    int TwoHopId = m_lstTwoHopNeighbors.get(j).TwoHopNeighborId;
                    if(m_lstTwoHopNeighbors.get(j).OneHopNeighborId == Id && !lstAlreadyCovered.contains(TwoHopId))
                        nCount++;
                }
                if(nCount >= nMaxCovered)
                {
                    nMaxId = Id;
                    nMaxCovered = nCount;
                }
            }
            m_lstMPR.add(nMaxId);
            for(int i = 0 ; i < m_lstTwoHopNeighbors.size() ; i++)
            {
                int TwoHopId = m_lstTwoHopNeighbors.get(i).TwoHopNeighborId;
                if(m_lstTwoHopNeighbors.get(i).OneHopNeighborId == nMaxId && !lstAlreadyCovered.contains(TwoHopId))
                {
                    lstAlreadyCovered.add(TwoHopId);
                    lstTwoHopNeighborsId.remove(lstTwoHopNeighborsId.indexOf(TwoHopId));
                }
            }
        }
    }
    private ArrayList<Integer> getNonMPRNeighbors()
    {
        ArrayList<Integer> lst = new ArrayList<Integer>();
        for(int i = 0 ; i < m_lstTwoHopNeighbors.size() ; i++)
            if(!lst.contains(m_lstTwoHopNeighbors.get(i).OneHopNeighborId))
                lst.add(m_lstTwoHopNeighbors.get(i).OneHopNeighborId);
        ArrayList<Integer> lstNonMPR = Subtract(lst, m_lstMPR);
        return lstNonMPR;
    }
    private ArrayList<Integer> getMPR_For_Isolated_TwoHopNeighbors()
    {
        ArrayList<Integer> lstIsolated = new ArrayList<Integer>();
        for(int i = 0 ; i < m_lstTwoHopNeighbors.size() ; i++)
        {
            if(getHowmanyOneHopsCoverThis(m_lstTwoHopNeighbors.get(i).TwoHopNeighborId , m_lstTwoHopNeighbors) == 1 && !lstIsolated.contains(m_lstTwoHopNeighbors.get(i).OneHopNeighborId))
                lstIsolated.add(m_lstTwoHopNeighbors.get(i).OneHopNeighborId);
        }
        return lstIsolated;
    }
    private ArrayList<Integer> getTwoHopNeighborSetOf(ArrayList<Integer> x_lstOneHop)
    {
        ArrayList<Integer> lstTwoHopList = new ArrayList<Integer>();
        for(int i = 0 ; i < x_lstOneHop.size() ; i++)
        {
            for(int j = 0 ; j < m_lstTwoHopNeighbors.size() ; j++)
            {
                if(m_lstTwoHopNeighbors.get(j).OneHopNeighborId == x_lstOneHop.get(i))
                {
                    if(!lstTwoHopList.contains(m_lstTwoHopNeighbors.get(j).TwoHopNeighborId))
                        lstTwoHopList.add(m_lstTwoHopNeighbors.get(j).TwoHopNeighborId);
                }
            }
        }
        return lstTwoHopList;
    }
    private int getHowmanyOneHopsCoverThis(int x_nTwoHopNeighborId,ArrayList<TwoHop_record> x_lstTwoHopNeighbors)
    {
        int nCount = 0;
        for(int i = 0 ; i < x_lstTwoHopNeighbors.size() ; i++)
        {
            if(x_lstTwoHopNeighbors.get(i).TwoHopNeighborId == x_nTwoHopNeighborId)
                nCount++;
        }
        return nCount;
    }
    private ArrayList<Integer> getAllTwoHopNeighbors()
    {
        ArrayList<Integer> lstTwoHopNeighbors = new ArrayList<Integer>();
        for(int i = 0 ; i < m_lstTwoHopNeighbors.size() ; i++)
        {
            if(!lstTwoHopNeighbors.contains(m_lstTwoHopNeighbors.get(i).TwoHopNeighborId))
                lstTwoHopNeighbors.add(m_lstTwoHopNeighbors.get(i).TwoHopNeighborId);
        }
        return lstTwoHopNeighbors;
    }
    private ArrayList<Integer> Subtract(ArrayList<Integer> x_lst1, ArrayList<Integer> x_lst2)
    {
        ArrayList<Integer> lstResult = new ArrayList<Integer>();
        for(int i = 0 ; i < x_lst1.size() ; i++)
        {
            if(!x_lst2.contains( x_lst1.get(i)))
                lstResult.add(x_lst1.get(i));
        }
        return lstResult;
    }

    private boolean IsMyOneHopBidirectionalNeighbor(int x_nId)
    {
        for(int i = 0 ; i < m_lstOneHopNeighbors.size() ; i++)
        {
            if(m_lstOneHopNeighbors.get(i).NodeId == x_nId && m_lstOneHopNeighbors.get(i).link_status == LinkState.Bidirectional)
                return true;
        }
        return false;
    }
    private boolean IsMyOneHopUnidirectionalNeighbor(int x_nId)
    {
        for(int i = 0 ; i < m_lstOneHopNeighbors.size() ; i++)
        {
            if(m_lstOneHopNeighbors.get(i).NodeId == x_nId && m_lstOneHopNeighbors.get(i).link_status == LinkState.UniDirectional)
                return true;
        }
        return false;
    }
    private boolean ExistsInTwoHopNeighbors(int x_nOneHopId,int x_nTwoHopId)
    {
        boolean bFound = false;
        for(int i = 0 ; i < m_lstTwoHopNeighbors.size() ; i++)
        {
            if(m_lstTwoHopNeighbors.get(i).OneHopNeighborId == x_nOneHopId && m_lstTwoHopNeighbors.get(i).TwoHopNeighborId == x_nTwoHopId)
                bFound = true;
        }
        return bFound;
    }
    private void SetOneHopNeighbor(int x_nId, LinkState x_status)
    {
        boolean bFound = false;
        for(int i = 0 ; i < m_lstOneHopNeighbors.size() ; i++)
        {
            if(m_lstOneHopNeighbors.get(i).NodeId == x_nId)
            {
                m_lstOneHopNeighbors.get(i).link_status = x_status;
                bFound = true;
                break;
            }
        }
        if(!bFound)
        {
            m_lstOneHopNeighbors.add(new OneHop_record(x_nId, x_status));
        }
            
    }
    private void removeFromTwoHopNeighbors(int x_nId)
    {
        for(int i = m_lstTwoHopNeighbors.size() -1 ; i >= 0 ; --i)
        {
            if(m_lstTwoHopNeighbors.get(i).TwoHopNeighborId == x_nId)
                m_lstTwoHopNeighbors.remove(i);
        }
    }
    private void RecalibrateRoutingTable()
    {
        //--> Erase the table
        m_RoutingTable.ClearTable();
        //--> Add bidirectional links
        //--> Start with First Hop Neighbors
        for(int i = 0 ; i < m_lstOneHopNeighbors.size() ; i++)
        {
            if(m_lstOneHopNeighbors.get(i).link_status == LinkState.Bidirectional)
            {
                m_RoutingTable.AddLink(m_nNodeId, m_lstOneHopNeighbors.get(i).NodeId);
            }
        }
        //--> Then the Two hop neighbors
        for(int i = 0 ; i < m_lstTwoHopNeighbors.size() ; i++)
        {
            int OnehopId = m_lstTwoHopNeighbors.get(i).OneHopNeighborId;
            int TwoHopId = m_lstTwoHopNeighbors.get(i).TwoHopNeighborId;
            m_RoutingTable.AddLink(OnehopId, TwoHopId);
        }
        //--> And Finally the TCTable
        for(int i = 0 ; i < m_TopologyTable.m_lstTCTable.size() ; i++)
        {
            int MS  = m_TopologyTable.m_lstTCTable.get(i).MSId;
            int MPR = m_TopologyTable.m_lstTCTable.get(i).MPRId;
            m_RoutingTable.AddLink(MS, MPR);
        }
    }
    public void ShowInfo()
    {
        System.out.println("Cycle: "+m_nCycleNo);
        System.out.print("One Hop Neighbors (UniDirection):");
        for(int i = 0 ; i < m_lstOneHopNeighbors.size() ; i++)
        {
            if(m_lstOneHopNeighbors.get(i).link_status == LinkState.UniDirectional)
                System.out.print(" "+ m_lstOneHopNeighbors.get(i).NodeId);
        }
        System.out.print("\nOne Hop Neighbors (BiDirection):");
        for(int i = 0 ; i < m_lstOneHopNeighbors.size() ; i++)
        {
            if(m_lstOneHopNeighbors.get(i).link_status == LinkState.Bidirectional)
                System.out.print(" "+ m_lstOneHopNeighbors.get(i).NodeId);
        }
        
        System.out.print("\nTwo Hop Neighbors:");
        for(int i = 0 ; i < m_lstTwoHopNeighbors.size() ; i++)
        {
            System.out.print(" " + m_lstTwoHopNeighbors.get(i).OneHopNeighborId+"->"+m_lstTwoHopNeighbors.get(i).TwoHopNeighborId);
        }
        System.out.println("\nMPRSet:");
        for(int i = 0 ; i < m_lstMPR.size() ; i++)
        {
            System.out.print(" "+ m_lstMPR.get(i));
        }
        System.out.println("\nMS Set:");
        for(int i = 0 ; i < m_lstMPRSelector.size() ; i++)
        {
            System.out.print(" "+ m_lstMPRSelector.get(i));
        }
        System.out.println("");
    }
    public void ShowRoutingTable()
    {
        System.out.println("************");
        for(int i = 0 ; i < Controller.MAX_NODE_COUNT ; i++)
        {
            if(i != m_nNodeId)
            {
                ArrayList<String> lst =  m_RoutingTable.getPathToDestination(i);
                for(int j = 0 ; j < lst.size() ; j++)
                {
                    System.out.println(lst.get(j));
                }
            }
                
        }
    }
}
