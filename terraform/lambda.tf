resource "aws_lambda_function" "cloud_incident_api" {
  function_name = "cloud-incident-api"

  role = aws_iam_role.lambda_role.arn

  runtime = "java21"

  handler = "com.lifted.incident.LambdaHandler::handleRequest"

  filename = "../target/cloud-incident-api-1.0.0.jar"

  source_code_hash = filebase64sha256(
    "../target/cloud-incident-api-1.0.0.jar"
  )

  timeout     = 30
  memory_size = 256

  tags = {
    Name        = "cloud-incident-api"
    Environment = "dev"
    Project     = "cloud-incident-api"
  }
}
