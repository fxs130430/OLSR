/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author asus
 */



public class Controller_Record {
    
    public int Time;
    public int Source_LinkId;
    public int Dest_LinkId;
    public boolean bConnected;
    
    public Controller_Record(int x_nTime,int x_nSource,int x_nDest,boolean x_bConn)
    {
        Time = x_nTime;
        Source_LinkId = x_nSource;
        Dest_LinkId = x_nDest;
        bConnected = x_bConn;
    }
}
