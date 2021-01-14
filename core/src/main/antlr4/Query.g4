grammar Query;

start
   : expression EOF
   ;

expression
    : OPEN_PAR expression CLOSE_PAR     #parenthesizedExpression
    | K_NOT expression                  #negativeExpression
    | expression K_AND expression       #andExpression
    | expression K_OR expression        #orExpression
    | simple_expression                 #simpleExpression
    ;

simple_expression
   : comparison_expression
   | between_expression
   | matches_expression
   | imatches_expression
   | in_expression
   | null_comparison_expression
   ;

field_path
   : (FIELD_PATH_PART '.')* FIELD_PATH_PART
   ;

value
   : boolean_value
   | number_value
   | date_value
   | string_value
   ;

boolean_value
    : BOOLEAN
    ;

number_value
    : NUMBER
    ;

date_value
    : CURRENT_DATE
    | STRING
    ;

string_value
    : STRING
    ;

comparison_expression
   : field_path boolean_comparison_operator boolean_value
   | field_path comparison_operator (number_value|date_value|string_value)
   ;

between_expression
   : field_path K_NOT? K_BETWEEN number_value K_AND number_value
   | field_path K_NOT? K_BETWEEN date_value K_AND date_value
   | field_path K_NOT? K_BETWEEN string_value K_AND string_value
   ;

in_expression
   : field_path K_NOT? K_IN OPEN_PAR (value (',' value)*) CLOSE_PAR
   ;

matches_expression
   : field_path K_NOT? K_MATCHES string_value
   ;

imatches_expression
   : field_path K_NOT? K_IMATCHES string_value
   ;

null_comparison_expression
   : field_path K_IS K_NOT? K_NULL
   ;

comparison_operator
   : '='
   | '!='
   | '>'
   | '>='
   | '<'
   | '<='
   ;

boolean_comparison_operator
    : '='
    | '!='
    ;

BOOLEAN
    : K_TRUE
    | K_FALSE
    ;

NUMBER
    : [+-]? [0-9]+ ('.' [0-9]+)?
    ;

CURRENT_DATE
    : K_CURRENT_DATE
    | K_CURRENT_TIME
    | K_CURRENT_DATE_TIME
    ;

STRING
    : ('\'' ('\\\'' | ~[\r\n'])* '\'')
    | '"' ('\\"' | ~[\r\n"])* '"'
    ;

OPEN_PAR : '(';
CLOSE_PAR : ')';

// Keywords
K_AND: A N D;
K_BETWEEN: B E T W E E N;
K_CURRENT_DATE: C U R R E N T '_' D A T E;
K_CURRENT_TIME: C U R R E N T '_' T I M E;
K_CURRENT_DATE_TIME: C U R R E N T '_' D A T E '_' T I M E;
K_FALSE: F A L S E;
K_IMATCHES: I M A T C H E S;
K_IN: I N;
K_IS: I S;
K_MATCHES: M A T C H E S;
K_NOT: N O T;
K_NULL: N U L L;
K_OR: O R;
K_TRUE: T R U E;

FIELD_PATH_PART: [a-zA-Z_] [a-zA-Z_0-9]*;

SPACES: [ \u000B\t\r\n] -> skip;

// Case-Insensitive Lexing
// See https://github.com/antlr/antlr4/blob/master/doc/case-insensitive-lexing.md
fragment A : [aA]; // match either an 'a' or 'A'
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];