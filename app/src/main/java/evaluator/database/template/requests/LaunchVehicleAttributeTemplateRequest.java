package evaluator.database.template.requests;

        import com.evaluator.LaunchVehicleAttributeQuery;
        import com.evaluator.MeasurementAttributeQuery;
        import com.evaluator.OrbitAttributeQuery;
        import com.evaluator.OrbitInformationQuery;
        import evaluator.database.service.QueryAPI;
        import evaluator.database.template.TemplateRequest;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;


public class LaunchVehicleAttributeTemplateRequest extends TemplateRequest {

    private String template_header;

    public static class Builder extends TemplateRequest.Builder<Builder> {

        private String template_header;

        public Builder() {}

        public Builder templateHeader(String template_header) {
            this.template_header = template_header;
            return this;
        }

        public LaunchVehicleAttributeTemplateRequest build() { return new LaunchVehicleAttributeTemplateRequest(this); }

    }

    protected LaunchVehicleAttributeTemplateRequest(Builder builder){

        super(builder);
        this.template_header      = builder.template_header;
    }

    public String processRequest(QueryAPI api) {
        try {
            // QUERY
            List<LaunchVehicleAttributeQuery.Item> items = api.launchVehicleAttributeQuery();
            Map<String, Object> context = new HashMap<>();

            // BUILD CONTEXT
            this.context.put("template_header", this.template_header);
            this.context.put("items", items);

            // EVALUATE
            this.template.evaluate(this.writer, this.context);
            return this.writer.toString();
        }
        catch (Exception e) {
            System.out.println("Error processing orbit template request: " +e.getClass() + " : " + e.getMessage());
            e.printStackTrace();
        }
        return "Measurement Template Request Failed";
    }

}
