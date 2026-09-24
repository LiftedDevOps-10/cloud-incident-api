resource "aws_apigatewayv2_api" "cloud_incident_api" {
  name          = "cloud-incident-http-api"
  protocol_type = "HTTP"

  tags = {
    Name        = "cloud-incident-http-api"
    Environment = "dev"
    Project     = "cloud-incident-api"
  }
}
