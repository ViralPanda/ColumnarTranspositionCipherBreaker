import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
	
	private static final int COLUMNS = 11;
	private static final int MAX_ITERATIONS = 100000000;
	private static Map<String, Double> parseScores(String fileName) throws FileNotFoundException{
		Map<String, Double> scores = new HashMap<>();
		Scanner scanner = new Scanner(new FileInputStream(fileName));
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			String[] entries = line.split(",");
			for(String entry : entries){
				int index = 0;
				while(!Character.isAlphabetic(entry.charAt(index))){
					index++;
					if(index == entry.length()){
						break;
					}
				}
				if(index < entry.length()){
					String key = entry.substring(index, index+4);
					String val = entry.substring(index+4, entry.lastIndexOf('\''));
					scores.put(key, Double.parseDouble(val));
				}
			}
		}
		
		scanner.close();
		return scores;
		
	}
	
	private static double getScores(List<Character[]> rep, Map<String, Double> scores){
		double score = 0;
		
		for(int i = 0; i < (rep.get(0).length-1) * rep.size() - 4; i++){
			String key = "";
			for(int j = 0; j < 4; j++){
				int bucket = (i + j) % COLUMNS;
				int index = (i + j) / COLUMNS + 1;
				key += rep.get(bucket)[index];
			}
			Double val = scores.get(key);
			if(val != null){
				score += val;
			}
		}
		return score;
	}
	
	private static void hillClimb(char[][] rep, Map<String, Double> scores){
		List<Character[]> transposed = new LinkedList<>();
		for(int j = 0; j < rep[0].length; j++){
			Character[] arr = new Character[rep.length];
			for(int i = 0; i < rep.length; i++){
				arr[i] = rep[i][j];
			}
			transposed.add(j, arr);
		}
		int buffLen = rep.length * rep[0].length;
		//test ended
		Collections.shuffle(transposed);
		double highScore = getScores(transposed, scores);
		printResult(transposed, highScore);
		double fudgeFactor = 0.15;
		double cycleLimit = 20;
		double cycle = 0;
		double noiseStep = 1.5;
		double noise = 1.0;
		double score = highScore;
		double cycleHigh = highScore;
		for(int i = 0; i < MAX_ITERATIONS; i++){
			int swapA = (int) (Math.random()*transposed.size());
			int swapB = (int) (Math.random()*transposed.size());
			reposition(transposed, swapA, swapB);
			score = getScores(transposed, scores);
			if(score > highScore){
				highScore = score;
				printResult(transposed, highScore);
			}
			if(score > cycleHigh-fudgeFactor*buffLen/noise){
				cycleHigh = score;
			}else{
				reposition(transposed, swapA, swapB);
				noise += noiseStep;
				cycle++;
				if(cycle > cycleLimit){
					noise = 1.0;
					cycle = 0;
				}
			}
		}
		
		System.out.println("\n-----------Final output, Look at output before this to find max-----------");
		printResult(transposed, score);
		
		
		
	}
	
	private static void printResult(List<Character[]> rep, double score){
		
		System.out.println("--------------------Score = " + score + " ---------------------");
		for(int i = 0; i < rep.get(0).length; i++){
			String str = "";
			for(int j = 0; j < rep.size(); j++){
				str += rep.get(j)[i];
			}
			System.out.println(str);
		}
	}
	
	private static void reposition(List<Character[]> rep, int swapIndex1, int swapIndex2){
		Collections.swap(rep, swapIndex1, swapIndex2);
	}
	
	//tested
	private static char[][] parseInputString(String fileName) throws FileNotFoundException{
		Scanner scanner = new Scanner(new FileInputStream(fileName));
		StringBuilder builder = new StringBuilder();
		while(scanner.hasNext()){
			builder.append(scanner.nextLine().replaceAll(" ", ""));
		}
		
		char[][] rep = new char[builder.length()/COLUMNS + 1][COLUMNS];
		for(int i = 0; i < COLUMNS; i++){
			rep[0][i] = (char) ('a' + i);
		}
		int builderPos = 0;
		for(int i = 0; i < COLUMNS; i++){
			for(int j = 1; j < rep.length; j++){
				rep[j][i] = builder.charAt(builderPos);
				builderPos++;
			}
		}
		scanner.close();
		return rep;
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		String scoresFileName = args[0];
		Map<String, Double> scores = parseScores(scoresFileName);
		String inputFile = args[1];
		char[][] rep = parseInputString(inputFile);
		hillClimb(rep, scores);
	}

}
