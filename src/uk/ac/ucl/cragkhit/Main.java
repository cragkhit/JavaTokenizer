package uk.ac.ucl.cragkhit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringEscapeUtils;

public class Main {
	private static Options options = new Options();
	private static int nVal = 4;
	private static TokenizerMode modes = new TokenizerMode();
	private static int ngram = Settings.Ngram.OFF;
	private static String inputFile = "";

	public static void main(String[] args) {
		// process the command line arguments
		processCommandLine(args);

		JavaTokenizer tokenizer = new JavaTokenizer(modes);
		
		if (modes.getEscape() == Settings.Normalize.ESCAPE_ON) {
			// generate JSON-escaped Java code 
			try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
				String line;
				while ((line = br.readLine()) != null) {
					ArrayList<String> tokens = tokenizer.noNormalizeAToken(escapeString(line).trim());
					System.out.print(printArray(tokens, false));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			nGramGenerator ngen = new nGramGenerator(nVal);
			try {
				ArrayList<String> tokens = tokenizer.getTokensFromFile(inputFile);
				if (ngram == Settings.Ngram.ON) {
					// convert the tokens to ngrams
					ArrayList<String> ngrams = ngen.generateNGramsFromJavaTokens(tokens);
					System.out.print(escapeString(printArray(ngrams, false)));
				} else {
					// if not, just use the tokens
					// System.out.print(escapeString(printArray(tokens, true)));
					System.out.print(printArray(tokens, true));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 * Print the give array list to string
	 * @param arr the array to be printed
	 * @param pretty pretty printing or not
	 */
	public static String printArray(ArrayList<String> arr, boolean pretty) {
		String s = "";
		for (int i = 0; i < arr.size(); i++) {
//			if (pretty && arr.get(i).equals("\n")) {
//				System.out.print(arr.get(i));
//				continue;
//			}
			s += arr.get(i) + " ";
		}
		return s;
	}

	/***
	 * Escape the Java code to be conformed to JSON format
	 * @param input the original Java code
	 * @return output the escaped string of Java
	 */
	private static String escapeString(String input) {
		return StringEscapeUtils.escapeJson(input);
	}

	/***
	 * Processor of the command line parameter
	 * @param args command line arguments
	 */
	private static void processCommandLine(String[] args) {

		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		options.addOption("f", "file", true, "the input file to normalise");
		options.addOption("v", "nval", true, "the value of n in ngram");
		options.addOption("l", "level", true, "normalisation. It can be a combination of x (none), w (words), d (datatypes), "
				+ "j (Java classes), p (Java packages), k (keywords), v (values), s (strings), e (escape). For example: wkvs");
		options.addOption("n", "ngram", true, "convert tokens into ngram (true/false) [default=false]");
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
				char[] normOptions = line.getOptionValue("l").toLowerCase().toCharArray();
				for (char c: normOptions) {
					// setting all normalisation options: w, d, j, p, k, v, s
					if (c == 'w') {
						modes.setWord(Settings.Normalize.WORD_NORM_ON);
					} else if (c=='d') {
						modes.setDatatype(Settings.Normalize.DATATYPE_NORM_ON);
					}
					else if (c=='j') {
						modes.setJavaClass(Settings.Normalize.JAVACLASS_NORM_ON);
					} else if (c=='p') {
						modes.setJavaPackage(Settings.Normalize.JAVAPACKAGE_NORM_ON);
					} else if (c=='k') {
						modes.setKeyword(Settings.Normalize.KEYWORD_NORM_ON);
					} else if (c=='v') {
						modes.setValue(Settings.Normalize.VALUE_NORM_ON);
					} else if (c=='s') {
						modes.setString(Settings.Normalize.STRING_NORM_ON);
					}
					else if (c=='x') {
						modes.setWord(Settings.Normalize.WORD_NORM_OFF);
						modes.setDatatype(Settings.Normalize.DATATYPE_NORM_OFF);
						modes.setJavaClass(Settings.Normalize.JAVACLASS_NORM_OFF);
						modes.setJavaPackage(Settings.Normalize.JAVAPACKAGE_NORM_OFF);
						modes.setKeyword(Settings.Normalize.KEYWORD_NORM_OFF);
						modes.setValue(Settings.Normalize.VALUE_NORM_OFF);
						modes.setValue(Settings.Normalize.STRING_NORM_OFF);
						modes.setEscape(Settings.Normalize.ESCAPE_ON);
					} else if (c=='e') {
						modes.setEscape(Settings.Normalize.ESCAPE_ON);
					}
				}
			}

			if (line.hasOption("n")) {
				if (line.getOptionValue("n").equals("true"))
					ngram = Settings.Ngram.ON;
				else
					ngram = Settings.Ngram.OFF;
			}

		} catch (ParseException exp) {
			System.out.println("Warning: " + exp.getMessage());
		}
	}

	
	/***
	 * Printing help
	 */
	private static void showHelp() {
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp("JavaTokenizer (v 0.5)\njava -jar checker.jar", options);
		System.exit(0);
	}
}
