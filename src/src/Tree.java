package src;

import java.util.ArrayList;
import java.util.List;

public class Tree {
	public final List<Instance> trainingDataset;
	public Node root;
	private final int numOfClasses;
	private double dictionaryEntropy;
	
	
	public Tree(List<Instance> dataset)
	{
		this.trainingDataset = dataset;
		this.numOfClasses = getNumOfClasses();
		this.dictionaryEntropy = dictEntropy();
	}
	
	public Node buildTree()
	{
		List<Double> atributeValue = new ArrayList<Double>();
		List<Integer> featuresAvaliable = new ArrayList<Integer>();
		int chosenFeature;
		
		for(int i = 0; i < trainingDataset.get(0).features.size(); i++)
			featuresAvaliable.add(i);
		
		chosenFeature = chosenFeature(featuresAvaliable, trainingDataset);
		getAtributeValue(atributeValue, trainingDataset, chosenFeature);
		
		root = new Node(chosenFeature, false, atributeValue);
		
		for(int i = 0; i < atributeValue.size(); i++)
		{
			if(featuresAvaliable.size() > 0)
				root.nextLevel.add(treeBody(featuresAvaliable, chosenFeature, atributeValue.get(i)));
		}
		return root;
	
	}
	
	public int prediction(Instance newInstance, Node treeNode)
	{
		int classification = 0;
		
		if(treeNode.leaf == true)
			classification = treeNode.classification;
		else
		{
			int nextLevel = newInstance.features.get(treeNode.feature).intValue();
			treeNode = treeNode.nextLevel.get(nextLevel);
			classification = prediction(newInstance, treeNode);
		}
		
		return classification;
	}
	
	public Node treeBody(List<Integer> featuresAvaliable, int feature, double value)
	{
		int newFeature = 0;
		List<Double> featureValue = new ArrayList<Double>();
		List<Instance> dictionary = new ArrayList<Instance>();
		boolean pure = true;
		int classification;
		
		for(Instance i : trainingDataset)
		{
			if(i.features.get(feature) == value)
				dictionary.add(i);
		}
		classification = dictionary.get(0).classification;
		
		for(Instance i : dictionary)
		{
			if(i.classification != classification)
				pure = false;
		}
		
		if(pure == true)
		{
			Node newNode = new Node(-1, true, null);
			newNode.classification = classification;
			return newNode;
		}
		
		newFeature = chosenFeature(featuresAvaliable, dictionary);
		getAtributeValue(featureValue, dictionary, newFeature);
		
		Node newNode = new Node(newFeature, false, featureValue);
		newNode.classification = -1;
		
		for(int i = 0; i < featureValue.size(); i++)
		{
			System.out.println(featuresAvaliable.size());
			if(featuresAvaliable.size() > 0)
				newNode.nextLevel.add(treeBody(featuresAvaliable, newFeature, featureValue.get(i)));
			else if(featuresAvaliable.size() == 0)
			{
				int[] classes = new int[numOfClasses];
				int chosenClass = 0;
				for(Instance j : dictionary)
					classes[j.classification - 1]++;
				for(int j = 0; j < numOfClasses; j++)
				{
					if(classes[j] > chosenClass)
					{
						chosenClass = classes[j];
						classification = j - 1;
					}
				}
				newNode.classification = classification;
				newNode.leaf = true;
				break;
					
			}
		}
		
		return newNode;
	}
	
	//Conta as possibilidades de classificação da classe a ser predita
	private int getNumOfClasses()
	{
		int numOfClasses = 0;
		List<Integer> classes = new ArrayList<Integer>();
		boolean found = false;
		
		for(Instance i : trainingDataset)
		{
			for(int j : classes)
				if(i.classification == j)
				{
					found = true;
				}
			if (found == false)
			{
				classes.add(i.classification);
			}
			found = false;
		}
		numOfClasses = classes.size();
		
		return numOfClasses;
	}
	//Retorna os possíveis valores de um atributo qualquer
	//OBS: ATRIBUTO CATEGÓRICO
	private void getAtributeValue(List<Double> value, List<Instance> dictionary, int feature)
	{
		boolean found = false;
		
		for(Instance i : dictionary)
		{
			for(double j : value)
				if(i.features.get(feature) == j)
					found = true;
			if(found == false)
				value.add(i.features.get(feature));
		}
		
	}
	//Conta os possíveis valores de um atributo qualquer
	//OBS: Again, CATEGÓRICO
	private void getClassifications(List<Instance> instances, int[] classifications)
	{
		
		for(int i = 0; i < classifications.length; i++)
			classifications[i] = 0;
		for(Instance i : instances)
		{
			classifications[i.classification - 1]++;
		}
	}
	//Calcula a entropia do conjunto de treinamento
	private double dictEntropy()
	{
		double entropy = 0;
		int datasetSize = trainingDataset.size();
		int[] instances = new int[numOfClasses];
		
		getClassifications(trainingDataset, instances);
		
		for (int i = 0; i < instances.length; i++)
			entropy -= (instances[i]/datasetSize) * (Math.log10(instances[i] / datasetSize) / Math.log10(2));
		
		return entropy;
	}
	//Calcula a entropia de um subconjunto de conjunto de treinamento (um valor x de um atributo)
	//Categórico
	private double calcEntropy(List<Instance> dictionary, int feature, Double value)
	{
		double entropy = 0;
		int datasetSize = 0;
		int[] instances = new int[numOfClasses];
		List<Instance> partialDataset = new ArrayList<Instance>();
		
		for(Instance i : dictionary)
		{
			if(i.features.get(feature) == value)
			{
				partialDataset.add(i);
				System.out.println(i.classification);
				datasetSize++;
			}
		}
		getClassifications(partialDataset, instances);
		
		for (int i = 0; i < instances.length; i++)
			entropy -= (instances[i]/datasetSize) * (Math.log10(instances[i]/datasetSize) / Math.log10(2));
		
		return entropy;
	}
	//Retorna o índice do atributo de maior ganho de informação
	private int chosenFeature(List<Integer> featuresAvaliable, List<Instance> dictionary)
	{
		
		List<Double> entropies = new ArrayList<Double>();
		List<Double> atributeValue;
		double entropy, averageE;
		int chosenFeature = 0;
		int i;
		double gain = 0;
		
		for(i = 0; i < featuresAvaliable.size(); i++)
		{
			averageE = 0;
			atributeValue = new ArrayList<Double>();
			getAtributeValue(atributeValue, dictionary, featuresAvaliable.get(i));
			for(int j = 0; j < atributeValue.size(); j++)
			{
				entropy = calcEntropy(dictionary, featuresAvaliable.get(i), atributeValue.get(j));
				averageE += entropy;
				System.out.println(averageE);
			}
			averageE /= atributeValue.size();
			System.out.println(averageE);
			entropies.add(averageE);
		}
									
		for (i = 0; i < entropies.size(); i++)
		{
			if(gain < (dictionaryEntropy - entropies.get(i)))
			{
				System.out.println(dictionaryEntropy - entropies.get(i));
				gain = dictionaryEntropy - entropies.get(i);
				chosenFeature = featuresAvaliable.get(i);
				break;
			}
		}
		featuresAvaliable.remove(i);
		
		return chosenFeature;
	}
	
}
