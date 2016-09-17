package extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Class is responsible for creating a connection to the freecite webapi.
 * 
 * Constructor consists of an arraylist of strings, whcih represents the data to be sent to the web api.
 * @author cwu323
 *
 */
public class FreeCiteConnection {

	ArrayList<String> input = new ArrayList<String>();
	ArrayList<String> encoded = new ArrayList<String>();

	public FreeCiteConnection(ArrayList<String> input) {
		this.input = input;
	}

	/**
	 * Creates a http POST request and receives its output as a string. 
	 * @return
	 */
	public String sendPostData() {
		URLConnection con;

		encoded = encodeInput(input);

		// Starting connection to freecite
		try {
			URL url = new URL(
					"http://freecite.library.brown.edu/citations/create");
			con = url.openConnection();
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		// Setting properties and parameters
		try {
			con.setRequestProperty("accept", "text/xml");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			OutputStreamWriter writer = new OutputStreamWriter(
					con.getOutputStream());
			
			String data = convertToParam(encoded);

			writer.write(data);
			writer.flush();

			// Retrieving output
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					con.getInputStream()));

			StringBuilder result = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}

			// return result
			return result.toString();

		} catch (IllegalStateException e) {
		} catch (IOException e) {
			return null;
		}
		return null;
	}

	/**
	 * Method to convert readable string input to URI encoded string
	 * @param input
	 * @return
	 */
	
	private ArrayList<String> encodeInput(ArrayList<String> input) {
		// Converting to URI encoded strings
		ArrayList<String> out = new ArrayList<String>();
		
		try {
			for (String x : input) {
				out.add(URLEncoder.encode(x, StandardCharsets.UTF_8.name()));
			}

		} catch (UnsupportedEncodingException e) {
		}
		return out;

	}

	/**
	 * Method to convert the input into the specified param format
	 * Exampele: citation[]=CITATION1&citation[]=CITATION2
	 * @param input
	 * @return
	 */
	
	private String convertToParam(ArrayList<String> input){
		StringBuilder sb = new StringBuilder();
		boolean first = true;

		
		for (String x : input) {
			if (first) {
				sb.append("citation[]=" + x);
				first = false;
			}

			else {
				sb.append("&citation[]=" + x);
			}
		}
		
		return sb.toString();
	}
}
