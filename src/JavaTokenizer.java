
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

public class JavaTokenizer {
	private HashMap<String, Integer> keywordMap;
	private HashMap<String, Integer> datatypeMap;
	private HashMap<String, Integer> wordMap;
	private HashMap<String, Integer> javaClassMap;
	private ArrayList<String> wordList;
	private int mode;

	public JavaTokenizer() {
		keywordMap = new HashMap<String, Integer>();
		datatypeMap = new HashMap<String, Integer>();
		wordMap = new HashMap<String, Integer>();
		javaClassMap = new HashMap<String, Integer>();
		wordList = new ArrayList<String>();
		setUpKeywordMap();
		setUpDatatypeMap();
	}
	
	public ArrayList<String> tokenize(Reader reader) throws IOException {
		StreamTokenizer tokenizer = new StreamTokenizer(reader);
		tokenizer.parseNumbers();
		// Don't parse slash as part of numbers.
		tokenizer.ordinaryChar('/');
		tokenizer.ordinaryChar('.');
		tokenizer.wordChars('_', '_');
		tokenizer.eolIsSignificant(false);
		tokenizer.ordinaryChars(0, ' ');
		tokenizer.slashSlashComments(true);
		tokenizer.slashStarComments(true);
		int tok;
		ArrayList<String> tokens = new ArrayList<String>();

		while ((tok = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
			switch (tok) {
			case StreamTokenizer.TT_NUMBER:
				double n = tokenizer.nval;
				// System.out.println("NUMBER:\t\t" + n);
				tokens.add("V");
				break;
			case StreamTokenizer.TT_WORD:
				String word = tokenizer.sval;

				if (word.contains(".")) {
					// System.out.print("WORD:\t\t");
					String temp = "";
					String[] w = word.split("\\.");
					for (int i = 0; i < w.length; i++) {
						if (wordMap.containsKey(w[i])) {
							// System.out.print("W");
							temp += "W";
						} else {
							// System.out.print(w[i]);
							temp += w[i];
						}
						if (i < w.length - 1) {
							// System.out.print(".");
							temp += ".";
						}
					}
					// System.out.println();
					tokens.add(temp);
				}
				/* && !Character.isUpperCase(word.charAt(0)) */
				/* && !javaClassMap.containsKey(word) */
				else { /* if (!keywordMap.containsKey(word) && !datatypeMap.containsKey(word)
						&& !javaClassMap.containsKey(word)) { */
					// System.out.println("WORD:\t\t" + word);
					tokens.add("W");
					if (!wordMap.containsKey(word)) {
						wordMap.put(word, 1);
						wordList.add(word);
					}
				} /* else {
					// System.out.println("WORD:\t\t" + word);
					tokens.add(word.trim());
				} */
				break;
			case '"':
				String doublequote = tokenizer.sval;
				// System.out.println("DQUOTE:\t\t" + doublequote);
				// System.out.println("STRING:\t\tS");
				tokens.add("S");
				break;
			case '\'':
				String singlequote = tokenizer.sval;
//				System.out.println("SQUOTE:\t\t" + singlequote);
//				tokens.add(singlequote);
				tokens.add("C");
				break;
			case StreamTokenizer.TT_EOL:
				// System.out.println("TT_EOL");
				break;
			case StreamTokenizer.TT_EOF:
				// System.out.println("TT_EOF");
				break;
			default:
				char character = (char) tokenizer.ttype;
				if (!Character.isWhitespace(character) && character != '\n' && character != '\r') {
//					System.out.println("TT_Type:\t\t" + character);
					tokens.add(String.valueOf(character));
				}
				break;
			}
		}

		reader.close();
		
		return tokens;
	}

	public ArrayList<String> getTokensFromFile(String file) throws Exception {
		// reset wordMap
		wordMap = new HashMap<String, Integer>();
		
		FileReader fileReader = new FileReader(file);
		readJavaClassNames("JavaClass.txt");
		return tokenize(fileReader);
	}
	
	public ArrayList<String> getTokensFromString(String input) throws Exception {
		// reset wordMap
		wordMap = new HashMap<String, Integer>();
		readJavaClassNames("JavaClass.txt");
		return tokenize(new StringReader(input));
	}

	public void readJavaClassNames(String filepath) {
		File file = new File(filepath);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				javaClassMap.put(line, 1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setUpKeywordMap() {
		String[] keywords = { "abstract", "continue", "for", "new", "switch", "assert", "default", "if", "package",
				"synchronized", "boolean", "do", "goto", "private", "this", "break", "double", "implements",
				"protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof",
				"return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface",
				"static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native",
				"super", "while" };
		for (int i = 0; i < keywords.length; i++) {
			keywordMap.put(keywords[i], 1);
		}
	}

	public void setUpDatatypeMap() {
		datatypeMap.put("byte", 1);
		datatypeMap.put("short", 1);
		datatypeMap.put("int", 1);
		datatypeMap.put("long", 1);
		datatypeMap.put("float", 1);
		datatypeMap.put("double", 1);
		datatypeMap.put("boolean", 1);
		datatypeMap.put("char", 1);
	}

	public boolean isKeyword(String x) {
		// System.out.println(x);
		if (keywordMap.get(x) != null)
			return true;
		else
			return false;
	}
}