package internseason.scheduler.model;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    Map<String, Task> tasks;
    List<Dependency> dependencies;
    Map<String, List<String>> adjacencyList;
    List<List<Task>> topologicalOrdering;


    public Graph() {
        tasks = new HashMap<>();
    }

    public void setTasks(Map<String,Task> tasks) {
        this.tasks = tasks;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public int size() {
        return tasks.size();
    }

    public Task getTask(String id) {
        return tasks.get(id);
    }

    public Map<String, Task> getTasks() {
        return this.tasks;
    }

    public List<List<Task>> getTopologicalOrdering(){
        if (topologicalOrdering == null){
            createTopologicalOrdering();
        }

        return topologicalOrdering;
    }

    private Map<String, List<String>> getAdjacencyList() {
        if (this.adjacencyList != null) {
            return this.adjacencyList;
        }
        this.adjacencyList = new HashMap<>();

        for (Task task : tasks.values()){
            adjacencyList.put(task.getId(), new ArrayList<>());
        }

        for(Dependency dependency : this.dependencies) {
            String sourceId = dependency.getSourceTask().getId();
            String targetId = dependency.getTargetTask().getId();

            List<String> list = adjacencyList.containsKey(sourceId) ? adjacencyList.get(sourceId) : new ArrayList<>();
            list.add(targetId);

            adjacencyList.put(sourceId, list);
        }
        return adjacencyList;
    }

    private void createTopologicalOrdering() {
        // Store the in degree of array
        adjacencyList = getAdjacencyList();

        Map<String, Integer> inDegrees = buildInDegrees(adjacencyList);
        inDegrees = getInDegrees(adjacencyList, inDegrees);
        Set<String> visitedSet = new HashSet<>();
        topologicalOrdering = new ArrayList<>();

        // Initialize a queue with all in-degree zero vertices
        LinkedList<String> zeroDegrees = new LinkedList<>();
        for (String task : inDegrees.keySet()){
            if (inDegrees.get(task) == 0){
                zeroDegrees.add(task);
            }
        }
        List<List<String>> layers = new ArrayList<>();
        //while there are vertices remaining in the queue
        while (!zeroDegrees.isEmpty()){
            visitedSet.addAll(zeroDegrees);
            List<String> layer  = new ArrayList<>();
            for (String vertex : zeroDegrees) {
                adjacencyList.remove(String.valueOf(vertex));
                layer.add(String.valueOf(vertex));
            }
            layers.add(layer);
            zeroDegrees.clear();
            //enqueue any vertice whose in degree became zero
            inDegrees = getInDegrees(adjacencyList, inDegrees);
            for (String task : inDegrees.keySet()){
                if (inDegrees.get(task) == 0 && !visitedSet.contains(task) && !zeroDegrees.contains(task)) {
                    zeroDegrees.add(task);
                }
            }
        }
        // convert to task objects
        for (List<String> layer : layers) {
            topologicalOrdering.add(buildTaskListFromIds(layer));
        }
    }

    /** Build the initial map of task ids to their indegree count
     * @param adj adjacency list of graph
     * @return map where indegree of each node is zero
     */
    private Map<String,Integer> buildInDegrees(Map<String, List<String>> adj){
        HashMap<String, Integer> inDegrees = new HashMap<>();
        for (String task : adj.keySet()){
            inDegrees.put(task,0);
        }
        for (List<String> tasks : adj.values()){
            for (String task : tasks){
                if (!inDegrees.containsKey(task)){
                    inDegrees.put(task,0);
                }
            }
        }
        return inDegrees;
    }

    /** Loop through adjaceny list and increment indegree value of each task in the indegree map
     * @param adj adjaceny list of graph
     * @param inDegrees map of tasks to their current indegree
     * @return
     */
    private Map<String,Integer> getInDegrees(Map<String,List<String>> adj, Map<String, Integer> inDegrees) {

        for (String task : inDegrees.keySet()){
            inDegrees.put(task,0);
        }
        for (List<String> value : adj.values()){
            for (String s : value){
                if (inDegrees.containsKey(s)){
                    int count = inDegrees.get(s);
                    count++;
                    inDegrees.put(s, count);
                } else {
                    inDegrees.put(s,1);
                }
            }
        }
        return inDegrees;
    }

    public List<Task> buildTaskListFromIds(List<String> taskIds) {
        List<Task> result = taskIds.stream()
                .map(t -> tasks.get(t))
                .collect(Collectors.toList());

        return result;
    }

}
