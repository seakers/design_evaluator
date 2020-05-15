package evaluator.database.template.requests;

import com.evaluator.LaunchVehicleInformationQuery;
import evaluator.database.service.QueryAPI;
import evaluator.database.template.TemplateRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LaunchVehicleTemplateRequest extends TemplateRequest {

    private String template_header;
    private String launch_vehicle_header;

    public static class Builder extends TemplateRequest.Builder<Builder> {

        private String template_header;
        private String launch_vehicle_header;

        public Builder() {}

        public Builder launchVehicleHeader(String launch_vehicle_header) {
            this.launch_vehicle_header = launch_vehicle_header;
            return this;
        }

        public Builder templateHeader(String template_header) {
            this.template_header = template_header;
            return this;
        }

        public LaunchVehicleTemplateRequest build() { return new LaunchVehicleTemplateRequest(this); }

    }

    protected LaunchVehicleTemplateRequest(Builder builder) {

        super(builder);
        this.template_header       = builder.template_header;
        this.launch_vehicle_header = builder.launch_vehicle_header;
    }

    public String processRequest(QueryAPI api) {
        try {
            // QUERY
            List<LaunchVehicleInformationQuery.Item> items = api.launchVehicleQuery();

            // BUILD CONTEXT
            this.context.put("template_header", this.template_header);
            this.context.put("launch_vehicle_header", this.launch_vehicle_header);
            this.context.put("items", items);

            // EVALUATE
            this.template.evaluate(this.writer, this.context);
            return this.writer.toString();

        }
        catch (Exception e) {
            System.out.println("Error processing orbit template request: " +e.getClass() + " : " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

}
