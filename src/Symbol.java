/**
 * 文法符号
 *
 * @author Bubu
 */
public class Symbol {
    /**
     * 文法符号的名字，大写字母，后面至多带一个'
     */
    public String name;
    /**
     * 文法符号的类型
     */
    public SymbolType type;
    
    public Symbol(String name, SymbolType type) {
        this.name = name;
        this.type = type;
    }
    
    /**
     * 重写hashCode方法，返回符号的hash值，用于判断符号是否相同
     *
     * @Return int
     * @author Bubu
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    /**
     * 重写equals方法，用于判断两个符号是否相同
     *
     * @param obj 待判断的对象
     * @Return boolean
     * @author Bubu
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Symbol other = (Symbol) obj;
        if (name == null) {
            return other.name == null;
        } else {
            return name.equals(other.name) && type.equals(other.type);
        }
        
    }
    
    @Override
    public String toString() {
        return name + ": " + type.toString();
    }
}
