/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author asus
 */
enum LinkState  {UniDirectional,Bidirectional,MPR};

public class OneHop_record {
    public int NodeId;
    public LinkState link_status;
    public int Timeout;
    
    public OneHop_record(int x_nNodeId, LinkState x_status)
    {
        NodeId = x_nNodeId;
        Timeout = 0;
        link_status = x_status;                
    }
    
}
