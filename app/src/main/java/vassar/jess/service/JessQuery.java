package vassar.jess.service;

import jess.Fact;
import jess.QueryResult;
import jess.Rete;
import jess.ValueVector;

import java.util.ArrayList;

public class JessQuery {

    private Rete   r;
    private String template;

    public static class Builder {

        private Rete   r;
        private String template;

        public Builder(Rete r) {
            this.r = r;
        }

        public Builder setQueryTemplate(String template) {
            this.template = template;
            return this;
        }

        public JessQuery build() {
            JessQuery build = new JessQuery();
            build.r = this.r;
            build.template = this.template;
            return build;
        }

    }

    public ArrayList<Fact> makeQuery() {
        ArrayList<Fact> facts = new ArrayList<>();
        String call = "(defquery TempArchitecture-query ?f <- (" + template + "))";
        try {
            r.eval(call);
            QueryResult q_result = r.runQueryStar("TempArchitecture-query", new ValueVector());

            while(q_result.next())
                facts.add((Fact) q_result.getObject("f"));

            r.removeDefrule("TempArchitecture-query");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return facts;
    }


}
