/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author asus
 */
public class TwoHop_record {
    
    public int TwoHopNeighborId;
    public int OneHopNeighborId;
    
    public TwoHop_record(int x_nOneHopNeighbor,int x_nTwoHopNeighbor)
    {
        TwoHopNeighborId = x_nTwoHopNeighbor;
        OneHopNeighborId = x_nOneHopNeighbor;
    }
    
}
