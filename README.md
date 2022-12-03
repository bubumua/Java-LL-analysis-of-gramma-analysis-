# LL(1)分析法

本项目尚未经充分测试！如有错误与不足，欢迎指正！

本项目基本实现了LL(1)分析法。

程序总体流程：

1. 先通过读取grammar.txt文本文件来获取文法。<i>（文本文件中文法的书写有一定要求，详细要求请见Main类代码注释。）</i>
2. 随后建立相应的LL(1)分析表。
3. 最后判断一段预置的字符串是否属于该文法的句子。

支持非终结符大写字母后带 ' 作为区分

支持用 | 作为候选式区分

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
