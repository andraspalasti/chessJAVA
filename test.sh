#!/usr/bin/env bash

# Compile tests
javac -d ./bin/ -cp "lib/junit-platform-console-standalone-1.9.1.jar:src/:test/" test/chess/core/*.java

# Run test
java -cp "lib/junit-platform-console-standalone-1.9.1.jar:./bin/:src/:test/" org.junit.platform.console.ConsoleLauncher \
  --select-package chess.core