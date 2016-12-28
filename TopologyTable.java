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
public class TopologyTable {
    
    private int                         m_nNodeId;
    public ArrayList<TCRecord>          m_lstTCTable;
    
    public TopologyTable(int x_nNodeId)
    {
        m_nNodeId = x_nNodeId;
        m_lstTCTable = new ArrayList<>();
    }
    public void OnUpdateState()
    {
        for(int i = m_lstTCTable.size() -1 ; i >= 0 ; --i)
        {
            m_lstTCTable.get(i).HoldingTime++;
            if(m_lstTCTable.get(i).HoldingTime == 45)
            {
                m_lstTCTable.remove(i);
            }
        }
    }
    public boolean OnReceiveTCMessage(TCMessage x_TCMessage)
    {
        boolean bUseful = false;
        if(x_TCMessage.m_nSeqNo < getSeqNo(x_TCMessage.m_nOriginatorId))
            return bUseful; // It's too old
        //System.out.println(x_TCMessage.getMessage());
        if(getCount(x_TCMessage.m_nOriginatorId) == 0) // Completely new record, just add it
        {
            bUseful = true;
            for(int i = 0 ; i < x_TCMessage.m_lstMS.size() ; i++)
            {
                UpdateRecord(x_TCMessage.m_nOriginatorId, x_TCMessage.m_lstMS.get(i), x_TCMessage.m_nSeqNo);
            }
        }
        else if(x_TCMessage.m_nSeqNo == getSeqNo(x_TCMessage.m_nOriginatorId))
        {
            if(x_TCMessage.m_lstMS.size() != getCount(x_TCMessage.m_nOriginatorId))
            {
                System.out.println("Incompatible count for local and received TC information of the same SeqNo");
                System.exit(-1);
            }
            for(int i = 0 ; i < x_TCMessage.m_lstMS.size() ; i++)
            {
                UpdateRecord(x_TCMessage.m_nOriginatorId,x_TCMessage.m_lstMS.get(i), x_TCMessage.m_nSeqNo);
            }
        }
        else // Local should be updated
        {
            bUseful = true;
            RemoveRecord(x_TCMessage.m_nOriginatorId);
            for(int i = 0 ; i < x_TCMessage.m_lstMS.size() ; i++)
            {
                UpdateRecord(x_TCMessage.m_nOriginatorId, x_TCMessage.m_lstMS.get(i), x_TCMessage.m_nSeqNo);
            }
        }
        return bUseful;
    }
    private int getSeqNo(int x_nMPR)
    {
        for(int i = 0 ; i < m_lstTCTable.size() ; i++)
        {
            if(m_lstTCTable.get(i).MPRId == x_nMPR)
            {
                return m_lstTCTable.get(i).SeqNo;
            }
        }
        return -1;        
    }
    private int getCount(int x_nMPR)
    {
        int nCount = 0;
        for(int i = 0 ; i < m_lstTCTable.size() ; i++)
        {
            if(m_lstTCTable.get(i).MPRId == x_nMPR)
                nCount++;
        }
        return nCount;
    }
    private void RemoveRecord(int x_nMS)
    {
        for(int i = m_lstTCTable.size() - 1 ; i >= 0 ; --i)
        {
            if(m_lstTCTable.get(i).MPRId == x_nMS)
            {
                //System.out.println("Remove MPR "+m_lstTCTable.get(i).MPRId+" MS "+m_lstTCTable.get(i).MSId );
                m_lstTCTable.remove(i);
            }
        }
    }
    private void UpdateRecord(int x_nMPR,int x_nMS, int x_nSeqNo)
    {
        boolean bFound = false;
        for(int i = 0 ; i < m_lstTCTable.size() ; i++)
        {
            if(m_lstTCTable.get(i).MSId == x_nMS && m_lstTCTable.get(i).MPRId == x_nMPR)
            {
                if(x_nSeqNo != m_lstTCTable.get(i).SeqNo)
                {
                    System.out.println("Unexpected unequal SeqNo for MS "+x_nMS+ " on MPR "+x_nMPR+" Local = "+m_lstTCTable.get(i).SeqNo+" received = "+x_nSeqNo);
                    System.exit(-1);                    
                }
                m_lstTCTable.get(i).HoldingTime = 0;
                bFound = true;
            }
        }
        if(!bFound) // It's a new information, just add it to the table
        {
            m_lstTCTable.add(new TCRecord(x_nMS, x_nMPR, x_nSeqNo, 0));
        }
    }    
}
