resource "aws_cloudwatch_log_group" "cloud_incident_api" {
  name              = "/aws/lambda/${aws_lambda_function.cloud_incident_api.function_name}"
  retention_in_days = 14

  tags = {
    Name        = "cloud-incident-api-logs"
    Environment = "dev"
    Project     = "cloud-incident-api"
  }
}
