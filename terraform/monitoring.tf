resource "aws_cloudwatch_metric_alarm" "lambda_errors" {
  alarm_name          = "cloud-incident-api-lambda-errors"
  alarm_description   = "Alerts when the Cloud Incident API Lambda function reports errors"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = 1
  metric_name         = "Errors"
  namespace           = "AWS/Lambda"
  period              = 60
  statistic           = "Sum"
  threshold           = 1

  dimensions = {
    FunctionName = aws_lambda_function.cloud_incident_api.function_name
  }

  treat_missing_data = "notBreaching"

  tags = {
    Name        = "cloud-incident-api-lambda-errors"
    Environment = "dev"
    Project     = "cloud-incident-api"
  }
}
resource "aws_cloudwatch_metric_alarm" "api_gateway_5xx" {
  alarm_name          = "cloud-incident-api-api-gateway-5xx"
  alarm_description   = "Alerts when the Cloud Incident API Gateway returns 5xx errors"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = 1
  metric_name         = "5xx"
  namespace           = "AWS/ApiGateway"
  period              = 60
  statistic           = "Sum"
  threshold           = 1

  dimensions = {
    ApiId = aws_apigatewayv2_api.cloud_incident_api.id
  }

  treat_missing_data = "notBreaching"

  tags = {
    Name        = "cloud-incident-api-api-gateway-5xx"
    Environment = "dev"
    Project     = "cloud-incident-api"
  }
}
