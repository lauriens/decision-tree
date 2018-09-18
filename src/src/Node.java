package src;

import java.util.ArrayList;
import java.util.List;

public class Node {
	public boolean leaf;
	public int feature;
	public int classification;
	public List<Double> values;
	public List<Node> nextLevel;
	
	public Node (int feature, boolean leaf, List<Double> values)
	{
		this.feature = feature;
		this.leaf = leaf;
		this.classification = 0;
		this.values = values;
		this.nextLevel = new ArrayList<Node>();
	}
	
	
}
