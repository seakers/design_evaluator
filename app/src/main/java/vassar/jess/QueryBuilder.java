package vassar.jess;

import jess.*;
import vassar.problem.Problem;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryBuilder {

    private Rete r;
    private HashMap<String, HashMap<String, Fact>> precomputedQueries;

    public static class Builder{

        private Rete r;
        private HashMap<String, HashMap<String, Fact>> precomputedQueries;

        public Builder(Rete r) {
            this.r = r;
            precomputedQueries = new HashMap<>();
        }

        public QueryBuilder build(){
            QueryBuilder build = new QueryBuilder();
            build.r = this.r;
            build.precomputedQueries = this.precomputedQueries;

            return build;
        }

    }

    public void computeQueries(Problem params){
        HashMap<String,Fact> db_instruments = new HashMap<>();
        for (int i = 0; i < params.getNumInstr(); i++) {
            String instr = params.getInstrumentList()[i];
            ArrayList<Fact> facts = this.makeQuery("DATABASE::Instrument (Name " + instr + ")");
            Fact f = facts.get(0);
            db_instruments.put(instr, f);
        }
        this.addPrecomputedQuery("DATABASE::Instrument", db_instruments);
    }

    public void addPrecomputedQuery(String key, HashMap<String, Fact> hm) {
        this.precomputedQueries.put(key, hm);
    }

    public ArrayList<Fact> makeQuery(String template) {
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
