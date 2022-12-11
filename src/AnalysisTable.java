import java.util.ArrayList;

/**
 * LL分析表
 *
 * @author Bubu
 */
public class AnalysisTable {
    /**
     * 表项
     */
    public ArrayList<TableItem> items;
    
    public AnalysisTable() {
        items = new ArrayList<TableItem>();
    }
    
    /**
     * 添加表项
     * 返回值为真，加入成功；为假，加入失败
     *
     * @param item 加入的表项
     * @Return boolean 若真，加入成功；若假，加入失败
     * @author Bubu
     */
    public boolean add(TableItem item) {
        if (containsItem(item)) {
            System.out.println("Multiple Entries!");
            return false;
        }
        items.add(item);
        return true;
    }
    
    /**
     * 根据行首和列首，获取在分析表中的表项
     * 获取不到则返回null
     *
     * @param nts  行首（非终结符号）
     * @param tabs 列首（终结符号或界限符#）
     * @Return TableItem 表项或null
     * @author Bubu
     */
    public TableItem getItem(NonTerminalSymbol nts, Symbol tabs) {
        ArrayList<Symbol> result = new ArrayList<Symbol>();
        for (TableItem item : items) {
            if (item.nonTerminalSymbol.equals(nts) && item.TaBS.equals(tabs)) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * 根据传入表项查询分析表中是否含有该表项
     *
     * @param item 要查询的表项
     * @Return boolean
     * @author Bubu
     * @see #getItem(NonTerminalSymbol, Symbol)
     */
    public boolean containsItem(TableItem item) {
        return containsItem(item.nonTerminalSymbol, item.TaBS);
    }
    
    /**
     * 根据行首和列首查询分析表中是否含有表项
     *
     * @param nts  行首（非终结符号）
     * @param tabs 列首（终结符号或界限符#）
     * @Return boolean
     * @author Bubu
     */
    public boolean containsItem(NonTerminalSymbol nts, Symbol tabs) {
        for (TableItem item : items) {
            if (item.nonTerminalSymbol.equals(nts) && item.TaBS.equals(tabs)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 输出整个分析表
     *
     * @Return void
     * @author Bubu
     */
    public void displayTable() {
        System.out.println("Analysis Table below:");
        for (TableItem item : items) {
            System.out.println(item.toString());
        }
    }
}
