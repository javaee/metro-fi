#!/bin/sh

if [ $# != 1 ]; then
    echo Usage: trountripTest [roundtrip test name]
    exit
fi


handle_dir() {
    
    cd "$1"
    for file in *
    do
        if [ -d "$file" ]; then
            #echo $file is directory
            handle_dir "$file" $2
        else
            #echo $file is file
            handle_file "$file" $2
        fi
    done 
    cd ".."
}     

handle_file() {
    file=$1
    cmd=$2
    ext=${file##*.}
    if [ $ext = "xml" ]; then
        #echo $file is an xml file
        $cmd $file $(pwd)
    #else
        #echo $file is not an xml file
    fi
}


cmd=$1

for file in *
do
    if [ -d "$file" ]; then
        #echo $file is directory
        handle_dir "$file" $cmd
    else
        #echo $file is file, perform Xerces test
        handle_file "$file" $cmd
    fi
done 

removeEmptyDir.sh
