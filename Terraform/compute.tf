data "aws_ami" "ubuntu" {
  most_recent = true

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["099720109477"] # Canonical
}


resource "aws_instance" "jenkins-instance" {
  ami             = data.aws_ami.ubuntu.id
  instance_type   = "t2.medium"
  key_name        = "${var.keyname}"
  #vpc_id          = aws_vpc.new_vpc.id
  security_groups = [aws_security_group.sg_allow_ssh_jenkins.name]
  #aws_security_group.sg_allow_ssh_jenkins.id
  #subnet_id          = aws_subnet.new_public_subnet.id
  #name            = "${var.name}"
  user_data = "${file("install_jenkins.sh")}"

  associate_public_ip_address = true
  tags = {
    Name = "Jenkins-Instance"
  }
}


output "jenkins_ip_address" {
  value = "${aws_instance.jenkins-instance.public_dns}"
}