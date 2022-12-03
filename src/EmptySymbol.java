public class EmptySymbol extends Symbol {
    public EmptySymbol(String name, SymbolType type) {
        super(name, type);
    }
    
    public EmptySymbol() {
        this("$", SymbolType.EMPTY_SYMBOL);
    }
}
