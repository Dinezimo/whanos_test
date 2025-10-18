// whanos-dsl.groovy - VERSION ALTERNATIVE
def createWhanosJob(String repoName, String gitUrl) {
    job("Projects/project-${repoName}") {
        description("Auto-generated Whanos job for ${repoName}")
        
        triggers { scm('* * * * *') }
        
        scm {
            git {
                remote { url(gitUrl) }
                branch('main')
            }
        }
        
        steps {
            shell("""#!/bin/bash
echo "🔄 Whanos Auto-Build: ${repoName}"

# Détection du langage
if [ -f "pom.xml" ]; then
    LANGUAGE="java"
elif [ -f "package.json" ]; then
    LANGUAGE="javascript"
else
    echo "❌ Unsupported project type"
    exit 1
fi

# ✅ CHEMIN ABSOLU vers les Dockerfiles de link-project
WHANOS_PATH="/var/jenkins_home/workspace/link-project"
docker build \\
    -t ${repoName}:latest \\
    -f "\\$WHANOS_PATH/images/\\$LANGUAGE/Dockerfile.standalone" .

echo "✅ Build successful!"
""")
        }
    }
}

createWhanosJob("${REPO_NAME}", "${GIT_URL}")