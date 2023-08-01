locals {
  sqs_listener_file_name = "${path.module}/../../../sqs-listener/target/sqs-listener-0.0.1-SNAPSHOT-aws.jar"
}


resource "aws_lambda_function" "sqs_listener_lambda" {
  function_name = "sqs_listener_function"
  role          = aws_iam_role.lambda_sqs_listener_role.arn
  handler       = "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest"
  memory_size   = 512
  filename      = local.sqs_listener_file_name
  //Used to trigger updates.
  source_code_hash = filebase64sha256(local.sqs_listener_file_name)
  runtime       = "java11"
  package_type  = "Zip"
  timeout       = 15
  environment {
    variables = {
      #     spring_cloud_function_definition = "placeOrderFromGateway"
      spring_cloud_function_definition = "processOrder"
    }
  }
  snap_start {
    apply_on    = "PublishedVersions"
  }
}


resource "aws_lambda_permission" "with_sqs" {
  statement_id  = "AllowExecutionFromSQS"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.sqs_listener_lambda.arn
  principal     = "sqs.amazonaws.com"
  source_arn    = var.sqs_arn
}

