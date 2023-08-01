
resource "aws_apigatewayv2_api" "spring-api-gatewway" {
  name                = "spring-api-gateway"
  protocol_type       = "HTTP"
#  target = var.lambda_arn
}

resource "aws_apigatewayv2_integration" "lambda_integrtion" {
  api_id              = aws_apigatewayv2_api.spring-api-gatewway.id
  integration_type    = "AWS_PROXY"
  integration_method  = "POST"
  payload_format_version = "2.0"
  integration_uri     = var.lambda_arn
}

resource "aws_apigatewayv2_route" "route" {
  api_id              = aws_apigatewayv2_api.spring-api-gatewway.id
  route_key           = "POST /test"
  target              = "integrations/${aws_apigatewayv2_integration.lambda_integrtion.id}"
}

resource "aws_apigatewayv2_stage" "stage" {
  api_id              = aws_apigatewayv2_api.spring-api-gatewway.id
  name                = "$default"
  auto_deploy         = true
}

resource "aws_lambda_permission" "lambda_permission" {
  statement_id        = "AllowAPIGatewayToInvoke"
  action              = "lambda:InvokeFunction"
  function_name       = "spring_boot_function"
  principal           = "apigateway.amazonaws.com"
  # The /*/*/* part allows invocation from any stage, method and resource path
  # within API Gateway REST API.
  source_arn          = "${aws_apigatewayv2_api.spring-api-gatewway.execution_arn}/*/*/*"
}

output "apiGatewayEndPoint" {
  value = aws_apigatewayv2_api.spring-api-gatewway.api_endpoint
}
