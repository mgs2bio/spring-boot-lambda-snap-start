resource "aws_iam_role" "lambda_iam_role" {
  managed_policy_arns = [
    aws_iam_policy.request_lambda_policy.arn
  ]
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

data "aws_iam_policy_document" "request_lambda_policy_document" {
  statement {

    effect = "Allow"

    actions = [
      "sqs:*"
    ]

    resources = [
      var.sqs_arn
    ]
  }

  statement {

    effect = "Allow"

    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents"
    ]

    resources = [
      "arn:aws:logs:*:*:*"
    ]
  }
}

resource "aws_iam_policy" "request_lambda_policy" {
  name_prefix = "lambda_policy"
  path        = "/"
  policy      = data.aws_iam_policy_document.request_lambda_policy_document.json
  lifecycle {
    create_before_destroy = true
  }
}


