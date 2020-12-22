package algorithm;

import node.Node;

import java.util.ArrayList;

public interface ParentFunc {

    ArrayList<Node> chooseParentForNChild(ArrayList<Node> nodes, int N);

}
