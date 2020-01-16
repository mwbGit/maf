#!/usr/bin/env bash
cd maf-project/maf-boot-parent
# 统一更改版本号
mvn -f "pom.xml" versions:set -DoldVersion=* -DnewVersion=1.0.1-RELEASE -DprocessAllModules=true -DallowSnapshots=true -DgenerateBackupPoms=true
#mvn versions:revert
cd ../../

