#!/bin/bash

turn=-1
bombsAvailable=2

read factoryCount
read linkCount

declare -A factories
declare -A links

for (( i=0; i<factoryCount; i++ )); do
    factories["${i}:id"]=$i
    factories["${i}:owner"]=0
    factories["${i}:cyborgs"]=0
    factories["${i}:production"]=0
    factories["${i}:turnsTillNormalProduction"]=0
    factories["${i}:incomingTroops"]=""
    factories["${i}:incomingBombs"]=""
    factories["${i}:bombSent"]=0
done

for (( i=0; i<linkCount; i++ )); do
    read factory1 factory2 distance
    links["${factory1}:${factory2}"]=$distance
    links["${factory2}:${factory1}"]=$distance
done

getScore() {
    incomingTroops=(${factories["${1}:incomingTroops"]})
    
    myCyborgs=0
    for i in "${incomingTroops[@]}"; do
        if [ "${troops["${i}:owner"]}" -eq "1" ]; then
            myCyborgs=$((myCyborgs + ${troops["${i}:cyborgs"]}))
        fi
    done
    
    enemyCyborgs=0
    for i in "${incomingTroops[@]}"; do
        if [ "${troops["${i}:owner"]}" -eq "-1" ]; then
            enemyCyborgs=$((enemyCyborgs + ${troops["${i}:cyborgs"]}))
        fi
    done
    
    case "${factories["${1}:owner"]}" in
    "1")
        production=0
        for i in "${incomingTroops[@]}"; do
            if [ "${troops["${i}:owner"]}" -eq "-1" ]; then
                production=$((production + $((${troops["${i}:remainingTurns"]} * ${factories["${1}:production"]}))))
            fi
        done
        
        echo $((${enemyCyborgs} - $((${factories["${1}:cyborgs"]} + ${myCyborgs} + ${production}))))
        ;;
    "-1")
        production=0
        for i in "${incomingTroops[@]}"; do
            if [ "${troops["${i}:owner"]}" -eq "1" ]; then
                production=$((production + $((${troops["${i}:remainingTurns"]} * ${factories["${1}:production"]}))))
            fi
        done
        
        echo $(($((${factories["${1}:cyborgs"]} + ${enemyCyborgs} + ${production})) - ${myCyborgs}))
        ;;
    "0")
        echo $(($((${factories["${1}:cyborgs"]} + ${enemyCyborgs})) - ${myCyborgs}))
        ;;
    esac
}

