package vassar.database.service;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.json.simple.JSONObject;

// I/O
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;


public class DebugAPI {

    private File           logFile;
    private BufferedWriter writer;
    private JSONObject     json;
    private FileWriter     jsonWriter;
    private String         outputFilePath; // = "/app/logs/jessInitDB.json"; ???

    public static class Builder {

        private File           logFile;
        private BufferedWriter writer;
        private JSONObject     json;
        private FileWriter     jsonWriter;
        private String         outputFilePath; // = "/app/logs/jessInitDB.json"; ???

        public Builder(String outputFilePath){
            try {
                this.outputFilePath = outputFilePath;
                this.logFile        = new File(this.outputFilePath);
                this.writer         = new BufferedWriter(new FileWriter(this.outputFilePath, true));
                this.json           = new JSONObject();
                this.jsonWriter     = new FileWriter(this.outputFilePath);
            }
            catch (Exception e) {
                System.out.println("EXC in DatabaseClient constructor " +e.getClass() + " : " + e.getMessage());
                e.printStackTrace();
            }
        }

        public Builder newFile(){
            try {
                if(!this.logFile.createNewFile()){
                    this.logFile.delete();
                    this.logFile.createNewFile();
                    this.logFile.setWritable(true, false);
                    this.logFile.setExecutable(true, false);
                    this.jsonWriter = new FileWriter(this.outputFilePath);
                }
            }
            catch (Exception e) {
                System.out.println("EXC in DatabaseClient constructor " +e.getClass() + " : " + e.getMessage());
                e.printStackTrace();
            }
            return this;
        }

        public DebugAPI build() {
            DebugAPI client    = new DebugAPI();

            client.logFile        = this.logFile;
            client.writer         = this.writer;
            client.json           = this.json;
            client.jsonWriter     = this.jsonWriter;
            client.outputFilePath = this.outputFilePath; // = "/app/logs/jessInitDB.json"; ???

            return client;
        }
    }

    public void putJson(String key, Object value) {
        JsonArray json_value  = new Gson().toJsonTree(value).getAsJsonArray();
        this.json.put(key, json_value);
    }

    public void writeJson() {
        try {
            System.out.println("Writing JSON to " + this.outputFilePath);
            String jsonString = this.json.toJSONString();
            this.jsonWriter.write(jsonString);
            this.jsonWriter.flush();
        }
        catch (Exception e) {
            System.out.println("EXC in DatabaseClient writeJson " +e.getClass() + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
