grammar InterpreterGrammar;

lineCommand: commands NEWLINE;
commands: (cmds += command PIPE)* cmds += command;

command: cat | echo | wc | pwd | exit;
cat: CAT ((args += string)* args += string);
echo: ECHO (((args += string)* args += string) | );
wc: WC (((args += string)* args += string) | );
pwd: PWD;
exit: EXIT;

string: SYMBOL* SYMBOL;

CAT: 'cat';
ECHO: 'echo';
WC: 'wc';
PWD: 'pwd';
EXIT: 'exit';

NEWLINE: '\n' | '\r\n';
PIPE: '|';
SPACE: ' ' -> skip;
SINGLE_QUOTE: '\'';
DOUBLE_QUOTE: '"';
DOT: '.';
COMMA: ','


fragment LETTER: ('a'..'z' | 'A'..'Z');
