import java.util.ArrayList;
import java.util.Collections;

/**
 * LL分析表的表项
 *
 * @author Bubu
 */
public class TableItem {
    /**
     * 行首（非终结符号）
     */
    NonTerminalSymbol nonTerminalSymbol;
    /**
     * 列首（终结符号或界限符#）
     */
    Symbol TaBS;
    /**
     * 产生式右部
     */
    ArrayList<Symbol> productionItem;
    
    public TableItem(NonTerminalSymbol nonTerminalSymbol, Symbol taBS, ArrayList<Symbol> productionItem) {
        this.nonTerminalSymbol = nonTerminalSymbol;
        TaBS = taBS;
        this.productionItem = productionItem;
    }
    
    public TableItem(NonTerminalSymbol nonTerminalSymbol, Symbol taBS) {
        this.nonTerminalSymbol = nonTerminalSymbol;
        TaBS = taBS;
        this.productionItem = new ArrayList<>();
    }
    
    /**
     * 获取产生式右部的逆序列表
     *
     * @Return java.util.ArrayList<Symbol>
     * @author Bubu
     */
    public ArrayList<Symbol> getReverseProduction() {
        ArrayList<Symbol> result = new ArrayList<>(productionItem);
        Collections.reverse(result);
        return result;
    }
    
    @Override
    public String toString() {
        return "[" + nonTerminalSymbol.name + "," + TaBS.name + "]=" + itemToString();
    }
    
    private String itemToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < productionItem.size(); i++) {
            sb.append(productionItem.get(i).name);
        }
        return sb.toString();
    }
}
