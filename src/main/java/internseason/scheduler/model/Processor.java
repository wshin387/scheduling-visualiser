package internseason.scheduler.model;

import javafx.util.Pair;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Processor implements Serializable {
    private int cost;
    private int processorId;

    private ArrayList<Pair<Task, Integer>> taskScheduleList; // task along with when its scheduled
    private HashMap<String, Integer> taskIdScheduleMap; // map from task to time scheduled

    public Processor(int processorId) {
        this.cost = 0;
        this.processorId = processorId;
        this.taskIdScheduleMap = new HashMap<>();
        this.taskScheduleList = new ArrayList<>();
    }

    public int getId() {
        return processorId;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void addTask(Task task) {
        taskIdScheduleMap.put(task.getId(), this.cost);
        taskScheduleList.add(new Pair(task, this.cost));
        this.cost += task.getCost();
    }

    public int getTaskStartTime(Task task) {
        return this.taskIdScheduleMap.get(task.getId());
    }

    public List<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (Pair<Task, Integer> pair: taskScheduleList) {
            result.add(pair.getKey());
        }

        return result;

    }

    //ToDo: make exception class
    public void addTaskAt(Task task,int time) {
        if (time < this.cost){
            throw new IllegalArgumentException("Illegal time");
        }

        this.taskIdScheduleMap.put(task.getId(), time);
        this.taskScheduleList.add(new Pair(task, time));

        this.cost = time + task.getCost();

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Pair<Task, Integer> t: this.taskScheduleList) {
            sb.append("t" + t.getKey().getId() + " scheduled at: " + t.getValue() + "\n");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(this.taskScheduleList);
        return builder.hashCode();
    }
}
