#!/bin/bash
CLASSES="~/distribSys/workspaceDS/P2/build/classes/java/main"
SCRIPT="cd $CLASSES; java -cp . cs455.scaling.client.Client $HOSTNAME $2 $3;"
#$1 is the command-line argument specifying how many times it should open the machine list. 
#$2 is the command-line argument for the server's port number
#$3 is the command-line argument for the message frequency

for ((j=1;j<=$1;j++));
do
    COMMAND='gnome-terminal'
    for i in `cat machine_list`
    do
        echo 'logging into '$i
        OPTION='--tab -e "ssh -t '$i' '$SCRIPT'"'
        COMMAND+=" $OPTION"
    done
    eval $COMMAND &
done
