aws-utils
=========

Some needed for me AWS utils

Implemented:
GlacierUpload - utility to upload/download/remove archivs from AWS Glacier. 
Currently suported upload only.

*Usage:*
* Download AWS SDK
* Compile and make jar (no script imlemented yet)
* set environment variables: AWS_ACCESS_KEY_ID and AWS_SECRET_KEY
* run with options:

Usage:
  java -jar GlacierUpload.jar command <parameters> filenames
	command:
		upload - once supported for now
	parameters:
		--vault=vaultname
		--region=regionname. Optional. Default value: 'us-east-1'

