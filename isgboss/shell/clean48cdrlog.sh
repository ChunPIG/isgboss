cdrtxt_home=/export/home/msplog

#rm msp cdr log before 180minute
find ${cdrtxt_home} -name "*.txt" -amin +180 -exec rm {} \;