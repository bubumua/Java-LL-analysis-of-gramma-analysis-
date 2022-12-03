import java.util.ArrayList;

/**
 * 完整的产生式，包含左部（一个非终结符号）和右部（符号串）
 *
 * @author Bubu
 */
public class Production {
    /**
     * 左部非终结符号
     */
    NonTerminalSymbol left;
    /**
     * 右部符号串
     */
    ArrayList<Symbol> right;
    
    public Production() {
        left = null;
        right = new ArrayList<Symbol>();
    }
    
    public Production(NonTerminalSymbol left, ArrayList<Symbol> right) {
        this.left = left;
        this.right = right;
    }
    
    /**
     * 查询右部中是否含有传入的符号
     *
     * @param symbol 要查询的符号
     * @Return boolean
     * @author Bubu
     */
    public boolean rightContains(Symbol symbol) {
        for (Symbol s : right) {
            if (s.equals(symbol)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 返回传入符号在右部的下标
     *
     * @param symbol 要查询的符号
     * @Return int -1表示右部不存在传入符号，其余表示符号出现位置的下标
     * @author Bubu
     */
    public int rightLocates(Symbol symbol) {
        for (Symbol s : right) {
            if (s.equals(symbol)) {
                return right.indexOf(s);
            }
        }
        return -1;
    }
    
    
    @Override
    public String toString() {
        return left.name + "-->" + rightToString();
    }
    
    String rightToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Symbol symbol : right) {
            stringBuilder.append(symbol.name);
        }
        return stringBuilder.toString();
    }
}
