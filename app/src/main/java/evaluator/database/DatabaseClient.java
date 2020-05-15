package evaluator.database;

// I/O
import java.util.ArrayList;
import java.util.List;

// JSON
import com.evaluator.InstrumentAttributeQuery;
import com.evaluator.LaunchVehicleInformationQuery;
import com.evaluator.MeasurementAttributeQuery;
import evaluator.database.service.DebugAPI;
import evaluator.database.service.QueryAPI;
import evaluator.database.template.TemplateRequest;


public class DatabaseClient {

    private boolean        debug;
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

    public String processTemplateRequest(TemplateRequest request) {

        // PROCESS REQUEST
        return request.processRequest(this.queryAPI);
    }

    public void writeDebugInfo() {
        this.debugAPI.writeJson();
    }

}
