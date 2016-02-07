package uk.ac.ucl.cragkhit;

import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		int mode = Settings.Normalize.MED_NORM;
		int ngram = Settings.Ngram.OFF;

		if (args.length < 1) {
			System.out.println("Usage: java -jar JavaTokenizer.jar filename [options]");
			System.out.println("Options:\nhi = heavy normalisation (every word is converted to 'W')"
					+ "\nmed = medium normalisation (Java keywords and Java classes are converted to 'W')"
					+ "\nlo = low normalisation (only Java keywords are converted to 'W')" + "\nngram = use ngram");
			return;
		} else if (args.length > 1) {
			if (args[1].equals("lo"))
				mode = Settings.Normalize.LO_NORM;
			else if (args[1].equals("med"))
				mode = Settings.Normalize.MED_NORM;
			else if (args[1].equals("hi"))
				mode = Settings.Normalize.HI_NORM;
		} 
		if (args.length > 2) {
			if (args[2].equals("ngram"))
				ngram = Settings.Ngram.ON;
		}

		JavaTokenizer tokenizer = new JavaTokenizer(mode);
		nGramGenerator ngen = new nGramGenerator(4);
		try {
			ArrayList<String> tokens = tokenizer.getTokensFromFile(args[0]);
			printArray(tokens, true);
			if (ngram == Settings.Ngram.ON) {
				ArrayList<String> ngrams = ngen.generateNGramsFromJavaTokens(tokens);
				printArray(ngrams, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printArray(ArrayList<String> arr, boolean pretty) {
		for (int i = 0; i < arr.size(); i++) {
			if (pretty && arr.get(i).equals("\n")) {
				System.out.print(arr.get(i));
				continue;
			}
			System.out.print(arr.get(i) + " ");
		}
	}

}
