package org.okbqa.rocknrole.parsing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.util.List;
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
    
    JsonParser parser;
    
    
    public ETRI() {

        ip = "143.248.135.60";
        url = "http://143.248.135.187:10117/controller/service/etri_parser";
        
        boundaries = "\\.|\\?"; // TODO Do something more sophisticated?
        
        parser = new JsonParser();
    }

    
    @Override
    public ParseResult parse(String text) {
        
        ParseResult result = new ParseResult();
        
        int i = 0;
        for (String sentence : text.split(boundaries)) { 
            
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

 //     System.out.println(">>>>>>>> ETRI \n" + etriResponse + "\n<<<<<<<<<<<");
        
        String stanford = "";
        
        try {
            JsonObject json = parser.parse(etriResponse).getAsJsonObject();

            JsonArray sentences = json.getAsJsonArray("sentence");

            for (JsonElement sentence : sentences) {
                
                // NE indexes
                
                List<Integer> nes = new ArrayList<>();
                for (JsonElement ne : sentence.getAsJsonObject().getAsJsonArray("NE")) {
                     int begin = ne.getAsJsonObject().getAsJsonPrimitive("begin").getAsInt();
                     int end   = ne.getAsJsonObject().getAsJsonPrimitive("end").getAsInt();
                     for(int i = begin; i <= end; i++) {
                         nes.add(i);
                     }
                }
                            
                // Word stems with POS
               
                Map<Integer,String> nodenames = new HashMap<>();
                
                Map<String,String> lemmas = new HashMap<>();
                JsonArray morps = sentence.getAsJsonObject().getAsJsonArray("morp");
                for (JsonElement morp : morps) {
                     String pos;
                     if (nes.contains(morp.getAsJsonObject().getAsJsonPrimitive("id").getAsInt())) {
                         pos = "NE";
                     } else {
                         pos = morp.getAsJsonObject().getAsJsonPrimitive("type").getAsString();
                     }
                     lemmas.put(morp.getAsJsonObject().getAsJsonPrimitive("lemma").getAsString(),pos);
                }
               
                JsonArray words = sentence.getAsJsonObject().getAsJsonArray("word");
                for (JsonElement word : words) {
                    
                     int id = word.getAsJsonObject().getAsJsonPrimitive("id").getAsInt()+1;
                     String nodename = "";
                    
                     String text = word.getAsJsonObject().getAsJsonPrimitive("text").getAsString();
                     for (String lemma : lemmas.keySet()) {
                          if (text.startsWith(lemma)) {
                              nodename += lemma;
                          if (!lemmas.get(lemma).isEmpty()) {
                              nodename += "/" + lemmas.get(lemma);
                          }
                          break;
                          }
                     }
                     if (nodename.isEmpty()) nodename += text;

                     nodenames.put(id,nodename);
                }
                
                // Dependency relations 
                
                JsonArray dependencies = sentence.getAsJsonObject().getAsJsonArray("dependency");
                for (JsonElement dependency : dependencies) {
                     int    dpnd  = dependency.getAsJsonObject().getAsJsonPrimitive("head").getAsInt()+1;
                     int    head  = dependency.getAsJsonObject().getAsJsonPrimitive("id").getAsInt()+1;
                     String label = dependency.getAsJsonObject().getAsJsonPrimitive("label").getAsString();
                     // if (label.contains("_")) label = label.split("_")[1];
                     
                     if (nodenames.containsKey(dpnd)) {
                         if (head == -1) { 
                             stanford += "root(ROOT-0," 
                                      + nodenames.get(dpnd) + "-" + dpnd + ")\n";
                         } 
                         else if (nodenames.containsKey(head)) {
                             stanford += label + "(" + nodenames.get(head) + "-" + head 
                                               + "," + nodenames.get(dpnd) + "-" + dpnd 
                                               + ")\n";  
                         }
                     }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
                
        return stanford.trim();
    }
}
