/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author asus
 */
public class TCRecord {
    
    public int MSId;
    public int MPRId;
    public int SeqNo;
    public int HoldingTime;
    
    public TCRecord()
    {
        MSId = -1;
        MPRId = -1;
        SeqNo = -1;
        HoldingTime = -1;
    }
    public TCRecord(int x_nMSId,int x_nMPRId, int x_nSeqNo, int x_nHoldingTime)
    {
        MSId = x_nMSId;
        MPRId = x_nMPRId;
        SeqNo = x_nSeqNo;
        HoldingTime = x_nHoldingTime;
    }
    
}
