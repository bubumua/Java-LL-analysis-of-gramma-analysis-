import java.util.ArrayList;

public class Productions {
    public ArrayList<Production> productions;
    
    public Productions() {
        this.productions = new ArrayList<Production>();
    }
    
    public void addProduction(Production production) {
        productions.add(production);
    }
    
    public ArrayList<Production> searchSymbolInRight(Symbol symbol) {
        ArrayList<Production> result = new ArrayList<>();
        for (int i = 0; i < productions.size(); i++) {
            Production production = productions.get(i);
            if (production.rightContains(symbol)) {
                result.add(production);
            }
        }
        return result;
    }
    
    public void displayAll() {
        System.out.println("all productions below:");
        for (Production production : productions) {
            System.out.println(production.toString());
        }
        System.out.println("--------------------------------");
    }
}
