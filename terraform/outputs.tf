output "api_gateway_url" {
  description = "Cloud Incident API Gateway endpoint"
  value       = aws_apigatewayv2_api.cloud_incident_api.api_endpoint
}

output "api_gateway_id" {
  description = "Cloud Incident API Gateway ID"
  value       = aws_apigatewayv2_api.cloud_incident_api.id
}

output "lambda_function_name" {
  description = "Cloud Incident Lambda function name"
  value       = aws_lambda_function.cloud_incident_api.function_name
}

output "lambda_function_arn" {
  description = "Cloud Incident Lambda function ARN"
  value       = aws_lambda_function.cloud_incident_api.arn
}

output "dynamodb_table_name" {
  description = "Cloud Incident DynamoDB table name"
  value       = aws_dynamodb_table.cloud_incidents.name
}

output "dynamodb_table_arn" {
  description = "Cloud Incident DynamoDB table ARN"
  value       = aws_dynamodb_table.cloud_incidents.arn
}
