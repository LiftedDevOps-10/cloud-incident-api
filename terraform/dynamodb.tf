resource "aws_dynamodb_table" "cloud_incidents" {
  name         = "cloud-incidents"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "incidentId"

  attribute {
    name = "incidentId"
    type = "S"
  }

  tags = {
    Name        = "cloud-incidents"
    Environment = "dev"
    Project     = "cloud-incident-api"
  }
}
