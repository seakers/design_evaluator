package vassar.database.template.request;

        import com.evaluator.InstrumentAttributeQuery;
        import vassar.database.service.QueryAPI;
        import vassar.database.template.TemplateRequest;
        import vassar.database.template.TemplateResponse;

        import java.util.List;


public class InstrumentAttributeTemplateRequest extends TemplateRequest {

    private String template_header;

    public static class Builder extends TemplateRequest.Builder<Builder> {

        private String template_header;

        public Builder() {}

        public Builder templateHeader(String template_header) {
            this.template_header = template_header;
            return this;
        }

        public InstrumentAttributeTemplateRequest build() { return new InstrumentAttributeTemplateRequest(this); }

    }

    protected InstrumentAttributeTemplateRequest(Builder builder){

        super(builder);
        this.template_header      = builder.template_header;
    }

    public TemplateResponse processRequest(QueryAPI api) {
        try {
            // QUERY
            List<InstrumentAttributeQuery.Item> items = api.instrumentAttributeQuery();

            // BUILD CONTEXT
            this.context.put("template_header", this.template_header);
            this.context.put("items", items);

            // EVALUATE
            this.template.evaluate(this.writer, this.context);

            return new TemplateResponse.Builder()
                                       .setTemplateString(this.writer.toString())
                                       .build();
        }
        catch (Exception e) {
            System.out.println("Error processing instrument template request: " +e.getClass() + " : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}

