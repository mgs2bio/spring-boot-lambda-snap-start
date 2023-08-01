resource "aws_sqs_queue" "terraform_queue" {
  name                      = "terraform-example-queue"
  delay_seconds             = 2
  max_message_size          = 2048
  message_retention_seconds = 86400
  receive_wait_time_seconds = 10
#  redrive_policy = jsonencode({
#    deadLetterTargetArn = aws_sqs_queue.terraform_queue_deadletter.arn
#    maxReceiveCount     = 4
#  }
#  )

  tags = {
    Environment = "production"
  }
}


output "sqs_arn" {
  value = aws_sqs_queue.terraform_queue.arn
}

output "sqs_queue_url" {
  value = aws_sqs_queue.terraform_queue.url
}


