package evaluator.database.template;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import evaluator.database.service.QueryAPI;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class TemplateRequest {
    // template file path
    //

    private PebbleEngine   engine;
    protected PebbleTemplate template;
    protected StringWriter   writer;
    protected String         templateFilePath;
    private String         requestType;
    protected Map<String, Object> context;


    public static class Builder<T extends Builder<T>>{

        private PebbleEngine   engine;
        private PebbleTemplate template;
        private StringWriter   writer;
        private String         templateFilePath;
        private String         requestType;

        public Builder() {
            this.engine = new PebbleEngine.Builder().build();
            this.writer = new StringWriter();
            this.requestType      = "";
            this.templateFilePath = "";
        }

        public T applyExtension(AbstractExtension extension) {
            this.engine           = new PebbleEngine.Builder().extension(extension).build();
            return (T) this;
        }

        public T requestType(String requestType){
            this.requestType = requestType;
            return (T) this;
        }

        public T templateFilePath(String templateFilePath){
            this.template = this.engine.getTemplate(templateFilePath);
            return (T) this;
        }

        public TemplateRequest build() { return new TemplateRequest(this);}

    }

    protected TemplateRequest(Builder<?> builder) {

        this.context          = new HashMap<>();
        this.engine           = builder.engine;
        this.writer           = builder.writer;
        this.requestType      = builder.requestType;
        this.templateFilePath = builder.templateFilePath;
        this.template         = builder.template;

    }


    public String processRequest(QueryAPI api) {
        return "";
    }

}
