terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "4.48.0"
    }
  }
}


provider "aws" {
#  endpoints {
#    lambda = "http://localhost:4566"
#    iam = "http://localhost:4566"
#    apigateway = "http://localhost:4566"
#  }
  profile = "default"
  region  = "us-east-1"
}

module "sqs" {
  source = "./modules/SQS"
}

module "request_lambda" {
  source = "./modules/request_lambda"
  sqs_arn = module.sqs.sqs_arn
  sqs_queue_url = module.sqs.sqs_arn
}

module "consumer_lambda" {
  source = "./modules/consumer_lambda"
  sqs_arn = module.sqs.sqs_arn
}

module "apiGateway" {
   source = "./modules/ApiGateway"
   lambda_arn = module.request_lambda.lambda_arn
 }




output "apiGatewayEndPoint" {
  value = module.apiGateway.apiGatewayEndPoint
}