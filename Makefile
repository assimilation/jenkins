# HELP
# This will output the help for each task
# thanks to https://marmelab.com/blog/2016/02/29/auto-documented-makefile.html
.PHONY: help

help: ## This help.
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

.DEFAULT_GOAL := help

login:  ## Login to dockerhub (to enable push)
	docker login

build:  ## Build the container (borgified/jenkins:staging)
	docker image build -t borgified/jenkins:staging .

push:   ## Upload the image to dockerhub
	docker image push borgified/jenkins

start:  ## Start the jenkins stack
	docker stack deploy -c jenkins.yml jenkins

stop:   ## Stop the jenkins stack
	docker stack rm jenkins
	echo "wait for networking to be removed" && sleep 15

restart: stop start ## stop/start the jenkins stack

plugins: ## update plugins.txt  curl creds in ~/.netrc (man curl for format)
	curl -s -k -n "http://localhost:8080/pluginManager/api/json?depth=1" | jq -r '.plugins[].shortName' | tee plugins.txt
