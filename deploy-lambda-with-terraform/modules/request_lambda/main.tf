locals {
  request_handler_file_name = "${path.module}/../../../request-handler/target/request-handler-0.0.1-SNAPSHOT-aws.jar"
}

resource "aws_lambda_function" "spring_boot_lambda" {
  function_name = "spring_boot_function"
  role          = aws_iam_role.lambda_iam_role.arn
  handler       = "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest"
  memory_size   = 512
  filename      = local.request_handler_file_name
  //Used to trigger updates.
  source_code_hash = filebase64sha256(local.request_handler_file_name)
  runtime       = "java11"
  package_type  = "Zip"
  timeout       = 15
  environment {
   variables = {
#     spring_cloud_function_definition = "placeOrderFromGateway"
     spring_cloud_function_definition = "placeOrder",
     SQSqueueName = var.sqs_queue_url
   }
  }
  snap_start {
    apply_on    = "PublishedVersions"
  }

}




output "lambda_arn" {
  value = aws_lambda_function.spring_boot_lambda.invoke_arn
}

