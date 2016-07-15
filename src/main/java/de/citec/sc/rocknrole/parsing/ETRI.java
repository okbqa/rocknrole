package de.citec.sc.rocknrole.parsing;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author cunger, Nam Daehwan, Won Yousung
 */
public class ETRI implements Parser {
    
    String ip;
    String url;
    String boundaries;
    
    public ETRI() {

        ip = "143.248.135.60";
        url = "http://143.248.135.187:10117/controller/service/etri_parser";
        boundaries = "\\.|\\?";
    }

    
    @Override
    public ParseResult parse(String text) {
        
        ParseResult result = new ParseResult();
        
        int i = 0;
        for (String sentence : text.split(boundaries)) { // TODO Do something more sophisticated here!
            
            i++;
            result.addSentence(i,sentence);
            
            String parse;
            try {
                //parse = request(url,sentence);
                parse = socket(sentence);
                result.addParse(i,convertToStanfordFormat(parse));
            }
            catch (Exception e) {
            }
        }

        return result;
    }
    
    public String request(String url, String text) throws UnsupportedEncodingException, IOException, Exception {
 
        HttpClient client = new DefaultHttpClient();        
        HttpPost post = new HttpPost("http://143.248.135.187:10117/controller/service/etri_parser");
                
        post.addHeader("Accept","application/x-www-form-urlencoded; charset=utf-8");
        post.addHeader("content-type","text/plain; charset=utf-8");
        HttpEntity entity = new ByteArrayEntity(text.getBytes("UTF-8"));
        post.setEntity(entity);

        // System.out.println("URI: " + post.getURI());
        
        HttpResponse response = client.execute(post);

        // System.out.println("ETRI response:\n" + response.getStatusLine());
            
        if (response.getStatusLine().getStatusCode() == 200) {
            return EntityUtils.toString(response.getEntity(),HTTP.UTF_8).trim();
        }
        else {
            throw new Exception("Status code returned by ETRI: " + response.getStatusLine().getStatusCode());
        }
    }
    
    public String socket(String text) throws Exception {
        
        StringBuffer sb = new StringBuffer();

        InetAddress ia = null;

        try {
            ia = InetAddress.getByName(ip);
            Socket soc = new Socket(ia,10117);

            OutputStream os = soc.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);

            bos.write((text).getBytes()); 
            bos.flush();
            soc.shutdownOutput();

            InputStream is = soc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line = null;
            while (true) {
                line = br.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.equals("")) continue;

                sb.append(line);
                sb.append("\n");
            }

            bos.close();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
        // System.out.println(sb.toString());
        
        return sb.toString();
    }
    
    public String convertToStanfordFormat(String etriResponse) {

        String stanford = "";
        
        JsonParser parser = new JsonParser();

        try {
            JsonObject json = (JsonObject) parser.parse(etriResponse);

            JsonArray sentences = (JsonArray) json.get("sentence");

            for (Object sentence : sentences) {
                JsonObject s = (JsonObject) sentence;
                                
                // Morphemes
                
                ArrayList<String> morphList = new ArrayList<>();

                JsonArray morphemes = (JsonArray) s.get("morp");
                for (Object mo : morphemes) {
                    JsonObject mor = (JsonObject)mo;
                    String lemma = (String) mor.get("lemma").getAsString();
                    morphList.add(lemma);
    		}

    		int morpCnt = 0; //count the number of characters of morphemes
    		int wordCnt = 0;
    			
    		Iterator<String> morphListIter = morphList.iterator();
                
                // Words 
                
                Map<String,String> wordIndex = new HashMap<>();
                
                JsonArray words = (JsonArray) s.get("word");
                
                for (Object word : words) {                	
                    JsonObject w = (JsonObject) word;                   
                    String id = w.get("id").toString();
                    String wordText = (String) w.get("text").getAsString();
                	
                    wordCnt += wordText.length();

                    String firstMorph = morphListIter.next();
                    morpCnt += firstMorph.length();
            		
                    while (morpCnt < wordCnt) {
                        String morph = morphListIter.next();
                	morpCnt += morph.length();
                	}

                    wordIndex.put(id,firstMorph);
                }
                
                // Dependency relations 
                
                JsonArray dependencies = (JsonArray) s.get("dependency");
                
                for (Object dependency : dependencies) {
                    JsonObject d = (JsonObject) dependency;
                    
                    String dpnd  = d.get("id").toString();
                    String head  = d.get("head").toString();
                    String label = (String) d.get("label").getAsString();
                    if (label.contains("_")) {
                        label = label.split("_")[1];
                    }
                    label = label.replace("-","");
                    
                    if (head.equals("-1")) continue;
                    
                    stanford += "\n" + label + "(" + wordIndex.get(head) + "-" + head + "," 
                                                   + wordIndex.get(dpnd) + "-" + dpnd + ")";
                }
                
                // SRLs
                
                JsonArray srls = (JsonArray) s.get("SRL");
                
                for (Object srl : srls) {
                    JsonObject r = (JsonObject) srl;
                    
                    String head = r.get("word_id").toString();
                    
                    JsonArray arguments = (JsonArray) r.get("argument");
                    
                    for (Object argument : arguments) {
                        JsonObject a = (JsonObject) argument;
                        
                        String role = (String) a.get("type").getAsString();
                        role = role.replace("-","");

                        String dpnd = a.get("word_id").toString();
                        
                        stanford += "\n" + role + "(" + wordIndex.get(head) + "-" + head + ","
                                                      + wordIndex.get(dpnd) + "-" + dpnd + ")";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        // System.out.println(stanford);
        
        return stanford.trim();
    }
}
