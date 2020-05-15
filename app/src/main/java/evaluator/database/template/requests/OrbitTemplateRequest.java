package evaluator.database.template.requests;

import com.evaluator.OrbitInformationQuery;
import evaluator.database.service.QueryAPI;
import evaluator.database.template.TemplateRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrbitTemplateRequest extends TemplateRequest {

    private String template_header;
    private String orbit_header;

    public static class Builder extends TemplateRequest.Builder<Builder> {

        private String template_header;
        private String orbit_header;

        public Builder() {}

        public Builder orbitHeader(String orbit_header) {
            this.orbit_header = orbit_header;
            return this;
        }

        public Builder templateHeader(String template_header) {
            this.template_header = template_header;
            return this;
        }

        public OrbitTemplateRequest build() { return new OrbitTemplateRequest(this); }

    }

    protected OrbitTemplateRequest(Builder builder) {

        super(builder);
        this.template_header      = builder.template_header;
        this.orbit_header         = builder.orbit_header;
    }

    public String processRequest(QueryAPI api) {
        try {
            // QUERY
            List<OrbitInformationQuery.Item> items = api.orbitQuery();

            //items.get(1).attributes().get(1).attribute().name();

            Map<String, Object> context = new HashMap<>();

            // BUILD CONTEXT
            this.context.put("template_header", this.template_header);
            this.context.put("orbit_header", this.orbit_header);
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
