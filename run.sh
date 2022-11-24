#!/usr/bin/env bash
javac -d bin src/chess/*.java src/chess/core/*.java src/chess/UI/*.java
java -cp bin:src chess.Application