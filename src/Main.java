import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    /**
     * 总符号表，记录文法的所有符号
     */
    public static Set<Symbol> sigma = new HashSet<>();
    /**
     * 记录文法的所有产生式
     */
    public static Productions allProductions = new Productions();
    /**
     * 匹配文法符号的正则表达式
     */
    static final String V_REG = "[^|^=^:]'?";
    
    /**
     * 文法txt文件路径
     * 文法文件书写规范：
     * $表示空符号
     * 大写的英文字母（后面可以至多带一个'）表示非终结符号
     * 其它字符(#除外)表示终结符号
     * 文法文件每一行必须以<非终结符号>::=<符号串>|<符号串>|...形式表示
     * 文法必须是LL(1)文法，且开始符号所在的产生式必须写在第一行！
     */
    static final String FILENAME = "grammar.txt";
    
    public static void main(String[] args) throws FileNotFoundException {
        // 获取文件路径
        String path = Main.class.getResource(FILENAME).getPath();
        // 读取文件
        Scanner scanner = new Scanner(new FileReader(path));
        // 逐行处理每一条文法规则，记录每个文法符号，以及非终结符号的产生式右部。
        // 若是第一条规则，还会标记设定开始符号
        int lineNum = 0;
        while (scanner.hasNextLine()) {
            readOneRule(scanner.nextLine(), lineNum == 0);
            lineNum++;
        }
        // 粗略检验文法是否合法
        if (!grammarIsLegal()) {
            System.out.println("grammar is not legal!");
            return;
        } else {
            System.out.println("grammar may be legal!");
        }
        // 记录所有的产生式，为FOLLOW集的生成提供便利（遍历）
        for (Symbol symbolInSigma : sigma) {
            // 对符号表中每个非终结符号
            if (symbolInSigma.type == SymbolType.NONTERMINAL_SYMBOL) {
                NonTerminalSymbol nts = (NonTerminalSymbol) symbolInSigma;
                for (int i = 0; i < nts.productions.size(); i++) {
                    allProductions.addProduction(new Production(nts, nts.productions.get(i)));
                }
            }
        }
        allProductions.displayAll();
        // 为每个非终结符号的每个产生式生成FIRST集
        generateFirst();
        // 为每个非终结符号生成FOLLOW集
        generateFollow();
        // 输出符号表
        displaySigma();
        AnalysisTable table = new AnalysisTable();
        // 生成分析表
        generateAnalysisTable(table);
        // 输出分析表
        table.displayTable();
        // 定义要判断的句子
        String sentence = "i+i*i";
        if (LLAnalyze(table, sentence)) {
            System.out.println(sentence + " belongs to this grammar");
        } else {
            System.out.println(sentence + " not belongs to this grammar");
        }
    }
    
    /**
     * 对输入的字符串（句子）根据分析表进行LL分析
     *
     * @param table    LL分析表
     * @param inputStr 要判断的句子
     * @Return boolean
     * @author Bubu
     */
    private static boolean LLAnalyze(AnalysisTable table, String inputStr) {
        // 创建分析栈与输入栈
        Stack<Symbol> analysisStack = new Stack<>();
        Stack<Symbol> inputStack = new Stack<>();
        // 将界限符入栈（没看出来分析栈的界限符有什么用，就没入栈）
        inputStack.push(new BoundarySymbol());
        // 将开始符号入分析栈
        for (Symbol symbolInSigma : sigma) {
            if (symbolInSigma.type == SymbolType.NONTERMINAL_SYMBOL && ((NonTerminalSymbol) symbolInSigma).isStartSymbol) {
                analysisStack.push(symbolInSigma);
                break;
            }
        }
        // 将输入串逆序入输入栈
        for (int i = inputStr.length() - 1; i >= 0; i--) {
            inputStack.push(new TerminalSymbol(inputStr.substring(i, i + 1), SymbolType.TERMINAL_SYMBOL));
        }
        // 当分析栈不为空且输入栈顶不为界限符时循环
        while (!(analysisStack.isEmpty() && inputStack.peek().equals(new BoundarySymbol()))) {
            // 获取分析栈和输入栈顶符号
            Symbol fromAnalysisStack = analysisStack.peek();
            Symbol fromInputStack = inputStack.peek();
            // 如果分析栈顶符号是非终结符号
            if (fromAnalysisStack.type == SymbolType.NONTERMINAL_SYMBOL) {
                // 在分析表中寻找表项
                TableItem item = table.getItem((NonTerminalSymbol) fromAnalysisStack, fromInputStack);
                // 如果无法寻找到表项，则说明出错
                if (item == null) {
                    System.out.println("not found item:[" + fromAnalysisStack.toString() + "," + fromInputStack + "]");
                    return false;
                }
                // 若能寻找到表项，则分析栈出栈，再将表项中的产生式右部非空符号逆序入分析栈
                else {
                    analysisStack.pop();
                    for (Symbol s : item.getReverseProduction()) {
                        if (s.type != SymbolType.EMPTY_SYMBOL) {
                            analysisStack.push(s);
                        }
                    }
                }
            }
            // 如果分析栈顶不是非终结符号，即为终结符号或界限符
            else {
                // 比较分析栈顶符号与输入栈顶符号
                // 若相同，则分析栈和输入栈分别出出栈
                if (fromAnalysisStack.equals(fromInputStack)) {
                    analysisStack.pop();
                    inputStack.pop();
                }
                // 若分析栈顶与输入栈顶不相同，则出错
                else {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * 生成LL分析表
     *
     * @param table 一个空的分析表
     * @Return void
     * @author Bubu
     */
    private static void generateAnalysisTable(AnalysisTable table) {
        for (Symbol symbolInSigma : sigma) {
            // 对符号表中每个非终结符号
            if (symbolInSigma.type == SymbolType.NONTERMINAL_SYMBOL) {
                NonTerminalSymbol nts = (NonTerminalSymbol) symbolInSigma;
                for (int i = 0; i < nts.productions.size(); i++) {
                    for (Symbol firstSymbol : nts.firsts.get(i)) {
                        if (firstSymbol.equals(new EmptySymbol())) {
                            for (Symbol followSymbol : nts.follow) {
                                table.add(new TableItem(nts, followSymbol, nts.productions.get(i)));
                            }
                        } else {
                            table.add(new TableItem(nts, firstSymbol, nts.productions.get(i)));
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 为每个非终结符号生成FOLLOW集
     *
     * @Return void
     * @author Bubu
     */
    private static void generateFollow() {
        ArrayList<Integer> before = new ArrayList<>();
        ArrayList<Integer> after = new ArrayList<>();
        int count = 0;
        // 循环执行直到各非终结符号的FOLLOW集不再增大，且至少执行一轮
        while (!before.equals(after) || count == 0) {
            before.clear();
            after.clear();
            for (Symbol symbolInSigma : sigma) {
                // 对符号表中每个非终结符号求取FOLLOW集
                if (symbolInSigma.type == SymbolType.NONTERMINAL_SYMBOL) {
                    NonTerminalSymbol nts = (NonTerminalSymbol) symbolInSigma;
                    // 刷新FOLLOW集前，记录于before
                    before.add(nts.follow.size());
                    // 刷新FOLLOW集
                    nts.follow.addAll(generateFollowForNTS(nts));
                    // 刷新FOLLOW集后，记录于after
                    after.add(nts.follow.size());
                }
            }
            count++;
        }
    }
    
    
    /**
     * 为非终结符号生成FOLLOW集
     *
     * @param nts 要生成FOLLOW集的非终结符号
     * @Return java.util.Set<Symbol> FOLLOW集
     * @author Bubu
     */
    private static Set<Symbol> generateFollowForNTS(NonTerminalSymbol nts) {
        Set<Symbol> result = new HashSet<>();
        // 规则一：如果是开始符号，则将界限符加入FOLLOW集
        if (nts.isStartSymbol) {
            Symbol boundary = new BoundarySymbol();
            result.add(boundary);
        }
        // 获取所有右部存在非终结符号的产生式
        ArrayList<Production> searchResult = allProductions.searchSymbolInRight(nts);
        // 若获取不到，直接返回结果
        if (searchResult.size() < 1) {
            // System.out.println("there is no production which has the "+nts.name+" in right!");
            return result;
        }
        // 获取到搜索结果后，对各个产生式扩充FOLLOW集
        for (Production production : searchResult) {
            int ind = production.rightLocates(nts);
            ArrayList<Symbol> productionAfter = new ArrayList<>(production.right.subList(ind + 1, production.right.size()));
            // 规则二：若nts不处在产生式右部末尾，且产生式右部末尾符号串不等于空符号，则将产生式右部末尾符号串的FIRST集加入结果
            if (ind < production.right.size() - 1 && !productionAfter.get(0).equals(new EmptySymbol())) {
                Set<Symbol> firstWithoutEmptySymbol = generateFirstForProduction(new ArrayList<>(production.right.subList(ind + 1, production.right.size())));
                firstWithoutEmptySymbol.remove(new EmptySymbol());
                result.addAll(firstWithoutEmptySymbol);
            }
            // 规则三：若nts为产生式末尾，或者非末尾但末尾可推导出空符号，则将产生式左部的FOLLOW加入结果
            if (ind == production.right.size() - 1 || productionDeductEmpty(productionAfter)) {
                result.addAll(production.left.follow);
            }
        }
        return result;
    }
    
    /**
     * 判断产生式是否能推导出空符号
     * 判断依据是产生式右部的FIRST集是否包含空符号
     *
     * @param production 要判断的产生式（右部）
     * @Return boolean
     * @author Bubu
     */
    static boolean productionDeductEmpty(ArrayList<Symbol> production) {
        Set<Symbol> first = generateFirstForProduction(production);
        if (first.contains(new EmptySymbol())) {
            return true;
        } else {
            return false;
        }
        
    }
    
    /**
     * 带额外提示信息地输出符号表
     *
     * @param extraInfo 额外提示信息
     * @Return void
     * @author Bubu
     */
    private static void displaySigma(String extraInfo) {
        System.out.println("sigma below - " + extraInfo + ":");
        for (Symbol sb : sigma) {
            System.out.println(sb.toString());
        }
        System.out.println("--------------------------------");
    }
    
    /**
     * 输出符号表
     *
     * @Return void
     * @author Bubu
     */
    private static void displaySigma() {
        System.out.println("sigma below:");
        for (Symbol sb : sigma) {
            System.out.println(sb.toString());
        }
        System.out.println("--------------------------------");
    }
    
    /**
     * 对所有非终结符号求各自所有产生式的FIRST集
     *
     * @Return void
     * @author Bubu
     */
    public static void generateFirst() {
        for (Symbol symbolInSigma : sigma) {
            if (symbolInSigma.type == SymbolType.NONTERMINAL_SYMBOL) {
                NonTerminalSymbol nonTerminalSymbol = (NonTerminalSymbol) symbolInSigma;
                for (int i = 0; i < nonTerminalSymbol.productions.size(); i++) {
                    nonTerminalSymbol.addFirst(generateFirstForProduction(nonTerminalSymbol.productions.get(i)));
                }
            }
        }
    }
    
    /**
     * 为非终结符号求FIRST集
     *
     * @param symbolToGenerate 要求FIRST集的非终结符号
     * @Return java.util.Set<Symbol> FIRST集
     * @author Bubu
     */
    static Set<Symbol> generateFirstForNTS(NonTerminalSymbol symbolToGenerate) {
        for (Symbol symbol : sigma) {
            // 找到非终结符号
            if (symbol.equals(symbolToGenerate)) {
                Set<Symbol> result = new HashSet<>();
                // 对每一条产生式求FIRST集，然后取它们的并集
                for (int i = 0; i < ((NonTerminalSymbol) symbol).productions.size(); i++) {
                    result.addAll(generateFirstForProduction(((NonTerminalSymbol) symbol).productions.get(i)));
                }
                return result;
            }
        }
        System.out.println("symbol not found");
        return null;
    }
    
    /**
     * 为产生式求FIRST集
     *
     * @param production 要求FIRST集的产生式
     * @Return java.util.Set<Symbol> FIRST集
     * @author Bubu
     */
    static Set<Symbol> generateFirstForProduction(ArrayList<Symbol> production) {
        Set<Symbol> result = new HashSet<>();
        // 如果产生式首符号不是非终结符号，那么直接将首符号加入first集合，并返回结果
        if (production.get(0).type != SymbolType.NONTERMINAL_SYMBOL) {
            result.add(production.get(0));
            return result;
        }
        // 如果产生式首符号是非终结符号
        for (int i = 0; i < production.size(); i++) {
            // 如果不是最后一个符号，对每个符号求FIRST集，分情况加入到总FIRST集
            if (i < production.size() - 1) {
                Set<Symbol> candidateFirst = generateFirstForNTS((NonTerminalSymbol) (production.get(i)));
                // 若含空符号，则将非空符号加入FIRST集，然后再看下一个符号
                if (candidateFirst.contains(new EmptySymbol())) {
                    candidateFirst.remove(new EmptySymbol());
                    result.addAll(candidateFirst);
                }
                // 若不含空符号，则在全部加入FIRST集后返回结果
                else {
                    result.addAll(candidateFirst);
                    return result;
                }
            }
            // 若是最后一个符号，则不管有没有空符号，全部加入FIRST集，返回结果
            else {
                result.addAll(generateFirstForNTS((NonTerminalSymbol) (production.get(i))));
            }
        }
        return result;
    }
    
    /**
     * 根据非终结符号产生式数目判断文法是否合法
     * 遍历查询符号表，若有非终结符号产生式数目小于一条，返回false；否则返回true
     *
     * @Return boolean
     * @author Bubu
     */
    static boolean grammarIsLegal() {
        for (Symbol symbolInSigma : sigma) {
            if (symbolInSigma.type == SymbolType.NONTERMINAL_SYMBOL) {
                assert symbolInSigma instanceof NonTerminalSymbol;
                if (((NonTerminalSymbol) symbolInSigma).productions.size() < 1) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * 处理一条文法规则，该文法规则可以包含若干候选式
     *
     * @param originProductionStr 文法规则字符串
     * @Return void
     * @author Bubu
     */
    static void readOneRule(String originProductionStr, boolean ifMarkStart) {
        // 先对原始的文法规则进行分割，分割成若干只有一个候选式的产生式
        for (String productionStr : dealWithOrigin(originProductionStr)) {
            // 对每一条产生式，进行符号识别，以及产生式的录入
            // System.out.println("split: " + productionStr);
            if (productionStr.trim().length() == 0) {
                System.out.println("productionStr is empty!");
                return;
            }
            Pattern VN = Pattern.compile(V_REG);
            Matcher matcher = VN.matcher(productionStr);
            // 计数器，用于判别产生式左部和右部
            int count = 0;
            Symbol left = null;
            ArrayList<Symbol> production = new ArrayList<>();
            // 对每一个找到的符号
            while (matcher.find()) {
                String symbolStr = matcher.group(0);
                Symbol symbol = null;
                // 如果符号的首字母为大写，则归为非终结符号
                if (Character.isUpperCase(symbolStr.charAt(0))) {
                    symbol = new NonTerminalSymbol(symbolStr, SymbolType.NONTERMINAL_SYMBOL);
                }
                //如果符号是$，则归为空符号
                else if ("$".equals(symbolStr)) {
                    symbol = new EmptySymbol(symbolStr, SymbolType.EMPTY_SYMBOL);
                }
                // 如果符号开头不是大写字母，也不是$空符号，那就是终结符号
                else {
                    symbol = new TerminalSymbol(symbolStr, SymbolType.TERMINAL_SYMBOL);
                }
                // 若是产生式的第一个符号，标记为产生式左部
                if (count == 0) {
                    left = symbol;
                }
                // 非第一个的都加入到左部的产生式
                else {
                    production.add(symbol);
                }
                // 将符号添加到符号表
                sigma.add(symbol);
                count++;
            }
            // 遍历查询符号表，为产生式左部非终结符添加产生式，设置是否为开始符号
            for (Symbol symbolInSigma : sigma) {
                if (symbolInSigma.equals(left)) {
                    assert symbolInSigma instanceof NonTerminalSymbol;
                    ((NonTerminalSymbol) symbolInSigma).addProduction(production);
                    ((NonTerminalSymbol) symbolInSigma).isStartSymbol = ifMarkStart;
                    break;
                }
            }
        }
        
    }
    
    /**
     * 处理原始的文法规则字符串，拆分成只有一个候选式的若干产生式
     *
     * @param origin 原始的文法规则字符串
     * @Return java.util.ArrayList<java.lang.String> 产生式字符串列表
     * @author Bubu
     */
    static ArrayList<String> dealWithOrigin(String origin) {
        ArrayList<String> result = new ArrayList<>();
        // 分割产生式左右部的分隔符
        String productionSeparator = "::=";
        // 存放左右部的字符串数组
        String[] leftAndRight = origin.split(productionSeparator);
        // 匹配右部候选式的正则表达式
        final String V_REG_PLUS = "([^|^=^:]'?)+";
        Matcher matcher = Pattern.compile(V_REG_PLUS).matcher(leftAndRight[1]);
        // 对于每一个匹配到的候选式
        while (matcher.find()) {
            // 将其重组为只有一个候选项的表达式，加入到返回值中
            result.add(leftAndRight[0] + productionSeparator + matcher.group(0));
        }
        return result;
    }
    
    
}