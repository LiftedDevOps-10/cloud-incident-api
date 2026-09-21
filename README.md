# Cloud Incident API

A serverless incident management API built with Java 21,
AWS Lambda, API Gateway, DynamoDB, Terraform and GitHub Actions.

## Project Status

Phase 1 - Project Setup

## Planned Technologies

- Java 21
- Maven
- AWS Lambda
- API Gateway
- DynamoDB
- Terraform
- GitHub Actions
- CloudWatch
- Git
- GitHub

## Planned Features

- Create incidents
- Retrieve incidents
- Retrieve a single incident
- Update incidents
- Close incidents
- Health check endpoint
- Automated testing
- CI/CD deployment
- Infrastructure as Code
- Cloud monitoring
## AWS Architecture

The Cloud Incident API uses:

- AWS API Gateway HTTP API
- AWS Lambda
- Java 21
- Amazon DynamoDB
- Amazon CloudWatch

### DynamoDB

The application stores incident records in the `cloud-incidents`
DynamoDB table.

Primary key:

`incidentId`