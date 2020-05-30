package vassar.database;

// I/O

// JSON
import vassar.database.service.DebugAPI;
import vassar.database.service.QueryAPI;
import vassar.database.template.TemplateRequest;
import vassar.database.template.TemplateResponse;


public class DatabaseClient {

    private boolean  debug;
    private QueryAPI queryAPI;
    private DebugAPI debugAPI;

    public static class Builder {

        private boolean  debug;
        private QueryAPI queryAPI;
        private DebugAPI debugAPI;

        public Builder(){
            this.debug = false;
            this.queryAPI = null;
        }
        public Builder debug(boolean debug){
            this.debug = debug;
            return this;
        }

        public Builder queryClient(QueryAPI qClient){
            this.queryAPI = qClient;
            return this;
        }

        public Builder debugClient(DebugAPI dClient){
            this.debugAPI = dClient;
            return this;
        }

        public DatabaseClient build() {
            DatabaseClient api    = new DatabaseClient();

            api.debug    = this.debug;
            api.queryAPI = this.queryAPI;
            api.debugAPI = this.debugAPI;

            return api;
        }
    }

    public TemplateResponse processTemplateRequest(TemplateRequest request) {

        // PROCESS REQUEST
        return request.processRequest(this.queryAPI);
    }

    public void writeDebugInfo() {
        this.debugAPI.writeJson();
    }

}