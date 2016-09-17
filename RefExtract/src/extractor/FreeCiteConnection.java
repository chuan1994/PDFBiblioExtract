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

public class FreeCiteConnection {

	ArrayList<String> input = new ArrayList<String>();
	ArrayList<String> encoded = new ArrayList<String>();

	public FreeCiteConnection(ArrayList<String> input) {
		this.input = input;

	}

	public String sendPostData() {
		URLConnection con;

		//Converting to URI encoded strings
		try {
			for(String x:input){
				encoded.add(URLEncoder.encode(x, StandardCharsets.UTF_8.name()));
			}
			
		} catch (UnsupportedEncodingException var4_4) {
		}
		
		
		//Starting connection to freecite
		try {
			URL url = new URL("http://freecite.library.brown.edu/citations/create");
			con = url.openConnection();
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		
		//Setting properties and parameters
		try {
			con.setRequestProperty("accept", "text/xml");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			
			for(String x : encoded){
				if (first){
					sb.append("citation[]=" + x);
					first = false;
				}
				
				else{
					sb.append("&citation[]=" + x);
				}
			}
			
			writer.write(sb.toString());
			writer.flush();
			
			//Retrieving output
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			StringBuilder result = new StringBuilder();
			
			String line;
			while((line = reader.readLine()) != null) {
			    result.append(line);
			}
			
			//return result
			return result.toString();
			
		} catch (IllegalStateException e) {
		} catch (IOException e) {
			return null;
		}
		return null;
	}

}
