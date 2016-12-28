/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 *
 * @author asus
 */
public class AllPaths<Vertex> {

    private Stack<String> path  = new Stack<String>();   // the current path
    private SET<String> onPath  = new SET<String>();     // the set of vertices on the path
    private ArrayList<String> m_lstPaths = new ArrayList<String>();
    public AllPaths(Graph G, String s, String t) {
        enumerate(G, s, t);
    }

    // use DFS
    private void enumerate(Graph G, String v, String t) {

        // add node v to current path from s
        path.push(v);
        onPath.add(v);

        // found path from s to t - currently prints in reverse order because of stack
        if (v.equals(t)) 
        {
            //System.out.println(path);
            m_lstPaths.add(path.toString());
        }

        // consider all neighbors that would continue path with repeating a node
        else {
            for (String w : G.adjacentTo(v)) {
                if (!onPath.contains(w)) enumerate(G, w, t);
            }
        }

        // done exploring from v, so remove from path
        path.pop();
        onPath.delete(v);
    }
    public ArrayList<String> getPaths()
    {
        ArrayList<String> lstTemp = new ArrayList<>();
        for(int i = 0 ; i < m_lstPaths.size() ; i++)
        {
            String tmp = m_lstPaths.get(i).substring(1,m_lstPaths.get(i).length() -1);
            StringTokenizer st = new StringTokenizer(tmp,",");
            lstTemp.add(tmp);
        }
        return lstTemp;
    }
}