getMove() {    
    myFactories=()    
    for (( i=0; i<factoryCount; i++ )); do
        if [ "${factories["${i}:owner"]}" -eq "1" ]; then
            myFactories+=($i)
        fi
    done
    
    for i in "${myFactories[@]}"; do        
        if [ "${factories["${i}:cyborgs"]}" -le "0" ]; then
            continue
        fi
        
        if [ $turn -gt "2" ] && [ "${factories["${i}:cyborgs"]}" -ge "10" ] && [ $(getScore $i) -le "-10" ] && [ "${factories["${i}:production"]}" -lt "3" ]; then
            factories["${i}:cyborgs"]=$((${factories["${i}:cyborgs"]} - 10))
            echo "INC ${i}"
            return 1
        fi
        
        if [ $(getScore $i) -gt "0" ]; then
            continue
        fi
        
        targets=()
        for j in "${factories[@]}"; do
            if [ "$j" != "$i" ]; then
                if [ "${factories["${j}:owner"]}" == "1" ] && [ $(getScore $j) -gt "0" ]; then
                    targets+=($j)
                fi
                
                if [ "${factories["${j}:owner"]}" != "1" ] && [ $(($(getScore $j) + 1)) -gt "0" ]; then
                    targets+=($j)
                fi
            fi
        done
        
        if [ ${#targets[@]} -gt "0" ]; then        
            targets=($(
                for j in "${targets[@]}"; do
                    echo ${links["${i}:${j}"]}
                done | sort
            ))
            
            target=0
            for j in "${factories[@]}"; do
                if [ "${links["$i:$j"]}" == "${targets[0]}" ]; then
                    target=$j
                    break
                fi
            done
            
            cyborgsToSend=$(getScore $target)
            if [ "${factories["${target}:owner"]}" -ne "1" ]; then
                cyborgsToSend=$((cyborgsToSend + 1))
            fi
            
            factories["${i}:cyborgs"]=$((factories["${i}:cyborgs"] - cyborgsToSend))
            
            echo "MOVE ${i} ${target} ${cyborgsToSend}"
            return 1
        fi
    done
    
    echo "WAIT"
}

while true; do
    turn=$((turn + 1))
    
    unset troops
    declare -A troops
    
    unset bombs
    declare -A bombs
    
    read entityCount
    for (( i=0; i<entityCount; i++ )); do
        read entityId entityType arg1 arg2 arg3 arg4 arg5
        
        case "$entityType" in
        "FACTORY")
            factories["${entityId}:owner"]=$arg1
            factories["${entityId}:cyborgs"]=$arg2
            factories["${entityId}:production"]=$arg3
            factories["${entityId}:turnsTillNormalProduction"]=$arg4
            factories["${entityId}:incomingTroops"]=""
            factories["${entityId}:incomingBombs"]=""
            factories["${entityId}:bombSent"]=0
            ;;
        "TROOP")
            troops["${entityId}:owner"]=$arg1
            troops["${entityId}:source"]=$arg2
            troops["${entityId}:destination"]=$arg3
            troops["${entityId}:cyborgs"]=$arg4
            troops["${entityId}:remainingTurns"]=$arg5
            
            factories["${arg3}:incomingTroops"]="${factories["${arg3}:incomingTroops"]} ${entityId}"
            ;;
        "BOMB")
            bombs["${entityId}:owner"]=$arg1
            bombs["${entityId}:source"]=$arg2
            bombs["${entityId}:destination"]=$arg3
            bombs["${entityId}:remainingTurns"]=$arg4
            
            if (($arg3 > -1)); then
                factories["${arg3}:incomingBombs"]="${factories["${arg3}:incomingBombs"]} ${entityId}"
            fi
            ;;
        esac
    done
    
    # Stdout: echo "Message"
    # Stderr: echo "Message" >&2
    
    moves=("MSG Uhm, someone there?")
    
    if ((bombsAvailable > 0)) && ((turn > 0)); then
        bombSources=()
        for (( i=0; i<factoryCount; i++ )); do
            if [ "${factories["${i}:owner"]}" -eq 1 ]; then
                bombSources+=($i)
            fi
        done
        
        bombTargets=()
        for (( i=0; i<factoryCount; i++ )); do
            if [ "${factories["${i}:owner"]}" -eq "-1" ] && [ "${factories["${i}:production"]}" -ge "2" ] && [ "${factories["${i}:cyborgs"]}" -ge "5" ] && [ $(getScore $i) -gt "0" ] && [ "${factories["${i}:incomingBombs"]}" == "" ]; then
                bombTargets+=($i)
            fi
        done
        
        if [ ${#bombSources[@]} -ne 0 ] && [ ${#bombTargets[@]} -ne 0 ]; then
            bombTargets=($(
                for i in "${bombTargets[@]}"; do
                    echo $(getScore $i)
                done | sort -r
            ))
            
            bombTarget=0
            for (( i=0; i<factoryCount; i++ )); do
                if [ "${factories["${i}:owner"]}" -eq "-1" ] && [ "$(getScore $i)" -eq "${bombTargets[0]}" ]; then
                    bombTarget=$i
                    break
                fi
            done
            
            bombSources=($(
                for i in "${bombSources[@]}"; do
                    echo ${links["${i}:${bombTarget}"]}
                done | sort
            ))
            
            bombSource=0
            for (( i=0; i<factoryCount; i++ )); do
                if [ "${factories["${i}:owner"]}" -eq "1" ] && [ "${links["${i}:${bombTarget}"]}" -eq "${bombSources[0]}" ]; then
                    bombSource=$i
                    break
                fi
            done
            
            moves+=("BOMB ${bombSource} ${bombTarget}")
            
            factories["${bombSource}:bombSent"]=1
            bombsAvailable=$((bombsAvailable - 1))
        fi
    fi
    
    while ((${#moves[@]} < 3)); do
        move=$(getMove)
        
        if [ "$move" == "WAIT" ]; then
            break;
        fi
        
        moves+=("$move")
    done
    
    echo $(IFS=";"; echo "${moves[*]}")
done