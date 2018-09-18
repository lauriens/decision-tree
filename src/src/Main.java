package src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
	

	public static void main(String args[]) throws IOException
	{
		List<Instance> trainingDataset = new ArrayList<Instance>();
		String trainingFile = "data/benchmarkValidacao.data";
		if (args.length > 2)
            System.out.println("Parametros de entrada: -f CAMINHO_DO_CONJUNTO_DE_TREINAMENTO");
        else
        {
            for (int i = 0; i < args.length; i++)
            {
                switch(args[i])
                {
                /*case "-c":
                	i++;
                	numOfClasses = Integer.parseInt(args[i]);
                	break;*/
                case "-f":
                    i++;
                    trainingFile = args[i];
                    break;
                default:
                    System.out.println("Parametro invalido! \n-f CAMINHO_DO_CONJUNTO_DE_TREINAMENTO");
                    System.exit(1);
                }
            }
        }
		extractData(trainingFile, trainingDataset);
		Tree decisionTree = new Tree (trainingDataset);
		Node treeRoot = decisionTree.buildTree();
		int newClassification = decisionTree.prediction(trainingDataset.get(0), treeRoot);
		System.out.println("Prediction: " + newClassification);
		
		
		
	}
	
	private static void extractData (String inputFile, List<Instance> dataset) throws IOException
	{
		String line;
        BufferedReader in;
        Instance features;
        int classification = 0;
         
        try {
            in = new BufferedReader(new FileReader(inputFile));
             
            while((line = in.readLine()) != null)
            {
                List<Double> f = new ArrayList<Double>();
                String[] parts = line.split(",");   
                 
                for(int i = 0; i < parts.length; i++) {
                    if (i < parts.length - 1)
                    	f.add(Double.parseDouble(parts[i]));
                    else if (i == parts.length - 1)
                    	classification = Integer.parseInt(parts[i]);
                }
                 
                features = new Instance(f, classification);
                                 
                dataset.add(features);
            }
            in.close();
             
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }   
         
    }
	
	
}
