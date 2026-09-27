# Cloud Incident API

A serverless incident management API built with **Java 21, AWS Lambda, API Gateway, DynamoDB, Terraform, GitHub Actions, and CloudWatch**.

This project demonstrates how to design, deploy, test, monitor, and manage a production-style serverless application using modern DevOps practices.

---

## Project Status

**Completed — Serverless DevOps Project**

The project currently supports:

- REST API development
- Serverless deployment
- Full CRUD operations
- DynamoDB persistence
- Infrastructure as Code with Terraform
- Automated CI/CD with GitHub Actions
- Automated API testing
- CloudWatch logging
- Lambda performance monitoring
- AWS IAM security
- Terraform remote state management

---

# Architecture

```text
                    Developer
                        |
                        v
                  GitHub Repository
                        |
                        v
               GitHub Actions CI/CD
                        |
          +-------------+-------------+
          |                           |
          v                           v
     Maven Build                Terraform
     Java 21 Tests              Infrastructure
          |                           |
          +-------------+-------------+
                        |
                        v
                 AWS API Gateway
                  HTTP API
                        |
                        v
                  AWS Lambda
                   Java 21
                        |
                        v
                  DynamoDB
               cloud-incidents
                        |
                        v
                 CloudWatch Logs
AWS Architecture

The application uses the following AWS services:

| Service     | Purpose                                  |
| ----------- | ---------------------------------------- |
| API Gateway | Provides the HTTP API endpoint           |
| AWS Lambda  | Runs the Java application serverlessly   |
| DynamoDB    | Stores cloud incident records            |
| IAM         | Controls AWS permissions                 |
| CloudWatch  | Provides application logs and monitoring |
| S3          | Stores Terraform remote state            |

Technology Stack

Application
Java 21
Maven
AWS Lambda Java Runtime
AWS SDK for Java
Jackson JSON processing

AWS

AWS Lambda
Amazon API Gateway
Amazon DynamoDB
AWS IAM
Amazon CloudWatch
Amazon S3

DevOps

Git
GitHub
GitHub Actions
Terraform
Infrastructure as Code
Automated testing
CI/CD

API Endpoints

Base URL:

https://nm0wmlj0hh.execute-api.eu-north-1.amazonaws.com

Health Check

GET /health

Example:

curl https://nm0wmlj0hh.execute-api.eu-north-1.amazonaws.com/health

Response:

{
  "service": "cloud-incident-api",
  "runtime": "Java 21",
  "message": "Cloud Incident API is running!",
  "status": "healthy"
}

Incident API

Create an Incident
POST /incidents

Example:

curl -X POST \
  https://nm0wmlj0hh.execute-api.eu-north-1.amazonaws.com/incidents \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Database connection failure",
    "description": "Application cannot connect to production database",
    "severity": "CRITICAL",
    "status": "OPEN"
  }'

Example response:

{
  "incidentId": "INC-12345678",
  "message": "Incident created successfully"
}

Retrieve All Incidents
GET /incidents

Example:

curl https://nm0wmlj0hh.execute-api.eu-north-1.amazonaws.com/incidents

Example response:

{
  "incidents": [
    {
      "incidentId": "INC-001",
      "title": "Web server unavailable",
      "description": "Production web server is not responding",
      "severity": "HIGH",
      "status": "OPEN"
    }
  ]
}

Retrieve a Single Incident
GET /incidents/{incidentId}

Example:

curl https://nm0wmlj0hh.execute-api.eu-north-1.amazonaws.com/incidents/INC-001

Update an Incident
PUT /incidents/{incidentId}

Example:

curl -X PUT \
  https://nm0wmlj0hh.execute-api.eu-north-1.amazonaws.com/incidents/INC-001 \
  -H "Content-Type: application/json" \
  -d '{
    "severity": "HIGH",
    "status": "RESOLVED"
  }'

Delete an Incident
DELETE /incidents/{incidentId}

Example:

curl -X DELETE \
  https://nm0wmlj0hh.execute-api.eu-north-1.amazonaws.com/incidents/INC-001

A subsequent GET returns:

HTTP 404

when the incident no longer exists.

DynamoDB

The application stores incidents in:

cloud-incidents

Partition key:

incidentId

Example record:

{
  "incidentId": "INC-001",
  "title": "Web server unavailable",
  "description": "Production web server is not responding",
  "severity": "HIGH",
  "status": "OPEN"
}

The table uses:

PAY_PER_REQUEST

which allows DynamoDB to automatically handle capacity without manually managing provisioned read/write capacity.

Infrastructure as Code

Terraform manages the AWS infrastructure.

The project includes Terraform configuration for:

terraform/
├── apigateway.tf
├── dynamodb.tf
├── iam.tf
├── lambda-permission.tf
├── lambda.tf
├── outputs.tf
└── provider.tf

Terraform manages resources including:

Lambda function
DynamoDB table
IAM role
IAM policy
API Gateway
Lambda permissions
Terraform outputs

Terraform Remote State

Terraform state is stored remotely in Amazon S3.

Backend:

S3

Bucket:

cloud-incident-api-tfstate-2026

State key:

cloud-incident-api/terraform.tfstate

Region:

eu-north-1

Remote state provides centralized infrastructure state management and allows the CI/CD environment to work with the same Terraform state.

CI/CD Pipeline

GitHub Actions automatically performs the following workflow:

Git Push
   |
   v
GitHub Actions
   |
   v
Checkout Code
   |
   v
Setup Java 21
   |
   v
Run Maven Tests
   |
   v
Build Lambda Package
   |
   v
Configure AWS Credentials
   |
   v
Terraform Init
   |
   v
Terraform Validate
   |
   v
Terraform Format Check
   |
   v
Terraform Plan
   |
   v
Terraform Apply
   |
   v
Get API Gateway URL
   |
   v
Health Check
   |
   v
Automated CRUD Tests
   |
   v
Pipeline Success

Automated Testing

The GitHub Actions pipeline performs automated API testing after deployment.

The test sequence is:

POST
 |
 v
Create Incident
 |
 v
GET
 |
 v
Find Created Incident
 |
 v
GET Single Incident
 |
 v
PUT
 |
 v
Update Incident
 |
 v
DELETE
 |
 v
GET
 |
 v
Confirm HTTP 404

The pipeline automatically fails if an expected API operation does not succeed.

This provides an automated deployment verification mechanism rather than simply checking whether Terraform completed successfully.

CI/CD Security

AWS credentials are stored as GitHub repository secrets.

The workflow uses:

AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY

The credentials are not stored directly inside the source code.

Terraform and GitHub Actions use these credentials to authenticate with AWS.

Lambda Configuration

Function name:

cloud-incident-api

Runtime:

Java 21

Handler:

com.lifted.incident.LambdaHandler::handleRequest

Memory:

256 MB

Timeout:

30 seconds

Region:

eu-north-1

Lambda Performance Monitoring

The application includes timing instrumentation around important operations.

For example:

DynamoDB GetItem starting
DynamoDB GetItem completed
Lambda request received
Lambda request completed

This makes it possible to distinguish between:

API Gateway latency
        +
Lambda execution time
        +
DynamoDB operation time

This was particularly useful when investigating intermittent latency.

The application was tested at both 128 MB and 256 MB Lambda memory.

At 256 MB, normal warm requests demonstrated substantially lower Lambda execution time, while CloudWatch DynamoDB service-side latency remained in the millisecond range.

CloudWatch Monitoring

Lambda logs are available through Amazon CloudWatch.

The application logs:

HTTP method
API path
Lambda request start
Lambda request completion
DynamoDB operation start
DynamoDB operation completion
DynamoDB operation duration
Lambda execution duration

Example:

Lambda request received: GET /incidents/INC-001

DynamoDB GetItem starting

DynamoDB GetItem completed in 182 ms

Lambda request completed in 202 ms

This provides basic observability for troubleshooting application performance.

Project Structure
cloud-incident-api/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── lifted/
│   │               └── incident/
│   │                   ├── Application.java
│   │                   └── LambdaHandler.java
│   │
│   └── test/
│       └── java/
│
├── terraform/
│   ├── apigateway.tf
│   ├── dynamodb.tf
│   ├── iam.tf
│   ├── lambda-permission.tf
│   ├── lambda.tf
│   ├── outputs.tf
│   └── provider.tf
│
├── .github/
│   └── workflows/
│       └── ci-cd.yml
│
├── pom.xml
├── README.md
├── .gitignore
└── architecture.png

Local Development

Clone the repository:

git clone https://github.com/LiftedDevOps-10/cloud-incident-api.git

Enter the project:

cd cloud-incident-api

Build the application:

mvn clean package

Run tests:

mvn test

The Lambda deployment package is generated under:

target/

Terraform Deployment

Enter the Terraform directory:

cd terraform

Initialize Terraform:

terraform init

Validate the configuration:

terraform validate

Format Terraform files:

terraform fmt -recursive

Review the infrastructure plan:

terraform plan

Apply the infrastructure:

terraform apply

Useful Terraform Outputs

The API Gateway URL can be retrieved with:

terraform output -raw api_gateway_url

Lambda function name:

terraform output -raw lambda_function_name

DynamoDB table name:

terraform output -raw dynamodb_table_name

DevOps Concepts Demonstrated

This project demonstrates practical knowledge of:

Infrastructure as Code

Terraform defines AWS infrastructure as code rather than manually creating every resource.

Continuous Integration

GitHub Actions automatically runs:

Maven Tests

and validates the Terraform configuration.

Continuous Deployment

GitHub Actions automatically deploys the infrastructure and Lambda package using Terraform.

Automated Testing

The deployed API is automatically tested using real HTTP requests.

Serverless Computing

AWS Lambda runs the application without managing EC2 servers.

Cloud Databases

DynamoDB provides persistent NoSQL storage.

IAM

AWS permissions are controlled through IAM roles and policies.

Observability

CloudWatch logs and application instrumentation provide visibility into Lambda execution.

Remote State

Terraform state is stored remotely in Amazon S3.

Lessons Learned

During development, this project provided practical experience with:

Java Lambda deployment
Maven packaging
AWS API Gateway
DynamoDB CRUD operations
IAM permissions
Terraform resource management
Terraform state management
GitHub Actions
AWS authentication
CloudWatch debugging
Lambda performance investigation
API testing
Serverless architecture
CI/CD troubleshooting

A major lesson was that successful infrastructure deployment does not automatically mean the application is healthy.

The project therefore uses:

Infrastructure Validation
+
Application Health Check
+
Automated CRUD Testing

to verify the deployment.

Future Improvements

Potential future improvements include:

Authentication and authorization
Amazon Cognito
API request validation
Structured JSON logging
CloudWatch alarms
CloudWatch dashboards
AWS X-Ray tracing
Dead-letter queues
SNS incident notifications
SQS event processing
API throttling
Custom domain
HTTPS custom domain configuration
Secrets Manager integration
AWS WAF
Multi-environment Terraform configuration
Terraform modules
Remote backend hardening
OIDC authentication for GitHub Actions
Automated security scanning
Dependency vulnerability scanning
Docker-based local development
Integration testing
Load testing

Production Evolution

The project can evolve from a simple serverless API into a larger cloud incident management platform:

                     Users
                       |
                       v
                 API Gateway
                       |
                       v
                    Lambda
                       |
          +------------+------------+
          |            |            |
          v            v            v
      DynamoDB       SNS/SQS     CloudWatch
          |            |            |
          |            v            |
          |       Notifications     |
          |                         |
          +------------+------------+
                       |
                       v
                 Monitoring
                       |
                       v
                Incident Response

This architecture could eventually support:

Incident management
Automated alerts
Event-driven processing
Operational dashboards
Notification systems
Audit trails
DevOps automation

Repository

GitHub:

https://github.com/LiftedDevOps-10/cloud-incident-api

Author

Lifted Olabisi

Cloud & DevOps Engineering Portfolio Project.

Key Skills Demonstrated
Java 21
AWS Lambda
API Gateway
DynamoDB
Terraform
GitHub Actions
CI/CD
IAM
CloudWatch
Maven
Git
GitHub
Infrastructure as Code
Serverless Architecture
API Testing
Cloud Troubleshooting
Observability



