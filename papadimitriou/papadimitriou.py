import random
import os
import fileinput
import math
import time

clauses = []
variables = []

def papadimitriou(fileName):
    text = open(fileName).read()
    lines = text.split("\n")
    lines = [line.strip().split(" ") for line in lines]
    varSize = int(lines[2][3])
    clauses = [[int(line[0]),int(line[1])] for line in lines[3:]]
    for i in range(varSize-1):
        variables.append(random.randint(0,1))
    startTime = time.time()
    errClauses = returnErrClauses(clauses)
    k = 2*varSize*varSize
    while len(errClauses) > 0 and k > 0:
        k-=1
        flip = random.choice(errClauses)
        access = abs(random.choice(flip))
        clauses[access] = 1 if clauses[access] == 0 else 0
        errClauses = returnErrClauses(clauses)
    
    finishingTime = time.time() - startTime

    if len(errClauses) == 0:
        print("Satisfiable")
        return finishingTime * 1000
    else:
        print("Unsatisfiable")
        return "err"

def returnErrClauses(listClauses):
    trueClauses = []
    falseClauses = []
    for clause in listClauses:
        firstNegated = False
        secondNegated = False
        if clause[0] < 0:
            firstNegated = not firstNegated
        if clause[1] < 0:
            secondNegated = not secondNegated
        firstLit = clause[0]
        first = variables[abs(firstLit) - 1]
        secondLit = clause[1]
        second = variables[abs(secondLit)-1]
        if not firstNegated and not secondNegated:
            trueClauses.append((firstLit,secondLit)) if first or second else falseClauses.append((firstLit,secondLit))
        elif firstNegated and not secondNegated:
            trueClauses.append((firstLit, secondLit)) if not first or second else falseClauses.append((firstLit,secondLit))
        elif not firstNegated and secondNegated:
            trueClauses.append((firstLit, secondLit)) if first or not second else falseClauses.append((firstLit,secondLit))
        else:
            trueClauses.append((firstLit, secondLit)) if not first or not second else falseClauses.append(firstLit,secondLit)
        return falseClauses



    return 0

totalTime = 0
s = 100
errCount = 0

for i in range(s):
    random.seed(i)
    n = papadimitriou("ex500.cnf")
    if n == "err":
        errCount+=1
    else:
        totalTime += n

print("time: ", totalTime/s)
print("err: ", errCount)