grammar Commands;

/**
 * Parser rules
 */
parse: command EOF;

command
    : quite_command
    | stat_request
    | computation_command;

quite_command: K_BYE;

stat_request
    : stat_reqs_command
    | stat_avg_time_command
    | stat_min_time_command
    | stat_max_time_command;

stat_reqs_command       : K_STAT_REQS;
stat_avg_time_command   : K_STAT_AVG_TIME;
stat_max_time_command   : K_STAT_MAX_TIME;
stat_min_time_command   : K_STAT_MIN_TIME;

computation_command: computation_kind UNDER values_kind SEMI_COLUMN variable_values_function SEMI_COLUMN expressions;
computation_kind : K_MIN | K_MAX | K_AVG | K_COUNT;
values_kind : K_GRID | K_LIST;

variable_values_function: variable_values (COMMA variable_values)*;
variable_values         : variable COLUMN variable_lower_value COLUMN variable_step_value COLUMN variable_upper_value;
variable_lower_value    : java_number;
variable_step_value     : java_number;
variable_upper_value    : java_number;

java_number : java_number_sign? DIGIT+ ('.')? DIGIT* java_number_exponential?
            | java_number_sign? (DIGIT+ '.')? DIGIT+ java_number_exponential?
            | java_number_sign? DIGIT* '.' DIGIT+ java_number_exponential?
            ;
java_number_exponential : ('e' | 'E') java_number_sign? DIGIT+;
java_number_sign        : ('-' | '+');

expressions: expression (SEMI_COLUMN expression)*;
// used for expressions build
evaluate: expression EOF;
expression : variable
           | number
           | PAR_OPEN expression operator expression PAR_CLOSE;
operator   : OP_ADD | OP_MINUS | OP_MUL | OP_DIV | OP_POW;
variable   : LETTER (LETTER | DIGIT)*;
number     : DIGIT+ ('.' DIGIT+)?;

/**
 * Lexer rules
 */
K_BYE : B Y E;
K_STAT_REQS : S T A T '_' R E Q S;
K_STAT_AVG_TIME : S T A T '_' A V G '_' T I M E;
K_STAT_MAX_TIME : S T A T '_' M A X '_' T I M E;
K_STAT_MIN_TIME : S T A T '_' M I N '_' T I M E;

K_GRID : G R I D;
K_LIST : L I S T;

K_MIN   : M I N;
K_MAX   : M A X;
K_AVG   : A V G;
K_COUNT : C O U N  T;

PAR_OPEN    : '(';
PAR_CLOSE   : ')';
// Math operators
OP_ADD      :   '+';
OP_MINUS    :   '-';
OP_MUL      :   '*';
OP_DIV      :   '/';
OP_POW      :   '^';

UNDER       : '_';
SEMI_COLUMN : ';';
COLUMN      : ':';
COMMA       : ',';

DIGIT: [0-9];
LETTER: [a-z];

fragment A : [A];
fragment B : [B];
fragment C : [C];
fragment D : [D];
fragment E : [E];
fragment F : [F];
fragment G : [G];
fragment H : [H];
fragment I : [I];
fragment J : [J];
fragment K : [K];
fragment L : [L];
fragment M : [M];
fragment N : [N];
fragment O : [O];
fragment P : [P];
fragment Q : [Q];
fragment R : [R];
fragment S : [S];
fragment T : [T];
fragment U : [U];
fragment V : [V];
fragment W : [W];
fragment X : [X];
fragment Y : [Y];
fragment Z : [Z];
