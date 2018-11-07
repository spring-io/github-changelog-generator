source /opt/concourse-java.sh

build() {
	run_maven clean install
}

setup_symlinks
cleanup_maven_repo "io.spring.githubreleasenotesgenerator"
