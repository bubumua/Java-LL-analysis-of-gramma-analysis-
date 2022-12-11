# LL(1)分析法

本项目尚未经充分测试！如有错误与不足，欢迎指正！

本项目大体实现了LL(1)分析法。

实验题目：

设计并实现一个LL(1)语法分析器，实现对算术文法

```
E->E+T|T ;
T->T*F|F ;
F->(E)|i ;
```

所定义的符号串进行识别，例如符号串98+99+80为文法所定义的句子，符号串(106-80(*95)不是文法所定义的句子。

实验要求

-   [ ] 检测左递归，如果有则进行消除；
-   [x] 求解FIRST集和FOLLOW集；
-   [x] 构建LL(1)分析表；
-   [x] 构建LL分析程序，对于用户输入的句子，能够利用所构造的分析程序进行分析，并显示出分析过程。

程序总体流程：

1. 先通过读取grammar.txt文本文件来获取文法。<i>（文本文件中文法的书写有一定要求，详细要求请见Main类代码注释。）</i>
2. 随后建立相应的LL(1)分析表。
3. 最后判断一段预置的字符串是否属于该文法的句子。

-   [x] 支持非终结符大写字母后带 ' 作为区分
-   [x] 支持用 | 作为候选式区分

## 示例

```
/* grammar.txt */
E::=TE'
E'::=+TE'|$
T::=FT'
T'::=*FT'|$
F::=(E)|i
```

```
/* 预置的要判断的句子 */
i+i*i
```

```
/* 运行结果 */
grammar may be legal!
all productions below:
E'-->+TE'
E'-->$
T'-->*FT'
T'-->$
T-->FT'
E-->TE'
F-->(E)
F-->i
--------------------------------
sigma below:
E': NONTERMINAL_SYMBOL (2){+TE',$} FIRST:{[+],[$]} FOLLOW:{#,)}
T': NONTERMINAL_SYMBOL (2){*FT',$} FIRST:{[*],[$]} FOLLOW:{#,),+}
T: NONTERMINAL_SYMBOL (1){FT'} FIRST:{[(,i]} FOLLOW:{#,),+}
$: EMPTY_SYMBOL
E: NONTERMINAL_SYMBOL* (1){TE'} FIRST:{[(,i]} FOLLOW:{#,)}
F: NONTERMINAL_SYMBOL (2){(E),i} FIRST:{[(],[i]} FOLLOW:{#,),*,+}
(: TERMINAL_SYMBOL
): TERMINAL_SYMBOL
i: TERMINAL_SYMBOL
*: TERMINAL_SYMBOL
+: TERMINAL_SYMBOL
--------------------------------
[E',+]=+TE'
[E',#]=$
[E',)]=$
[T',*]=*FT'
[T',#]=$
[T',)]=$
[T',+]=$
[T,(]=FT'
[T,i]=FT'
[E,(]=TE'
[E,i]=TE'
[F,(]=(E)
[F,i]=i
i+i*i belongs to this grammar
```
