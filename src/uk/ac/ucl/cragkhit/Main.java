package uk.ac.ucl.cragkhit;

import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
	private static Options options = new Options();
	private static int nVal = 4;
	private static int mode = Settings.Normalize.MED_NORM;
	private static int ngram = Settings.Ngram.OFF;
	private static String inputFile = "";

	public static void main(String[] args) {
		// process the command line arguments
		processCommandLine(args);

		JavaTokenizer tokenizer = new JavaTokenizer(mode);
		nGramGenerator ngen = new nGramGenerator(nVal);
		try {
			ArrayList<String> tokens = tokenizer.getTokensFromFile(inputFile);
			if (ngram == Settings.Ngram.ON) {
				ArrayList<String> ngrams = ngen.generateNGramsFromJavaTokens(tokens);
				printArray(ngrams, false);
			} else {
				printArray(tokens, true);
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

	private static void processCommandLine(String[] args) {
		
		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		options.addOption("f", "file", true, "the input file to normalise");
		options.addOption("v", "nval", true, "the value of n in ngram");
		options.addOption("l", "level", true, "normalisation level (hi [default]/lo)");
		options.addOption("n", "ngram", false, "convert tokens into ngram [default=no]");
		options.addOption("h", "help", false, "print help");
		
		// if no parameters given, print help
		if (args.length == 0) {
			showHelp();
			System.exit(0);
		}
		
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("h")) {
				showHelp();
			}
			// get the input file path
			if (line.hasOption("f")) {
				inputFile = line.getOptionValue("f");
			} else {
				throw new ParseException("No input file location provided.");
			}

			// validate that line count has been set
			if (line.hasOption("v")) {
				nVal = Integer.valueOf(line.getOptionValue("v"));
			}

			if (line.hasOption("l")) {
				if (line.getOptionValue("l").toLowerCase().equals("lo"))
					mode = Settings.Normalize.LO_NORM;
				else
					mode = Settings.Normalize.HI_NORM;
			}

			if (line.hasOption("n")) {
				ngram = Settings.Ngram.ON;
			}
			
		} catch (ParseException exp) {
			System.out.println("Warning: " + exp.getMessage());
		}
	}

	private static void showHelp() {
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp("java -jar checker.jar", options);
		System.exit(0);
	}
}
