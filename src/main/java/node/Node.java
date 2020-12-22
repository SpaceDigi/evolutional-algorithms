package node;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.ArrayList;
import java.util.Objects;

@Getter
@Setter
public class Node {

    private ArrayList<Double> genList;
    private Double health;
    private HealthFunc healthFunc;
    private ArrayList<Node> childList;
    private static long NFE = 0;

    public Node(ArrayList<Double> genList, HealthFunc healthFunc) {
        this.genList = genList;
        this.healthFunc = healthFunc;
        this.health = healthFunc.health(genList);
        this.childList = new ArrayList<>();
        NFE++;
    }

    public Node(Node node) {
        this.genList = new ArrayList<>();
        for(Double d:node.getGenList()){
            genList.add(new Double(d));
        }
        this.healthFunc = node.healthFunc;
        this.health = node.healthFunc.health(genList);
        this.childList = new ArrayList<>();
    }

    public void addChild(Node node) {
        childList.add(node);
    }

    public void mutate(double v) {
        int indexForMutate = (int) (Math.random() * genList.size());

        if (genList.get(indexForMutate) + v > 1)
            genList.set(indexForMutate, 1.0);
        else if (genList.get(indexForMutate) + v < 0)
            genList.set(indexForMutate, 0.0);
        else {
            double gen = genList.get(indexForMutate);
            gen += v;
            genList.set(indexForMutate, gen);
        }
        health = healthFunc.health(genList);
        NFE++;
    }

    public static void refresh() {
        NFE = 0;
    }

    public static long NFE() {
        return NFE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(getGenList(), node.getGenList()) &&
                Objects.equals(getHealth(), node.getHealth()) &&
                Objects.equals(getHealthFunc(), node.getHealthFunc()) &&
                Objects.equals(getChildList(), node.getChildList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGenList(), getHealth(), getHealthFunc(), getChildList());
    }

    @Override
    public String toString() {
        return "Node{" +
                "genList=" + genList +
                ", health=" + health +
                ", healthFunc=" + healthFunc +
                ", childList=" + childList +
                '}';
    }
}
