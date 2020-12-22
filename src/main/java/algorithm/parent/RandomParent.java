package algorithm.parent;

import algorithm.ParentFunc;
import node.Node;

import java.util.ArrayList;

public class RandomParent implements ParentFunc {
    @Override
    public ArrayList<Node> chooseParentForNChild(ArrayList<Node> nodeList, int N) {
        int index = (int) (Math.random() * nodeList.size() - 1);
        ArrayList<Node> res = new ArrayList<>();
        Node parent = nodeList.get(index);
        for (int k = 0; k < N; k++)
            res.add(parent);

        return res;
    }

    @Override
    public String toString() {
        return "RANDOM";
    }
    
}
