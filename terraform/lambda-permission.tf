resource "aws_lambda_permission" "apigateway_invoke" {
  statement_id  = "apigateway-invoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.cloud_incident_api.function_name
  principal     = "apigateway.amazonaws.com"

  source_arn = "${aws_apigatewayv2_api.cloud_incident_api.execution_arn}/*"
}
