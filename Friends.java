package friends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import structures.Queue;
import structures.Stack;

public class Friends {

    /**
     * Finds the shortest chain of people from p1 to p2.
     * Chain is returned as a sequence of names starting with p1,
     * and ending with p2. Each pair (n1,n2) of consecutive names in
     * the returned chain is an edge in the graph.
     *
     * @param g Graph for which shortest chain is to be found.
     * @param p1 Person with whom the chain originates
     * @param p2 Person at whom the chain terminates
     * @return The shortest chain from p1 to p2. Null if there is no
     *         path from p1 to p2
     */
    public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {

        //bfs code
        boolean[] visited = new boolean[g.members.length];

        Queue<ArrayList<String>> path = new Queue<ArrayList<String>>();
        ArrayList<String> first = new ArrayList<String>();
        first.add(g.members[g.map.get(p1)].name);
        path.enqueue(first);

        Queue<Person> trail = new Queue<Person>();
        trail.enqueue(g.members[g.map.get(p1)]);

        while(!trail.isEmpty()) {
            Person crnt = trail.dequeue();

            ArrayList<String> crntlist = path.dequeue();
            //let m = every neighbor of crnt
            Friend m = g.members[g.map.get(crnt.name)].first;
            while(m!=null) {
                if(!visited[m.fnum]) {
                    ArrayList<String> advance = new ArrayList(crntlist);
                    advance.add(g.members[m.fnum].name);

                    if(g.members[m.fnum].name.contentEquals(p2))
                        return advance;

                    visited[g.map.get(crnt.name)]= true;
                    trail.enqueue(g.members[m.fnum]);
                    path.enqueue(advance);
                }
                m=m.next;
        }}

        return null;

    }

    /**
     * Finds all cliques of students in a given school.
     *
     * Returns an array list of array lists - each constituent array list contains
     * the names of all students in a clique.
     *
     * @param g Graph for which cliques are to be found.
     * @param school Name of school
     * @return Array list of clique array lists. Null if there is no student in the
     *         given school
     */
    public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {

        boolean[] visited = new boolean[g.members.length];
        ArrayList<ArrayList<String>> cliques = new ArrayList<>();

        for(int x = 0; x < g.members.length; x++) {
            ArrayList<String> newClique = new ArrayList<>();
            cDFS(g, school, visited, newClique, x);

            if(newClique.size() > 0)
                cliques.add(newClique);
        }
        if(cliques.size()==0)
            return null;
        else
            return cliques;
    }

    private static void cDFS(Graph g, String school, boolean[] visited, ArrayList<String> cMembers, int index) {
        Person n = g.members[index];
        if(visited[index] || !n.student)
            return;

        if(!visited[index] && n.student && n.school.equals(school))
            cMembers.add(n.name);

        visited[g.map.get(n.name)] = true;

        Friend m = g.members[index].first;
        while(m != null) {
            int num = m.fnum;
            Person person = g.members[num];

            if(visited[num] == false && person.student) {
                if((person.school).equals(school))
                cDFS(g, school, visited, cMembers, num);
            }
            m = m.next;
        }
    }

    /**
     * Finds and returns all connectors in the graph.
     *
     * @param g Graph for which connectors needs to be found.
     * @return Names of all connectors. Null if there are no connectors.
     */
    public static ArrayList<String> connectors(Graph g) {

        HashMap<String, Integer> back = new HashMap<>();
        ArrayList<String> Connectors = new ArrayList<>();
        HashMap<String, Integer> dfsNums = new HashMap<>();
        HashSet<String> backedUp = new HashSet<>();

        boolean[] visited = new boolean[g.members.length];

        for(int i = 0; i < g.members.length; i++) {
            int[] numbers = new int[2]; //to assign the values
            numbers[0] = 0;
            numbers[1] = 0;
            connectDFS(g, visited, Connectors, numbers, i, true, dfsNums, back, backedUp);
        }
        return Connectors;
    }

    private static void connectDFS(Graph g, boolean[] visited, ArrayList<String> connectors, int[] nums, int ind, boolean startingPoint,
            HashMap<String,Integer> dfsNums, HashMap<String,Integer> back, HashSet<String> duplicate) {

        if(visited[ind])//base case
            return;

        Person crnt = g.members[ind];
        visited[g.map.get(crnt.name)] = true;

        dfsNums.put(crnt.name, nums[0]);
        back.put(crnt.name, nums[1]);

        Friend m = g.members[ind].first;
        while(m != null) {
            Person f = g.members[m.fnum];
            if(!visited[m.fnum]) {
                nums[0]++; nums[1]++;

                connectDFS(g, visited, connectors, nums, m.fnum, false, dfsNums, back, duplicate);

                if(dfsNums.get(crnt.name) > back.get(f.name))
                    back.put(crnt.name, Math.min(back.get(crnt.name),back.get(f.name)));
                if(dfsNums.get(crnt.name) <= back.get(f.name)) { //this means it's a connector
                    if(!startingPoint || duplicate.contains(crnt.name))
                        if(!connectors.contains(crnt.name))
                        		connectors.add(crnt.name);
                }
                duplicate.add(crnt.name);
            } else
                back.put(crnt.name, Math.min(back.get(crnt.name), dfsNums.get(f.name)));
            m = m.next;
        }
    }
}
