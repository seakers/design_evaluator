package vassar.database.template.request;

import com.evaluator.AttributeInheritanceQuery;
import com.evaluator.RequirementRuleAttributeQuery;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import vassar.GlobalScope;
import vassar.database.service.QueryAPI;
import vassar.database.template.TemplateRequest;
import vassar.database.template.TemplateResponse;
import vassar.database.template.response.BatchTemplateResponse;
import vassar.problem.Problem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RequirementRuleAttributeTemplateRequest extends TemplateRequest {

    private String wrapperTemplatePath;

    public static class Builder extends TemplateRequest.Builder<Builder> {

        private String wrapperTemplatePath;

        public Builder() {}

        public Builder setWrapperTemplatePath(String wrapperTemplatePath) {
            this.wrapperTemplatePath = wrapperTemplatePath;
            return this;
        }

        public RequirementRuleAttributeTemplateRequest build() { return new RequirementRuleAttributeTemplateRequest(this); }
    }

    protected RequirementRuleAttributeTemplateRequest(Builder builder){

        super(builder);
        this.wrapperTemplatePath = builder.wrapperTemplatePath;
    }

    // REQUIREMENT RULE
    public class RequirementRules{

        public String subobj;
        public HashMap<String, Integer> subobjRuleCount;
        public ArrayList<Rule> rules;
        public Problem.Builder problemBuilder;

        public RequirementRules(Problem.Builder problemBuilder){
            subobjRuleCount = new HashMap<>();
            rules           = new ArrayList<>();
            this.problemBuilder  = problemBuilder;
        }

        public void addRule(RequirementRuleAttributeQuery.Item item){
            // RECORD RULE NUMBER FOR SUBOBJECTIVE
            subobj     = item.objective().name();
            int    numAttrib  = 1; // one-up numbering

            if (!subobjRuleCount.containsKey(subobj)) {
                numAttrib  = 1;
            }
            else {
                numAttrib  = subobjRuleCount.get(subobj);
            }
            subobjRuleCount.put(subobj, numAttrib);

            // BUILD AND ADD RULE
            rules.add(
                    new Rule(item, numAttrib, this.problemBuilder)
            );
        }

        public class Rule{

            public String subobj;
            public String currentSubobj;
            public String index;
            public String parent;
            public String param;
            public String thresholds;
            public String scores;
            public String attrib;
            public String justif;
            public String numAttrib;
            public String naString;
            public String naFuzzyString;


            public Rule(RequirementRuleAttributeQuery.Item item, int numAttrib, Problem.Builder problemBuilder){

                subobj = currentSubobj = item.objective().name();
                index      = subobj.split("-",2)[1];
                parent     = subobj.split("-",2)[0];
                param      = item.measurement().name() ;

                naString      = StringUtils.repeat("N-A ", numAttrib);
                naFuzzyString = StringUtils.repeat("N-A ", numAttrib + 2);


                thresholds = arrayListToString((ArrayList<String>) item.thresholds()); //ArrayList<string / decimal>


                // attrib     = item.measurement_attribute().name() + " " + item.type() + " " + arrayListToString((ArrayList<String>) item.thresholds()) + " " + arrayListToString((ArrayList<String>) item.scores());
                attrib     = item.measurement_attribute().name();
                justif     =  StringEscapeUtils.unescapeHtml4(item.justification()).replaceAll("&lt;", "<").replaceAll("&gt;", ">");
                this.numAttrib  = Integer.toString(numAttrib);

                problemBuilder.subobjectivesToMeasurements.put(currentSubobj, param);
            }

            public String arrayListToString(ArrayList<String> ary) {
                String toReturn = "";

                for (int i = 0; i < ary.size(); i++) {
                    toReturn = toReturn + " " + objToString(ary.get(i));
                }

                return toReturn;
            }

            public String objToString(Object obj){
                if (obj instanceof BigDecimal)
                    return ((BigDecimal) obj).toString();
                else
                    return (String) obj;
            }
        }

    }


    // PROCESS REQUEST
    public TemplateResponse processRequest(QueryAPI api) {
        try {
            RequirementRules  rules = new RequirementRules(this.problemBuilder);

            // QUERY
            List<RequirementRuleAttributeQuery.Item> items = api.requirementRuleAttributeQuery();

            items.forEach(
                    item -> rules.addRule(item)
            );

            this.context.put("rules", rules);
            this.template.evaluate(this.writer, this.context);
            String ruleStr = this.writer.toString();
            this.writer.flush();

            this.engine.getTemplate(this.wrapperTemplatePath).evaluate(this.writer, this.context);
            String wrapperStr = this.writer.toString();

            GlobalScope.buildMeasurementsToSubobjectives();
            return new TemplateResponse.Builder()
                    .setTemplateString(ruleStr + " " + wrapperStr)
                    .build();
        }
        catch (Exception e) {
            System.out.println("Error processing orbit template request: " +e.getClass() + " : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
