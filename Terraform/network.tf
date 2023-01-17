# # resource "aws_vpc" "new_vpc" {
# #   cidr_block = "10.0.0.0/16"

# #   tags = {
# #     Name = "New VPC"
# #   }
# # }


# # resource "aws_subnet" "new_public_subnet" {
# #   vpc_id            = aws_vpc.new_vpc.id
# #   cidr_block        = "10.0.1.0/24"
# #   # availability_zone = "1a"

# #   tags = {
# #     Name = "New Public Subnet"
# #   }
# # }


# # resource "aws_internet_gateway" "New_Internet_Gateway" {
# #   vpc_id = aws_vpc.new_vpc.id

# #   tags = {
# #     Name = "New Internet Gateway"
# #   }
# # }


# # resource "aws_route_table" "Public_Route" {
# #   vpc_id = aws_vpc.new_vpc.id

# #   route {
# #     cidr_block = "0.0.0.0/0"
# #     gateway_id = aws_internet_gateway.New_Internet_Gateway.id
# #   }

# #   route {
# #     ipv6_cidr_block = "::/0"
# #     gateway_id      = aws_internet_gateway.New_Internet_Gateway.id
# #   }

# #   tags = {
# #     Name = "Public Route Table"
# #   }
# # }


# # resource "aws_route_table_association" "public_1_rt_a" {
# #   subnet_id      = aws_subnet.new_public_subnet.id
# #   route_table_id = aws_route_table.Public_Route.id
# # }


resource "aws_security_group" "sg_allow_ssh_jenkins" {
  name        = "allow_ssh_jenkins"
  description = "Allow SSH and Jenkins inbound traffic"
  #vpc_id      = aws_vpc.new_vpc.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
    cidr_blocks     = ["0.0.0.0/0"]
  }
}