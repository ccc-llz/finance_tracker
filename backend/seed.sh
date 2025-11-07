#!/bin/bash
# Simple script to run seed data generation

./mvnw spring-boot:run -Dspring-boot.run.arguments="--seed-data"

