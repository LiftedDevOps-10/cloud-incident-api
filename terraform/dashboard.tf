resource "aws_cloudwatch_dashboard" "cloud_incident_api" {
  dashboard_name = "cloud-incident-api-dashboard"

  dashboard_body = jsonencode({
    widgets = [
      {
        type   = "metric"
        x      = 0
        y      = 0
        width  = 12
        height = 6

        properties = {
          title  = "API Gateway Requests"
          region = "eu-north-1"
          view   = "timeSeries"
          stat   = "Sum"
          period = 300

          metrics = [
            [
              "AWS/ApiGateway",
              "Count",
              "ApiId",
              aws_apigatewayv2_api.cloud_incident_api.id
            ]
          ]
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 0
        width  = 12
        height = 6

        properties = {
          title  = "API Gateway Latency"
          region = "eu-north-1"
          view   = "timeSeries"
          stat   = "Average"
          period = 300

          metrics = [
            [
              "AWS/ApiGateway",
              "Latency",
              "ApiId",
              aws_apigatewayv2_api.cloud_incident_api.id
            ]
          ]
        }
      },
      {
        type   = "metric"
        x      = 0
        y      = 6
        width  = 12
        height = 6

        properties = {
          title  = "API Gateway 5xx Errors"
          region = "eu-north-1"
          view   = "timeSeries"
          stat   = "Sum"
          period = 300

          metrics = [
            [
              "AWS/ApiGateway",
              "5xx",
              "ApiId",
              aws_apigatewayv2_api.cloud_incident_api.id
            ]
          ]
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 6
        width  = 12
        height = 6

        properties = {
          title  = "Lambda Errors"
          region = "eu-north-1"
          view   = "timeSeries"
          stat   = "Sum"
          period = 300

          metrics = [
            [
              "AWS/Lambda",
              "Errors",
              "FunctionName",
              aws_lambda_function.cloud_incident_api.function_name
            ]
          ]
        }
      },
      {
        type   = "metric"
        x      = 0
        y      = 12
        width  = 12
        height = 6

        properties = {
          title  = "Lambda Duration"
          region = "eu-north-1"
          view   = "timeSeries"
          stat   = "Average"
          period = 300

          metrics = [
            [
              "AWS/Lambda",
              "Duration",
              "FunctionName",
              aws_lambda_function.cloud_incident_api.function_name
            ]
          ]
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 12
        width  = 12
        height = 6

        properties = {
          title  = "DynamoDB Consumed Read Capacity"
          region = "eu-north-1"
          view   = "timeSeries"
          stat   = "Sum"
          period = 300

          metrics = [
            [
              "AWS/DynamoDB",
              "ConsumedReadCapacityUnits",
              "TableName",
              aws_dynamodb_table.cloud_incidents.name
            ]
          ]
        }
      }
    ]
  })
}
