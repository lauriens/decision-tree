package src;

import java.util.List;

public class Instance {
	public final List<Double> features;
	public final int classification;
	
	public Instance(List<Double> features)
	{
		this.features = features;
		this.classification = -1;
	}
	
	public Instance(List<Double> features, int classification)
	{
		this.features = features;
		this.classification = classification;
	}
}
