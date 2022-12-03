import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 文法符号中的非终结符号
 *
 * @author Bubu
 */
public class NonTerminalSymbol extends Symbol {
    /**
     * 是否为开始符号
     */
    boolean isStartSymbol;
    /**
     * 产生式列表
     */
    ArrayList<ArrayList<Symbol>> productions;
    /**
     * FIRST集列表
     */
    ArrayList<Set<Symbol>> firsts;
    /**
     * FOLLOW集
     */
    Set<Symbol> follow;
    
    public NonTerminalSymbol(String name, SymbolType type) {
        super(name, type);
        this.isStartSymbol = false;
        productions = new ArrayList<>();
        firsts = new ArrayList<>();
        follow = new HashSet<>();
    }
    
    /**
     * 为以该非终结符号为左部的产生式添加FIRST集
     *
     * @param first FIRST集
     * @Return void
     * @author Bubu
     */
    public void addFirst(Set<Symbol> first) {
        this.firsts.add(first);
    }
    
    /**
     * 添加以该非终结符号为左部的产生式的右部
     *
     * @param production 产生式右部
     * @Return void
     * @author Bubu
     */
    public void addProduction(ArrayList<Symbol> production) {
        productions.add(production);
    }
    
    @Override
    public String toString() {
        return super.toString() +
                (isStartSymbol ? "*" : "") +
                " " + "(" + productions.size() + ")" +
                "{" + productionsToString() + "}" +
                " FIRST:{" + firstsToString() + "}" +
                " FOLLOW:{" + followToString() + "}";
    }
    
    String followToString() {
        ArrayList<Symbol> temp = new ArrayList<>(follow);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < temp.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(temp.get(i).name);
        }
        return sb.toString();
    }
    
    String firstsToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < firsts.size(); i++) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(firstToString(firsts.get(i)));
        }
        return stringBuilder.toString();
    }
    
    String firstToString(Set<Symbol> first) {
        ArrayList<Symbol> temp = new ArrayList<>(first);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < temp.size(); i++) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(temp.get(i).name);
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
    
    String productionsToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < productions.size(); i++) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(productionToString(productions.get(i)));
        }
        return stringBuilder.toString();
    }
    
    String productionToString(ArrayList<Symbol> production) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Symbol symbol : production) {
            stringBuilder.append(symbol.name);
        }
        return stringBuilder.toString();
    }
}
